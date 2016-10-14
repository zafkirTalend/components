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

package org.talend.components.jms;

import org.talend.components.common.SchemaProperties;
import org.talend.components.common.dataset.DatasetProperties;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;

import static org.talend.daikon.properties.property.PropertyFactory.newEnum;

public class JmsDatasetProperties extends PropertiesImpl implements DatasetProperties{

    public JmsDatasetProperties(String name) {
        super(name);
    }

    public enum ProcessingMode {
        // type message jms / string etc
        raw,
        content
    }
    public enum AdvancedPropertiesArrayType {
        raw,
        content
    }

    public enum JmsMsgType {
        topic,
        queue
    }

    public SchemaProperties main = new SchemaProperties("main");

    public Property<JmsMsgType> msgType = newEnum("msgType", JmsMsgType.class).setRequired();

    public Property<ProcessingMode> processingMode = newEnum("processingMode", ProcessingMode.class);

    public JmsDatastoreProperties datastore = new JmsDatastoreProperties("datastore");

    @Override
    public void setupLayout() {
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(main.getForm(Form.MAIN));
        mainForm.addRow(msgType);
        mainForm.addRow(processingMode);
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        // Main properties
        if (form.getName().equals(Form.MAIN)) {
            form.getWidget(msgType.getName()).setHidden(false);
            form.getWidget(processingMode.getName()).setHidden(false);
        }
    }
}
