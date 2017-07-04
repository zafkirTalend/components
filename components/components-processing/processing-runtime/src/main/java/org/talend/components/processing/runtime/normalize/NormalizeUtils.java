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
     * Generate a new Record which is the filtered result of the input record. @TODO
     *
     * @return the new record
     */
    public static GenericRecord generateNormalizedRecord(IndexedRecord inputRecord, Schema inputSchema,
            String[] pathToElementToNormalize, int pathIterator, Object outputValue) {

        GenericRecordBuilder outputRecord = null;
        if ((pathIterator == pathToElementToNormalize.length - 1) && (Schema.Type.ARRAY.equals(inputSchema.getType()))) {
            // we are on the element to normalise, and this is a list => Get the schema of the sub-element.
            outputRecord = new GenericRecordBuilder(inputSchema.getElementType());
        } else {
            outputRecord = new GenericRecordBuilder(inputSchema);
        }

        for (Schema.Field field : inputSchema.getFields()) {
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
                            if (inputChildSchema.getType().equals(Schema.Type.RECORD)) {
                                pathIterator++;
                                Object childRecord = generateNormalizedRecord((IndexedRecord) inputValue, inputChildSchema,
                                        pathToElementToNormalize, pathIterator, outputValue);
                                outputRecord.set(field.name(), childRecord);
                            }
                        } else if (inputValue instanceof List) {
                            System.out.println(inputValue);
                            if (pathIterator == pathToElementToNormalize.length - 1) {

                                Schema inputChildSchema = ((GenericRecord) outputValue).getSchema();
                                if (inputChildSchema.getType().equals(Schema.Type.RECORD)) {
                                    Object childRecord = duplicateRecord((IndexedRecord) outputValue, inputChildSchema);
                                    outputRecord.set(field.name(), childRecord);
                                    GenericRecord tmp = (GenericRecord) outputRecord.get(field.name());
                                    System.out.println(tmp);
                                }

                            } else {
                                // @TODO throw an exception
                            }
                            System.out.println(outputValue);
                        } else {
                            if (pathIterator == pathToElementToNormalize.length - 1) {
                                outputRecord.set(field.name(), outputValue);
                            } else {
                                // @TODO throw an exception
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
                            if (inputChildSchema.getType().equals(Schema.Type.RECORD)) {
                                Object childRecord = duplicateRecord((IndexedRecord) inputValue, inputChildSchema);
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
                        if (inputChildSchema.getType().equals(Schema.Type.RECORD)) {
                            Object childRecord = duplicateRecord((IndexedRecord) inputValue, inputChildSchema);
                            outputRecord.set(field.name(), childRecord);
                        }
                    } else {
                        outputRecord.set(field.name(), inputValue);
                    }
                }
            } else {
                throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_ARGUMENT,
                        new Throwable(String.format("The field %s is not present on the input schema", field.name())));
            }
        }
        return outputRecord.build();
    }

    /**
     * Generate a new Index Record which is the duplicated of the input record.
     *
     * @return the new record
     */
    public static GenericRecord duplicateRecord(IndexedRecord inputRecord, Schema inputSchema) {
        GenericRecordBuilder outputRecord = new GenericRecordBuilder(inputSchema);
        for (Schema.Field field : inputSchema.getFields()) {
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
                    if (inputChildSchema.getType().equals(Schema.Type.RECORD)) {
                        Object childRecord = duplicateRecord((IndexedRecord) inputValue, inputChildSchema);
                        outputRecord.set(field.name(), childRecord);
                    }
                } else {
                    outputRecord.set(field.name(), inputValue);
                }
            } else {
                throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_ARGUMENT,
                        new Throwable(String.format("The field %s is not present on the input schema", field.name())));
            }
        }
        return outputRecord.build();
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
