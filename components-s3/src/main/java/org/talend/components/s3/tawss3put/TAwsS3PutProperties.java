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
package org.talend.components.s3.tawss3put;

import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.s3.AwsS3ConnectionProperties;
import org.talend.components.s3.AwsS3FileBucketKeyProperties;
import org.talend.components.s3.AwsS3LoaderPropertiesProvider;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

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
 */
public class TAwsS3PutProperties extends ComponentPropertiesImpl implements AwsS3LoaderPropertiesProvider {

    public AwsS3ConnectionProperties connectionProperties = new AwsS3ConnectionProperties("connectionProperties");

    public AwsS3FileBucketKeyProperties fileBucketKeyProperties = new AwsS3FileBucketKeyProperties("fileBucketKeyProperties");

    public Property<Boolean> enableServerSideEncryption = PropertyFactory.newBoolean("enableServerSideEncryption", false);

    public Property<Integer> uploadPartSize = PropertyFactory.newInteger("uploadPartSize", 5);

    public TAwsS3PutProperties(String name) {
        super(name);
    }

    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(connectionProperties.getForm(Form.REFERENCE));
        mainForm.addRow(fileBucketKeyProperties.getForm(Form.MAIN));
        mainForm.addRow(enableServerSideEncryption);

        Form advancedForm = new Form(this, Form.ADVANCED);
        advancedForm.addRow(connectionProperties.getForm(Form.ADVANCED));
        advancedForm.addRow(uploadPartSize);
    }

    @Override
    public AwsS3ConnectionProperties getConnectionProperties() {
        return connectionProperties;
    }

    @Override
    public AwsS3FileBucketKeyProperties getFileBucketKeyProperties() {
        return fileBucketKeyProperties;
    }

}
