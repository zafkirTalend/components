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

package org.talend.components.salesforce;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.junit.Before;
import org.junit.Test;
import org.talend.components.salesforce.runtime.SObjectAdapterFactory;
import org.talend.components.salesforce.runtime.SalesforceSchemaConstants;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.avro.converter.IndexedRecordConverter;

import com.sforce.soap.partner.sobject.SObject;

/**
 *
 */
public class SObjectAdapterFactoryTest {

    public static final Schema SCHEMA = SchemaBuilder.builder().record("Schema").fields() //
            .name("Id").prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type().stringType().noDefault() //
            .name("Name").type().stringType().noDefault() //
            .name("FieldX").type().intType().noDefault() //
            .name("FieldY").type().booleanType().noDefault() //
            .endRecord();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000Z'");

    private SObjectAdapterFactory converter;

    @Before
    public void setUp() {
        converter = new SObjectAdapterFactory();
    }

    @Test
    public void testConvertToAvroBasic() throws IOException {
        converter.setSchema(SCHEMA);

        assertNotNull(converter.getSchema());
        assertEquals(SObject.class, converter.getDatumClass());

        SObject sObject = new SObject("Account");
        sObject.addField("Id", "12345");
        sObject.addField("Name", "Qwerty");
        sObject.addField("FieldX", 42);
        sObject.addField("FieldY", true);

        IndexedRecord indexedRecord = converter.convertToAvro(sObject);
        assertNotNull(indexedRecord);
        assertNotNull(indexedRecord.getSchema());
        assertEquals(SCHEMA, indexedRecord.getSchema());

        assertEquals("12345", indexedRecord.get(0));
        assertEquals("Qwerty", indexedRecord.get(1));
        assertEquals(Integer.valueOf(42), indexedRecord.get(2));
        assertEquals(Boolean.TRUE, indexedRecord.get(3));
    }

    @Test
    public void testConvertToAvroForAggregateResult() throws Exception {

        Schema schema = SchemaBuilder.builder().record("Schema").fields() //
                .name("Id").prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type().stringType().noDefault() //
                .name("Name").type(AvroUtils._string()).noDefault() //
                .name("FieldA").type(AvroUtils._int()).noDefault() //
                .name("FieldB").type(AvroUtils._boolean()).noDefault() //
                .name("FieldC").type(AvroUtils._date()).noDefault() //
                .name("FieldD").type(AvroUtils._double()).noDefault() //
                .endRecord();
        schema.getField("FieldC").addProp(SchemaConstants.TALEND_COLUMN_PATTERN,
                "yyyy-MM-dd'T'HH:mm:ss'.000Z'");
        schema.addProp(SalesforceSchemaConstants.COLUMNNAME_DELIMTER, "_");

        converter.setSchema(schema);

        SObject sObject = new SObject("AggregateResult");
        sObject.addField("Id", "12345");
        sObject.addField("Name", "Qwerty");
        sObject.addField("FieldA", "42");
        sObject.addField("FieldB", "true");
        sObject.addField("FieldC", "2017-06-15T18:26:34.000Z");
        sObject.addField("FieldD", 10200.45);

        IndexedRecord indexedRecord = converter.convertToAvro(sObject);
        assertNotNull(indexedRecord);
        assertNotNull(indexedRecord.getSchema());
        assertEquals(schema, indexedRecord.getSchema());

        assertEquals("12345", indexedRecord.get(0));
        assertEquals("Qwerty", indexedRecord.get(1));
        assertEquals(Integer.valueOf(42), indexedRecord.get(2));
        assertEquals(Boolean.TRUE, indexedRecord.get(3));
        assertEquals(dateFormat.parse("2017-06-15T18:26:34.000Z").getTime(), indexedRecord.get(4));
        assertEquals(Double.valueOf(10200.45), indexedRecord.get(5));
    }

    @Test(expected = IndexedRecordConverter.UnmodifiableAdapterException.class)
    public void testConvertToDatum() throws IOException {
        converter.setSchema(SCHEMA);
        converter.convertToDatum(new GenericData.Record(converter.getSchema()));
    }

    @Test(expected = IndexedRecordConverter.UnmodifiableAdapterException.class)
    public void testIndexedRecordUnmodifiable() throws IOException {
        converter.setSchema(SCHEMA);

        SObject sObject = new SObject("Account");
        sObject.addField("Id", "12345");
        sObject.addField("Name", "Qwerty");

        IndexedRecord indexedRecord = converter.convertToAvro(sObject);
        indexedRecord.put(1, "Asdfgh");
    }
}
