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

import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.commons.lang3.StringUtils;
import org.talend.components.processing.normalize.NormalizeProperties;
import org.talend.daikon.avro.converter.IndexedRecordConverter;

public class NormalizeDoFn extends DoFn<IndexedRecord, IndexedRecord> {

    private NormalizeProperties properties = null;

    private IndexedRecordConverter converter = null;

    @Setup
    public void setup() throws Exception {
    }

    @ProcessElement
    public void processElement(ProcessContext context) throws Exception {
        IndexedRecord inputRecord = context.element();
        Schema schema = inputRecord.getSchema();

        String columnToNormalize = properties.columnToNormalize.getValue();
        String delim = properties.fieldSeparator.getValue();
        boolean isDiscardTrailingEmptyStr = properties.discardTrailingEmptyStr.getValue();
        boolean isTrim = properties.trim.getValue();

        if (!StringUtils.isEmpty(columnToNormalize)) {

            String[] path = columnToNormalize.split("\\.");

            List<Object> normalizedFields = NormalizeUtils.getInputFields(inputRecord, columnToNormalize);

            if (NormalizeUtils.isSimpleField(normalizedFields)) {
                normalizedFields = NormalizeUtils.delimit(String.valueOf(normalizedFields.get(0)), delim,
                        isDiscardTrailingEmptyStr, isTrim);
            }

            for (Object outputValue : normalizedFields) {
                GenericRecord outputRecord = NormalizeUtils.generateNormalizedRecord(context.element(),
                        context.element().getSchema(), schema,
                        path, 0,
                        outputValue);
                context.output(outputRecord);
            }
        }
    }

    public NormalizeDoFn withProperties(NormalizeProperties properties) {
        this.properties = properties;
        return this;
    }
}
