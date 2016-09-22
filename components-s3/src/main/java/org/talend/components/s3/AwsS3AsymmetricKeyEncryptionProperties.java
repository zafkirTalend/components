// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.s3;

import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;

import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

/**
 * Properties for Encrypted Amazon S3 connection with Asymmetric encryption key
 */
public class AwsS3AsymmetricKeyEncryptionProperties extends AwsS3WithAlgorithmEncryptionProperties {

    public Property<String> publicKeyFilePath = newProperty("publicKeyFilePath").setRequired();

    public Property<String> privateKeyFilePath = newProperty("privateKeyFilePath").setRequired();

    public AwsS3AsymmetricKeyEncryptionProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        algorithm.setPossibleValues(Algorithm.RSA);
        algorithm.setValue(Algorithm.RSA);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form main = getForm(Form.MAIN);
        main.addRow(widget(publicKeyFilePath).setWidgetType(Widget.FILE_WIDGET_TYPE));
        main.addRow(widget(privateKeyFilePath).setWidgetType(Widget.FILE_WIDGET_TYPE));
    }

}
