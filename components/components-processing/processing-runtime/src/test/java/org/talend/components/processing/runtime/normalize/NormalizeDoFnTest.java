package org.talend.components.processing.runtime.normalize;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.DoFnTester;
import org.junit.Test;
import org.talend.components.processing.normalize.NormalizeProperties;
import org.talend.components.processing.runtime.normalize.NormalizeDoFn;
import org.talend.components.processing.runtime.normalize.NormalizeRuntime;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class NormalizeDoFnTest {

    private final Schema inputSimpleSchema = SchemaBuilder.record("inputRow") //
            .fields() //
            .name("a").type().optional().stringType() //
            .name("b").type().optional().stringType() //
            .name("c").type().optional().stringType() //
            .endRecord();

    private final GenericRecord inputSimpleRecord = new GenericRecordBuilder(inputSimpleSchema) //
            .set("a", "aaa") //
            .set("b", "  bbb;  bbb  ;") //
            .set("c", "ccc") //
            .build();

    private final GenericRecord inputSimpleRecord2 = new GenericRecordBuilder(inputSimpleSchema) //
            .set("a", "aaa") //
            .set("b", "  bbb;  bbb  ; ") //
            .set("c", "ccc") //
            .build();

    private void checkOutput_AllInputFieldsAreValidAndFilled(DoFnTester<Object, IndexedRecord> fnTester) throws Exception {
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
    private void checkOutput_AllInputFieldsAreValidAndFilled2(DoFnTester<Object, IndexedRecord> fnTester) throws Exception {
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
    private void checkOutput_AllInputFieldsAreValidAndFilled3(DoFnTester<Object, IndexedRecord> fnTester) throws Exception {
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
        DoFnTester<Object, IndexedRecord> fnTester = DoFnTester.of(function);
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
        DoFnTester<Object, IndexedRecord> fnTester = DoFnTester.of(function);
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
        DoFnTester<Object, IndexedRecord> fnTester = DoFnTester.of(function);
        checkOutput_AllInputFieldsAreValidAndFilled3(fnTester);
    }
}
