// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.jdbc.common.DBTestUtils;
import org.talend.components.jdbc.runtime.JDBCSourceOrSink;
import org.talend.components.jdbc.runtime.setting.AllSetting;
import org.talend.components.jdbc.tjdbcconnection.TJDBCConnectionDefinition;
import org.talend.components.jdbc.tjdbcconnection.TJDBCConnectionProperties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.runtime.RuntimeInfo;
import org.talend.daikon.runtime.RuntimeUtil;
import org.talend.daikon.sandbox.SandboxedInstance;

public class JDBCConnectionTestIT {

    private JDBCSourceOrSink sourceOrSink = null;

    public static AllSetting allSetting;

    @BeforeClass
    public static void init() throws Exception {
        java.util.Properties props = new java.util.Properties();
        try (InputStream is = JDBCConnectionTestIT.class.getClassLoader().getResourceAsStream("connection.properties")) {
            props = new java.util.Properties();
            props.load(is);
        }

        allSetting = DBTestUtils.createAllSetting(props);
    }

    @After
    public void release() {
        if (sourceOrSink == null) {
            return;
        }

        try {
            Connection conn = sourceOrSink.getConnection(null);
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            // close quietly
        } finally {
            DBTestUtils.shutdownDBIfNecessary();
        }
    }

    // we want to test the dynamic library loading with the field driverPath, but fail, so seems that no way to test it
    // now
    @Ignore
    @Test
    public void testDynamicLibLoad() {
        TJDBCConnectionDefinition definition = new TJDBCConnectionDefinition();
        TJDBCConnectionProperties properties = DBTestUtils.createCommonJDBCConnectionProperties(allSetting, definition);

        RuntimeInfo runtimeInfo = definition.getRuntimeInfo(properties, ConnectorTopology.NONE);
        try (SandboxedInstance sandboxedInstance = RuntimeUtil.createRuntimeClass(runtimeInfo,
                definition.getClass().getClassLoader())) {
            sourceOrSink = (JDBCSourceOrSink) sandboxedInstance.getInstance();
            sourceOrSink.initialize(null, properties);
            ValidationResult result = sourceOrSink.validate(null);
            assertTrue(result.getStatus() == ValidationResult.Result.OK);
            try {
                Connection conn = sourceOrSink.getConnection(null);
                assertTrue(conn == sourceOrSink.getConnection(null));
                assertTrue(!conn.isClosed());
            } catch (ClassNotFoundException | SQLException e) {
                Assert.fail(e.getMessage());
            }
        }
    }

    public void testConnection() {
        TJDBCConnectionDefinition definition = new TJDBCConnectionDefinition();
        TJDBCConnectionProperties properties = DBTestUtils.createCommonJDBCConnectionProperties(allSetting, definition);

        sourceOrSink = new JDBCSourceOrSink();
        sourceOrSink.initialize(null, properties);
        ValidationResult result = sourceOrSink.validate(null);
        assertTrue(result.getStatus() == ValidationResult.Result.OK);
        try {
            Connection conn = sourceOrSink.getConnection(null);
            assertTrue(conn == sourceOrSink.getConnection(null));
            assertTrue(!conn.isClosed());
        } catch (ClassNotFoundException | SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testConnectionWithWrongDriver() {
        TJDBCConnectionDefinition definition = new TJDBCConnectionDefinition();
        TJDBCConnectionProperties properties = (TJDBCConnectionProperties) definition.createRuntimeProperties();

        properties.connection.driverClass.setValue("wrongDriver");
        properties.connection.jdbcUrl.setValue(allSetting.getJdbcUrl());
        properties.connection.userPassword.userId.setValue(allSetting.getUsername());
        properties.connection.userPassword.password.setValue(allSetting.getPassword());

        sourceOrSink = new JDBCSourceOrSink();
        sourceOrSink.initialize(null, properties);
        ValidationResult result = sourceOrSink.validate(null);
        assertTrue(result.getStatus() == ValidationResult.Result.ERROR);
        assertTrue(result.getMessage() != null && !result.getMessage().isEmpty());
    }

    @Test
    public void testConnectionWithWrongURL() {
        TJDBCConnectionDefinition definition = new TJDBCConnectionDefinition();
        TJDBCConnectionProperties properties = (TJDBCConnectionProperties) definition.createRuntimeProperties();

        properties.connection.driverClass.setValue(allSetting.getDriverClass());
        properties.connection.jdbcUrl.setValue("wrongUrl");
        properties.connection.userPassword.userId.setValue(allSetting.getUsername());
        properties.connection.userPassword.password.setValue(allSetting.getPassword());

        sourceOrSink = new JDBCSourceOrSink();
        sourceOrSink.initialize(null, properties);
        ValidationResult result = sourceOrSink.validate(null);
        assertTrue(result.getStatus() == ValidationResult.Result.ERROR);
        assertTrue(result.getMessage() != null && !result.getMessage().isEmpty());
    }

    @Test
    public void testNotAutoCommit() {
        TJDBCConnectionDefinition definition = new TJDBCConnectionDefinition();
        TJDBCConnectionProperties properties = DBTestUtils.createCommonJDBCConnectionProperties(allSetting, definition);

        properties.useAutoCommit.setValue(true);
        properties.autocommit.setValue(false);

        sourceOrSink = new JDBCSourceOrSink();
        sourceOrSink.initialize(null, properties);
        ValidationResult result = sourceOrSink.validate(null);
        assertTrue(result.getStatus() == ValidationResult.Result.OK);

        try {
            Connection conn = sourceOrSink.getConnection(null);
            assertTrue(!conn.getAutoCommit());
            assertTrue(!conn.isClosed());
        } catch (ClassNotFoundException | SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testAutoCommit() {
        TJDBCConnectionDefinition definition = new TJDBCConnectionDefinition();
        TJDBCConnectionProperties properties = DBTestUtils.createCommonJDBCConnectionProperties(allSetting, definition);

        properties.useAutoCommit.setValue(true);
        properties.autocommit.setValue(true);

        sourceOrSink = new JDBCSourceOrSink();
        sourceOrSink.initialize(null, properties);
        ValidationResult result = sourceOrSink.validate(null);
        assertTrue(result.getStatus() == ValidationResult.Result.OK);

        try {
            Connection conn = sourceOrSink.getConnection(null);
            assertTrue(conn.getAutoCommit());
            assertTrue(!conn.isClosed());
        } catch (ClassNotFoundException | SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

}
