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

import static org.talend.daikon.properties.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.PropertyFactory.newString;
import static org.talend.daikon.properties.presentation.Widget.widget;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.ComponentReferenceProperties;
import org.talend.components.api.properties.ComponentReferencePropertiesEnclosing;
import org.talend.components.common.UserPasswordProperties;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.presentation.Widget.WidgetType;

public class DBConnectionProperties extends ComponentProperties implements DBProvideConnectionProperties, ComponentReferencePropertiesEnclosing {

    // basic setting start
    public Property               host                  = (Property) newString("host").setRequired(true);

    public Property               port                  = (Property) newString("port").setRequired(true);

    public Property               database              = (Property) newString("database").setRequired(true);

    public Property               dbschema              = newString("dbschema");

    private final String          userpassword          = "userPassword";

    public UserPasswordProperties userPassword          = new UserPasswordProperties(userpassword);

    public Property               jdbcparameter         = newString("jdbcparameter");

    public ComponentReferenceProperties referencedComponent = new ComponentReferenceProperties("referencedComponent", this);

    // advanced setting start
    public Property               autocommit            = newBoolean("autocommit");

    public DBConnectionProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form mainForm = CommonUtils.addForm(this, Form.MAIN);

        mainForm.addRow(host);
        mainForm.addColumn(port);

        mainForm.addRow(database);
        mainForm.addColumn(dbschema);

        mainForm.addRow(userPassword.getForm(Form.MAIN));

        mainForm.addRow(jdbcparameter);

        Form advancedForm = CommonUtils.addForm(this, Form.ADVANCED);
        advancedForm.addRow(autocommit);

        // only store it, will use it later
        Form refForm = CommonUtils.addForm(this, Form.REFERENCE);
        
        Widget compListWidget = widget(referencedComponent).setWidgetType(WidgetType.COMPONENT_REFERENCE);
        referencedComponent.componentType.setValue(getReferencedComponentName());
        
        refForm.addRow(compListWidget);
        refForm.addRow(mainForm);
        
        
    }

    protected String getReferencedComponentName() {
        return null;
    }
    
    public String getReferencedComponentId() {
        return referencedComponent.componentInstanceId.getStringValue();
    }

    @Override
    public void afterReferencedComponent() {
        refreshLayout(getForm(Form.MAIN));
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);

        String refComponentIdValue = getReferencedComponentId();
        boolean useOtherConnection = refComponentIdValue != null
                && refComponentIdValue.startsWith(getReferencedComponentName());
        
        if (form.getName().equals(Form.MAIN)) {
            if (useOtherConnection) {
                form.getWidget(host.getName()).setVisible(false);
                form.getWidget(port.getName()).setVisible(false);
                form.getWidget(database.getName()).setVisible(false);
                form.getWidget(dbschema.getName()).setVisible(false);
                form.getWidget(userPassword.getName()).setVisible(false);
                form.getWidget(jdbcparameter.getName()).setVisible(false);
                return;
            }
            
            form.getWidget(host.getName()).setVisible(true);
            form.getWidget(port.getName()).setVisible(true);
            form.getWidget(database.getName()).setVisible(true);
            form.getWidget(dbschema.getName()).setVisible(true);
            form.getWidget(userPassword.getName()).setVisible(true);
            form.getWidget(jdbcparameter.getName()).setVisible(true);
        }
    }

    @Override
    public DBConnectionProperties getConnectionProperties() {
        return this;
    }

}
