package org.talend.components.oracle.runtime;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.oracle.DBConnectionProperties;
import org.talend.components.oracle.toracleinput.TOracleInputProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.properties.ValidationResult;

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
        Connection conn = null;

        try {
            DBConnectionProperties dbprops = properties.getConnectionProperties();
            conn = template.connect(dbprops);
            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet resultset = metadata.getColumns(null, dbprops.dbschema.getStringValue(), schemaName, null);
            return OracleAvroRegistry.get().inferSchema(resultset);
        } catch (Exception e) {
            throw new ComponentException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected DBTemplate getDBTemplate() {
        return new OracleTemplate();
    }

    @Override
    public Schema getPossibleSchemaFromProperties(RuntimeContainer adaptor) throws IOException {
        return getSchema(adaptor, ((TOracleInputProperties) properties).tablename.getStringValue());
    }

    @Override
    public Schema getSchemaFromProperties(RuntimeContainer adaptor) throws IOException {
        String schemaString = null;

        if (properties instanceof TOracleInputProperties) {
            schemaString = ((TOracleInputProperties) properties).schema.schema.getStringValue();
        }

        return schemaString == null ? null : new Schema.Parser().parse(schemaString);
    }

}
