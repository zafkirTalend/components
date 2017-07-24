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
package org.talend.components.snowflake.runtime;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.snowflake.tsnowflakeoutput.TSnowflakeOutputProperties;
import org.talend.components.snowflake.tsnowflakeoutput.TSnowflakeOutputProperties.OutputAction;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;

import net.snowflake.client.jdbc.internal.joda.time.DateTime;
import net.snowflake.client.loader.LoaderFactory;
import net.snowflake.client.loader.LoaderProperty;
import net.snowflake.client.loader.StreamLoader;

/**
 * Unit tests for {@link SnowflakeWriter} class
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(LoaderFactory.class)
public class SnowflakeWriterTest {

    private SnowflakeWriter writer;

    private SnowflakeSink sink;

    private TSnowflakeOutputProperties properties;

    private SnowflakeWriteOperation writeOperation;

    private StreamLoader loader;

    @Before
    public void setup() throws Exception {
        properties = new TSnowflakeOutputProperties("outputProperties");
        properties.init();
        sink = Mockito.mock(SnowflakeSink.class);
        Mockito.when(sink.getSnowflakeOutputProperties()).thenReturn(properties);
        writeOperation = new SnowflakeWriteOperation(sink);
        writer = new SnowflakeWriter(writeOperation, null);

        Schema schema = SchemaBuilder.record("record").fields().name("id")
                .prop(SchemaConstants.TALEND_COLUMN_IS_KEY, Boolean.TRUE.toString()).type().stringType().noDefault()
                .requiredString("column").requiredString("field").endRecord();
        Mockito.when(sink.connect(null)).thenReturn(Mockito.mock(Connection.class));
        properties.table.main.schema.setValue(schema);
        properties.table.tableName.setValue("Table");
        properties.connection.schemaName.setValue("dbSchema");
        properties.connection.db.setValue("db");

        PowerMockito.mockStatic(LoaderFactory.class);
        loader = Mockito.mock(StreamLoader.class);
        Mockito.when(LoaderFactory.createLoader(Mockito.anyMapOf(LoaderProperty.class, Object.class),
                Mockito.any(Connection.class), Mockito.any(Connection.class))).thenReturn(loader);

    }

    @Test
    public void testGetSuccessfulWrites() {
        Assert.assertTrue(((List<IndexedRecord>) writer.getSuccessfulWrites()).isEmpty());
    }

    @Test
    public void testGetRejectedWrites() {
        Assert.assertTrue(((List<IndexedRecord>) writer.getRejectedWrites()).isEmpty());
    }

    @Test
    public void testGetWriteOperations() {
        Assert.assertEquals(writeOperation, writer.getWriteOperation());
    }

    @Test
    public void testOpenUpsert() throws Exception {

        properties.outputAction.setValue(OutputAction.UPSERT);
        properties.upsertKeyColumn.setValue("id_2");

        writer.open("uId");

        Mockito.verify(loader, Mockito.times(1)).start();

        Result result = writer.close();
        // Since we can't affect on listener.
        Assert.assertEquals(0, result.totalCount);
        Assert.assertEquals(0, result.successCount);
        Assert.assertEquals(0, result.rejectCount);
    }

    @Test
    public void testOpenInsert() throws Exception {
        Schema schema = SchemaBuilder.record("record").fields().requiredString("column").requiredString("field").endRecord();
        AvroUtils.setIncludeAllFields(schema, true);
        properties.table.main.schema.setValue(schema);
        properties.outputAction.setValue(OutputAction.INSERT);
        Schema schemaDB = SchemaBuilder.record("record").fields().requiredString("id").requiredString("column")
                .requiredString("field").endRecord();
        Mockito.when(sink.getSchema(Mockito.any(RuntimeContainer.class), Mockito.any(Connection.class), Mockito.anyString()))
                .thenReturn(schemaDB);

        writer.open("uId");

        Mockito.verify(sink, Mockito.times(1)).getSchema(Mockito.any(RuntimeContainer.class), Mockito.any(Connection.class),
                Mockito.anyString());
        Mockito.verify(loader, Mockito.times(1)).start();
    }

    @Test
    public void testWrite() throws Exception {
        int daysFrom1970 = 17337;
        DateTime dateTime = new DateTime(0).plusDays(daysFrom1970);
        int timeMillis = 1498031820;
        long timeStamp = 1498031820264L;
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss.SSS");
        timeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat timestampFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSXXX");

        Object[] expectedRow = { "FirstValue", 10_000, 99.9, dateFormatter.format(dateTime.toDate()), true, timeFormatter.format(new Date(timeMillis)),
                timestampFormatter.format(new Date(timeStamp)), null };

        SnowflakeAvroRegistry registry = SnowflakeAvroRegistry.get();
        List<Field> fields = new ArrayList<>();
        Field field = registry.sqlType2Avro(18, 10, Types.VARCHAR, true, "firstColumn", "first_column", "");
        fields.add(field);
        field = registry.sqlType2Avro(18, 10, Types.BIGINT, true, "secondColumn", "second_column", 0);
        fields.add(field);
        field = registry.sqlType2Avro(18, 10, Types.DOUBLE, true, "thirdColumn", "third_column", 0.0);
        fields.add(field);
        field = registry.sqlType2Avro(18, 10, Types.DATE, true, "fourthColumn", "fourth_column", 1);
        fields.add(field);
        field = registry.sqlType2Avro(18, 10, Types.BOOLEAN, true, "fifthColumn", "fifth_column", true);
        fields.add(field);
        field = registry.sqlType2Avro(18, 10, Types.TIME, true, "sixthColumn", "sixth_column", 1);
        fields.add(field);
        field = registry.sqlType2Avro(18, 10, Types.TIMESTAMP, true, "eleventhColumn", "eleventh_column", 1);
        fields.add(field);
        field = registry.sqlType2Avro(18, 10, Types.ARRAY, true, "eighthColumn", "eighth_column", "");
        fields.add(field);
        Schema schema = Schema.createRecord("records", null, null, false, fields);
        properties.table.main.schema.setValue(schema);
        properties.outputAction.setValue(OutputAction.INSERT);
        IndexedRecord record = Mockito.mock(IndexedRecord.class);
        Mockito.when(record.getSchema()).thenReturn(schema);
        Mockito.when(record.get(Mockito.anyInt())).thenReturn("FirstValue", 10_000, 99.9, daysFrom1970, true, timeMillis, timeStamp,
                null);

        writer.open("uId");
        writer.write(record);

        // Need to check if specific array came to loader
        Mockito.verify(loader, Mockito.times(1)).submitRow(Mockito.eq(expectedRow));

    }

    /**
     * Skip write if object is null.
     *
     * @throws Exception
     */
    @Test
    public void testWriteNullObject() throws Exception {
        writer.write(null);
        Mockito.verify(loader, Mockito.never()).submitRow(Mockito.any(Object[].class));
    }

    @Test(expected = IOException.class)
    public void testCloseFailedToFinishLoader() throws Exception {
        properties.outputAction.setValue(OutputAction.DELETE);
        writer.open("uId");
        Mockito.doThrow(new Exception("Failed to finish loader")).when(loader).finish();
        writer.close();
    }

    @Test(expected = IOException.class)
    public void testCloseFailedToCloseSnowflakeConnection() throws Exception {
        properties.outputAction.setValue(OutputAction.UPDATE);
        writer.open("uId");
        Mockito.doThrow(new SQLException("Failed to finish loader")).when(sink)
                .closeConnection(Mockito.any(RuntimeContainer.class), Mockito.any(Connection.class));
        writer.close();
    }
}
