package org.talend.components.processing.normalize;

import org.apache.avro.Schema;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.ISchemaListener;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.properties.presentation.Form;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by zafkir on 19/06/2017.
 */
public class NormalizeProperties extends FixedConnectorsComponentProperties {

    public transient PropertyPathConnector MAIN_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "main");

    public SchemaProperties main = new SchemaProperties("main") {

        @SuppressWarnings("unused")
        public void afterSchema() {
            updateOutputSchemas();
        }
    };

    // output schema
    public transient PropertyPathConnector FLOW_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "schemaFlow");

    public SchemaProperties schemaFlow = new SchemaProperties("schemaFlow");

    // reject schema
    public transient PropertyPathConnector REJECT_CONNECTOR = new PropertyPathConnector(Connector.REJECT_NAME, "schemaReject");

    public SchemaProperties schemaReject = new SchemaProperties("schemaReject");

    public transient ISchemaListener schemaListener;

    public NormalizeProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        schemaListener = new ISchemaListener() {

            @Override
            public void afterSchema() {
                updateOutputSchemas();
            }

        };
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (form.getName().equals(Form.MAIN)) {
        }
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        HashSet<PropertyPathConnector> connectors = new LinkedHashSet<PropertyPathConnector>();
        if (isOutputConnection) {
            // output schemas
            connectors.add(FLOW_CONNECTOR);
            connectors.add(REJECT_CONNECTOR);
        } else {
            // input schema
            connectors.add(MAIN_CONNECTOR);
        }
        return connectors;
    }

    protected void updateOutputSchemas() {
        // Copy the "main" schema into the "flow" schema
        Schema inputSchema = main.schema.getValue();
        schemaFlow.schema.setValue(inputSchema);

        // Copy the "main" schema in to the "reject" schema
        schemaReject.schema.setValue(inputSchema);
    }

    /**
     * TODO: This method will be used once the field autocompletion will be implemented
     */
    private Boolean isString(Schema.Type type) {
        return Schema.Type.STRING.equals(type);
    }

    /**
     * TODO: This method will be used once the field autocompletion will be implemented
     */
    private Boolean isNumerical(Schema.Type type) {
        return Schema.Type.INT.equals(type) || Schema.Type.LONG.equals(type) //
                || Schema.Type.DOUBLE.equals(type) || Schema.Type.FLOAT.equals(type);
    }
}
