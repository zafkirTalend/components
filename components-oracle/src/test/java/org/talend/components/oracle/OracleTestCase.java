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
package org.talend.components.oracle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.oracle.runtime.OracleTemplate;
import org.talend.components.oracle.toracleinput.TOracleInputDefinition;
import org.talend.components.oracle.toracleinput.TOracleInputProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.schema.Schema;
import org.talend.daikon.schema.SchemaElement;

public class OracleTestCase {

    private static DBConnectionProperties properties;
    
    private static BoundedSource source;

    private static OracleTemplate template = new OracleTemplate();

    @BeforeClass
    public static void init() throws Exception {
        java.util.Properties props = new java.util.Properties();
        try (InputStream is = OracleTestCase.class.getClassLoader().getResourceAsStream("connection.properties")) {
            props = new java.util.Properties();
            props.load(is);
        }

        initSource(props);
        
        Connection conn = template.connect(properties);
        
        try {
            dropTestTable(conn);
        } catch(Exception e) {
            //do nothing
        }
        createTestTable(conn);
        loadTestData(conn);
        
        conn.close();
    }
    
    private static void initSource(java.util.Properties props) {
        ComponentDefinition definition = new TOracleInputDefinition();
        TOracleInputProperties properties = (TOracleInputProperties) definition.createRuntimeProperties();

        properties.connection.setValue("host", props.getProperty("host"));
        properties.connection.setValue("port", props.getProperty("port"));
        properties.connection.setValue("database", props.getProperty("database"));
        properties.connection.setValue("dbschema", props.getProperty("dbschema"));
        properties.connection.userPassword.setValue("userId", props.getProperty("userId"));
        properties.connection.userPassword.setValue("password", props.getProperty("password"));

        properties.setValue("tablename", props.getProperty("tablename"));
        properties.setValue("sql", props.getProperty("sql"));
        
        OracleTestCase.properties = properties.getConnectionProperties();

        source = (BoundedSource) ((org.talend.components.api.component.InputComponentDefinition) definition).getRuntime();
        source.initialize(null, properties);
    }
    
    @AfterClass
    public static void destory() throws Exception {
        properties = null;
        source = null;
        template = null;
    }

    @Test
    public void validate() {
        ValidationResult result = source.validate(null);
        assertTrue(result.getStatus() == ValidationResult.Result.OK);
    }
    
    @Test
    public void testGetSchemaNames() throws Exception {
        List<NamedThing> schemaNames = source.getSchemaNames(null);
        assertTrue(schemaNames != null);
        assertTrue(!schemaNames.isEmpty());
        
        boolean exists = false;
        for(NamedThing name : schemaNames) {
            if("TEST".equals(name.getName())) {
                exists = true;
                break;
            }
        }
        
        assertTrue(exists);
    }

    @Test
    public void testGetSchema() throws Exception {
        Schema schema = source.getSchema(null, "TEST");
        assertTrue(schema.getRoot() != null);
        assertTrue(schema.getRoot().getChildren() != null);
        assertTrue(!schema.getRoot().getChildren().isEmpty());
        
        SchemaElement element = schema.getRoot().getChildren().get(0);
        assertEquals("ID", element.getName());
        assertEquals(SchemaElement.Type.DECIMAL, element.getType());
        
        element = schema.getRoot().getChildren().get(1);
        assertEquals("NAME", element.getName());
        assertEquals(SchemaElement.Type.STRING, element.getType());
    }
    
    @Test
    public void testReader() throws Exception {
        BoundedReader reader = source.createReader(null);

        reader.start();

        Map<String, Object> row = (Map) reader.getCurrent();
        String id = (String) row.get("ID");
        String name = (String) row.get("NAME");

        assertEquals("1", id);
        assertEquals("wangwei", name);

        reader.advance();

        row = (Map) reader.getCurrent();
        id = (String) row.get("ID");
        name = (String) row.get("NAME");

        assertEquals("2", id);
        assertEquals("gaoyan", name);

        reader.advance();

        row = (Map) reader.getCurrent();
        id = (String) row.get("ID");
        name = (String) row.get("NAME");

        assertEquals("3", id);
        assertEquals("dabao", name);
    }

    private static void createTestTable(Connection conn) throws Exception {
        Statement statement = conn.createStatement();
        statement.execute("create table TEST (ID int, NAME varchar(64))");
        statement.close();
    }

    private static void dropTestTable(Connection conn) throws Exception {
        Statement statement = conn.createStatement();
        statement.execute("drop table TEST");
        statement.close();
    }

    private static void loadTestData(Connection conn) throws Exception {
        PreparedStatement statement = conn.prepareStatement("insert into TEST values(?,?)");

        statement.setInt(1, 1);
        statement.setString(2, "wangwei");

        statement.executeUpdate();

        statement.setInt(1, 2);
        statement.setString(2, "gaoyan");

        statement.executeUpdate();

        statement.setInt(1, 3);
        statement.setString(2, "dabao");

        statement.executeUpdate();

        statement.close();

        conn.commit();
    }

}
