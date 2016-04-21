package org.talend.components.cassandra;

import org.apache.avro.Schema;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.ConnectionPropertiesProvider;
import org.talend.components.api.properties.HasSchemaProperty;
import org.talend.daikon.properties.presentation.Form;

import java.util.List;

public class CassandraIOBasedProperties extends ComponentProperties implements ConnectionPropertiesProvider<CassandraConnectionProperties>, HasSchemaProperty {
    /**
     * named constructor to be used is these properties are nested in other properties. Do not subclass this method for
     * initialization, use {@link #init()} instead.
     *
     * @param name
     */
    public CassandraIOBasedProperties(String name) {
        super(name);
    }

    private CassandraConnectionProperties connectionProperties;
    private CassandraSchemaProperties schemaProperties;

    public CassandraSchemaProperties getSchemaProperties() {
        return schemaProperties;
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        // Allow for subclassing
        connectionProperties = new CassandraConnectionProperties("connection");
        schemaProperties = new CassandraSchemaProperties("table", connectionProperties);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(connectionProperties.getForm(Form.REFERENCE));
        mainForm.addRow(schemaProperties.getForm(Form.REFERENCE));
    }

    @Override
    public CassandraConnectionProperties getConnectionProperties() {
        return connectionProperties;
    }

    @Override
    public List<Schema> getSchemas() {
        return schemaProperties.getSchemas();
    }

    @Override
    public void setSchemas(List<Schema> schemas) {
        schemaProperties.setSchemas(schemas);
    }
}
