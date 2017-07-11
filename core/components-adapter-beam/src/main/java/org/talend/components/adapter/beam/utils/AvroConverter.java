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
package org.talend.components.adapter.beam.utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AvroConverter implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(AvroConverter.class);

    private static final String NAME = "name";

    private static final String TYPE = "type";

    private static final String ARRAY = "array";

    private static final String ITEMS = "items";

    private static final String STRING = "string";

    private static final String RECORD = "record";

    private static final String FIELDS = "fields";

    private static final String NULL = "null";

    private final ObjectMapper mapper;

    /**
     * Constructor
     *
     * @param mapper
     */
    public AvroConverter(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Convert json parameter to avro schema.
     *
     * @param json to convert
     * @return avro schema
     * @throws IOException
     */
    public Schema convertToAvroSchema(final String json) throws IOException {
        final JsonNode jsonNode = mapper.readTree(json);
        final ObjectNode finalSchema = mapper.createObjectNode();
        finalSchema.put("namespace", "org.talend");
        finalSchema.put(NAME, "outer_record");
        finalSchema.put(TYPE, RECORD);
        finalSchema.set(FIELDS, getFields(jsonNode));
        Schema outputSchema = new Schema.Parser().parse(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(finalSchema));
        return outputSchema;
    }

    /**
     * Get fields of json node.
     *
     * @param jsonNode
     * @return fields of json node
     */
    public ArrayNode getFields(final JsonNode jsonNode) {
        final ArrayNode fields = mapper.createArrayNode();
        final Iterator<Map.Entry<String, JsonNode>> elements = jsonNode.fields();

        Map.Entry<String, JsonNode> map;
        while (elements.hasNext()) {
            map = elements.next();
            final JsonNode nextNode = map.getValue();
            ObjectNode fieldNode = mapper.createObjectNode();
            ArrayNode fieldTypeArray = mapper.createArrayNode();

            if (!(nextNode instanceof NullNode)) {
                switch (nextNode.getNodeType()) {
                case NUMBER:
                    fieldNode.put(NAME, map.getKey());
                    fieldTypeArray.add(NULL);
                    if (nextNode.isInt()) {
                        fieldTypeArray.add("int");
                    } else if (nextNode.isLong()) {
                        fieldTypeArray.add("long");
                    } else {
                        fieldTypeArray.add("double");
                    }
                    fieldNode.put(TYPE, fieldTypeArray);
                    fields.add(fieldNode);
                    break;

                case STRING:
                    fieldNode.put(NAME, map.getKey());
                    fieldTypeArray.add(NULL);
                    fieldTypeArray.add(STRING);
                    fieldNode.put(TYPE, fieldTypeArray);
                    fields.add(fieldNode);
                    break;

                case ARRAY:
                    final ArrayNode arrayNode = (ArrayNode) nextNode;
                    final JsonNode element = arrayNode.get(0);
                    final ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put(NAME, map.getKey());

                    if (element.getNodeType() == JsonNodeType.NUMBER) {
                        fieldNode.put(TYPE, ARRAY);
                        fieldTypeArray.add(NULL);
                        fieldTypeArray.add((nextNode.get(0).isLong() ? "long" : "double"));
                        fieldNode.put(ITEMS, fieldTypeArray);
                        objectNode.set(TYPE, fieldNode);
                    } else if (element.getNodeType() == JsonNodeType.STRING) {
                        fieldNode.put(TYPE, ARRAY);
                        fieldTypeArray.add(NULL);
                        fieldTypeArray.add(STRING);
                        fieldNode.put(ITEMS, fieldTypeArray);
                        objectNode.set(TYPE, fieldNode);
                    } else {
                        objectNode.set(TYPE, mapper.createObjectNode().put(TYPE, ARRAY).set(ITEMS, mapper.createObjectNode()
                                .put(TYPE, RECORD).put(NAME, generateRandomNumber(map)).set(FIELDS, getFields(element))));
                    }
                    fields.add(objectNode);
                    break;

                case OBJECT:
                    ObjectNode node = mapper.createObjectNode();
                    node.put(NAME, map.getKey());
                    node.set(TYPE, mapper.createObjectNode().put(TYPE, RECORD).put(NAME, generateRandomNumber(map)).set(FIELDS,
                            getFields(nextNode)));
                    fields.add(node);
                    break;

                default:
                    logger.error("Node type not found - " + nextNode.getNodeType());
                    throw new RuntimeException("Unable to determine action for node type " + nextNode.getNodeType()
                            + "; Allowed types are ARRAY, STRING, NUMBER, OBJECT");
                }
            } else {
                fieldNode.put(NAME, map.getKey());
                fieldTypeArray.add(NULL);
                fieldTypeArray.add(STRING);
                fieldNode.put(TYPE, fieldTypeArray);
                fields.add(fieldNode);
            }
        }
        return fields;
    }

    /**
     * Generate random.
     *
     * @param map to create random number
     */
    public String generateRandomNumber(Map.Entry<String, JsonNode> map) {
        return (map.getKey() + "_" + new Random().nextInt(100));
    }
}
