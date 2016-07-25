package org.talend.components.s3;

import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;

import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.api.properties.ComponentReferenceProperties;
import org.talend.components.api.properties.ComponentReferencePropertiesEnclosing;
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
public class AwsS3ConnectionProperties extends ComponentPropertiesImpl implements ComponentReferencePropertiesEnclosing {

    public ComponentReferenceProperties referencedComponent = new ComponentReferenceProperties("referencedComponent", this);

    public AccessSecretKeyProperties accessSecretKeyProperties = new AccessSecretKeyProperties("accessSecretKeyProperties");

    public Property<Boolean> inheritFromAwsRole = newBoolean("inheritFromAwsRole", false);

    public EnumProperty<Region> region = newEnum("region", Region.class);

    public AwsS3ConnectionProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        // Code for property initialization goes here
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = Form.create(this, Form.MAIN);
        mainForm.addRow(accessSecretKeyProperties.getForm(Form.MAIN));
        mainForm.addRow(inheritFromAwsRole);
        mainForm.addRow(region);
    }

    @Override
    public void afterReferencedComponent() {
        // TODO Auto-generated method stub

    }

    public void afterInheritFromAwsRole() {
        refreshLayout();
    }

    public void refreshLayout() {
        refreshLayout(getForm(Form.MAIN));
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (form.getName().equals(Form.MAIN)) {
            Widget keyPropertiesWidget = getForm(Form.MAIN).getWidget(accessSecretKeyProperties.getName());
            if (inheritFromAwsRole.getValue()) {
                keyPropertiesWidget.setHidden(true);
            } else {
                keyPropertiesWidget.setHidden(false);
            }
        }
    }

}
