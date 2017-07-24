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
package org.talend.components.jdbc;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.components.api.component.runtime.Reader;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.api.exception.DataRejectException;
import org.talend.components.jdbc.common.DBTestUtils;
import org.talend.components.jdbc.module.PreparedStatementTable;
import org.talend.components.jdbc.runtime.JDBCRowSink;
import org.talend.components.jdbc.runtime.JDBCRowSource;
import org.talend.components.jdbc.runtime.JDBCRowSourceOrSink;
import org.talend.components.jdbc.runtime.setting.AllSetting;
import org.talend.components.jdbc.runtime.writer.JDBCRowWriter;
import org.talend.components.jdbc.tjdbcinput.TJDBCInputDefinition;
import org.talend.components.jdbc.tjdbcinput.TJDBCInputProperties;
import org.talend.components.jdbc.tjdbcrow.TJDBCRowDefinition;
import org.talend.components.jdbc.tjdbcrow.TJDBCRowProperties;
import org.talend.daikon.properties.ValidationResult;

public class JDBCRowTestIT {

    public static AllSetting allSetting;

    @BeforeClass
    public static void beforeClass() throws Exception {
        allSetting = DBTestUtils.createAllSetting();

        DBTestUtils.createTable(allSetting);
    }

    @AfterClass
    public static void afterClass() throws ClassNotFoundException, SQLException {
        DBTestUtils.releaseResource(allSetting);
    }

    @Before
    public void before() throws Exception {
        DBTestUtils.truncateTableAndLoadData(allSetting);
    }

    @Test
    public void test_basic_no_connector() throws Exception {
        TJDBCRowDefinition definition = new TJDBCRowDefinition();
        TJDBCRowProperties properties = DBTestUtils.createCommonJDBCRowProperties(allSetting, definition);

        properties.tableSelection.tablename.setValue(DBTestUtils.getTablename());
        properties.sql.setValue("insert into test values(4, 'momo')");
        properties.dieOnError.setValue(true);
        randomCommit(properties);

        JDBCRowSourceOrSink sourceOrSink = new JDBCRowSourceOrSink();

        sourceOrSink.initialize(null, properties);
        ValidationResult result = sourceOrSink.validate(null);
        Assert.assertTrue(result.getStatus() == ValidationResult.Result.OK);

        TJDBCInputDefinition definition1 = new TJDBCInputDefinition();
        TJDBCInputProperties properties1 = DBTestUtils.createCommonJDBCInputProperties(allSetting, definition1);
        List<IndexedRecord> records = DBTestUtils.fetchDataByReaderFromTable(DBTestUtils.getTablename(),
                DBTestUtils.createTestSchema(), definition1, properties1);

        assertThat(records, hasSize(4));
        Assert.assertEquals("4", records.get(3).get(0));
        Assert.assertEquals("momo", records.get(3).get(1));
    }

    @Test
    public void test_use_preparedstatement_no_connector() throws Exception {
        TJDBCRowDefinition definition = new TJDBCRowDefinition();
        TJDBCRowProperties properties = DBTestUtils.createCommonJDBCRowProperties(allSetting, definition);

        properties.tableSelection.tablename.setValue(DBTestUtils.getTablename());
        properties.sql.setValue("insert into test values(?, ?)");
        properties.dieOnError.setValue(true);
        randomCommit(properties);

        properties.usePreparedStatement.setValue(true);
        properties.preparedStatementTable.indexs.setValue(Arrays.asList(1, 2));
        properties.preparedStatementTable.types
                .setValue(Arrays.asList(PreparedStatementTable.Type.Int.name(), PreparedStatementTable.Type.String.name()));
        properties.preparedStatementTable.values.setValue(Arrays.<Object> asList(4, "momo"));

        JDBCRowSourceOrSink sourceOrSink = new JDBCRowSourceOrSink();

        sourceOrSink.initialize(null, properties);
        ValidationResult result = sourceOrSink.validate(null);
        Assert.assertTrue(result.getStatus() == ValidationResult.Result.OK);

        TJDBCInputDefinition definition1 = new TJDBCInputDefinition();
        TJDBCInputProperties properties1 = DBTestUtils.createCommonJDBCInputProperties(allSetting, definition1);
        List<IndexedRecord> records = DBTestUtils.fetchDataByReaderFromTable(DBTestUtils.getTablename(),
                DBTestUtils.createTestSchema(), definition1, properties1);

        assertThat(records, hasSize(4));
        Assert.assertEquals("4", records.get(3).get(0));
        Assert.assertEquals("momo", records.get(3).get(1));
    }

    @Test
    public void test_die_on_error_no_connector() throws Exception {
        TJDBCRowDefinition definition = new TJDBCRowDefinition();
        TJDBCRowProperties properties = DBTestUtils.createCommonJDBCRowProperties(allSetting, definition);

        properties.tableSelection.tablename.setValue(DBTestUtils.getTablename());
        properties.sql.setValue("insert into test values(4, 'a too long value')");
        properties.dieOnError.setValue(true);
        randomCommit(properties);

        JDBCRowSourceOrSink sourceOrSink = new JDBCRowSourceOrSink();

        sourceOrSink.initialize(null, properties);
        ValidationResult result = sourceOrSink.validate(null);
        Assert.assertTrue(result.getStatus() == ValidationResult.Result.ERROR);
        Assert.assertNotNull(result.getMessage());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void test_basic_as_input() throws Exception {
        TJDBCRowDefinition definition = new TJDBCRowDefinition();
        TJDBCRowProperties properties = DBTestUtils.createCommonJDBCRowProperties(allSetting, definition);

        Schema schema = DBTestUtils.createTestSchema4();
        properties.main.schema.setValue(schema);
        properties.updateOutputSchemas();

        properties.tableSelection.tablename.setValue(DBTestUtils.getTablename());
        properties.sql.setValue("select id, name from test");
        properties.dieOnError.setValue(true);
        randomCommit(properties);

        properties.propagateQueryResultSet.setValue(true);// the field is the unique reason to use the component as a input
                                                          // component
        properties.beforeUseColumn();
        properties.useColumn.setValue(properties.useColumn.getPossibleValues().get(0).toString());

        JDBCRowSource source = new JDBCRowSource();

        source.initialize(null, properties);
        ValidationResult result = source.validate(null);
        Assert.assertTrue(result.getStatus() == ValidationResult.Result.OK);

        Reader reader = source.createReader(null);
        try {
            reader.start();
            IndexedRecord row = (IndexedRecord) reader.getCurrent();
            ResultSet resultSet = (ResultSet) row.get(0);

            resultSet.next();
            Assert.assertEquals(1, resultSet.getInt(1));
            Assert.assertEquals("wangwei", resultSet.getString(2));

            resultSet.next();
            Assert.assertEquals(2, resultSet.getInt(1));
            Assert.assertEquals("gaoyan", resultSet.getString(2));

            resultSet.next();
            Assert.assertEquals(3, resultSet.getInt(1));
            Assert.assertEquals("dabao", resultSet.getString(2));

            resultSet.close();

            Assert.assertFalse(reader.advance());// only output one row when it works as a input component

            reader.close();
        } finally {
            reader.close();
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void test_use_preparedstatement_as_input() throws Exception {
        TJDBCRowDefinition definition = new TJDBCRowDefinition();
        TJDBCRowProperties properties = DBTestUtils.createCommonJDBCRowProperties(allSetting, definition);

        Schema schema = DBTestUtils.createTestSchema4();
        properties.main.schema.setValue(schema);
        properties.updateOutputSchemas();

        properties.tableSelection.tablename.setValue(DBTestUtils.getTablename());
        properties.sql.setValue("select id, name from test where id = ?");
        properties.dieOnError.setValue(true);
        randomCommit(properties);

        properties.propagateQueryResultSet.setValue(true);// the field is the unique reason to use the component as a input
                                                          // component
        properties.beforeUseColumn();
        properties.useColumn.setValue(properties.useColumn.getPossibleValues().get(0).toString());

        properties.usePreparedStatement.setValue(true);
        properties.preparedStatementTable.indexs.setValue(Arrays.asList(1));
        properties.preparedStatementTable.types.setValue(Arrays.asList(PreparedStatementTable.Type.Int.name()));
        properties.preparedStatementTable.values.setValue(Arrays.<Object> asList(1));

        JDBCRowSource source = new JDBCRowSource();

        source.initialize(null, properties);
        ValidationResult result = source.validate(null);
        Assert.assertTrue(result.getStatus() == ValidationResult.Result.OK);

        Reader reader = source.createReader(null);
        try {
            reader.start();
            IndexedRecord row = (IndexedRecord) reader.getCurrent();
            ResultSet resultSet = (ResultSet) row.get(0);

            resultSet.next();
            Assert.assertEquals(1, resultSet.getInt(1));
            Assert.assertEquals("wangwei", resultSet.getString(2));

            resultSet.close();

            Assert.assertFalse(reader.advance());// only output one row when it works as a input component

            reader.close();
        } finally {
            reader.close();
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void test_reject_as_input() throws Exception {
        TJDBCRowDefinition definition = new TJDBCRowDefinition();
        TJDBCRowProperties properties = DBTestUtils.createCommonJDBCRowProperties(allSetting, definition);

        Schema schema = DBTestUtils.createTestSchema4();
        properties.main.schema.setValue(schema);
        properties.updateOutputSchemas();

        properties.tableSelection.tablename.setValue(DBTestUtils.getTablename());
        properties.sql.setValue("select id, name from notexists");
        properties.dieOnError.setValue(false);
        randomCommit(properties);

        properties.propagateQueryResultSet.setValue(true);// the field is the unique reason to use the component as a input
                                                          // component
        properties.beforeUseColumn();
        properties.useColumn.setValue(properties.useColumn.getPossibleValues().get(0).toString());

        JDBCRowSource source = new JDBCRowSource();

        source.initialize(null, properties);
        ValidationResult result = source.validate(null);
        Assert.assertTrue(result.getStatus() == ValidationResult.Result.OK);

        Reader reader = source.createReader(null);
        try {
            reader.start();

            reader.getCurrent();

            Assert.fail();// should go to the exception before current statement

            reader.advance();

            reader.close();
        } catch (DataRejectException e) {
            Map<String, Object> info = e.getRejectInfo();
            IndexedRecord data = (IndexedRecord) info.get("talend_record");
            Assert.assertNull(data.get(0));
            Assert.assertNotNull(data.get(1));
            Assert.assertNotNull(data.get(2));
        } finally {
            reader.close();
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void test_basic_as_output() throws Exception {
        TJDBCRowDefinition definition = new TJDBCRowDefinition();
        TJDBCRowProperties properties = DBTestUtils.createCommonJDBCRowProperties(allSetting, definition);

        Schema schema = DBTestUtils.createTestSchema();
        properties.main.schema.setValue(schema);
        properties.updateOutputSchemas();

        properties.tableSelection.tablename.setValue(DBTestUtils.getTablename());
        properties.sql.setValue("insert into test values(?,?)");
        properties.dieOnError.setValue(true);
        randomCommit(properties);

        properties.usePreparedStatement.setValue(true);
        properties.preparedStatementTable.indexs.setValue(Arrays.asList(1, 2));
        properties.preparedStatementTable.types
                .setValue(Arrays.asList(PreparedStatementTable.Type.Int.name(), PreparedStatementTable.Type.String.name()));
        properties.preparedStatementTable.values.setValue(Arrays.<Object> asList(4, "momo"));

        JDBCRowSink sink = new JDBCRowSink();

        sink.initialize(null, properties);
        ValidationResult result = sink.validate(null);
        Assert.assertTrue(result.getStatus() == ValidationResult.Result.OK);

        WriteOperation operation = sink.createWriteOperation();
        JDBCRowWriter writer = (JDBCRowWriter) operation.createWriter(null);

        try {
            writer.open("wid");

            IndexedRecord r1 = new GenericData.Record(properties.main.schema.getValue());
            r1.put(0, 4);
            r1.put(1, "xiaoming");
            writer.write(r1);

            DBTestUtils.assertSuccessRecord(writer, r1);

            IndexedRecord r2 = new GenericData.Record(properties.main.schema.getValue());
            r2.put(0, 5);
            r2.put(1, "xiaobai");
            writer.write(r2);

            DBTestUtils.assertSuccessRecord(writer, r2);

            writer.close();
        } finally {
            writer.close();
        }

        TJDBCInputDefinition definition1 = new TJDBCInputDefinition();

        TJDBCInputProperties properties1 = DBTestUtils.createCommonJDBCInputProperties(allSetting, definition1);

        List<IndexedRecord> records = DBTestUtils.fetchDataByReaderFromTable(DBTestUtils.getTablename(), schema, definition1,
                properties1);

        assertThat(records, hasSize(5));
        Assert.assertEquals("4", records.get(3).get(0));
        Assert.assertEquals("momo", records.get(3).get(1));
        Assert.assertEquals("4", records.get(4).get(0));
        Assert.assertEquals("momo", records.get(4).get(1));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void test_reject_as_output() throws Exception {
        TJDBCRowDefinition definition = new TJDBCRowDefinition();
        TJDBCRowProperties properties = DBTestUtils.createCommonJDBCRowProperties(allSetting, definition);

        Schema schema = DBTestUtils.createTestSchema();
        properties.main.schema.setValue(schema);
        properties.updateOutputSchemas();

        properties.tableSelection.tablename.setValue(DBTestUtils.getTablename());
        properties.sql.setValue("insert into test values(?,?)");
        properties.dieOnError.setValue(false);
        randomCommit(properties);

        properties.usePreparedStatement.setValue(true);
        properties.preparedStatementTable.indexs.setValue(Arrays.asList(1, 2));
        properties.preparedStatementTable.types
                .setValue(Arrays.asList(PreparedStatementTable.Type.Int.name(), PreparedStatementTable.Type.String.name()));
        properties.preparedStatementTable.values.setValue(Arrays.<Object> asList(4, "a too long value"));

        JDBCRowSink sink = new JDBCRowSink();

        sink.initialize(null, properties);
        ValidationResult result = sink.validate(null);
        Assert.assertTrue(result.getStatus() == ValidationResult.Result.OK);

        WriteOperation operation = sink.createWriteOperation();
        JDBCRowWriter writer = (JDBCRowWriter) operation.createWriter(null);

        try {
            writer.open("wid");

            IndexedRecord r1 = new GenericData.Record(properties.main.schema.getValue());
            r1.put(0, 4);
            r1.put(1, "xiaoming");
            writer.write(r1);

            List<IndexedRecord> rejects = writer.getRejectedWrites();
            assertThat(rejects, hasSize(1));
            IndexedRecord reject = rejects.get(0);
            Assert.assertEquals(4, reject.get(0));
            Assert.assertEquals("xiaoming", reject.get(1));
            Assert.assertNotNull(reject.get(2));
            Assert.assertNotNull(reject.get(3));
            assertThat(writer.getSuccessfulWrites(), empty());

            IndexedRecord r2 = new GenericData.Record(properties.main.schema.getValue());
            r2.put(0, 5);
            r2.put(1, "xiaobai");
            writer.write(r2);

            rejects = writer.getRejectedWrites();
            assertThat(rejects, hasSize(1));
            reject = rejects.get(0);
            Assert.assertEquals(5, reject.get(0));
            Assert.assertEquals("xiaobai", reject.get(1));
            Assert.assertNotNull(reject.get(2));
            Assert.assertNotNull(reject.get(3));
            assertThat(writer.getSuccessfulWrites(), empty());

            writer.close();
        } finally {
            writer.close();
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void test_die_on_error_as_output() throws Exception {
        TJDBCRowDefinition definition = new TJDBCRowDefinition();
        TJDBCRowProperties properties = DBTestUtils.createCommonJDBCRowProperties(allSetting, definition);

        Schema schema = DBTestUtils.createTestSchema();
        properties.main.schema.setValue(schema);
        properties.updateOutputSchemas();

        properties.tableSelection.tablename.setValue(DBTestUtils.getTablename());
        properties.sql.setValue("insert into test values(?,?)");
        properties.dieOnError.setValue(true);
        randomCommit(properties);

        properties.usePreparedStatement.setValue(true);
        properties.preparedStatementTable.indexs.setValue(Arrays.asList(1, 2));
        properties.preparedStatementTable.types
                .setValue(Arrays.asList(PreparedStatementTable.Type.Int.name(), PreparedStatementTable.Type.String.name()));
        properties.preparedStatementTable.values.setValue(Arrays.<Object> asList(4, "a too long value"));

        JDBCRowSink sink = new JDBCRowSink();

        sink.initialize(null, properties);
        ValidationResult result = sink.validate(null);
        Assert.assertTrue(result.getStatus() == ValidationResult.Result.OK);

        WriteOperation operation = sink.createWriteOperation();
        JDBCRowWriter writer = (JDBCRowWriter) operation.createWriter(null);

        try {
            writer.open("wid");

            IndexedRecord r1 = new GenericData.Record(properties.main.schema.getValue());
            r1.put(0, 4);
            r1.put(1, "xiaoming");
            writer.write(r1);

            writer.close();
        } catch (ComponentException e) {
            Assert.assertNotNull(e.getCause());
        } finally {
            writer.close();
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void test_propagate_query_result_set_as_output() throws Exception {
        TJDBCRowDefinition definition = new TJDBCRowDefinition();
        TJDBCRowProperties properties = DBTestUtils.createCommonJDBCRowProperties(allSetting, definition);

        Schema schema = DBTestUtils.createTestSchema5();
        properties.main.schema.setValue(schema);
        properties.updateOutputSchemas();

        properties.tableSelection.tablename.setValue(DBTestUtils.getTablename());
        properties.sql.setValue("select id, name from test where id = ?");
        properties.dieOnError.setValue(true);
        randomCommit(properties);

        properties.usePreparedStatement.setValue(true);
        properties.preparedStatementTable.indexs.setValue(Arrays.asList(1));
        properties.preparedStatementTable.types.setValue(Arrays.asList(PreparedStatementTable.Type.Int.name()));
        properties.preparedStatementTable.values.setValue(Arrays.<Object> asList(3));

        properties.propagateQueryResultSet.setValue(true);
        properties.beforeUseColumn();
        properties.useColumn.setValue(properties.useColumn.getPossibleValues().get(2).toString());

        JDBCRowSink sink = new JDBCRowSink();

        sink.initialize(null, properties);
        ValidationResult result = sink.validate(null);
        Assert.assertTrue(result.getStatus() == ValidationResult.Result.OK);

        WriteOperation operation = sink.createWriteOperation();
        JDBCRowWriter writer = (JDBCRowWriter) operation.createWriter(null);

        try {
            writer.open("wid");

            IndexedRecord r1 = new GenericData.Record(properties.main.schema.getValue());
            r1.put(0, 4);
            r1.put(1, "xiaoming");
            writer.write(r1);

            assertThat(writer.getRejectedWrites(), empty());
            List<IndexedRecord> successfulWrites = writer.getSuccessfulWrites();
            assertThat(successfulWrites, hasSize(1));
            IndexedRecord successRecord = successfulWrites.get(0);
            Assert.assertEquals(4, successRecord.get(0));
            Assert.assertEquals("xiaoming", successRecord.get(1));

            ResultSet resultSet = (ResultSet) successRecord.get(2);
            resultSet.next();
            Assert.assertEquals(3, resultSet.getInt(1));
            Assert.assertEquals("dabao", resultSet.getString(2));
            resultSet.close();

            IndexedRecord r2 = new GenericData.Record(properties.main.schema.getValue());
            r2.put(0, 5);
            r2.put(1, "xiaobai");
            writer.write(r2);

            assertThat(writer.getRejectedWrites(), empty());
            successfulWrites = writer.getSuccessfulWrites();
            assertThat(successfulWrites, hasSize(1));
            successRecord = successfulWrites.get(0);
            Assert.assertEquals(5, successRecord.get(0));
            Assert.assertEquals("xiaobai", successRecord.get(1));

            resultSet = (ResultSet) successRecord.get(2);
            resultSet.next();
            Assert.assertEquals(3, resultSet.getInt(1));
            Assert.assertEquals("dabao", resultSet.getString(2));
            resultSet.close();

            writer.close();
        } finally {
            writer.close();
        }
    }

    private String randomCommit(TJDBCRowProperties properties) {
        properties.commitEvery.setValue(DBTestUtils.randomInt());
        return new StringBuilder().append("commitEvery:").append(properties.commitEvery.getValue()).toString();
    }

}
