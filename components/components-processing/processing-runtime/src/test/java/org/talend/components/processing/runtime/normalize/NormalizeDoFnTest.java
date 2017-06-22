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

/**
 * Created by zafkir on 22/06/2017.
 */
public class NormalizeDoFnTest {

    private final Schema inputSimpleSchema = SchemaBuilder.record("inputRow") //
            .fields() //
            .name("a").type().optional().stringType() //
            .name("b").type().optional().stringType() //
            .name("c").type().optional().stringType() //
            .endRecord();

    private final GenericRecord inputSimpleRecord = new GenericRecordBuilder(inputSimpleSchema) //
            .set("a", "aaa") //
            .set("b", "BBB,BBB") //
            .set("c", "Ccc") //
            .build();

    private void checkSimpleInputValidOutput(DoFnTester<Object, IndexedRecord> fnTester) throws Exception {
        List<IndexedRecord> outputs = fnTester.processBundle(inputSimpleRecord);
        for(int i=0; i<outputs.size(); i++) {
            System.out.println(outputs.get(i));
        }
    }

    @Test
    public void test_FilterWithNullValue() throws Exception {
        NormalizeProperties properties = new NormalizeProperties("test");
        properties.init();
        properties.schemaListener.afterSchema();
        properties.columnName.setValue("b");

        NormalizeDoFn function = new NormalizeDoFn().withProperties(properties);
        DoFnTester<Object, IndexedRecord> fnTester = DoFnTester.of(function);
        checkSimpleInputValidOutput(fnTester);
    }
}
