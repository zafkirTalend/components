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

import java.util.EnumSet;

import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.EnumProperty;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

/**
 * Class containing properties for Amazon S3 Symmetric key encrypted connection.
 */
public class AwsS3SymmetricKeyEncryptionProperties extends AwsS3WithAlgorithmEncryptionProperties {

    public EnumProperty<Encoding> encoding = PropertyFactory.newEnum("encoding", Encoding.class);

    public Property<String> key = PropertyFactory.newString("key").setRequired()
            .setFlags(EnumSet.of(Property.Flags.ENCRYPT, Property.Flags.SUPPRESS_LOGGING));

    public Property<String> keyFilePath = newProperty("keyFilePath").setRequired();

    /**
     * DOC dmytro.chmyga S3SymmetricKeyEncryptionProperties constructor comment.
     * 
     * @param name
     */
    public AwsS3SymmetricKeyEncryptionProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        algorithm.setPossibleValues(Algorithm.AES);
        algorithm.setValue(Algorithm.AES);
        encoding.setValue(Encoding.BASE_64);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form main = getForm(Form.MAIN);
        main.addRow(encoding);
        main.addRow(key);
        main.addRow(widget(keyFilePath).setWidgetType(Widget.FILE_WIDGET_TYPE));
        afterEncoding();
    }

    public void afterEncoding() {
        refreshLayout(getForm(Form.MAIN));
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (form.getName().equals(Form.MAIN)) {
            if (encoding.getValue() == Encoding.BASE_64) {
                getForm(Form.MAIN).getWidget(key.getName()).setHidden(false);
                getForm(Form.MAIN).getWidget(keyFilePath.getName()).setHidden(true);
            } else {
                getForm(Form.MAIN).getWidget(key.getName()).setHidden(true);
                getForm(Form.MAIN).getWidget(keyFilePath.getName()).setHidden(false);
            }
        }
    }

}
