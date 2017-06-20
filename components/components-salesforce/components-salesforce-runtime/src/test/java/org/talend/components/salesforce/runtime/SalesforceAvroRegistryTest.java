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
package org.talend.components.salesforce.runtime;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.talend.daikon.avro.SchemaConstants.TALEND_COLUMN_DEFAULT;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.SchemaBuilder;
import org.junit.Before;
import org.junit.Test;
import org.talend.components.api.exception.ComponentException;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.avro.converter.AvroConverter;

import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.FieldType;

/**
 * Unit tests for the {@link SalesforceAvroRegistry}.
 */
@SuppressWarnings("nls")
public class SalesforceAvroRegistryTest {

    private static final SalesforceAvroRegistry sRegistry = SalesforceAvroRegistry.get();

    private SimpleDateFormat dateFormat;

    @Before
    public void setUp() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Tests that the {@link SalesforceAvroRegistry} has added support to get a {@link Schema} that describes
     * {@link DescribeSObjectResult} objects.
     */
    @Test
    public void testInferSchemaDescribeSObjectResult() {
        Schema s;
        // Setup.
        {
            Field booleanField = new Field();
            booleanField.setName("valid");
            booleanField.setType(FieldType._boolean);

            Field defaultField = new Field();
            defaultField.setName("def");
            defaultField.setType(FieldType._boolean);
            defaultField.setDefaultValueFormula(Boolean.TRUE.toString());

            Field dateField = new Field();
            dateField.setName("date");
            dateField.setType(FieldType.date);

            Field stringWithLengthField = new Field();
            stringWithLengthField.setName("string_with_length");
            stringWithLengthField.setType(FieldType.string);
            stringWithLengthField.setLength(20);

            Field numberWithScaleAndPrecisionField = new Field();
            numberWithScaleAndPrecisionField.setName("number_with_scale_and_precision");
            numberWithScaleAndPrecisionField.setType(FieldType._double);
            numberWithScaleAndPrecisionField.setPrecision(10);
            numberWithScaleAndPrecisionField.setScale(2);

            Field doubleWithNullable = new Field();
            doubleWithNullable.setName("double_with_nullable");
            doubleWithNullable.setType(FieldType._double);
            doubleWithNullable.setPrecision(18);
            doubleWithNullable.setScale(15);
            doubleWithNullable.setNillable(true);

            DescribeSObjectResult dsor = new DescribeSObjectResult();
            dsor.setName("MySObjectRecord");
            dsor.setFields(new Field[] { booleanField, defaultField, dateField, stringWithLengthField,
                    numberWithScaleAndPrecisionField, doubleWithNullable });
            s = sRegistry.inferSchema(dsor);
        }

        assertThat(s.getType(), is(Schema.Type.RECORD));
        assertThat(s.getName(), is("MySObjectRecord"));
        assertThat(s.getFields(), hasSize(6));
        assertThat(s.getObjectProps().keySet(), empty());

        // Check out the field.
        Schema.Field f = s.getFields().get(0);
        assertThat(f.name(), is("valid"));
        assertThat(f.schema().getType(), is(Schema.Type.BOOLEAN));
        assertThat(f.schema().getObjectProps().keySet(), empty());

        f = s.getField("def");
        assertThat(f.name(), is("def"));
        assertThat(f.schema().getType(), is(Schema.Type.BOOLEAN));
        assertThat(f.getObjectProps().keySet(), containsInAnyOrder(TALEND_COLUMN_DEFAULT));
        assertThat(f.getProp(TALEND_COLUMN_DEFAULT), is(Boolean.TRUE.toString()));

        f = s.getField("date");
        assertThat(f.name(), is("date"));
        assertTrue(AvroUtils.isSameType(f.schema(), AvroUtils._date()));
        assertThat(f.getObjectProps().keySet(), containsInAnyOrder(SchemaConstants.TALEND_COLUMN_PATTERN));
        assertThat(f.getProp(SchemaConstants.TALEND_COLUMN_PATTERN), is("yyyy-MM-dd"));

        f = s.getField("string_with_length");
        assertThat(f.name(), is("string_with_length"));
        assertTrue(AvroUtils.isSameType(f.schema(), AvroUtils._string()));
        assertThat(f.getObjectProps().keySet(), containsInAnyOrder(SchemaConstants.TALEND_COLUMN_DB_LENGTH));
        assertThat(f.getProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH), is("20"));

        f = s.getField("number_with_scale_and_precision");
        assertThat(f.name(), is("number_with_scale_and_precision"));
        assertTrue(AvroUtils.isSameType(f.schema(), AvroUtils._double()));
        assertThat(f.getObjectProps().keySet(),
                containsInAnyOrder(SchemaConstants.TALEND_COLUMN_DB_LENGTH, SchemaConstants.TALEND_COLUMN_PRECISION));
        assertThat(f.getProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH), is("10"));
        assertThat(f.getProp(SchemaConstants.TALEND_COLUMN_PRECISION), is("2"));

        f = s.getField("double_with_nullable");
        assertThat(f.name(), is("double_with_nullable"));
        assertThat(f.schema().getType(), is(Schema.Type.UNION));
        assertThat(f.schema().getTypes(), containsInAnyOrder(AvroUtils._double(), Schema.create(Schema.Type.NULL)));
        assertThat(f.getObjectProps().keySet(),
                containsInAnyOrder(SchemaConstants.TALEND_COLUMN_DB_LENGTH, SchemaConstants.TALEND_COLUMN_PRECISION));
        assertThat(f.getProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH), is("18"));
        assertThat(f.getProp(SchemaConstants.TALEND_COLUMN_PRECISION), is("15"));
    }

    @Test
    public void testInferSchemaWithReferenceField() {
        Field referenceField = new Field();
        referenceField.setName("reference");
        referenceField.setType(FieldType.string);
        referenceField.setReferenceTo(new String[]{"SomeRecord"});
        referenceField.setRelationshipName("relationship");

        DescribeSObjectResult dsor = new DescribeSObjectResult();
        dsor.setName("MySObjectRecord");
        dsor.setFields(new Field[] { referenceField });

        Schema schema = sRegistry.inferSchema(dsor);

        Schema.Field field = schema.getField("reference");

        assertThat(field.schema().getType(), is(Schema.Type.STRING));
        assertThat(field.getProp(SalesforceSchemaConstants.REF_MODULE_NAME), is("SomeRecord"));
        assertThat(field.getProp(SalesforceSchemaConstants.REF_FIELD_NAME), is("relationship"));
    }

    /**
     * Tests that the {@link SalesforceAvroRegistry} has added support to get a {@link Schema} that describes
     * {@link Field} objects.
     */
    @Test
    public void testInferSchemaField() {
        // Test boolean extensively.
        Field f = new Field();
        f.setType(FieldType._boolean);

        Schema s = sRegistry.inferSchema(f);
        assertThat(s.getType(), is(Schema.Type.BOOLEAN));
        // The properties injected into boolean.
        assertThat(s.getObjectProps().keySet(), empty());

        // The same thing if nullable.
        f.setNillable(true);
        s = sRegistry.inferSchema(f);
        assertThat(s.getType(), is(Schema.Type.UNION));
        assertThat(s.getTypes(), hasSize(2));
        assertThat(s.getObjectProps().keySet(), empty());
        s = AvroUtils.unwrapIfNullable(s);
        assertThat(s.getType(), is(Schema.Type.BOOLEAN));
        assertThat(s.getObjectProps().keySet(), empty());

        f = new Field();
        f.setType(FieldType._int);
        s = sRegistry.inferSchema(f);
        assertThat(s.getType(), is(Schema.Type.INT));

        f.setType(FieldType.date);
        s = sRegistry.inferSchema(f);
        assertThat(s.getType(), is(Schema.Type.LONG));
        assertThat(s.getProp(SchemaConstants.JAVA_CLASS_FLAG), is(Date.class.getCanonicalName()));

        f.setType(FieldType.datetime);
        s = sRegistry.inferSchema(f);
        assertThat(s.getType(), is(Schema.Type.LONG));
        assertThat(s.getProp(SchemaConstants.JAVA_CLASS_FLAG), is(Date.class.getCanonicalName()));

        f.setType(FieldType._double);
        s = sRegistry.inferSchema(f);
        assertThat(s.getType(), is(Schema.Type.DOUBLE));

        f.setType(FieldType.currency);
        f.setPrecision(8);
        f.setScale(5);
        s = sRegistry.inferSchema(f);
        assertThat(s.getType(), is(Schema.Type.STRING));
        assertThat(s.getProp(SchemaConstants.JAVA_CLASS_FLAG), is(BigDecimal.class.getCanonicalName()));
        // assertThat(s.getLogicalType(), is((LogicalType) LogicalTypes.decimal(8, 5)));
    }

    /**
     * Tests {@link SalesforceAvroRegistry#inferSchema(Object)} returns {@link Schema} of type {@link Type#DOUBLE},
     * when percent Field is passed
     * 
     * This test-case related to https://jira.talendforge.org/browse/TDI-37479 bug
     */
    @Test
    public void testInferSchemaFieldPercent() {
        Field percentField = new Field();
        percentField.setType(FieldType.percent);

        Schema schema = sRegistry.inferSchema(percentField);
        Schema.Type actualType = schema.getType();
        assertThat(actualType, is(Schema.Type.DOUBLE));
    }

    @Test
    public void testStringToBooleanConverter() {
        Schema schema = SchemaBuilder.builder().record("Schema").fields() //
                .name("Field_A").type(AvroUtils._boolean()).noDefault() //
                .endRecord();

        AvroConverter<String, ?> converter = sRegistry.getConverterFromString(schema.getField("Field_A"));
        assertNotNull(converter);
        assertEquals(String.class, converter.getDatumClass());
        assertEquals(schema.getField("Field_A").schema(), converter.getSchema());

        Object value = converter.convertToAvro("true");
        assertNotNull(value);
        assertThat(value, instanceOf(Boolean.class));
        assertThat((Boolean) value, equalTo(Boolean.TRUE));

        assertNull(converter.convertToAvro(""));
        assertNull(converter.convertToAvro(null));
    }

    @Test
    public void testStringToBytesConverter() {
        Schema schema = SchemaBuilder.builder().record("Schema").fields() //
                .name("Field_A").type(AvroUtils._bytes()).noDefault() //
                .endRecord();

        AvroConverter<String, ?> converter = sRegistry.getConverterFromString(schema.getField("Field_A"));
        assertNotNull(converter);
        assertEquals(String.class, converter.getDatumClass());
        assertEquals(schema.getField("Field_A").schema(), converter.getSchema());

        Object value = converter.convertToAvro("a1b2");
        assertNotNull(value);
        assertThat(value, instanceOf(byte[].class));
        assertArrayEquals(new byte[]{97, 49, 98, 50}, (byte[]) value);

        assertNull(converter.convertToAvro(null));
    }

    @Test
    public void testStringToDecimalConverter() {
        Schema schema = SchemaBuilder.builder().record("Schema").fields() //
                .name("Field_A").type(AvroUtils._decimal()).noDefault() //
                .endRecord();

        AvroConverter<String, ?> converter = sRegistry.getConverterFromString(schema.getField("Field_A"));
        assertNotNull(converter);
        assertEquals(String.class, converter.getDatumClass());
        assertEquals(schema.getField("Field_A").schema(), converter.getSchema());

        Object value = converter.convertToAvro("20000000000000000000000000.123456789");
        assertNotNull(value);
        assertThat(value, instanceOf(BigDecimal.class));
        assertThat((BigDecimal) value, equalTo(new BigDecimal("20000000000000000000000000.123456789")));

        assertNull(converter.convertToAvro(""));
        assertNull(converter.convertToAvro(null));

        String sValue = ((AvroConverter<String, BigDecimal>) converter).convertToDatum(
                new BigDecimal("20000000000000000000000000.123456789"));
        assertNotNull(sValue);
        assertThat(sValue, equalTo("20000000000000000000000000.123456789"));
    }

    @Test
    public void testStringToDoubleConverter() {
        Schema schema = SchemaBuilder.builder().record("Schema").fields() //
                .name("Field_A").type(AvroUtils._double()).noDefault() //
                .endRecord();

        AvroConverter<String, ?> converter = sRegistry.getConverterFromString(schema.getField("Field_A"));
        assertNotNull(converter);
        assertEquals(String.class, converter.getDatumClass());
        assertEquals(schema.getField("Field_A").schema(), converter.getSchema());

        Object value = converter.convertToAvro("102030405060.12345");
        assertNotNull(value);
        assertThat(value, instanceOf(Double.class));
        assertThat((Double) value, equalTo(Double.valueOf(102030405060.12345)));

        assertNull(converter.convertToAvro(""));
        assertNull(converter.convertToAvro(null));
    }

    @Test
    public void testStringToIntegerConverter() {
        Schema schema = SchemaBuilder.builder().record("Schema").fields() //
                .name("Field_A").type(AvroUtils._int()).noDefault() //
                .endRecord();

        AvroConverter<String, ?> converter = sRegistry.getConverterFromString(schema.getField("Field_A"));
        assertNotNull(converter);
        assertEquals(String.class, converter.getDatumClass());
        assertEquals(schema.getField("Field_A").schema(), converter.getSchema());

        Object value = converter.convertToAvro("1020304050");
        assertNotNull(value);
        assertThat(value, instanceOf(Integer.class));
        assertThat((Integer) value, equalTo(Integer.valueOf(1020304050)));

        assertNull(converter.convertToAvro(""));
        assertNull(converter.convertToAvro(null));
    }

    @Test
    public void testStringToDateConverter() throws ParseException {
        Schema schema = SchemaBuilder.builder().record("Schema").fields() //
                .name("Field_A").type(AvroUtils._date()).noDefault() //
                .endRecord();
        schema.getField("Field_A").addProp(SchemaConstants.TALEND_COLUMN_PATTERN,"yyyy-MM-dd");

        AvroConverter<String, ?> converter = sRegistry.getConverterFromString(schema.getField("Field_A"));
        assertNotNull(converter);
        assertEquals(String.class, converter.getDatumClass());
        assertEquals(schema.getField("Field_A").schema(), converter.getSchema());

        Object value = converter.convertToAvro("2017-06-15");
        assertNotNull(value);
        assertThat(value, instanceOf(Long.class));
        assertThat((Long) value, equalTo(Long.valueOf(dateFormat.parse("2017-06-15").getTime())));

        assertNull(converter.convertToAvro(""));
        assertNull(converter.convertToAvro(null));

        Date date = dateFormat.parse("2017-06-15");
        String sValue = ((AvroConverter<String, Long>) converter).convertToDatum(Long.valueOf(date.getTime()));
        assertNotNull(sValue);
        assertEquals(dateFormat.format(date), sValue);

        assertNull(converter.convertToDatum(null));
    }

    @Test(expected = ComponentException.class)
    public void testStringToDateConverterParseError() throws ParseException {
        Schema schema = SchemaBuilder.builder().record("Schema").fields() //
                .name("Field_A").type(AvroUtils._date()).noDefault() //
                .endRecord();
        schema.getField("Field_A").addProp(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-dd");

        AvroConverter<String, ?> converter = sRegistry.getConverterFromString(schema.getField("Field_A"));

        converter.convertToAvro("2017/6/15");
    }
}
