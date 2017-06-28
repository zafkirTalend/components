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
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.processing.normalize.NormalizeProperties;
import org.talend.components.processing.runtime.Utils;
import org.talend.daikon.avro.converter.IndexedRecordConverter;

public class NormalizeDoFn extends DoFn<IndexedRecord, IndexedRecord> {

    private static final Logger LOG = LoggerFactory.getLogger(NormalizeDoFn.class);

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
            if (StringUtils.contains(columnToNormalize, ".")) {
                String[] path = columnToNormalize.split("\\.");
                int indexColumnToNormalize = schema.getField(path[0]).pos();
                List<Object> inputValues = Utils.getInputFields(inputRecord, columnToNormalize);

                for (int i = 0; i < inputValues.size(); i++) {

                    Object inputValue = inputValues.get(i);

                    if (inputValue instanceof GenericData.Record) {

                        List<Object> inputChildValues = Utils.getInputFields((IndexedRecord) inputValue, path[1]);
                        for (int j = 0; j < inputChildValues.size(); j++) {
                            String[] strDelimited = delimit(inputChildValues.get(j), delim, isDiscardTrailingEmptyStr, isTrim);
                            for (int k = 0; k < strDelimited.length; k++) {
                                GenericRecord outputRecord = new GenericData.Record(schema);
                                for (int l = 0; l < schema.getFields().size(); l++) {
                                    if (l != indexColumnToNormalize) {
                                        outputRecord.put(l, inputRecord.get(l));
                                    }
                                }
                                outputRecord.put(indexColumnToNormalize, strDelimited[k]);
                                context.output(outputRecord);
                            }
                        }
                    } else {
                        String[] strDelimited = delimit(inputValue, delim, isDiscardTrailingEmptyStr, isTrim);
                        for (int j = 0; j < strDelimited.length; j++) {
                            GenericRecord outputRecord = new GenericData.Record(schema);

                            for (int k = 0; k < schema.getFields().size(); k++) {
                                if (k != indexColumnToNormalize) {
                                    outputRecord.put(k, inputRecord.get(k));
                                }
                            }

                            // GenericRecord indexedRecordToNormalize = (GenericRecord) inputRecord.get(indexColumnToNormalize);

                            Schema columnSchemaToNormalize = schema.getField(path[0]).schema();

                            IndexedRecord indexedRecordToNormalize = new GenericData.Record(columnSchemaToNormalize);

                            for (int k = 0; k < columnSchemaToNormalize.getFields().size(); k++) {

                                IndexedRecord tmp = (IndexedRecord) inputRecord.get(indexColumnToNormalize);
                                indexedRecordToNormalize.put(k, tmp.get(k));
                            }

                            int indexChildColumnToNormalize = columnSchemaToNormalize.getField(path[1]).pos();

                            indexedRecordToNormalize.put(indexChildColumnToNormalize, strDelimited[j]);

                            outputRecord.put(indexColumnToNormalize, indexedRecordToNormalize);

                            context.output(outputRecord);
                        }
                    }
                }
            } else {
                int indexColumnToNormalize = schema.getField(columnToNormalize).pos();
                List<Object> inputValues = Utils.getInputFields(inputRecord, columnToNormalize);
                for (int i = 0; i < inputValues.size(); i++) {
                    String[] strDelimited = delimit(inputValues.get(i), delim, isDiscardTrailingEmptyStr, isTrim);
                    for (int j = 0; j < strDelimited.length; j++) {
                        GenericRecord outputRecord = new GenericData.Record(schema);
                        for (int k = 0; k < schema.getFields().size(); k++) {
                            if (k != indexColumnToNormalize) {
                                outputRecord.put(k, inputRecord.get(k));
                            }
                        }
                        outputRecord.put(indexColumnToNormalize, strDelimited[j]);
                        context.output(outputRecord);
                    }
                }
            }
        }
    }

    /**
     * Splits obj around matches of the given delim parameter.
     * 
     * @param obj
     * @param delim the delimiting regular expression
     * @param isDiscardTrailingEmptyStr
     * @param isTrim
     * @return
     * @throws Exception if cannot cast obj to String.
     */
    private String[] delimit(Object obj, String delim, boolean isDiscardTrailingEmptyStr, boolean isTrim) throws Exception {
        String[] strDelimited = {};
        try {
            String str = (String) obj;
            strDelimited = str.split(delim);
            for (int i = 0; i < strDelimited.length; i++) {
                if (isDiscardTrailingEmptyStr) {
                    strDelimited[i] = strDelimited[i].replaceAll("\\s+$", "");
                }
                if (isTrim) {
                    strDelimited[i] = strDelimited[i].trim();
                }
            }
        } catch (Exception e) {
            LOG.debug("Cannot cast Object to String {}", e.getMessage());
        }
        return strDelimited;
    }

    public NormalizeDoFn withProperties(NormalizeProperties properties) {
        this.properties = properties;
        return this;
    }
}
