package org.talend.components.processing.runtime.window;

import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.Sum;
import org.apache.beam.sdk.transforms.windowing.AfterWatermark;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.SlidingWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;
import org.talend.components.adapter.beam.BeamJobBuilder;
import org.talend.components.adapter.beam.BeamJobContext;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.processing.definition.window.WindowProperties;
import org.talend.components.processing.definition.window.WindowType;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;

import io.netty.util.internal.StringUtil;

public class WindowRuntime extends PTransform<PCollection<String>, PCollection<String>>
        implements RuntimableRuntime, BeamJobBuilder {

    transient private WindowProperties properties;

    @Override
    public PCollection<String> apply(PCollection<String> indexedRecordPCollection) {

        // Window type Time
        if (properties.windowType.getValue().equals(WindowType.TIME)) {
            if (properties.windowDurationLength.getValue() != -1) {
                if (properties.windowSlideLength.getValue() == -1) {
                    // Fixed Window
                    PCollection<String> windowed_items = indexedRecordPCollection.apply(Window
                            .<String> into(FixedWindows.of(new Duration(properties.windowDurationLength.getValue().intValue()))));
                    return windowed_items;
                } else {
                    // Sliding Windows
                    PCollection<String> windowed_items = indexedRecordPCollection.apply(Window
                            .<String> into(SlidingWindows.of(new Duration(properties.windowDurationLength.getValue().intValue()))
                                    .every(new Duration(properties.windowSlideLength.getValue().intValue()))));
                    return windowed_items;
                }
            } else {
                throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_ARGUMENT);
            }
        } else if (properties.windowType.getValue().equals(WindowType.COUNT)) {
            if (properties.windowDurationLength.getValue() != -1) {
                if (properties.windowSlideLength.getValue() == -1) {
                    // Fixed Window
                    PCollection<String> windowed_items = indexedRecordPCollection
                            .apply(Window.into(FixedWindows.of(new Duration(9999)))
                                    .triggering(AtWatermark()
                                            .withLateFirings(AtCount(properties.windowDurationLength.getValue().intValue())))
                                    .accumulatingFiredPanes())
                            .apply(Sum.integersPerKey());

                    return windowed_items;
                } else {
                    // Sliding Windows
                    PCollection<String> windowed_items = indexedRecordPCollection.apply(Window
                            .<String> into(SlidingWindows.of(new Duration(properties.windowDurationLength.getValue().intValue()))
                                    .every(new Duration(properties.windowSlideLength.getValue().intValue()))));
                    return windowed_items;
                }
            } else {
                throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_ARGUMENT);
            }
        } else {
            throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_ARGUMENT);
        }
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
            PCollection<String> mainPCollection = ctx.getPCollectionByLinkName(mainLink);
            String flowLink = ctx.getLinkNameByPortName("output_" + properties.FLOW_CONNECTOR.getName());
            if ((mainPCollection != null) && (!StringUtil.isNullOrEmpty(flowLink))) {
                ctx.putPCollectionByLinkName(flowLink, apply(mainPCollection));
            }
        }
    }
}
