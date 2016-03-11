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
package org.talend.components.oracle;

import static org.talend.daikon.properties.PropertyFactory.newString;

import org.talend.components.api.properties.ComponentPropertyFactory;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.Property.Type;
import org.talend.daikon.properties.presentation.Form;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DBInputProperties extends DBCommonProperties {

    public Property         tablename = (Property) newString("tablename").setRequired(true);

    public SchemaProperties schema    = new SchemaProperties("schema");

    public Property         sql       = newString("sql");

    public DBInputProperties(@JsonProperty("name") String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form mainForm = getForm(Form.MAIN);
        mainForm.addRow(tablename);
        mainForm.addColumn(schema.getForm(Form.REFERENCE));
        mainForm.addRow(sql);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();

        returns = ComponentPropertyFactory.newReturnsProperty();
        ComponentPropertyFactory.newReturnProperty(returns, Type.INT, "NB_LINE");
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
    }

}
