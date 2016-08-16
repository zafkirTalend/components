package org.talend.components.s3;

import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.api.properties.ComponentReferenceProperties;
import org.talend.components.api.properties.ComponentReferencePropertiesEnclosing;
import org.talend.components.s3.tawss3connection.TAwsS3ConnectionDefinition;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.EnumProperty;
import org.talend.daikon.properties.property.Property;

/**
 * The ComponentProperties subclass provided by a component stores the configuration of a component and is used for:
 * 
 * <ol>
 * <li>Specifying the format and type of information (properties) that is provided at design-time to configure a
 * component for run-time,</li>
 * <li>Validating the properties of the component at design-time,</li>
 * <li>Containing the untyped values of the properties, and</li>
 * <li>All of the UI information for laying out and presenting the properties to the user.</li>
 * </ol>
 * 
 * The tAWSS3ConnectionProperties has two properties:
 * <ol>
 * <li>{code filename}, a simple property which is a String containing the file path that this component will read.</li>
 * <li>{code schema}, an embedded property referring to a Schema.</li>
 * </ol>
 */
public class AwsS3ConnectionProperties extends ComponentPropertiesImpl
        implements ComponentReferencePropertiesEnclosing, AwsS3ConnectionPropertiesProvider {

    public ComponentReferenceProperties referencedComponent = new ComponentReferenceProperties("referencedComponent", this);

    public AccessSecretKeyProperties accessSecretKeyProperties = new AccessSecretKeyProperties("accessSecretKeyProperties");

    public Property<Boolean> inheritFromAwsRole = newBoolean("inheritFromAwsRole", false);

    public EnumProperty<Region> region = newEnum("region", Region.class);

    public Property<Boolean> encrypt = newBoolean("encrypt", false);

    public AwsS3ConnectionEncryptionProperties encryptionProperties = new AwsS3ConnectionEncryptionProperties(
            "encryptionProperties");

    public Property<Boolean> configClient = newBoolean("configClient", false);

    public AwsS3ClientConfigTable configClientTable = new AwsS3ClientConfigTable("configClientTable");

    public Property<Boolean> assumeRole = newBoolean("assumeRole", false);

    public AwsAssumeRoleProperties assumeRoleProps = new AwsAssumeRoleProperties("assumeRoleProps");

    public Property<Boolean> setStsEndpoint = newBoolean("setStsEndpoint", false);

    public Property<String> stsEndpoint = newString("stsEndpoint", "");

    public AwsS3ConnectionProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        region.setValue(Region.DEFAULT);
        stsEndpoint.setRequired();
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = Form.create(this, Form.MAIN);
        mainForm.addRow(accessSecretKeyProperties.getForm(Form.MAIN));
        mainForm.addRow(inheritFromAwsRole);
        mainForm.addRow(assumeRole);
        mainForm.addRow(assumeRoleProps.getForm(Form.MAIN));
        mainForm.addRow(region);
        mainForm.addRow(encrypt);
        mainForm.addRow(encryptionProperties.getForm(Form.MAIN));

        Form advancedForm = Form.create(this, Form.ADVANCED);
        advancedForm.addRow(configClient);
        advancedForm.addRow(widget(configClientTable).setWidgetType(Widget.TABLE_WIDGET_TYPE));
        advancedForm.addRow(setStsEndpoint);
        advancedForm.addColumn(stsEndpoint);

        Form refForm = Form.create(this, Form.REFERENCE);
        Widget compListWidget = widget(referencedComponent).setWidgetType(Widget.COMPONENT_REFERENCE_WIDGET_TYPE);
        referencedComponent.componentType.setValue(TAwsS3ConnectionDefinition.COMPONENT_NAME);
        refForm.addRow(compListWidget);
        refForm.addRow(mainForm);

        refreshLayout();
    }

    @Override
    public void afterReferencedComponent() {
        refreshLayout();
    }

    public void afterInheritFromAwsRole() {
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterEncrypt() {
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterConfigClient() {
        refreshLayout(getForm(Form.ADVANCED));
    }

    public void afterAssumeRole() {
        refreshLayout();
    }

    public void afterSetStsEndpoint() {
        refreshLayout(getForm(Form.ADVANCED));
    }

    public void refreshLayout() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.ADVANCED));
        refreshLayout(getForm(Form.REFERENCE));
    }

    @Override
    public void refreshLayout(Form form) {
        String refComponentIdValue = getReferencedComponentId();
        boolean useOtherConnection = refComponentIdValue != null
                && refComponentIdValue.startsWith(TAwsS3ConnectionDefinition.COMPONENT_NAME);
        super.refreshLayout(form);
        if (form.getName().equals(Form.MAIN)) {
            if (useOtherConnection) {
                form.setHidden(true);
            } else {
                form.setHidden(false);
                Widget keyPropertiesWidget = getForm(Form.MAIN).getWidget(accessSecretKeyProperties.getName());
                if (inheritFromAwsRole.getValue()) {
                    keyPropertiesWidget.setHidden(true);
                } else {
                    keyPropertiesWidget.setHidden(false);
                }
                Widget assumeRolePropsWidget = getForm(Form.MAIN).getWidget(assumeRoleProps.getName());
                assumeRolePropsWidget.setHidden(!assumeRole.getValue());
                if (encrypt.getValue()) {
                    getForm(Form.MAIN).getWidget(encryptionProperties.getName()).setHidden(false);
                    encryptionProperties.refreshLayout();
                } else {
                    getForm(Form.MAIN).getWidget(encryptionProperties.getName()).setHidden(true);
                }
            }
        } else if (form.getName().equals(Form.ADVANCED)) {
            if (useOtherConnection) {
                form.setHidden(true);
            } else {
                form.setHidden(false);
                form.getWidget(configClientTable.getName()).setHidden(!configClient.getValue());
            }
            if (assumeRole.getValue()) {
                form.getWidget(setStsEndpoint.getName()).setHidden(false);
                form.getWidget(stsEndpoint.getName()).setHidden(!setStsEndpoint.getValue());
            } else {
                form.getWidget(setStsEndpoint.getName()).setHidden(true);
                form.getWidget(stsEndpoint.getName()).setHidden(true);
            }
        }
    }

    public boolean isRegionSet() {
        Region region = this.region.getValue();
        return region != null && region != Region.DEFAULT;
    }

    @Override
    public AwsS3ConnectionProperties getConnectionProperties() {
        return this;
    }

    public String getReferencedComponentId() {
        return referencedComponent.componentInstanceId.getStringValue();
    }

    public AwsS3ConnectionProperties getReferencedConnectionProperties() {
        AwsS3ConnectionProperties refProps = (AwsS3ConnectionProperties) referencedComponent.componentProperties;
        if (refProps != null) {
            return refProps;
        }
        return null;
    }

}
