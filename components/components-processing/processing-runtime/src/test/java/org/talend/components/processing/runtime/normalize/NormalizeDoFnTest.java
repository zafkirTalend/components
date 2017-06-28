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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.DoFnTester;
import org.junit.Test;
import org.talend.components.processing.normalize.NormalizeProperties;

public class NormalizeDoFnTest {

    private final Schema inputSimpleSchema = SchemaBuilder.record("inputRow") //
            .fields() //
            .name("a").type().optional().stringType() //
            .name("b").type().optional().stringType() //
            .name("c").type().optional().stringType() //
            .endRecord();

    private final Schema inputEmbeddedSchema = SchemaBuilder.record("inputEmbeddedRow") //
            .fields() //
            .name("x").type().optional().stringType() //
            .name("y").type().optional().stringType() //
            .endRecord();

    private final Schema inputHierarchicalSchema = SchemaBuilder.record("inputHierarchicalRow") //
            .fields() //
            .name("a").type().optional().stringType() //
            .name("b").type(inputEmbeddedSchema).noDefault() //
            .name("c").type().optional().stringType() //
            .endRecord();

    private final GenericRecord inputEmbeddedRecord = new GenericRecordBuilder(inputEmbeddedSchema) //
            .set("x", "xxx1;xxx2") //
            .set("y", "yyy1") //
            .build();

    private final GenericRecord inputEmbeddedRecord2 = new GenericRecordBuilder(inputEmbeddedSchema) //
            .set("x", "xxx2") //
            .set("y", "yyy2") //
            .build();

    List<GenericRecord> list = Arrays.asList(inputEmbeddedRecord, inputEmbeddedRecord2);

    private final GenericRecord inputHierarchicalRecord = new GenericRecordBuilder(inputHierarchicalSchema) //
            .set("a", "aaa") //
            .set("b", inputEmbeddedRecord) //
            .set("c", "ccc") //
            .build();

    private final GenericRecord inputHierarchicalRecordWithList = new GenericRecordBuilder(inputHierarchicalSchema) //
            .set("a", "aaa") //
            .set("b", list) //
            .set("c", "ccc") //
            .build();

    private final GenericRecord inputSimpleRecord = new GenericRecordBuilder(inputSimpleSchema) //
            .set("a", "aaa") //
            .set("b", "  bbb1;  bbb2  ;") //
            .set("c", "ccc") //
            .build();

    private final GenericRecord inputSimpleRecord2 = new GenericRecordBuilder(inputSimpleSchema) //
            .set("a", "aaa") //
            .set("b", "  bbb1;  bbb2  ; ") //
            .set("c", "ccc") //
            .build();

    @Test
    public void test() throws Exception {
        NormalizeProperties properties = new NormalizeProperties("test");
        properties.init();
        properties.schemaListener.afterSchema();
        properties.columnToNormalize.setValue("b.x");
        properties.fieldSeparator.setValue(";");
        properties.trim.setValue(true);
        properties.discardTrailingEmptyStr.setValue(true);

        NormalizeDoFn function = new NormalizeDoFn().withProperties(properties);
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);

        List<IndexedRecord> outputs = fnTester.processBundle(inputHierarchicalRecord); // inputHierarchicalRecord
                                                                                       // inputSimpleRecord
                                                                                       // inputHierarchicalRecordWithList
        System.out.println(outputs);
        // assertEquals(2, outputs.size());
    }

    private void checkOutput_AllInputFieldsAreValidAndFilled(DoFnTester<IndexedRecord, IndexedRecord> fnTester) throws Exception {
        List<IndexedRecord> outputs = fnTester.processBundle(inputSimpleRecord);
        assertEquals(2, outputs.size());
        assertEquals("aaa", outputs.get(0).get(0));
        assertEquals("bbb", outputs.get(0).get(1));
        assertEquals("ccc", outputs.get(0).get(2));
        assertEquals("aaa", outputs.get(1).get(0));
        assertEquals("bbb", outputs.get(1).get(1));
        assertEquals("ccc", outputs.get(1).get(2));
    }

    /**
     * trim is set to false
     */
    private void checkOutput_AllInputFieldsAreValidAndFilled2(DoFnTester<IndexedRecord, IndexedRecord> fnTester)
            throws Exception {
        List<IndexedRecord> outputs = fnTester.processBundle(inputSimpleRecord);
        assertEquals(2, outputs.size());
        assertEquals("aaa", outputs.get(0).get(0));
        assertEquals("  bbb", outputs.get(0).get(1));
        assertEquals("ccc", outputs.get(0).get(2));
        assertEquals("aaa", outputs.get(1).get(0));
        assertEquals("  bbb", outputs.get(1).get(1));
        assertEquals("ccc", outputs.get(1).get(2));
    }

    /**
     * discardTrailingEmptyStr is set to false
     */
    private void checkOutput_AllInputFieldsAreValidAndFilled3(DoFnTester<IndexedRecord, IndexedRecord> fnTester)
            throws Exception {
        List<IndexedRecord> outputs = fnTester.processBundle(inputSimpleRecord2);
        assertEquals(3, outputs.size());
        assertEquals("aaa", outputs.get(0).get(0));
        assertEquals("bbb", outputs.get(0).get(1));
        assertEquals("ccc", outputs.get(0).get(2));
        assertEquals("aaa", outputs.get(1).get(0));
        assertEquals("bbb", outputs.get(1).get(1));
        assertEquals("ccc", outputs.get(1).get(2));
        assertEquals("aaa", outputs.get(2).get(0));
        assertEquals("", outputs.get(2).get(1));
        assertEquals("ccc", outputs.get(2).get(2));
    }

    @Test
    public void test_AllInputFieldsAreValidAndFilled() throws Exception {
        NormalizeProperties properties = new NormalizeProperties("test");
        properties.init();
        properties.schemaListener.afterSchema();
        properties.columnToNormalize.setValue("b");
        properties.fieldSeparator.setValue(";");
        properties.trim.setValue(true);
        properties.discardTrailingEmptyStr.setValue(true);

        NormalizeDoFn function = new NormalizeDoFn().withProperties(properties);
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);
        checkOutput_AllInputFieldsAreValidAndFilled(fnTester);
    }

    /**
     * trim is set to false
     */
    @Test
    public void test_AllInputFieldsAreValidAndFilled2() throws Exception {
        NormalizeProperties properties = new NormalizeProperties("test");
        properties.init();
        properties.schemaListener.afterSchema();
        properties.columnToNormalize.setValue("b");
        properties.fieldSeparator.setValue(";");
        properties.trim.setValue(false);
        properties.discardTrailingEmptyStr.setValue(true);

        NormalizeDoFn function = new NormalizeDoFn().withProperties(properties);
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);
        checkOutput_AllInputFieldsAreValidAndFilled2(fnTester);
    }

    /**
     * discardTrailingEmptyStr is set to false
     */
    @Test
    public void test_AllInputFieldsAreValidAndFilled3() throws Exception {
        NormalizeProperties properties = new NormalizeProperties("test");
        properties.init();
        properties.schemaListener.afterSchema();
        properties.columnToNormalize.setValue("b");
        properties.fieldSeparator.setValue(";");
        properties.trim.setValue(true);
        properties.discardTrailingEmptyStr.setValue(false);

        NormalizeDoFn function = new NormalizeDoFn().withProperties(properties);
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);
        checkOutput_AllInputFieldsAreValidAndFilled3(fnTester);
    }
}
