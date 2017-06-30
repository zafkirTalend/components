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
package org.talend.components.processing.runtime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;

public class Utils {

    List<IndexedRecord> indexedRecords = new ArrayList<IndexedRecord>();

    public static JsonNode searchNode(JsonNode jsonNode, String[] path) {

        for (int i = 0; i < path.length - 1; i++) {
            jsonNode = jsonNode.with(path[i]);
        }
        try {
            jsonNode = jsonNode.with(path[path.length - 1]);
        } catch (Exception e) {
            jsonNode = jsonNode.findPath(path[path.length - 1]);
        }
        return jsonNode;
    }

    /*
     * public static List<Node> getChildren(IndexedRecord inputRecord) {
     * List<Node> children = new ArrayList<Node>();
     * Schema inputSchema = inputRecord.getSchema();
     * List<Schema.Field> fields = inputRecord.getSchema().getFields();
     * for (Schema.Field field : fields) {
     * Object inputValue = inputRecord.get(inputSchema.getField(field.name()).pos());
     * Node node = new Node();
     * node.value = inputValue;
     * children.add(node);
     * }
     * return children;
     * }
     */

    public static String[] delimit(String str, String delim, boolean isDiscardTrailingEmptyStr, boolean isTrim) {
        String[] strDelimited = {};
        try {
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
            // LOG the exception
        }
        return strDelimited;
    }

    public static Object changeChildElement(boolean first, JsonNode parentNode, JsonNode childNode, String[] path, int j) {

        JsonNode firstParentNode = null;
        if (first == true) {
            firstParentNode = parentNode;
        }

        if (parentNode.get(path[j]) != null) {
            Iterator<Map.Entry<String, JsonNode>> nodes = parentNode.get(path[j]).getFields();

            if (!nodes.hasNext()) {
                System.out.println(parentNode.get(path[j]).getTextValue());
                ((ObjectNode) parentNode).put(path[j], childNode); // .getTextValue()
                return parentNode;
            } else {

                while (nodes.hasNext()) {
                    Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodes.next();

                    System.out.println("key --> " + entry.getKey() + " value-->" + entry.getValue());

                    Object returnValue = changeChildElement(false, entry.getValue(), childNode, path, j + 1);

                    if (returnValue != null)
                        return returnValue;

                    j++;
                }
            }
        }

        return null;
    }

    public static Object getFieldNormalizedFromNode(JsonNode jsonNode, String[] path, String delimitedValue, int j) {

        if (jsonNode.get(path[j]) != null) {
            Iterator<Map.Entry<String, JsonNode>> nodes = jsonNode.get(path[j]).getFields();

            if (!nodes.hasNext()) {
                System.out.println(jsonNode.get(path[j]).getTextValue());
                ((ObjectNode) jsonNode).put(path[j], delimitedValue);
                return jsonNode;
            } else {

                while (nodes.hasNext()) {
                    Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodes.next();

                    System.out.println("key --> " + entry.getKey() + " value-->" + entry.getValue());

                    Object returnValue = getFieldNormalizedFromNode(entry.getValue(), path, delimitedValue, j + 1);

                    if (returnValue != null)
                        return returnValue;

                    j++;
                }
            }
        }

        return null;
    }

    public static String[] tmp(JsonNode jsonNode, String[] path, int j) {

        if (jsonNode.get(path[j]) != null) {
            Iterator<Map.Entry<String, JsonNode>> nodes = jsonNode.get(path[j]).getFields();

            if (!nodes.hasNext()) {
                System.out.println(jsonNode.get(path[j]).getTextValue());
                return delimit(jsonNode.get(path[j]).getTextValue(), ";", true, true);
            } else {
                while (nodes.hasNext()) {
                    Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodes.next();

                    System.out.println("key --> " + entry.getKey() + " value-->" + entry.getValue());

                    String[] str = tmp(entry.getValue(), path, j + 1);
                    if (str != null)
                        return str;

                    j++;
                }
            }
        }
        return null;
    }

    /*
     * public static Object getFieldNormalized(IndexedRecord inputRecord, String[] path, int indexColumnToNormalizeParent, int l,
     * String delim) {
     * 
     * Schema schema = inputRecord.getSchema();
     * Schema.Field fieldToNormalize = schema.getField(path[0]);
     * Schema schemaField = fieldToNormalize.schema();
     * IndexedRecord recordToReturn = new GenericData.Record(schemaField);
     * 
     * Object inputRecordParentField = inputRecord.get(indexColumnToNormalizeParent);
     * 
     * for (Integer i = 0; i < path.length; i++) {
     * 
     * Object inputValue = inputRecord.get(schema.getField(path[i]).pos());
     * // System.out.println(inputValue);
     * 
     * if (inputValue instanceof GenericData.Record) {
     * // If we are on a record, we need to recursively do the process
     * inputRecord = (IndexedRecord) inputValue;
     * 
     * // The sub-schema at this level is a union of "empty" and a
     * // record, so we need to get the true
     * // sub-schema
     * if (schema.getField(path[i]).schema().getType().equals(Schema.Type.RECORD)) {
     * schema = schema.getField(path[i]).schema();
     * } else if (schema.getField(path[i]).schema().getType().equals(Schema.Type.UNION)) {
     * for (Schema childSchema : schema.getField(path[i]).schema().getTypes()) {
     * if (childSchema.getType().equals(Schema.Type.RECORD)) {
     * schema = childSchema;
     * break;
     * }
     * }
     * }
     * } else if (inputValue instanceof List) {
     * 
     * break;
     * } else {
     * // if we are on a object, then this is or the expected value of
     * // an error.
     * if (i == path.length - 1) {
     * // inputFields.add(inputValue);
     * 
     * String strs = (String) inputValue;
     * String[] strDelimited = strs.split(delim);
     * 
     * // for (String str : strDelimited) {
     * 
     * Object updatedElement = updateElement(inputRecordParentField, path, path[i], strDelimited[l]);
     * return updatedElement;
     * // }
     * 
     * }
     * /*
     * else {
     * // No need to go further, return an empty list
     * break;
     * }
     */
    /*
     * }}
     * 
     * return null;}
     */

    public static int combienNormalize(IndexedRecord inputRecord, String columnName, String delimit) {
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
                        return 1;
                    }
                } else if (schema.getField(path[i]).schema().getType().equals(Schema.Type.UNION)) {
                    for (Schema childSchema : schema.getField(path[i]).schema().getTypes()) {
                        if (childSchema.getType().equals(Schema.Type.RECORD)) {
                            schema = childSchema;
                            break;
                        }
                    }
                }
            } else if (inputValue instanceof List) {
                return 1;
            } else {
                // if we are on a object, then this is or the expected value of
                // an error.
                if (i == path.length - 1) {
                    String str = (String) inputValue;
                    return str.split(delimit).length;
                }
                /*
                 * else {
                 * // No need to go further, return an empty list
                 * break;
                 * }
                 */
            }
        }

        return 0;
    }

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
}
