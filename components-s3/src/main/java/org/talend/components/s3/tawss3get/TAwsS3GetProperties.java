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
package org.talend.components.s3.tawss3get;

import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.s3.AwsS3ConnectionProperties;
import org.talend.components.s3.AwsS3ConnectionPropertiesProvider;
import org.talend.daikon.properties.presentation.Form;

/**
 * created by dmytro.chmyga on Jul 27, 2016
 */
public class TAwsS3GetProperties extends ComponentPropertiesImpl implements AwsS3ConnectionPropertiesProvider {

    public AwsS3ConnectionProperties connectionProperties = new AwsS3ConnectionProperties("connectionProperties");

    /**
     * DOC dmytro.chmyga TAwsS3GetProperties constructor comment.
     * 
     * @param name
     */
    public TAwsS3GetProperties(String name) {
        super(name);
    }

    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(connectionProperties.getForm(Form.REFERENCE));
    }

    @Override
    public AwsS3ConnectionProperties getConnectionProperties() {
        return connectionProperties;
    }

}
