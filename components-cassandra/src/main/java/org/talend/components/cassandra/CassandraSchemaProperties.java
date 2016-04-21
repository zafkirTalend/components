package org.talend.components.cassandra;

import org.apache.avro.Schema;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.HasSchemaProperty;
import org.talend.components.cassandra.runtime.CassandraSourceOrSink;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.talend.daikon.properties.PropertyFactory.newEnum;

public class CassandraSchemaProperties extends ComponentProperties implements HasSchemaProperty {

    /**
     * named constructor to be used is these properties are nested in other properties. Do not subclass this method for
     * initialization, use {@link #init()} instead.
     *
     * @param name
     */
    public CassandraSchemaProperties(String name, CassandraConnectionProperties connectionProperties) {
        super(name);
        this.connectionProperties = connectionProperties;
    }

    private CassandraConnectionProperties connectionProperties;

    public Property keyspace = newEnum("keyspace");
    public Property columnFamily = newEnum("columnFamily");
    public SchemaProperties schema = new SchemaProperties("schema");

    @Override
    public void setupProperties() {
        super.setupProperties();
        //FIXME what's the meaning of setTaggedValue
        //schema.schema.setTaggedValue(StudioConstants.CONNECTOR_TYPE_SCHEMA_KEY, Connector.ConnectorType.FLOW);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form schemaForm = new Form(this, Form.MAIN);
        schemaForm.addRow(Widget.widget(keyspace).setWidgetType(Widget.WidgetType.NAME_SELECTION_AREA));
        schemaForm.addRow(Widget.widget(columnFamily).setWidgetType(Widget.WidgetType.NAME_SELECTION_AREA));
        refreshLayout(schemaForm);//FIXME why need to invoke refreshLayout here? refer to SalesforceModuleProperties

        Form schemaRefForm = new Form(this, Form.REFERENCE);
        schemaRefForm.addRow(Widget.widget(keyspace).setWidgetType(Widget.WidgetType.NAME_SELECTION_REFERENCE));
        schemaRefForm.addRow(Widget.widget(columnFamily).setWidgetType(Widget.WidgetType.NAME_SELECTION_REFERENCE));
        schemaRefForm.addRow(schema.getForm(Form.REFERENCE));//FIXME why need schema ref form here but don't need schema main form above
        refreshLayout(schemaRefForm);
    }

    public ValidationResult beforeKeyspace() {
        CassandraSourceOrSink cassandraSourceOrSink = new CassandraSourceOrSink();
        cassandraSourceOrSink.initialize(null, connectionProperties);
        try {
            List<NamedThing> keyspaceNames = cassandraSourceOrSink.getKeyspaceNames(null);
            keyspace.setPossibleValues(keyspaceNames);
        } catch (IOException e) {
            return new ValidationResult().setStatus(ValidationResult.Result.ERROR).setMessage(e.getMessage());
        }
        return ValidationResult.OK;
    }

    public ValidationResult beforeColumnFamily() {
        CassandraSourceOrSink cassandraSourceOrSink = new CassandraSourceOrSink();
        cassandraSourceOrSink.initialize(null, connectionProperties);
        try {
            List<NamedThing> tableNames = cassandraSourceOrSink.getTableNames(null, keyspace.getStringValue());
            columnFamily.setPossibleValues(tableNames);
        } catch (IOException e) {
            return new ValidationResult().setStatus(ValidationResult.Result.ERROR).setMessage(e.getMessage());
        }
        return ValidationResult.OK;
    }

    public ValidationResult afterColumnFamily() {
        CassandraSourceOrSink cassandraSourceOrSink = new CassandraSourceOrSink();
        cassandraSourceOrSink.initialize(null, connectionProperties);
        try {
            schema.schema.setValue(cassandraSourceOrSink.getSchema(null, keyspace.getStringValue(), columnFamily.getStringValue()));
        } catch (IOException e) {
            return new ValidationResult().setStatus(ValidationResult.Result.ERROR).setMessage(e.getMessage());
        }
        return ValidationResult.OK;
    }

    @Override
    public List<Schema> getSchemas() {
        return Arrays.asList(new Schema[]{new Schema.Parser().parse(schema.schema.getStringValue())});
    }

    @Override
    public void setSchemas(List<Schema> schemas) {
        schema.schema.setValue(schemas.get(0));
    }

}