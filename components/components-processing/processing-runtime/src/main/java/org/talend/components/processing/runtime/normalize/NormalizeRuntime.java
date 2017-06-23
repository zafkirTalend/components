package org.talend.components.processing.runtime.normalize;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.commons.lang3.StringUtils;
import org.talend.components.adapter.beam.BeamJobBuilder;
import org.talend.components.adapter.beam.BeamJobContext;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.processing.normalize.NormalizeProperties;
import org.talend.daikon.properties.ValidationResult;

public class NormalizeRuntime extends PTransform<PCollection<Object>, PCollection>
        implements BeamJobBuilder, RuntimableRuntime<NormalizeProperties> {

    final static TupleTag<IndexedRecord> flowOutput = new TupleTag<IndexedRecord>() {
    };

    private NormalizeProperties properties;

    private boolean hasFlow;

    @Override
    public ValidationResult initialize(RuntimeContainer container, NormalizeProperties componentProperties) {
        this.properties = componentProperties;
        return ValidationResult.OK;
    }

    @Override
    public PCollection expand(PCollection<Object> inputPCollection) {
        NormalizeDoFn doFn = new NormalizeDoFn() //
                .withProperties(properties);

        PCollection outputCollection = inputPCollection.apply(properties.getName(), ParDo.of(doFn));
        return outputCollection;
    }

    @Override
    public void build(BeamJobContext ctx) {
        String mainLink = ctx.getLinkNameByPortName("input_" + properties.MAIN_CONNECTOR.getName());
        if (!StringUtils.isEmpty(mainLink)) {
            PCollection<Object> mainPCollection = ctx.getPCollectionByLinkName(mainLink);
            if (mainPCollection != null) {
                String flowLink = ctx.getLinkNameByPortName("output_" + properties.FLOW_CONNECTOR.getName());

                hasFlow = !StringUtils.isEmpty(flowLink);

                PCollection outputTuples = expand(mainPCollection);

                if (hasFlow) {
                    ctx.putPCollectionByLinkName(flowLink, outputTuples);
                }
            }
        }
    }
}
