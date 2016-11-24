
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
package org.talend.components.udp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.avro.Schema;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.presentation.Form;

/**
 * The ComponentProperties subclass provided by a component stores the 
 * configuration of a component and is used for:
 * 
 * <ol>
 * <li>Specifying the format and type of information (properties) that is 
 *     provided at design-time to configure a component for run-time,</li>
 * <li>Validating the properties of the component at design-time,</li>
 * <li>Containing the untyped values of the properties, and</li>
 * <li>All of the UI information for laying out and presenting the 
 *     properties to the user.</li>
 * </ol>
 * 
 * The tUdpSocketInputProperties has two properties:
 * <ol>
 * <li>{code filename}, a simple property which is a String containing the 
 *     file path that this component will read.</li>
 * <li>{code schema}, an embedded property referring to a Schema.</li>
 * </ol>
 */
public class tUdpSocketInputProperties extends FixedConnectorsComponentProperties {
    public static final String FIELD_DATA = "data";

    public Property port = PropertyFactory.newInteger("port"); //$NON-NLS-1$
    public Property sizeArray = PropertyFactory.newInteger("sizeArray"); //$NON-NLS-1$
    public SchemaProperties schema = new SchemaProperties("schema"); //$NON-NLS-1$
    protected transient PropertyPathConnector mainConnector = new PropertyPathConnector(Connector.MAIN_NAME, "schema");
 
    public tUdpSocketInputProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        // Code for property initialization goes here

        final List<Schema.Field> additionalMainFields = new ArrayList<Schema.Field>();



        Schema.Field field = new Schema.Field(FIELD_DATA, Schema.create(Schema.Type.BYTES), null, (Object) null);
        field.addProp(SchemaConstants.TALEND_IS_LOCKED, "true");
        field.addProp(SchemaConstants.TALEND_FIELD_GENERATED, "true");
        additionalMainFields.add(field);

        Schema newSchema = Schema.createRecord("RandomName","test","anothertest",false);

        newSchema.setFields(additionalMainFields);

        schema.schema.setValue(newSchema);
        port.setValue(9876);
        port.setNullable(false);
        port.setRequired(true);
        sizeArray.setValue(1024);
        sizeArray.setNullable(false);
        sizeArray.setRequired(true);

    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form form = Form.create(this, Form.MAIN);
        form.addRow(schema.getForm(Form.REFERENCE));
        form.addRow(port);
        form.addRow(sizeArray);
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputComponent) {
        if (isOutputComponent) {
            return Collections.singleton(mainConnector);
        }
        return Collections.emptySet();
    }

}
