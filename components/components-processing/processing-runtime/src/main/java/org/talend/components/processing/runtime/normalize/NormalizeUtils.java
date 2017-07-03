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
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;

public class NormalizeUtils {

    public static Schema getUnwrappedSchema(Schema.Field field) {
        return AvroUtils.unwrapIfNullable(field.schema());
    }

    public static Schema getUnwrappedSchema(IndexedRecord record) {
        return AvroUtils.unwrapIfNullable(record.getSchema());
    }

    /**
     * Get the fields from inputRecord of the field columnName.
     *
     * @return list contains the fields from inputRecord of the field columnName
     */
    public static List<Object> getInputFields(IndexedRecord inputRecord, String columnName) {
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
                if (i == path.length - 1) {
                    inputFields.add(inputValue);
                }
            }
        }

        return inputFields;
    }

    /**
     * Generate a new Index Record which is the filtered result of the input record.
     *
     * @return the new record
     */
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
                outputRecord.set(field.name(), generateEmptyRecord(outputSchema, field.name()));
            }
        }
        return outputRecord.build();
    }

    /**
     * Generate a new Index Record which is the dupplicated of the input record.
     *
     * @return the new record
     */
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
                outputRecord.set(field.name(), generateEmptyRecord(outputSchema, field.name()));
            }
        }
        return outputRecord.build();
    }

    /**
     * Use a Schema to generate a hierarchical GenericRecord that contains only null values.
     *
     * @param schema the parent schema of the field to set as null
     * @param fieldName the name of the field to set as null
     * @return if fieldName is a Record of the schema, the method will return a GenericRecord with any leaf set as null,
     * otherwise return null
     */
    public static IndexedRecord generateEmptyRecord(Schema schema, String fieldName) {
        if (schema.getType().equals(Schema.Type.RECORD)) {
            Schema unwrappedSchema = getUnwrappedSchema(schema.getField(fieldName));
            if (unwrappedSchema.getType().equals(Schema.Type.RECORD)) {
                GenericRecordBuilder outputRecord = new GenericRecordBuilder(unwrappedSchema);
                for (Schema.Field field : unwrappedSchema.getFields()) {
                    IndexedRecord value = generateEmptyRecord(unwrappedSchema, field.name());
                    outputRecord.set(field.name(), value);
                }
                return outputRecord.build();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Check if the list contains a simple field.
     * 
     * @return true if list parameter contains a simple field
     */
    public static boolean isSimpleField(List<Object> list) {
        if (list != null && list.size() == 1 && !(list.get(0) instanceof GenericRecord)) {
            return true;
        }
        return false;
    }

    /**
     * Splits toSplit parameter around matches of the given delim parameter.
     *
     * @param toSplit string to split
     * @param delim the delimiting regular expression
     * @param isDiscardTrailingEmptyStr
     * @param isTrim
     * @return substrings
     */
    public static List<Object> delimit(String toSplit, String delim, boolean isDiscardTrailingEmptyStr, boolean isTrim) {

        String[] strSplitted = toSplit.split(delim);
        List<Object> strList = new ArrayList<Object>();
        for (int i = 0; i < strSplitted.length; i++) {
            if (isDiscardTrailingEmptyStr) {
                strSplitted[i] = strSplitted[i].replaceAll("\\s+$", "");
            }
            if (isTrim) {
                strSplitted[i] = strSplitted[i].trim();
            }
            strList.add(strSplitted[i]);
        }
        return strList;
    }
}
