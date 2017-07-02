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

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;

public class NormalizeUtils {

    private static final Logger LOG = LoggerFactory.getLogger(NormalizeUtils.class);

    public static List<Object> getInputFields(IndexedRecord inputRecord, String columnName) {
        // TODO current implementation will only extract one element, but
        // further implementation may
        ArrayList<Object> inputFields = new ArrayList<Object>();
        String[] path = columnName.split("\\.");
        Schema schema = inputRecord.getSchema();

        for (Integer i = 0; i < path.length; i++) {
            // The column was existing on the input record, we forward it to the
            // output record.
            if (schema.getField(path[i]) == null) {
                throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_ARGUMENT,
                        new Throwable(String.format("The field %s is not present on the input record", columnName)));
            }
            Object inputValue = inputRecord.get(schema.getField(path[i]).pos());

            // The current column can be a Record (an hierarchical sub-object)
            // or directly a value.
            if (inputValue instanceof GenericData.Record) {
                // If we are on a record, we need to recursively do the process
                inputRecord = (IndexedRecord) inputValue;

                // The sub-schema at this level is a union of "empty" and a
                // record, so we need to get the true
                // sub-schema
                if (schema.getField(path[i]).schema().getType().equals(Schema.Type.RECORD)) {
                    schema = schema.getField(path[i]).schema();
                    if (i == path.length - 1) {
                        inputFields.add(inputValue);
                    }
                } else if (schema.getField(path[i]).schema().getType().equals(Schema.Type.UNION)) {
                    if (i == path.length - 1) {
                        inputFields.add(inputValue);
                        break;
                    }
                    for (Schema childSchema : schema.getField(path[i]).schema().getTypes()) {
                        if (childSchema.getType().equals(Schema.Type.RECORD)) {
                            schema = childSchema;
                            break;
                        }
                    }
                }
            } else if (inputValue instanceof List) {
                for (int j = 0; j < ((List) inputValue).size(); j++) {
                    inputFields.add(((List) inputValue).get(j));
                }
                break;
            } else {
                // if we are on a object, then this is or the expected value of
                // an error.
                if (i == path.length - 1) {
                    inputFields.add(inputValue);
                }
                /*
                 * else {
                 * // No need to go further, return an empty list
                 * break;
                 * }
                 */
            }
        }

        return inputFields;
    }

    public static GenericRecord generateNormalizedRecord(IndexedRecord inputRecord, Schema inputSchema, Schema outputSchema,
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
                // element not found => set to the value and its hierarchy to null
                outputRecord.set(field.name(), SchemaGeneratorUtils.generateEmptyRecord(outputSchema, field.name()));
            }
        }
        return outputRecord.build();
    }

    public static GenericRecord dupplicateRecord(IndexedRecord inputRecord, Schema inputSchema, Schema outputSchema) {
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
                            && outputChildSchema.getType().equals(Schema.Type.RECORD)) {
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

    /*
     * public static Object[] delimit(Object obj, String delim, boolean isDiscardTrailingEmptyStr, boolean isTrim, NodeType
     * nodeType)
     * throws Exception {
     * 
     * if (nodeType.getIsArray()) {
     * Iterator<JsonNode> iteratorNodes = ((JsonNode) obj).getElements();
     * return Iterators.toArray(iteratorNodes, Object.class);
     * } else if (nodeType.getIsTextual()) {
     * String[] strDelimited = {};
     * try {
     * JsonNode txtNode = (JsonNode) obj;
     * String str = txtNode.getTextValue();
     * strDelimited = str.split(delim);
     * for (int i = 0; i < strDelimited.length; i++) {
     * if (isDiscardTrailingEmptyStr) {
     * strDelimited[i] = strDelimited[i].replaceAll("\\s+$", "");
     * }
     * if (isTrim) {
     * strDelimited[i] = strDelimited[i].trim();
     * }
     * }
     * } catch (Exception e) {
     * LOG.debug("Cannot cast Object to String {}", e.getMessage());
     * }
     * return strDelimited;
     * } else if (nodeType.getIsContainer()) {
     * return new Object[] { obj };
     * }
     * return null;
     * }
     */

    public static boolean isSimpleField(List<Object> list) {
        if (list != null && list.size() == 1 && !(list.get(0) instanceof GenericRecord)) {
            return true;
        }
        return false;
    }

    /**
     * Splits toDelimit around matches of the given delim parameter.
     *
     * @param toDelimit
     * @param delim the delimiting regular expression
     * @param isDiscardTrailingEmptyStr
     * @param isTrim
     * @return
     * @throws Exception if cannot cast obj to String.
     */
    public static List<Object> delimit(String toDelimit, String delim, boolean isDiscardTrailingEmptyStr, boolean isTrim) {

        String[] strDelimited = toDelimit.split(delim);
        List<Object> strList = new ArrayList<Object>();
        for (int i = 0; i < strDelimited.length; i++) {
            if (isDiscardTrailingEmptyStr) {
                strDelimited[i] = strDelimited[i].replaceAll("\\s+$", "");
            }
            if (isTrim) {
                strDelimited[i] = strDelimited[i].trim();
            }
            strList.add(strDelimited[i]);
        }
        return strList;
    }
}
