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
package org.talend.components.salesforce;

import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.NameAndLabel;
import org.talend.components.api.properties.Property;
import org.talend.components.api.properties.ValidationResult;
import org.talend.components.api.properties.presentation.Form;
import org.talend.components.api.properties.presentation.Widget;
import org.talend.components.common.SchemaProperties;
import org.talend.components.salesforce.metadata.SalesforceMetadata;

import java.util.List;

import static org.talend.components.api.properties.PropertyFactory.newEnum;
import static org.talend.components.api.properties.presentation.Widget.widget;

public class SalesforceModuleProperties extends ComponentProperties {

    //TODO: why not use extend instead of property
    private SalesforceConnectionProperties connection;

    //
    // Properties
    //
    public Property moduleName = newEnum("moduleName"); //$NON-NLS-1$

    public SchemaProperties schema = new SchemaProperties("schema");

    public SalesforceModuleProperties(String name) {
        super(name);
    }

    // FIXME - OK what about if we are using a connection from a separate component
    // that defines the connection, how do we get that separate component?
    public SalesforceModuleProperties setConnection(SalesforceConnectionProperties conn) {
        connection = conn;
        return this;
    }

    public SalesforceConnectionProperties getConnection() {
        return connection;
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form moduleForm = new Form(this, Form.MAIN);
        moduleForm.addRow(widget(moduleName).setWidgetType(Widget.WidgetType.NAME_SELECTION_AREA));
        refreshLayout(moduleForm);

        Form moduleRefForm = new Form(this, Form.REFERENCE);
        moduleRefForm.addRow(widget(moduleName).setWidgetType(Widget.WidgetType.NAME_SELECTION_REFERENCE));

        moduleRefForm.addRow(schema.getForm(Form.REFERENCE));
        refreshLayout(moduleRefForm);
    }

    // consider beforeActivate and beforeRender (change after to afterActivate)l

    public ValidationResult beforeModuleName() {
        SalesforceMetadata metadata = new SalesforceMetadata();
        try {
            List<NameAndLabel> moduleNames = metadata.getSchemasName(connection);
            moduleName.setPossibleValues(moduleNames);
        } catch (TalendConnectionException e) {
            ValidationResult vr = new ValidationResult();
            vr.setStatus(ValidationResult.Result.ERROR);
            vr.setMessage(e.getMessage());
        }
        return ValidationResult.OK;
    }

    public ValidationResult afterModuleName() {
        SalesforceMetadata metadata = new SalesforceMetadata();
        try {
            metadata.initSchema(this);
        } catch (TalendConnectionException e) {
            ValidationResult vr = new ValidationResult();
            vr.setStatus(ValidationResult.Result.ERROR);
            vr.setMessage(e.getMessage());
            return vr;
        }
        return ValidationResult.OK;
    }

}
