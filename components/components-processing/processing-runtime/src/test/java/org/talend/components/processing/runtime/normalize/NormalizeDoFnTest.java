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
import org.junit.Assert;
import org.junit.Test;
import org.talend.components.processing.normalize.NormalizeProperties;
import org.talend.daikon.exception.TalendRuntimeException;

public class NormalizeDoFnTest {

    private final Schema inputSchemaL = SchemaBuilder.record("inputRow") //
            .fields() //
            .name("l").type().optional().stringType() //
            .endRecord();

    private final Schema inputSchemaListOfL = SchemaBuilder.array().items(inputSchemaL);

    private final Schema inputSchemaJK = SchemaBuilder.record("inputRow") //
            .fields() //
            .name("j").type(inputSchemaListOfL).noDefault() //
            .name("k").type().optional().stringType() //
            .endRecord();

    private final Schema inputSchemaDE = SchemaBuilder.record("inputRow") //
            .fields() //
            .name("d").type(inputSchemaJK).noDefault() //
            .name("e").type().optional().stringType() //
            .endRecord();

    private final Schema inputSchemaHI = SchemaBuilder.record("inputRow") //
            .fields() //
            .name("h").type().optional().stringType() //
            .name("i").type().optional().stringType() //
            .endRecord();

    private final Schema inputSchemaListOfHI = SchemaBuilder.array().items(inputSchemaHI);

    private final Schema inputSchemaFGArray = SchemaBuilder.record("inputRow") //
            .fields() //
            .name("f").type().optional().stringType() //
            .name("g").type(inputSchemaListOfHI).noDefault() //
            .endRecord();

    private final Schema inputSchemaFGSimple = SchemaBuilder.record("inputRow") //
            .fields() //
            .name("f").type().optional().stringType() //
            .name("g").type(inputSchemaHI).noDefault() //
            .endRecord();

    private final Schema inputSchemaXY = SchemaBuilder.record("inputRow") //
            .fields() //
            .name("x").type().optional().stringType() //
            .name("y").type(inputSchemaDE).noDefault() //
            .endRecord();

    private final Schema inputParentSchema = SchemaBuilder.record("inputParentRow") //
            .fields() //
            .name("a").type().optional().stringType() //
            .name("b").type(inputSchemaXY).noDefault() //
            .name("c").type(inputSchemaFGArray).noDefault() //
            .endRecord();

    private final Schema inputParentSchemaSimpleFG = SchemaBuilder.record("inputParentRow") //
            .fields() //
            .name("a").type().optional().stringType() //
            .name("b").type(inputSchemaXY).noDefault() //
            .name("c").type(inputSchemaFGSimple).noDefault() //
            .endRecord();

    /**
     * {"l":"l1"}
     */
    private final GenericRecord inputRecordL1 = new GenericRecordBuilder(inputSchemaL) //
            .set("l", "l1") //
            .build();

    /**
     * {"l":"l2"}
     */
    private final GenericRecord inputRecordL2 = new GenericRecordBuilder(inputSchemaL) //
            .set("l", "l2") //
            .build();

    /**
     * [{"l":"l1"},{"l":"l2"}]
     */
    private final List<GenericRecord> listInputRecordL = Arrays.asList(inputRecordL1, inputRecordL2);

    /**
     * {"j": [{"l":"l1"},{"l":"l2"}], "k": "k1;k2"}
     */
    private final GenericRecord inputRecordJK = new GenericRecordBuilder(inputSchemaJK) //
            .set("j", listInputRecordL) //
            .set("k", "k1;k2") //
            .build();

    /**
     * {"d": {"j": [{"l":"l1"},{"l":"l2"}], "k": "k1;k2"}, "e": "e"}
     */
    private final GenericRecord inputRecordDE = new GenericRecordBuilder(inputSchemaDE) //
            .set("d", inputRecordJK) //
            .set("e", "e") //
            .build();

    /**
     * {"h": "h1", "i": "i2"}
     */
    private final GenericRecord inputRecordHI1 = new GenericRecordBuilder(inputSchemaHI) //
            .set("h", "h1") //
            .set("i", "i2") //
            .build();

    /**
     * {"h": "h2", "i": "i1"}
     */
    private final GenericRecord inputRecordHI2 = new GenericRecordBuilder(inputSchemaHI) //
            .set("h", "h2") //
            .set("i", "i1") //
            .build();

    /**
     * [{"h": "h1", "i": "i2"}, {"h": "h2", "i": "i1"}]
     */
    private final List<GenericRecord> listInputRecordG = Arrays.asList(inputRecordHI1, inputRecordHI2);

    /**
     * {"f": "f", "g": [{"h": "h1", "i": "i2"}, {"h": "h2", "i": "i1"}]}
     */
    private final GenericRecord inputRecordFG = new GenericRecordBuilder(inputSchemaFGArray) //
            .set("f", "f") //
            .set("g", listInputRecordG) // inputRecordHI
            .build();

    /**
     * {"x": "x1;x2", "y": {"d": {"j": [{"l":"l1"},{"l":"l2"}], "k": "k1;k2"}, "e": "e"}}
     */
    private final GenericRecord inputRecordXY = new GenericRecordBuilder(inputSchemaXY) //
            .set("x", "x1;x2") //
            .set("y", inputRecordDE) // listDE
            .build();

    /**
     * {
     * "a": "aaa",
     * "b": {"x": "x1;x2", "y": {"d": {"j": [{"l":"l1"},{"l":"l2"}], "k": "k1;k2"}, "e": "e"}},
     * "c": {"f": "f", "g": [{"h": "h1", "i": "i2"}, {"h": "h2", "i": "i1"}]}
     * }
     */
    private final GenericRecord inputParentRecord = new GenericRecordBuilder(inputParentSchema) //
            .set("a", "aaa") //
            .set("b", inputRecordXY) //
            .set("c", inputRecordFG) //
            .build();

    /**
     * Input parent record: {@link NormalizeDoFnTest#inputParentRecord}
     *
     * Normalize simple fields: `a` | `b.x` | `b.y.d.k`
     *
     * Expected normalized results of the field `a`:
     *
     * [{"a": "aaa",
     * "b": {"x": "x1;x2", "y": {"d": {"j": [{"l": "l1"}, {"l": "l2"}], "k": "k1;k2"}, "e": "e"}},
     * "c": {"f": "f", "g": [{"h": "h1", "i": "i2"}, {"h": "h2", "i": "i1"}]}}]
     *
     * Expected normalized results of the field `b.x`:
     *
     * [{"a": "aaa",
     * "b": {"x": "x1", "y": {"d": {"j": [{"l": "l1"}, {"l": "l2"}], "k": "k1;k2"}, "e": "e"}},
     * "c": {"f": "f", "g": [{"h": "h1", "i": "i2"}, {"h": "h2", "i": "i1"}]}},
     * {"a": "aaa",
     * "b": {"x": "x2", "y": {"d": {"j": [{"l": "l1"}, {"l": "l2"}], "k": "k1;k2"}, "e": "e"}},
     * "c": {"f": "f", "g": [{"h": "h1", "i": "i2"}, {"h": "h2", "i": "i1"}]}}]
     *
     * Expected normalized results of the field `b.y.d.k`:
     *
     * [{"a": "aaa",
     * "b": {"x": "x1;x2", "y": {"d": {"j": [{"l": "l1"}, {"l": "l2"}], "k": "k1"}, "e": "e"}},
     * "c": {"f": "f", "g": [{"h": "h1", "i": "i2"}, {"h": "h2", "i": "i1"}]}},
     * {"a": "aaa",
     * "b": {"x": "x1;x2", "y": {"d": {"j": [{"l": "l1"}, {"l": "l2"}], "k": "k2"}, "e": "e"}},
     * "c": {"f": "f", "g": [{"h": "h1", "i": "i2"}, {"h": "h2", "i": "i1"}]}}]
     * 
     * @throws Exception
     */
    @Test
    public void testNormalizeSimpleFields() throws Exception {
        NormalizeProperties properties = new NormalizeProperties("test");
        properties.init();
        properties.schemaListener.afterSchema();
        properties.fieldSeparator.setValue(";");
        properties.trim.setValue(true);
        properties.discardTrailingEmptyStr.setValue(true);

        // Normalize `a` simple field
        properties.columnToNormalize.setValue("a");

        NormalizeDoFn function = new NormalizeDoFn().withProperties(properties);
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);
        List<IndexedRecord> outputs = fnTester.processBundle(inputParentRecord);
        Assert.assertEquals(1, outputs.size());
        Assert.assertEquals(inputParentRecord, outputs.get(0));

        // Normalize `b.x` simple field
        properties.columnToNormalize.setValue("b.x");

        function = new NormalizeDoFn().withProperties(properties);
        fnTester = DoFnTester.of(function);
        outputs = fnTester.processBundle(inputParentRecord);
        Assert.assertEquals(2, outputs.size());

        GenericRecord inputRecordX1Y = new GenericRecordBuilder(inputSchemaXY) //
                .set("x", "x1") //
                .set("y", inputRecordDE) //
                .build();
        GenericRecord inputRecordX2Y = new GenericRecordBuilder(inputSchemaXY) //
                .set("x", "x2") //
                .set("y", inputRecordDE) //
                .build();
        GenericRecord inputParentRecordX1 = new GenericRecordBuilder(inputParentSchema) //
                .set("a", "aaa") //
                .set("b", inputRecordX1Y) //
                .set("c", inputRecordFG) //
                .build();
        GenericRecord inputParentRecordX2 = new GenericRecordBuilder(inputParentSchema) //
                .set("a", "aaa") //
                .set("b", inputRecordX2Y) //
                .set("c", inputRecordFG) //
                .build();

        GenericRecord record1 = (GenericRecord) outputs.get(0);
        GenericRecord record2 = (GenericRecord) outputs.get(1);
        Assert.assertEquals(inputParentRecordX1, record1);
        Assert.assertEquals(inputParentRecordX2, record2);

        // Normalize `b.y.d.k` simple field
        properties.columnToNormalize.setValue("b.y.d.k");

        function = new NormalizeDoFn().withProperties(properties);
        fnTester = DoFnTester.of(function);
        outputs = fnTester.processBundle(inputParentRecord);
        Assert.assertEquals(2, outputs.size());

        GenericRecord inputRecordJK1 = new GenericRecordBuilder(inputSchemaJK) //
                .set("j", listInputRecordL) //
                .set("k", "k1") //
                .build();
        GenericRecord inputRecordJK2 = new GenericRecordBuilder(inputSchemaJK) //
                .set("j", listInputRecordL) //
                .set("k", "k2") //
                .build();
        GenericRecord inputRecordDE1 = new GenericRecordBuilder(inputSchemaDE) //
                .set("d", inputRecordJK1) //
                .set("e", "e") //
                .build();
        GenericRecord inputRecordDE2 = new GenericRecordBuilder(inputSchemaDE) //
                .set("d", inputRecordJK2) //
                .set("e", "e") //
                .build();
        GenericRecord inputRecordXY1 = new GenericRecordBuilder(inputSchemaXY) //
                .set("x", "x1;x2") //
                .set("y", inputRecordDE1) //
                .build();
        GenericRecord inputRecordXY2 = new GenericRecordBuilder(inputSchemaXY) //
                .set("x", "x1;x2") //
                .set("y", inputRecordDE2) //
                .build();
        GenericRecord inputParentRecordK1 = new GenericRecordBuilder(inputParentSchema) //
                .set("a", "aaa") //
                .set("b", inputRecordXY1) //
                .set("c", inputRecordFG) //
                .build();
        GenericRecord inputParentRecordK2 = new GenericRecordBuilder(inputParentSchema) //
                .set("a", "aaa") //
                .set("b", inputRecordXY2) //
                .set("c", inputRecordFG) //
                .build();

        record1 = (GenericRecord) outputs.get(0);
        record2 = (GenericRecord) outputs.get(1);
        Assert.assertEquals(inputParentRecordK1, record1);
        Assert.assertEquals(inputParentRecordK2, record2);
    }

    /**
     * Input parent record: {@link NormalizeDoFnTest#inputParentRecord}
     *
     * Normalize array fields: `c.g` | `b.y.d.j`
     *
     * Expected normalized results of the field `c.g`:
     *
     * [{"a": "aaa",
     * "b": {"x": "x1;x2", "y": {"d": {"j": [{"l": "l1"}, {"l": "l2"}], "k": "k1;k2"}, "e": "e"}},
     * "c": {"f": "f", "g": {"h": "h1", "i": "i2"}}},
     * {"a": "aaa",
     * "b": {"x": "x1;x2", "y": {"d": {"j": [{"l": "l1"}, {"l": "l2"}], "k": "k1;k2"}, "e": "e"}},
     * "c": {"f": "f", "g": {"h": "h2", "i": "i1"}}}]
     *
     * Expected normalized results of the field `b.y.d.j`:
     *
     * [{"a": "aaa",
     * "b": {"x": "x1;x2", "y": {"d": {"j": {"l": "l1"}, "k": "k1;k2"}, "e": "e"}},
     * "c": {"f": "f", "g": [{"h": "h1", "i": "i2"}, {"h": "h2", "i": "i1"}]}},
     * {"a": "aaa",
     * "b": {"x": "x1;x2", "y": {"d": {"j": {"l": "l2"}, "k": "k1;k2"}, "e": "e"}},
     * "c": {"f": "f", "g": [{"h": "h1", "i": "i2"}, {"h": "h2", "i": "i1"}]}}]
     *
     * @throws Exception
     */
    @Test
    public void testNormalizeArrayFields() throws Exception {
        NormalizeProperties properties = new NormalizeProperties("test");
        properties.init();
        properties.schemaListener.afterSchema();

        // Normalize `c.g` array field
        properties.columnToNormalize.setValue("c.g");

        NormalizeDoFn function = new NormalizeDoFn().withProperties(properties);
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);
        List<IndexedRecord> outputs = fnTester.processBundle(inputParentRecord);
        Assert.assertEquals(2, outputs.size());

        GenericRecord expectedRecordFG1 = new GenericRecordBuilder(inputSchemaFGSimple) //
                .set("f", "f") //
                .set("g", inputRecordHI1) //
                .build();
        GenericRecord expectedRecordFG2 = new GenericRecordBuilder(inputSchemaFGSimple) //
                .set("f", "f") //
                .set("g", inputRecordHI2) //
                .build();
        GenericRecord expectedParentRecordG1 = new GenericRecordBuilder(inputParentSchemaSimpleFG) //
                .set("a", "aaa") //
                .set("b", inputRecordXY) //
                .set("c", expectedRecordFG1) //
                .build();
        GenericRecord expectedParentRecordG2 = new GenericRecordBuilder(inputParentSchemaSimpleFG) //
                .set("a", "aaa") //
                .set("b", inputRecordXY) //
                .set("c", expectedRecordFG2) //
                .build();
        GenericRecord record1 = (GenericRecord) outputs.get(0);
        GenericRecord record2 = (GenericRecord) outputs.get(1);
        Assert.assertEquals(expectedParentRecordG1.toString(), record1.toString());
        Assert.assertEquals(expectedParentRecordG2.toString(), record2.toString());
        Assert.assertEquals(expectedParentRecordG1.getSchema().toString(), record1.toString());
        Assert.assertEquals(expectedParentRecordG2.getSchema().toString(), record2.toString());

        // Normalize `b.y.d.j` array field
        properties.columnToNormalize.setValue("b.y.d.j");

        function = new NormalizeDoFn().withProperties(properties);
        fnTester = DoFnTester.of(function);
        outputs = fnTester.processBundle(inputParentRecord);

        GenericRecord inputRecordJ1K = new GenericRecordBuilder(inputSchemaJK) //
                .set("j", inputRecordL1) //
                .set("k", "k1;k2") //
                .build();
        GenericRecord inputRecordJ2K = new GenericRecordBuilder(inputSchemaJK) //
                .set("j", inputRecordL2) //
                .set("k", "k1;k2") //
                .build();
        GenericRecord inputRecordDE1 = new GenericRecordBuilder(inputSchemaDE) //
                .set("d", inputRecordJ1K) //
                .set("e", "e") //
                .build();
        GenericRecord inputRecordDE2 = new GenericRecordBuilder(inputSchemaDE) //
                .set("d", inputRecordJ2K) //
                .set("e", "e") //
                .build();
        GenericRecord inputRecordXY1 = new GenericRecordBuilder(inputSchemaXY) //
                .set("x", "x1;x2") //
                .set("y", inputRecordDE1) //
                .build();
        GenericRecord inputRecordXY2 = new GenericRecordBuilder(inputSchemaXY) //
                .set("x", "x1;x2") //
                .set("y", inputRecordDE2) //
                .build();
        GenericRecord inputParentRecordL1 = new GenericRecordBuilder(inputParentSchema) //
                .set("a", "aaa") //
                .set("b", inputRecordXY1) //
                .set("c", inputRecordFG) //
                .build();
        GenericRecord inputParentRecordL2 = new GenericRecordBuilder(inputParentSchema) //
                .set("a", "aaa") //
                .set("b", inputRecordXY2) //
                .set("c", inputRecordFG) //
                .build();
        record1 = (GenericRecord) outputs.get(0);
        record2 = (GenericRecord) outputs.get(1);
        Assert.assertEquals(inputParentRecordL1, record1);
        Assert.assertEquals(inputParentRecordL2, record2);
    }

    /**
     * Input parent record: {@link NormalizeDoFnTest#inputParentRecord}
     *
     * Normalize complex fields: `b` | `b.y` | `b.y.d`
     *
     * Expected normalized results of the fields `b` | `b.y` | `b.y.d`:
     *
     * [{"a": "aaa",
     * "b": {"x": "x1;x2", "y": {"d": {"j": [{"l": "l1"}, {"l": "l2"}], "k": "k1;k2"}, "e": "e"}},
     * "c": {"f": "f", "g": [{"h": "h1", "i": "i2"}, {"h": "h2", "i": "i1"}]}}]
     *
     * @throws Exception
     */
    @Test
    public void testNormalizeComplexFields() throws Exception {
        NormalizeProperties properties = new NormalizeProperties("test");
        properties.init();
        properties.schemaListener.afterSchema();

        // Normalize `b` complex field
        properties.columnToNormalize.setValue("b");

        NormalizeDoFn function = new NormalizeDoFn().withProperties(properties);
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);
        List<IndexedRecord> outputs = fnTester.processBundle(inputParentRecord);
        Assert.assertEquals(1, outputs.size());
        GenericRecord record = (GenericRecord) outputs.get(0);
        Assert.assertEquals(inputParentRecord, record);

        // Normalize `b.y` complex field
        properties.columnToNormalize.setValue("b.y");

        function = new NormalizeDoFn().withProperties(properties);
        fnTester = DoFnTester.of(function);
        outputs = fnTester.processBundle(inputParentRecord);
        Assert.assertEquals(1, outputs.size());
        record = (GenericRecord) outputs.get(0);
        Assert.assertEquals(inputParentRecord, record);

        // Normalize `b.y.d` complex field
        properties.columnToNormalize.setValue("b.y.d");

        function = new NormalizeDoFn().withProperties(properties);
        fnTester = DoFnTester.of(function);
        outputs = fnTester.processBundle(inputParentRecord);
        Assert.assertEquals(1, outputs.size());
        record = (GenericRecord) outputs.get(0);
        Assert.assertEquals(inputParentRecord, record);
    }

    /**
     * Normalize a field not present in the input record will throw TalendRuntimeException.
     * 
     * @throws Exception
     */
    @Test(expected = TalendRuntimeException.class)
    public void testNormalizeNotFoundField() throws Exception {
        NormalizeProperties properties = new NormalizeProperties("test");
        properties.init();
        properties.schemaListener.afterSchema();
        properties.columnToNormalize.setValue("b.y.f");

        NormalizeDoFn function = new NormalizeDoFn().withProperties(properties);
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);
        fnTester.processBundle(inputParentRecord);
    }
}
