package org.talend.components.processing.normalize;

import org.apache.avro.Schema;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.ISchemaListener;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by zafkir on 19/06/2017.
 */
public class NormalizeProperties extends FixedConnectorsComponentProperties {

    public transient PropertyPathConnector MAIN_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "main");

    // output schema
    public transient PropertyPathConnector FLOW_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "schemaFlow");

    public SchemaProperties schemaFlow = new SchemaProperties("schemaFlow");

    public SchemaProperties main = new SchemaProperties("main") {

        @SuppressWarnings("unused")
        public void afterSchema() {
            updateOutputSchemas();
        }
    };

    public Property<String> columnName = PropertyFactory.newString("columnName").setRequired();

    public Property<String> fieldSeparator = PropertyFactory.newString("fieldSeparator", ",").setRequired();

    public Property<Boolean> csvOption = PropertyFactory.newBoolean("csvOption", false);

    public Property<String> escapeMode = PropertyFactory.newString("escapeMode", NormalizeConstant.ESCAPE_MODES.get(0))
            .setPossibleValues(NormalizeConstant.ESCAPE_MODES);

    public Property<String> txtEnclosure = PropertyFactory.newString("txtEnclosure", "\"");

    public Property<Boolean> discardTrailingEmptyStr = PropertyFactory.newBoolean("discardTrailingEmptyStr", false);

    public Property<Boolean> trim = PropertyFactory.newBoolean("trim", false);

    public transient ISchemaListener schemaListener;

    public NormalizeProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(columnName);
        mainForm.addRow(fieldSeparator);
        /*mainForm.addRow(csvOption);
        mainForm.addRow(escapeMode);
        mainForm.addRow(txtEnclosure);*/
        mainForm.addRow(discardTrailingEmptyStr);
        mainForm.addRow(trim);
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
        /*if (form.getName().equals(Form.MAIN)) {
            form.getWidget(escapeMode).setVisible(csvOption);
            form.getWidget(txtEnclosure).setVisible(csvOption);
        }*/
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        HashSet<PropertyPathConnector> connectors = new LinkedHashSet<PropertyPathConnector>();
        if (isOutputConnection) {
            // output schemas
            connectors.add(FLOW_CONNECTOR);
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
    }
}
