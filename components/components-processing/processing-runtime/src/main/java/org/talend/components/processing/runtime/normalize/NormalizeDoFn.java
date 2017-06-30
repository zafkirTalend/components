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
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.processing.normalize.NormalizeProperties;
import org.talend.components.processing.runtime.Utils;
import org.talend.daikon.avro.AvroUtils;
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

            // structure hierarchique
            if (StringUtils.contains(columnToNormalize, ".")) {

                String[] path = columnToNormalize.split("\\.");


                ObjectMapper mapper = new ObjectMapper();

                // transform inputRecord to jsonNode
                JsonNode hierarchicalNode = mapper.readTree(inputRecord.toString());

                // search the normalized node
                JsonNode normalizedNode = Utils.searchNode(hierarchicalNode, path);

                // get the normalized values delimited
                String[] delimited = delimit(normalizedNode.getTextValue(), delim, isDiscardTrailingEmptyStr, isTrim); // Utils.tmp(actualObj,
                // path, 0);

                Object[] outputValues = { "h1", "h2" };
                for (Object outputValue : delimited) {
                    context.output(generateNormalizedRecord(context.element(), context.element().getSchema(), schema, path, 0,
                            outputValue));
                }
            }

            // simple structure
            else {
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

    private GenericRecord generateNormalizedRecord(IndexedRecord inputRecord, Schema inputSchema, Schema outputSchema,
            String[] pathToElementToNormalize, int pathIterator, Object outputValue) {
        GenericRecordBuilder outputRecord = new GenericRecordBuilder(outputSchema);
        for (Schema.Field field : outputSchema.getFields()) {
            if (inputSchema.getField(field.name()) != null) {
                // The column was existing on the input record, we forward it to the output record.
                Object inputValue = inputRecord.get(inputSchema.getField(field.name()).pos());

                if (pathToElementToNormalize.length > 0 && pathIterator < pathToElementToNormalize.length) {
                    if (field.name().equals(pathToElementToNormalize[pathIterator])) {
                        // The current column can be a Record (an hierarchical sub-object) or directly a value.
                        // If we are on a record, we need to recursively do the process
                        // if we are on a object, we save it to the output.
                        if (inputValue instanceof GenericData.Record) {
                            // The sub-schema at this level is a union of "empty" and a record,
                            // so we need to get the true sub-schema
                            Schema inputChildSchema = AvroUtils.unwrapIfNullable(inputSchema.getField(field.name()).schema());
                            Schema outputChildSchema = AvroUtils.unwrapIfNullable(outputSchema.getField(field.name()).schema());
                            if (inputChildSchema.getType().equals(Schema.Type.RECORD)
                                    && outputChildSchema.getType().equals(Schema.Type.RECORD)) {
                                pathIterator++;
                                Object childRecord = generateNormalizedRecord((IndexedRecord) inputValue, inputChildSchema,
                                        outputChildSchema, pathToElementToNormalize, pathIterator, outputValue);
                                outputRecord.set(field.name(), childRecord);
                            }
                        } else {
                            if (pathIterator == pathToElementToNormalize.length - 1) {
                                outputRecord.set(field.name(), outputValue);
                            } else {
                                // TODO return exception
                            }
                        }
                    } else {
                        // The current column can be a Record (an hierarchical sub-object) or directly a value.
                        // If we are on a record, we need to recursively do the process
                        // if we are on a object, we save it to the output.
                        if (inputValue instanceof GenericData.Record) {
                            // The sub-schema at this level is a union of "empty" and a record,
                            // so we need to get the true sub-schema
                            Schema inputChildSchema = AvroUtils.unwrapIfNullable(inputSchema.getField(field.name()).schema());
                            Schema outputChildSchema = AvroUtils.unwrapIfNullable(outputSchema.getField(field.name()).schema());
                            if (inputChildSchema.getType().equals(Schema.Type.RECORD)
                                    && outputChildSchema.getType().equals(Schema.Type.RECORD)) {
                                Object childRecord = dupplicateRecord((IndexedRecord) inputValue, inputChildSchema,
                                        outputChildSchema);
                                outputRecord.set(field.name(), childRecord);
                            }
                        } else {
                            outputRecord.set(field.name(), inputValue);
                        }
                    }
                } else {
                    // TODO return exception
                }
            } else {
                // element not found => set to the value and its hierarchy to null
                outputRecord.set(field.name(), SchemaGeneratorUtils.generateEmptyRecord(outputSchema, field.name()));
            }
        }
        return outputRecord.build();
    }

    private GenericRecord dupplicateRecord(IndexedRecord inputRecord, Schema inputSchema, Schema outputSchema) {
        GenericRecordBuilder outputRecord = new GenericRecordBuilder(outputSchema);
        for (Schema.Field field : outputSchema.getFields()) {
            if (inputSchema.getField(field.name()) != null) {
                // The column was existing on the input record, we forward it to the output record.
                Object inputValue = inputRecord.get(inputSchema.getField(field.name()).pos());

                // The current column can be a Record (an hierarchical sub-object) or directly a value.
                // If we are on a record, we need to recursively do the process
                // if we are on a object, we save it to the output.
                if (inputValue instanceof GenericData.Record) {
                    // The sub-schema at this level is a union of "empty" and a record,
                    // so we need to get the true sub-schema
                    Schema inputChildSchema = AvroUtils.unwrapIfNullable(inputSchema.getField(field.name()).schema());
                    Schema outputChildSchema = AvroUtils.unwrapIfNullable(outputSchema.getField(field.name()).schema());
                    if (inputChildSchema.getType().equals(Schema.Type.RECORD)
                            && outputChildSchema.getType().equals(Type.RECORD)) {
                        Object childRecord = dupplicateRecord((IndexedRecord) inputValue, inputChildSchema, outputChildSchema);
                        outputRecord.set(field.name(), childRecord);
                    }
                } else {
                    outputRecord.set(field.name(), inputValue);
                }
            } else {
                // element not found => set to the value and its hierarchy to null
                outputRecord.set(field.name(), SchemaGeneratorUtils.generateEmptyRecord(outputSchema, field.name()));
            }
        }
        return outputRecord.build();
    }

    public NormalizeDoFn withProperties(NormalizeProperties properties) {
        this.properties = properties;
        return this;
    }
}
