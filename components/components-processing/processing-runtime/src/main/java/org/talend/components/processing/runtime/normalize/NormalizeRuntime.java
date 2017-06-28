// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.processing.runtime.normalize;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.commons.lang3.StringUtils;
import org.talend.components.adapter.beam.BeamJobBuilder;
import org.talend.components.adapter.beam.BeamJobContext;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.processing.normalize.NormalizeProperties;
import org.talend.daikon.properties.ValidationResult;

public class NormalizeRuntime extends PTransform<PCollection<IndexedRecord>, PCollection>
        implements BeamJobBuilder, RuntimableRuntime<NormalizeProperties> {

    private NormalizeProperties properties;

    @Override
    public ValidationResult initialize(RuntimeContainer container, NormalizeProperties componentProperties) {
        this.properties = componentProperties;
        return ValidationResult.OK;
    }

    @Override
    public PCollection expand(PCollection<IndexedRecord> inputPCollection) {
        NormalizeDoFn doFn = new NormalizeDoFn() //
                .withProperties(properties);

        PCollection outputCollection = inputPCollection.apply(properties.getName(), ParDo.of(doFn));
        return outputCollection;
    }

    @Override
    public void build(BeamJobContext ctx) {
        String mainLink = ctx.getLinkNameByPortName("input_" + properties.MAIN_CONNECTOR.getName());
        if (!StringUtils.isEmpty(mainLink)) {
            PCollection<IndexedRecord> mainPCollection = ctx.getPCollectionByLinkName(mainLink);
            if (mainPCollection != null) {
                String flowLink = ctx.getLinkNameByPortName("output_" + properties.FLOW_CONNECTOR.getName());

                PCollection outputTuples = expand(mainPCollection);

                if (!StringUtils.isEmpty(flowLink)) {
                    ctx.putPCollectionByLinkName(flowLink, outputTuples);
                }
            }
        }
    }
}
