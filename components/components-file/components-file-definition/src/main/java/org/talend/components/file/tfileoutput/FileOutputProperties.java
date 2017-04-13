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
package org.talend.components.file.tfileoutput;

import java.util.Collections;
import java.util.Set;

import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.components.file.LineEnding;
import org.talend.components.file.StringDelimiter;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.EnumProperty;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class FileOutputProperties extends FixedConnectorsComponentProperties{

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_PATH = "test_1.txt";

    public SchemaProperties schema = new SchemaProperties("schema");

    protected final transient PropertyPathConnector mainConnector =
            new PropertyPathConnector(Connector.MAIN_NAME, "schema");

    public final EnumProperty<StringDelimiter> delimiter =
            PropertyFactory.newEnum("delimiter", StringDelimiter.class);

    public final Property<LineEnding> lineSeparator =
            PropertyFactory.newEnum("lineSeparator", LineEnding.class);

    public final Property<String> fileToSave = PropertyFactory.newString("fileToSave");

    public FileOutputProperties(String name) {
        super(name);
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        return Collections.singleton(mainConnector);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        this.delimiter.setValue(StringDelimiter.COMMA);
        this.lineSeparator.setValue(LineEnding.CRLF);
        this.fileToSave.setValue(DEFAULT_PATH);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form form = Form.create(this, Form.MAIN);
        form.addRow(schema.getForm(Form.REFERENCE));
        form.addRow(delimiter);
        form.addRow(lineSeparator);
        form.addRow(Widget.widget(fileToSave).setWidgetType(Widget.FILE_WIDGET_TYPE));

    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);

        if (form.getName().equals(Form.MAIN)) {
            form.getWidget(delimiter.getName()).setVisible();
            form.getWidget(lineSeparator.getName()).setVisible();
            form.getWidget(fileToSave.getName()).setVisible();
        }
    }

}
