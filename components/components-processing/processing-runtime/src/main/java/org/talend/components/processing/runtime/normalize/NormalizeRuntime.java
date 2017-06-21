package org.talend.components.processing.runtime.normalize;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TupleTagList;
import org.apache.commons.lang3.StringUtils;
import org.talend.components.adapter.beam.BeamJobBuilder;
import org.talend.components.adapter.beam.BeamJobContext;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.processing.normalize.NormalizeProperties;
import org.talend.daikon.properties.ValidationResult;

/**
 * Created by zafkir on 19/06/2017.
 */
public class NormalizeRuntime extends PTransform<PCollection<Object>, PCollectionTuple>
        implements BeamJobBuilder, RuntimableRuntime<NormalizeProperties> {

    final static TupleTag<IndexedRecord> flowOutput = new TupleTag<IndexedRecord>() {
    };

    // Word lengths side output.
    final static TupleTag<IndexedRecord> rejectOutput = new TupleTag<IndexedRecord>() {
    };

    private NormalizeProperties properties;

    private boolean hasFlow;

    private boolean hasReject;

    @Override
    public ValidationResult initialize(RuntimeContainer container, NormalizeProperties componentProperties) {
        this.properties = componentProperties;
        return ValidationResult.OK;
    }

    @Override
    public PCollectionTuple expand(PCollection<Object> inputPCollection) {
        NormalizeDoFn doFn = new NormalizeDoFn() //
                .withProperties(properties) //
                .withOutputSchema(hasFlow) //
                .withRejectSchema(hasReject);
        return inputPCollection.apply(properties.getName(),
                ParDo.withOutputTags(flowOutput, TupleTagList.of(rejectOutput)).of(doFn));

    }

    @Override
    public void build(BeamJobContext ctx) {
        String mainLink = ctx.getLinkNameByPortName("input_" + properties.MAIN_CONNECTOR.getName());
        if (!StringUtils.isEmpty(mainLink)) {
            PCollection<Object> mainPCollection = ctx.getPCollectionByLinkName(mainLink);
            if (mainPCollection != null) {
                String flowLink = ctx.getLinkNameByPortName("output_" + properties.FLOW_CONNECTOR.getName());
                String rejectLink = ctx.getLinkNameByPortName("output_" + properties.REJECT_CONNECTOR.getName());

                hasFlow = !StringUtils.isEmpty(flowLink);
                hasReject = !StringUtils.isEmpty(rejectLink);

                PCollectionTuple outputTuples = expand(mainPCollection);

                if (hasFlow) {
                    ctx.putPCollectionByLinkName(flowLink, outputTuples.get(flowOutput));
                }
                if (hasReject) {
                    ctx.putPCollectionByLinkName(rejectLink, outputTuples.get(rejectOutput));
                }
            }
        }
    }
}
