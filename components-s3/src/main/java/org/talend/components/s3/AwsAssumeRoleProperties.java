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
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class AwsAssumeRoleProperties extends PropertiesImpl {

    public Property<String> arn = PropertyFactory.newString("arn", "");

    public Property<String> roleSessionName = PropertyFactory.newString("roleSessionName", "");

    public Property<Integer> sessionDuration = PropertyFactory.newInteger("sessionDuration", 15);

    public AwsAssumeRoleProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = Form.create(this, Form.MAIN);
        mainForm.addRow(arn);
        mainForm.addRow(roleSessionName);
        mainForm.addRow(sessionDuration);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();

        arn.setRequired();
        roleSessionName.setRequired();
        sessionDuration.setRequired();
    }

    public int getSessionDurationSeconds() {
        return sessionDuration.getValue() * 60;
    }

}
