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

import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.EnumProperty;
import org.talend.daikon.properties.property.PropertyFactory;

/**
 * created by dmytro.chmyga on Jul 21, 2016
 */
public class AwsS3WithAlgorithmEncryptionProperties extends PropertiesImpl {

    public EnumProperty<Algorithm> algorithm = PropertyFactory.newEnum("algorithm", Algorithm.class);

    /**
     * DOC dmytro.chmyga S3WithAlgorithmEncryptionProperties constructor comment.
     * 
     * @param name
     */
    public AwsS3WithAlgorithmEncryptionProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        algorithm.setRequired();
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = Form.create(this, Form.MAIN);
        mainForm.addRow(algorithm);
    }

    public void refreshLayout() {
        refreshLayout(getForm(Form.MAIN));
    }

}
