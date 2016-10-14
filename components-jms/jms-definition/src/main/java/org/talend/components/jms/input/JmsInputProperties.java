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

package org.talend.components.jms.input;

import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.common.SchemaProperties;
import org.talend.components.jms.JmsDatasetProperties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

import static org.talend.daikon.properties.property.PropertyFactory.newInteger;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

public class JmsInputProperties extends ComponentPropertiesImpl {

    public JmsInputProperties(String name) {
        super(name);
    }

    public SchemaProperties main = new SchemaProperties("main");

    public Property<String> from = PropertyFactory.newString("from","");

    public Property<Integer> timeout = PropertyFactory.newInteger("timeout",-1);

    public Property<Integer> max_msg = PropertyFactory.newInteger("max_msg",-1);

    public Property<String> msg_selector = PropertyFactory.newString("msg_selector","");

    public JmsDatasetProperties dataset = new JmsDatasetProperties("dataset");

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(main);
        mainForm.addRow(from);
        mainForm.addRow(timeout);
        mainForm.addRow(max_msg);
        mainForm.addRow(msg_selector);
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (form.getName().equals(Form.MAIN)) {
            form.getWidget(from.getName()).setHidden(false);
            form.getWidget(timeout.getName()).setHidden(false);
            form.getWidget(max_msg.getName()).setHidden(false);
            form.getWidget(msg_selector.getName()).setHidden(false);
        }
    }
}
