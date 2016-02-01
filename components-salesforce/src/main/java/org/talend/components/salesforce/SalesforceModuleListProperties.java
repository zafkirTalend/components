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
import org.talend.components.api.schema.Schema;
import org.talend.components.api.schema.SchemaElement;
import org.talend.components.api.service.ComponentService;
import org.talend.components.salesforce.metadata.SalesforceMetadata;

import java.util.List;

import static org.talend.components.api.properties.PropertyFactory.newString;
import static org.talend.components.api.properties.presentation.Widget.widget;

public class SalesforceModuleListProperties extends ComponentProperties {

    private SalesforceConnectionProperties connectionProps;

    private String repositoryLocation;

    private List<NameAndLabel> moduleNames;

    transient private ComponentService compService;

    //
    // Properties
    //
    public Property moduleName = (Property) newString("moduleName").setOccurMaxTimes(SchemaElement.INFINITE); //$NON-NLS-1$

    public SalesforceModuleListProperties(String name) {
        super(name);
    }

    public SalesforceModuleListProperties setConnection(SalesforceConnectionProperties connection) {
        connectionProps = connection;
        return this;
    }

    public SalesforceModuleListProperties setRepositoryLocation(String location) {
        repositoryLocation = location;
        return this;
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form moduleForm = Form.create(this, Form.MAIN, "Salesforce Modules");
        // Since this is a repeating property it has a list of values
        moduleForm.addRow(widget(moduleName).setWidgetType(Widget.WidgetType.NAME_SELECTION_AREA));
        refreshLayout(moduleForm);
    }

    // For the tests
    public SalesforceConnectionProperties getConnectionProps() {
        return connectionProps;
    }

    public void beforeFormPresentMain() throws Exception {
        SalesforceMetadata metadata = new SalesforceMetadata();
        moduleNames = metadata.getSchemasName(connectionProps);
        moduleName.setPossibleValues(moduleNames);
        getForm(Form.MAIN).setAllowBack(true);
        getForm(Form.MAIN).setAllowFinish(true);
    }

    public ValidationResult afterFormFinishMain() {
        SalesforceMetadata metadata = new SalesforceMetadata();

        String connRepLocation = compService.storeComponentProperties(connectionProps, (String) connectionProps.name.getValue(),
                repositoryLocation, null);

        @SuppressWarnings("unchecked")
        List<NameAndLabel> selectedModuleNames = (List<NameAndLabel>) moduleName.getValue();
        for (NameAndLabel nl : selectedModuleNames) {
            SalesforceModuleProperties modProps = new SalesforceModuleProperties(nl.getName()).setConnection(connectionProps);
            modProps.initForRuntime();
            modProps.moduleName.setValue(nl.getName());
            try {
                metadata.initSchema(modProps);
            } catch (TalendConnectionException e) {
                ValidationResult vr = new ValidationResult();
                vr.setMessage(e.getMessage());
                vr.setStatus(ValidationResult.Result.ERROR);
                return vr;
            }
            compService.storeComponentProperties(modProps, nl.getName(), connRepLocation, (Schema) modProps.schema.schema.getValue());
        }
        return ValidationResult.OK;
    }

    /**
     * Sets the compService.
     *
     * @param compService the compService to set
     * @return
     */
    public SalesforceModuleListProperties setComponentService(ComponentService compService) {
        this.compService = compService;
        return this;
    }
}
