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
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

/**
 * created by dmytro.chmyga on Jul 27, 2016
 */
public class AwsS3FileBucketKeyProperties extends PropertiesImpl {

    public Property<String> bucket = newString("bucket", "").setRequired();

    public Property<String> key = newString("key", "").setRequired();

    public Property<String> filePath = newString("filePath", "").setRequired();

    /**
     * DOC dmytro.chmyga AwsS3FileBucketKeyProperties constructor comment.
     * 
     * @param name
     */
    public AwsS3FileBucketKeyProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        Form form = Form.create(this, Form.MAIN);
        form.addRow(bucket);
        form.addColumn(key);
        form.addRow(widget(filePath).setWidgetType(Widget.FILE_WIDGET_TYPE));
    }

}
