package org.talend.components.oracle.runtime;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.oracle.DBConnectionProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.properties.PropertyFactory;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.schema.Schema;
import org.talend.daikon.schema.SchemaElement;
import org.talend.daikon.schema.SchemaFactory;

public class OracleSource extends DBSource {

    OracleTemplate template = new OracleTemplate();//no state object
    
    @Override
    public ValidationResult validate(RuntimeContainer adaptor) {
        ValidationResult vr = new ValidationResult();
        Connection conn = null;
        try {
            DBConnectionProperties dbprops = properties.getConnectionProperties();
            conn = template.connect(dbprops);
        } catch (Exception ex) {
            fillValidationResult(vr, ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return vr;
    }

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer adaptor) throws IOException {
        List<NamedThing> result = new ArrayList<>();
        Connection conn = null;
        try {
            DBConnectionProperties dbprops = properties.getConnectionProperties();
            conn = template.connect(dbprops);
            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet resultset = metadata.getTables(null, dbprops.dbschema.getStringValue(), null, new String[] { "TABLE" });
            while (resultset.next()) {
                String tablename = resultset.getString("TABLE_NAME");
                result.add(new SimpleNamedThing(tablename, tablename));
            }
        } catch (Exception e) {
            throw new ComponentException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public Schema getSchema(RuntimeContainer adaptor, String schemaName) throws IOException {
        Schema schema = SchemaFactory.newSchema();
        SchemaElement root = SchemaFactory.newSchemaElement("Root");
        schema.setRoot(root);
        
        Connection conn = null;

        try {
            DBConnectionProperties dbprops = properties.getConnectionProperties();
            conn = template.connect(dbprops);
            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet resultset = metadata.getColumns(null, dbprops.dbschema.getStringValue(), schemaName, null);
            while (resultset.next()) {
                String columnname = resultset.getString("COLUMN_NAME");
                SchemaElement child = PropertyFactory.newProperty(columnname);
                setupSchemaElement(resultset, child);
                root.addChild(child);
            }
        } catch (Exception e) {
            throw new ComponentException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return schema;
    }

    @Override
    protected DBTemplate getDBTemplate() {
        return new OracleTemplate();
    }

    private void setupSchemaElement(ResultSet resultset, SchemaElement element) throws SQLException {
        int dbtype = resultset.getInt("DATA_TYPE");
        if (java.sql.Types.VARCHAR == dbtype || java.sql.Types.CHAR == dbtype) {
            element.setType(SchemaElement.Type.STRING);
        } else if (java.sql.Types.BOOLEAN == dbtype) {
            element.setType(SchemaElement.Type.BOOLEAN);
        } else if (java.sql.Types.INTEGER == dbtype) {
            element.setType(SchemaElement.Type.INT);
        } else if (java.sql.Types.DATE == dbtype) {
            element.setType(SchemaElement.Type.DATE);
            element.setPattern("\"yyyy-MM-dd\"");
        } else if (java.sql.Types.TIMESTAMP == dbtype) {
            element.setType(SchemaElement.Type.DATETIME);
            element.setPattern("\"yyyy-MM-dd\'T\'HH:mm:ss\'.000Z\'\"");
        } else if (java.sql.Types.DOUBLE == dbtype) {
            element.setType(SchemaElement.Type.DOUBLE);
        } else if (java.sql.Types.DECIMAL == dbtype) {
            element.setType(SchemaElement.Type.DECIMAL);
        }

        element.setNullable(!"NO".equals(resultset.getString("IS_NULLABLE")));
    }

}
