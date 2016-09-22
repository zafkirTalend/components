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

import static org.talend.daikon.properties.property.PropertyFactory.newEnum;

import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.EnumProperty;

/**
 * Amazon S3 encrypted connection properties.
 */
public class AwsS3ConnectionEncryptionProperties extends PropertiesImpl {

    public EnumProperty<EncryptionKeyType> encryptionKeyType = newEnum("encryptionKeyType", EncryptionKeyType.class);

    public KmsCmkEncryptionProperties kmsCmkProperties = new KmsCmkEncryptionProperties("kmsCmkProperties");

    public AwsS3SymmetricKeyEncryptionProperties symmetricKeyProperties = new AwsS3SymmetricKeyEncryptionProperties(
            "symmetricKeyProperties");

    public AwsS3AsymmetricKeyEncryptionProperties asymmetricKeyProperties = new AwsS3AsymmetricKeyEncryptionProperties(
            "asymmetricKeyProperties");

    public AwsS3ConnectionEncryptionProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form main = Form.create(this, Form.MAIN);
        main.addRow(encryptionKeyType);
        main.addRow(kmsCmkProperties.getForm(Form.MAIN));
        main.addRow(symmetricKeyProperties.getForm(Form.MAIN));
        main.addRow(asymmetricKeyProperties.getForm(Form.MAIN));
        refreshLayout();
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        encryptionKeyType.setValue(EncryptionKeyType.KMS_MANAGED_CUSTOMER_MASTER_KEY);
    }

    public void afterEncryptionKeyType() {
        Form main = getForm(Form.MAIN);
        refreshLayout(main);
    }

    public void refreshLayout() {
        refreshLayout(getForm(Form.MAIN));
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (form.getName().equals(Form.MAIN)) {
            EncryptionKeyType keyType = encryptionKeyType.getValue();
            if (keyType == EncryptionKeyType.KMS_MANAGED_CUSTOMER_MASTER_KEY) {
                form.getWidget(kmsCmkProperties.getName()).setHidden(false);
                form.getWidget(symmetricKeyProperties.getName()).setHidden(true);
                form.getWidget(asymmetricKeyProperties.getName()).setHidden(true);
            } else if (keyType == EncryptionKeyType.SYMMETRIC_MASTER_KEY) {
                form.getWidget(kmsCmkProperties.getName()).setHidden(true);
                form.getWidget(symmetricKeyProperties.getName()).setHidden(false);
                form.getWidget(asymmetricKeyProperties.getName()).setHidden(true);
            } else {
                form.getWidget(kmsCmkProperties.getName()).setHidden(true);
                form.getWidget(symmetricKeyProperties.getName()).setHidden(true);
                form.getWidget(asymmetricKeyProperties.getName()).setHidden(false);
            }
            symmetricKeyProperties.refreshLayout();
            asymmetricKeyProperties.refreshLayout();
        }
    }

}
