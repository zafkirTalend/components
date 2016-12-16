// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.jdbc.dataprep;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.components.jdbc.common.DBTestUtils;
import org.talend.components.jdbc.dataset.JDBCDatasetProperties;
import org.talend.components.jdbc.datastore.JDBCDatastoreDefinition;
import org.talend.components.jdbc.datastore.JDBCDatastoreProperties;
import org.talend.components.jdbc.runtime.dataprep.JDBCDatasetRuntime;
import org.talend.components.jdbc.runtime.setting.AllSetting;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.java8.Consumer;
import org.talend.daikon.properties.test.PropertiesTestUtils;

public class JDBCDatasetTestIT {

    public static AllSetting allSetting;

    @BeforeClass
    public static void beforeClass() throws Exception {
        PropertiesTestUtils.setupPaxUrlFromMavenLaunch();
        allSetting = DBTestUtils.createAllSetting();
        DBTestUtils.createTableWithDateType(allSetting);
        DBTestUtils.truncateTableAndLoadDataWithDateType(allSetting);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        DBTestUtils.releaseResource(allSetting);
    }

    @Test
    public void testUpdateSchema() {
        JDBCDatasetProperties dataset = createDatasetProperties(true);

        Schema schema = dataset.main.schema.getValue();

        Assert.assertNotNull(schema);
        DBTestUtils.testMetadata(schema.getFields());
    }

    @Test
    public void testGetSchema() {
        JDBCDatasetProperties dataset = createDatasetProperties(false);

        JDBCDatasetRuntime runtime = new JDBCDatasetRuntime();
        runtime.initialize(null, dataset);
        Schema schema = runtime.getSchema();

        Assert.assertNotNull(schema);
        DBTestUtils.testMetadata(schema.getFields());
    }

    @Test
    public void testGetSampleWithValidDesignSchema() {
        JDBCDatasetProperties dataset = createDatasetProperties(true);
        getSampleAction(dataset);
    }

    @Test
    public void testGetSampleWithoutDesignSchema() {
        JDBCDatasetProperties dataset = createDatasetProperties(false);
        getSampleAction(dataset);
    }

    private void getSampleAction(JDBCDatasetProperties dataset) {
        JDBCDatasetRuntime runtime = new JDBCDatasetRuntime();
        runtime.initialize(null, dataset);
        final List<IndexedRecord> records = new ArrayList<IndexedRecord>();
        Consumer<IndexedRecord> storeTheRecords = new Consumer<IndexedRecord>() {

            @Override
            public void accept(IndexedRecord data) {
                records.add(data);
            }
        };

        runtime.getSample(2, storeTheRecords);
        Assert.assertEquals(2, records.size());

        Schema schema = runtime.getSchema();
        Assert.assertNotNull(schema);

        Schema dateFieldSchema = schema.getFields().get(2).schema();
        Assert.assertNotNull(dateFieldSchema);

        // first row
        IndexedRecord record = records.get(0);
        Assert.assertEquals(1, record.get(0));
        Assert.assertEquals("wangwei", record.get(1));

        Date date = null;
        Object value = record.get(2);
        date = convert2DateIfDateType(dateFieldSchema, value);
        Assert.assertEquals("2012-11-12 12:12:12", DBTestUtils.DATE_FORMATTER.format(date));

        // second row
        record = records.get(1);
        Assert.assertEquals(2, record.get(0));
        Assert.assertEquals("gaoyan", record.get(1));

        value = record.get(2);
        date = convert2DateIfDateType(dateFieldSchema, value);
        Assert.assertEquals("1911-11-12 12:12:12", DBTestUtils.DATE_FORMATTER.format(date));
    }

    /**
     * now we store the date type in AVRO like this :
     * 
     * in AVRO schema : we store the some information to show it's a date type.
     * in AVRO record : we store the long value for the date object.
     * 
     * when you use the AVRO record in the platform like studio, should do some convert action to parse the long value to date
     * object if the AVRO schema show it's a date type object.
     * 
     * Please see : https://jira.talendforge.org/browse/TDI-37989
     * 
     * TODO : adapter the new date type feature, now it's enough to show the usage in the dataprep platform
     */
    private Date convert2DateIfDateType(Schema dateFieldSchema, Object value) {
        if (isDateField(dateFieldSchema)) {
            return new Date((Long) value);
        } else {
            Assert.fail("expect date type, but not");
        }

        return null;
    }

    private boolean isDateField(Schema dateFieldSchema) {
        return AvroUtils.isSameType(AvroUtils.unwrapIfNullable(dateFieldSchema), AvroUtils._date());
    }

    private JDBCDatasetProperties createDatasetProperties(boolean updateSchema) {
        JDBCDatastoreDefinition def = new JDBCDatastoreDefinition();
        JDBCDatastoreProperties datastore = new JDBCDatastoreProperties("datastore");

        datastore.dbTypes.setValue("DERBY");
        datastore.afterDbTypes();

        datastore.jdbcUrl.setValue(allSetting.getJdbcUrl());
        datastore.userId.setValue(allSetting.getUsername());
        datastore.password.setValue(allSetting.getPassword());

        JDBCDatasetProperties dataset = (JDBCDatasetProperties) def.createDatasetProperties(datastore);
        dataset.sql.setValue(DBTestUtils.getSQL());

        if (updateSchema) {
            dataset.updateSchema();
        }
        return dataset;
    }

}
