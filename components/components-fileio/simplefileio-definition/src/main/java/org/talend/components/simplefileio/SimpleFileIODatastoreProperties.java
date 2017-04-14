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

package org.talend.components.simplefileio;

import java.util.EnumSet;

import org.talend.components.common.datastore.DatastoreProperties;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class SimpleFileIODatastoreProperties extends PropertiesImpl implements DatastoreProperties {

    public Property<FileSystemType> fileSystemType = PropertyFactory.newEnum("fileSystemType", FileSystemType.class)
            .setValue(FileSystemType.HDFS);

    // HDFS
    public Property<String> userName = PropertyFactory.newString("userName");

    public Property<Boolean> useKerberos = PropertyFactory.newBoolean("useKerberos", false);

    public Property<String> kerberosPrincipal = PropertyFactory.newString("kerberosPrincipal", "username@EXAMPLE.COM");

    public Property<String> kerberosKeytab = PropertyFactory.newString("kerberosKeytab", "/home/username/username.keytab");

    // S3
    public Property<String> accessKey = PropertyFactory.newString("accessKey");

    public Property<String> secretKey = PropertyFactory.newString("secretKey")
            .setFlags(EnumSet.of(Property.Flags.ENCRYPT, Property.Flags.SUPPRESS_LOGGING));

    public Property<S3Region> region = PropertyFactory.newEnum("region", S3Region.class).setValue(S3Region.US_EAST_1);

    public SimpleFileIODatastoreProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(fileSystemType);
        mainForm.addRow(useKerberos);
        mainForm.addRow(kerberosPrincipal);
        mainForm.addRow(kerberosKeytab);
        mainForm.addRow(userName);
        mainForm.addRow(accessKey);
        mainForm.addRow(secretKey);
        mainForm.addRow(region);
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        // Main properties
        if (form.getName().equals(Form.MAIN)) {
            // handle HDFS
            boolean useHDFS = FileSystemType.HDFS.equals(fileSystemType.getValue());
            form.getWidget(useKerberos).setVisible(useHDFS);
            boolean isVisibleUseKerberos = form.getWidget(useKerberos).isVisible() && useKerberos.getValue();
            form.getWidget(kerberosPrincipal.getName()).setVisible(isVisibleUseKerberos);
            form.getWidget(kerberosKeytab.getName()).setVisible(isVisibleUseKerberos);
            form.getWidget(userName.getName()).setVisible(useHDFS && !isVisibleUseKerberos);

            // handle S3
            boolean useS3 = FileSystemType.S3.equals(fileSystemType.getValue());
            form.getWidget(accessKey.getName()).setVisible(useS3);
            form.getWidget(secretKey.getName()).setVisible(useS3);
            form.getWidget(region.getName()).setVisible(useS3);
        }
    }

    public void afterUseKerberos() {
        refreshLayout(getForm(Form.MAIN));
    }
}
