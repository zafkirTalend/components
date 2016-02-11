// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.common;

import static org.talend.daikon.properties.PropertyFactory.*;
import static org.talend.daikon.properties.presentation.Widget.*;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.PropertyFactory;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.schema.DataSchema;
import org.talend.daikon.schema.MakoElement;
import org.talend.daikon.schema.DataSchemaFactory;

public class SchemaProperties extends ComponentProperties {

    public SchemaProperties(String name) {
        super(name);
    }

    //
    // Properties
    //
    // FIXME - change to Schema
    public Property schema = newProperty(MakoElement.Type.SCHEMA, "schema"); //$NON-NLS-1$

    @Override
    public void setupProperties() {
        super.setupProperties();
        schema.setValue(DataSchemaFactory.newSchema());
    }

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form schemaForm = Form.create(this, Form.MAIN, "Schema"); //$NON-NLS-1$
        schemaForm.addRow(widget(schema).setWidgetType(Widget.WidgetType.SCHEMA_EDITOR));

        Form schemaRefForm = Form.create(this, Form.REFERENCE, "Schema"); //$NON-NLS-1$
        schemaRefForm.addRow(widget(schema).setWidgetType(Widget.WidgetType.SCHEMA_REFERENCE));
    }
}
