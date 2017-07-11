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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class AvroConverterTest {

    private final AvroConverter avroConverter = new AvroConverter(new ObjectMapper());

    private final String simpleJson = "{\"a\": {\"b\": \"b1\"}, \"d\": \"d1\"}";

    private final String arrayJson = "{\"a\": [{\"b\": \"b1\"}, {\"b\": \"b2\"}]}";

    private final String nullJson = "{\"a\": null}";

    private final String intJson = "{\"a\": 100}";

    private final String doubleJson = "{\"a\": 100.1}";

    /**
     * Get fields of the input record: {@link AvroConverterTest#simpleJson}
     *
     * Expected fields:
     * [{"name":"a","type":{"type":"record","name":"a_49","fields":[{"name":"b","type":["null","string"]}]}},{"name":"d","type":["null","string"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFields_simpleJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(simpleJson);

        ArrayNode arrayNode = avroConverter.getFields(jsonNode);
        assertEquals(2, arrayNode.size());

        // Check `a` field
        JsonNode nodeA = arrayNode.get(0);

        // "name":"a"
        assertEquals("a", nodeA.get("name").textValue());

        // "type":{"type":"record","name":"a_49","fields":[{"name":"b","type":["null","string"]}]}
        JsonNode nodeAType = nodeA.get("type");

        // type":"record"
        JsonNode nodeATypeRecord = nodeAType.get("type");
        assertEquals("record", nodeATypeRecord.textValue());

        // "fields":[{"name":"b","type":["null","string"]}]
        ArrayNode nodeATypeFields = (ArrayNode) nodeAType.get("fields");

        // {"name":"b","type":["null","string"]}
        JsonNode nodeB = nodeATypeFields.get(0);

        // "name":"b"
        assertEquals("b", nodeB.get("name").textValue());

        // "type":["null","string"]
        ArrayNode nodeBType = (ArrayNode) nodeB.get("type");
        assertEquals("null", nodeBType.get(0).textValue());
        assertEquals("string", nodeBType.get(1).textValue());

        // Check `d` field
        JsonNode nodeD = arrayNode.get(1);

        // "name":"d"
        assertEquals("d", nodeD.get("name").textValue());

        // "type":["null","string"]
        ArrayNode nodeDType = (ArrayNode) nodeD.get("type");
        assertEquals("null", nodeDType.get(0).textValue());
        assertEquals("string", nodeDType.get(1).textValue());
    }

    /**
     * Get fields of the input record: {@link AvroConverterTest#arrayJson}
     *
     * Expected fields:
     * [{"name":"a","type":{"type":"array","items":{"type":"record","name":"a_35","fields":[{"name":"b","type":["null","string"]}]}}}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFields_arrayJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(arrayJson);

        ArrayNode arrayNode = avroConverter.getFields(jsonNode);
        assertEquals(1, arrayNode.size());

        // Check `a` field
        JsonNode nodeA = arrayNode.get(0);

        // "name":"a"
        assertEquals("a", nodeA.get("name").textValue());

        // "type":{"type":"array","items":{"type":"record","name":"a_35","fields":[{"name":"b","type":["null","string"]}]}}
        JsonNode nodeAType = nodeA.get("type");

        // type":"array"
        JsonNode nodeATypeArray = nodeAType.get("type");
        assertEquals("array", nodeATypeArray.textValue());

        // "items":{"type":"record","name":"a_35","fields":[{"name":"b","type":["null","string"]}]}
        JsonNode nodeATypeItems = nodeAType.get("items");

        // "fields":[{"name":"b","type":["null","string"]}
        ArrayNode nodeAFields = (ArrayNode) nodeATypeItems.get("fields");

        JsonNode nodeB = nodeAFields.get(0);

        // "name":"b"
        assertEquals("b", nodeB.get("name").textValue());

        // "type":["null","string"]
        ArrayNode nodeBType = (ArrayNode) nodeB.get("type");
        assertEquals("null", nodeBType.get(0).textValue());
        assertEquals("string", nodeBType.get(1).textValue());
    }

    /**
     * Get fields of the input record: {@link AvroConverterTest#nullJson}
     *
     * Expected fields:
     * [{"name":"a","type":["null","string"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFields_nullJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(nullJson);

        ArrayNode arrayNode = avroConverter.getFields(jsonNode);
        assertEquals(1, arrayNode.size());

        // Check `a` field
        JsonNode nodeA = arrayNode.get(0);

        // "name":"a"
        assertEquals("a", nodeA.get("name").textValue());

        // "type":["null","string"]
        ArrayNode nodeAType = (ArrayNode) nodeA.get("type");
        assertEquals("null", nodeAType.get(0).textValue());
        assertEquals("string", nodeAType.get(1).textValue());
    }

    /**
     * Get fields of the input record: {@link AvroConverterTest#intJson}
     *
     * Expected fields:
     * [{"name":"a","type":["null","int"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFields_intJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(intJson);

        ArrayNode arrayNode = avroConverter.getFields(jsonNode);
        assertEquals(1, arrayNode.size());

        // Check `a` field
        JsonNode nodeA = arrayNode.get(0);

        // "name":"a"
        assertEquals("a", nodeA.get("name").textValue());

        // "type":["null","int"]
        ArrayNode nodeAType = (ArrayNode) nodeA.get("type");
        assertEquals("null", nodeAType.get(0).textValue());
        assertEquals("int", nodeAType.get(1).textValue());
    }

    /**
     * Get fields of the input record: {@link AvroConverterTest#doubleJson}
     *
     * Expected fields:
     * [{"name":"a","type":["null","double"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFields_doubleJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(doubleJson);

        ArrayNode arrayNode = avroConverter.getFields(jsonNode);
        assertEquals(1, arrayNode.size());

        // Check `a` field
        JsonNode nodeA = arrayNode.get(0);

        // "name":"a"
        assertEquals("a", nodeA.get("name").textValue());

        // "type":["null","double"]
        ArrayNode nodeAType = (ArrayNode) nodeA.get("type");
        assertEquals("null", nodeAType.get(0).textValue());
        assertEquals("double", nodeAType.get(1).textValue());
    }

    /**
     * Convert the input record: {@link AvroConverterTest#simpleJson} to avro schema
     *
     * Expected avro schema:
     * {"type":"record","name":"outer_record","namespace":"org.talend",
     * "fields":[{"name":"a","type":{"type":"record","name":"a_98","fields":[{"name":"b","type":["null","string"]}]}},
     * {"name":"d","type":["null","string"]}]}
     * 
     * @throws IOException
     */
    @Test
    public void testConvertToAvroSchema() throws IOException {

        Schema schema = avroConverter.convertToAvroSchema(simpleJson);
        List<Field> fields = schema.getFields();
        assertEquals(2, fields.size());

        // Get `a` field
        Field fieldA = fields.get(0);
        Schema schemaA = fieldA.schema();
        List<Field> fieldsA = schemaA.getFields();

        // Check the schema of `b` field
        Field fieldB = fieldsA.get(0);
        Schema schemaB = fieldB.schema();
        List<Schema> typesB = schemaB.getTypes();
        assertEquals("null", typesB.get(0).getName());
        assertEquals("string", typesB.get(1).getName());

        // Check the schema of `d` field
        Field fieldD = fields.get(1);
        Schema schemaD = fieldD.schema();
        List<Schema> typesD = schemaD.getTypes();
        assertEquals("null", typesD.get(0).getName());
        assertEquals("string", typesD.get(1).getName());
    }

    @Test
    public void testGenerateRandomNumber() {
        Map.Entry<String, JsonNode> entry = new AbstractMap.SimpleEntry<String, JsonNode>("a", null);
        String randomNumber = avroConverter.generateRandomNumber(entry);
        assertNotNull(randomNumber);
    }
}
