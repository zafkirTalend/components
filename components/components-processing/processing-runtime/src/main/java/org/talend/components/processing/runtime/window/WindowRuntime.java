package org.talend.components.processing.runtime.window;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Sessions;
import org.apache.beam.sdk.transforms.windowing.SlidingWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;
import org.talend.components.adapter.beam.BeamJobBuilder;
import org.talend.components.adapter.beam.BeamJobContext;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.processing.definition.window.WindowProperties;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;

import io.netty.util.internal.StringUtil;

public class WindowRuntime extends PTransform<PCollection<IndexedRecord>, PCollection<IndexedRecord>>
        implements RuntimableRuntime, BeamJobBuilder {

    transient private WindowProperties properties;

    @Override
    public PCollection<IndexedRecord> apply(PCollection<IndexedRecord> indexedRecordPCollection) {
        PCollection<IndexedRecord> windowed_items;

        if (properties.windowDurationLength.getValue() == -1) {
            throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_ARGUMENT);
        }

        // Session Window
        if (properties.windowSession.getValue()) {
                windowed_items = indexedRecordPCollection.apply(Window.<IndexedRecord> into(
                        Sessions.withGapDuration(Duration.millis(properties.windowDurationLength.getValue().intValue()))));
                return windowed_items;
        }

        if (properties.windowSlideLength.getValue() == -1) {
            // Fixed Window
            windowed_items = indexedRecordPCollection.apply(
                    Window.<IndexedRecord> into(FixedWindows.of(new Duration(properties.windowDurationLength.getValue().intValue()))));
        } else {
            // Sliding Window
            windowed_items = indexedRecordPCollection.apply(
                    Window.<IndexedRecord> into(SlidingWindows.of(new Duration(properties.windowDurationLength.getValue().intValue()))
                            .every(new Duration(properties.windowSlideLength.getValue().intValue()))));
        }
        return windowed_items;
    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, Properties properties) {
        this.properties = (WindowProperties) properties;
        return ValidationResult.OK;
    }

    @Override
    public void build(BeamJobContext ctx) {
        String mainLink = ctx.getLinkNameByPortName("input_" + properties.MAIN_CONNECTOR.getName());
        if (!StringUtil.isNullOrEmpty(mainLink)) {
            PCollection<IndexedRecord> mainPCollection = ctx.getPCollectionByLinkName(mainLink);
            String flowLink = ctx.getLinkNameByPortName("output_" + properties.FLOW_CONNECTOR.getName());
            if ((mainPCollection != null) && (!StringUtil.isNullOrEmpty(flowLink))) {
                ctx.putPCollectionByLinkName(flowLink, apply(mainPCollection));
            }
        }
    }
}
