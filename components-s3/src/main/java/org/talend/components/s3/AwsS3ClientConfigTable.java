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

import java.util.List;

import org.apache.commons.lang3.reflect.TypeLiteral;
import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

/**
 * created by dmytro.chmyga on Jul 26, 2016
 */
public class AwsS3ClientConfigTable extends ComponentPropertiesImpl {

    private static final TypeLiteral<List<AwsS3ClientConfigFields>> LIST_ENUM_TYPE = new TypeLiteral<List<AwsS3ClientConfigFields>>() {// empty
    };

    private static final TypeLiteral<List<Object>> LIST_OBJECT_TYPE = new TypeLiteral<List<Object>>() {// empty
    };

    public Property<List<AwsS3ClientConfigFields>> configField = newProperty(LIST_ENUM_TYPE, "configField");

    public Property<List<Object>> configValue = newProperty(LIST_OBJECT_TYPE, "configValue");

    /**
     * DOC dmytro.chmyga ClientConfigTable constructor comment.
     * 
     * @param name
     */
    public AwsS3ClientConfigTable(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        Object[] possibleValues = AwsS3ClientConfigFields.values();
        configField.setPossibleValues(possibleValues);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addColumn(new Widget(configField).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addColumn(configValue);
    }

}
