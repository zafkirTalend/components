package org.talend.components.cassandra;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.ComponentReferenceProperties;
import org.talend.components.api.properties.ComponentReferencePropertiesEnclosing;
import org.talend.components.api.properties.ConnectionPropertiesProvider;
import org.talend.components.cassandra.connection.TCassandraConnectionDefinition;
import org.talend.components.common.UserPasswordProperties;
import org.talend.daikon.properties.PresentationItem;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

import static org.talend.daikon.properties.PropertyFactory.*;
import static org.talend.daikon.properties.presentation.Widget.widget;

public class CassandraConnectionProperties extends ComponentProperties implements ComponentReferencePropertiesEnclosing, ConnectionPropertiesProvider<CassandraConnectionProperties> {
    /**
     * named constructor to be used is these properties are nested in other properties. Do not subclass this method for
     * initialization, use {@link #init()} instead.
     *
     * @param name
     */
    public CassandraConnectionProperties(String name) {
        super(name);
    }

    public static final String V_CASSANDRA_3_0 = "CASSANDRA_3_0";

    public static final String V_CASSANDRA_2_0 = "CASSANDRA_2_0";

    public Property version = newEnum("version", V_CASSANDRA_2_0, V_CASSANDRA_3_0);

    public Property host = newString("host", "localhost");

    public Property port = newString("port", "9042");

    public Property needAuth = newBoolean("needAuth", false);

    public UserPasswordProperties userPassword = new UserPasswordProperties("userPassword");

    public PresentationItem testConnection = new PresentationItem("testConnection", "Test connection");

    public ComponentReferenceProperties referencedComponent = new ComponentReferenceProperties("referencedComponent", this);

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form wizardForm = new Form(this, "Wizard");
        wizardForm.addRow((Property) newString("name").setRequired());
        wizardForm.addRow(widget(version).setDeemphasize(true));
        wizardForm.addRow(host);
        wizardForm.addColumn(port);
        wizardForm.addRow(needAuth);
        wizardForm.addRow(userPassword.getForm(Form.MAIN));
        wizardForm.addColumn(widget(testConnection).setLongRunning(true).setWidgetType(Widget.WidgetType.BUTTON));

        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(version);
        mainForm.addRow(host);
        mainForm.addColumn(port);
        mainForm.addRow(needAuth);
        mainForm.addRow(userPassword.getForm(Form.MAIN));

        Form mainAndRefForm = new Form(this, Form.REFERENCE);
        Widget compListWidget = widget(referencedComponent).setWidgetType(Widget.WidgetType.COMPONENT_REFERENCE);
        referencedComponent.componentType.setValue(TCassandraConnectionDefinition.COMPONENT_NAME);
        mainAndRefForm.addRow(compListWidget);
        mainAndRefForm.addRow(mainForm);
    }

    @Override
    public void afterReferencedComponent() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.REFERENCE));
    }

    public String getReferencedComponentId() {
        return referencedComponent.componentInstanceId.getStringValue();
    }

    public CassandraConnectionProperties getReferencedConnectionProperties() {
        CassandraConnectionProperties refProps = (CassandraConnectionProperties) referencedComponent.componentProperties;
        if (refProps != null)
            return refProps;
        return null;
    }

    public void afterNeedAuth() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm("Wizard"));
    }

    //TODO implement it after validateConnection method
    //    public ValidationResult validateTestConnection() throws Exception{
    //    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);

        String refComponentIdValue = getReferencedComponentId();
        boolean useOtherConnection = refComponentIdValue != null && refComponentIdValue.startsWith(TCassandraConnectionDefinition.COMPONENT_NAME);
        if (form.getName().equals(Form.MAIN) || form.getName().equals("Wizard")) {
            if (useOtherConnection) {
                form.getWidget(version.getName()).setVisible(false);
                form.getWidget(host.getName()).setVisible(false);
                form.getWidget(port.getName()).setVisible(false);
                form.getWidget(needAuth.getName()).setVisible(false);
                form.getWidget(userPassword.getName()).setVisible(false);
            } else {
                form.getWidget(version.getName()).setVisible(true);
                form.getWidget(host.getName()).setVisible(true);
                form.getWidget(port.getName()).setVisible(true);
                if (needAuth.getBooleanValue()) {
                    form.getWidget(userPassword.getName()).setVisible(true);
                } else {
                    form.getWidget(userPassword.getName()).setVisible(false);
                }
            }
        }
    }

    @Override
    public CassandraConnectionProperties getConnectionProperties() {
        return this;
    }
}
