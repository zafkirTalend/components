package org.talend.components.cassandra;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.cassandra.runtime.CassandraSourceOrSink;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

import java.io.IOException;
import java.util.List;

import static org.talend.daikon.properties.PropertyFactory.newEnum;

public class CassandraSchemaProperties extends ComponentProperties {

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

    public static final String KEYSPACE = "keyspace";
    public Property keyspace = newEnum(KEYSPACE);
    public static final String COLUMN_FAMILY = "columnFamily";
    public Property columnFamily = newEnum(COLUMN_FAMILY);
    public SchemaProperties main = new SchemaProperties("main");

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
        schemaRefForm.addRow(main.getForm(Form.REFERENCE));//FIXME why need schema ref form here but don't need schema main form above
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
            main.schema.setValue(cassandraSourceOrSink.getSchema(null, keyspace.getStringValue(), columnFamily.getStringValue()));
        } catch (IOException e) {
            return new ValidationResult().setStatus(ValidationResult.Result.ERROR).setMessage(e.getMessage());
        }
        return ValidationResult.OK;
    }

}