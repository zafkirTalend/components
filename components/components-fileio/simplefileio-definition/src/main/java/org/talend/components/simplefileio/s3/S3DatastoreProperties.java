// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.components.simplefileio.s3;

import java.util.EnumSet;

import org.talend.components.common.datastore.DatastoreProperties;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class S3DatastoreProperties extends PropertiesImpl implements DatastoreProperties {

    public Property<String> accessKey = PropertyFactory.newString("accessKey");

    public Property<String> secretKey = PropertyFactory.newString("secretKey")
            .setFlags(EnumSet.of(Property.Flags.ENCRYPT, Property.Flags.SUPPRESS_LOGGING));

    public Property<S3Region> region = PropertyFactory.newEnum("region", S3Region.class).setValue(S3Region.US_EAST_1);

    public S3DatastoreProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(accessKey);
        mainForm.addRow(secretKey);
        mainForm.addRow(region);
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        // Main properties
        if (form.getName().equals(Form.MAIN)) {
            // handle S3
            form.getWidget(accessKey.getName()).setVisible();
            form.getWidget(secretKey.getName()).setVisible();
            form.getWidget(region.getName()).setVisible();
        }
    }

    public void afterUseKerberos() {
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterFileSystemType() {
        refreshLayout(getForm(Form.MAIN));
    }
}
