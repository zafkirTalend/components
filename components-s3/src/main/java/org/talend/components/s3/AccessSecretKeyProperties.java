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

import static org.talend.daikon.properties.property.PropertyFactory.newProperty;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import java.util.EnumSet;

import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;

/**
 * Class, that represents panel with Access key and secret key properties.
 */
public class AccessSecretKeyProperties extends PropertiesImpl {

    /**
     * Access key used for Amazon S3 connection property.
     */
    public Property<String> accessKey = newString("accessKey", "");

    /**
     * Secret key used for Amazon S3 connection property.
     */
    public Property<String> secretKey = newProperty("secretKey").setRequired()
            .setFlags(EnumSet.of(Property.Flags.ENCRYPT, Property.Flags.SUPPRESS_LOGGING));

    public AccessSecretKeyProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = Form.create(this, Form.MAIN);
        mainForm.addRow(accessKey);
        mainForm.addColumn(secretKey);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();

        accessKey.setRequired();
        secretKey.setRequired();
    }

}
