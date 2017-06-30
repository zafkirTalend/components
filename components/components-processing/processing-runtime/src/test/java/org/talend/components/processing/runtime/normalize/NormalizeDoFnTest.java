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

    private final Schema inputSimpleSchemaDE = SchemaBuilder.record("inputRow") //
            .fields() //
            .name("d").type().optional().stringType() //
            .name("e").type().optional().stringType() //
            .endRecord();

    private final Schema inputSimpleSchemaHI = SchemaBuilder.record("inputRow") //
            .fields() //
            .name("h").type().optional().stringType() //
            .name("i").type().optional().stringType() //
            .endRecord();

    private final Schema inputSimpleSchemaFG = SchemaBuilder.record("inputRow") //
            .fields() //
            .name("f").type().optional().stringType() //
            .name("g").type(inputSimpleSchemaHI).noDefault() //
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
            .name("c").type(inputSimpleSchemaFG).noDefault() //
            .endRecord();

    private final GenericRecord inputSimpleRecordDE = new GenericRecordBuilder(inputSimpleSchemaDE) //
            .set("d", "d") //
            .set("e", "e") //
            .build();

    private final GenericRecord inputSimpleRecordDE2 = new GenericRecordBuilder(inputSimpleSchemaDE) //
            .set("d", "d2") //
            .set("e", "e2") //
            .build();

    private final GenericRecord inputSimpleRecordHI = new GenericRecordBuilder(inputSimpleSchemaHI) //
            .set("h", "h1;h2") //
            .set("i", "i") //
            .build();

    private final GenericRecord inputSimpleRecordFG = new GenericRecordBuilder(inputSimpleSchemaFG) //
            .set("f", "f") //
            .set("g", inputSimpleRecordHI) //
            .build();

    List<GenericRecord> listDE = Arrays.asList(inputSimpleRecordDE, inputSimpleRecordDE2);

    private final GenericRecord inputEmbeddedRecord = new GenericRecordBuilder(inputEmbeddedSchema) //
            .set("x", "xxx1;xxx2") //
            .set("y", inputSimpleRecordDE) // listDE
            .build();

    private final GenericRecord inputEmbeddedRecord2 = new GenericRecordBuilder(inputEmbeddedSchema) //
            .set("x", "xxx2") //
            .set("y", "yyy2") //
            .build();

    List<GenericRecord> list = Arrays.asList(inputEmbeddedRecord, inputEmbeddedRecord2);


    private final GenericRecord inputHierarchicalRecord = new GenericRecordBuilder(inputHierarchicalSchema) //
            .set("a", "aaa") //
            .set("b", inputEmbeddedRecord) //
            .set("c", inputSimpleRecordFG) //
            .build();

    private final GenericRecord inputHierarchicalRecordWithList = new GenericRecordBuilder(inputHierarchicalSchema) //
            .set("a", "aaa") //
            .set("b", list) //
            .set("c", "ccc") //
            .build();

    @Test
    public void test() throws Exception {
        NormalizeProperties properties = new NormalizeProperties("test");
        properties.init();
        properties.schemaListener.afterSchema();
        properties.columnToNormalize.setValue("c.g.h"); // c.g b.y
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
}
