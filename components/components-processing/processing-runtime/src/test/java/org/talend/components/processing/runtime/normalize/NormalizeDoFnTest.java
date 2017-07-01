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

    private final Schema inputSchemaDE = SchemaBuilder.record("inputRow") //
            .fields() //
            .name("d").type().optional().stringType() //
            .name("e").type().optional().stringType() //
            .endRecord();

    private final Schema inputSchemaHI = SchemaBuilder.record("inputRow") //
            .fields() //
            .name("h").type().optional().stringType() //
            .name("i").type().optional().stringType() //
            .endRecord();

    private final Schema inputSchemaFG = SchemaBuilder.record("inputRow") //
            .fields() //
            .name("f").type().optional().stringType() //
            .name("g").type(inputSchemaHI).noDefault() //
            .endRecord();

    private final Schema inputSchemaXY = SchemaBuilder.record("inputEmbeddedRow") //
            .fields() //
            .name("x").type().optional().stringType() //
            .name("y").type(inputSchemaDE).noDefault() //
            .endRecord();

    private final Schema inputHierarchicalSchema = SchemaBuilder.record("inputHierarchicalRow") //
            .fields() //
            .name("a").type().optional().stringType() //
            .name("b").type(inputSchemaXY).noDefault() //
            .name("c").type(inputSchemaFG).noDefault() //
            .endRecord();

    private final GenericRecord inputRecordDE = new GenericRecordBuilder(inputSchemaDE) //
            .set("d", "d") //
            .set("e", "e") //
            .build();

    private final GenericRecord inputRecordHI = new GenericRecordBuilder(inputSchemaHI) //
            .set("h", "h1;h2") //
            .set("i", "i") //
            .build();

    private final GenericRecord inputRecordHI2 = new GenericRecordBuilder(inputSchemaHI) //
            .set("h", "h") //
            .set("i", "i") //
            .build();

    List<GenericRecord> listInputRecordG = Arrays.asList(inputRecordHI2, inputRecordHI2);

    private final GenericRecord inputRecordFG = new GenericRecordBuilder(inputSchemaFG) //
            .set("f", "f") //
            .set("g", listInputRecordG) // inputRecordHI
            .build();

    // List<GenericRecord> listDE = Arrays.asList(inputSimpleRecordDE, inputSimpleRecordDE2);

    private final GenericRecord inputRecordXY = new GenericRecordBuilder(inputSchemaXY) //
            .set("x", "xxx1;xxx2") //
            .set("y", inputRecordDE) // listDE
            .build();

    private final GenericRecord inputHierarchicalRecord = new GenericRecordBuilder(inputHierarchicalSchema) //
            .set("a", "aaa") //
            .set("b", inputRecordXY) //
            .set("c", inputRecordFG) //
            .build();

    @Test
    public void test() throws Exception {
        NormalizeProperties properties = new NormalizeProperties("test");
        properties.init();
        properties.schemaListener.afterSchema();
        properties.columnToNormalize.setValue("c.g"); // c.g b.y
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
