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
package org.talend.components.snowflake.tsnowflakeoutput;

import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.avro.Schema;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.ISchemaListener;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.VirtualComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.components.snowflake.SnowflakeConnectionTableProperties;
import org.talend.components.snowflake.SnowflakeTableProperties;
import org.talend.components.snowflake.tsnowflakeoutputreject.TSnowflakeOutputRejectProperties;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

public class TSnowflakeOutputProperties extends SnowflakeConnectionTableProperties implements VirtualComponentProperties {

    public enum OutputAction {
        INSERT, UPDATE, UPSERT, DELETE
    }

    public Property<TSnowflakeOutputProperties.OutputAction> outputAction = newEnum("outputAction",
            TSnowflakeOutputProperties.OutputAction.class); // $NON-NLS-1$

    public Property<String> upsertKeyColumn = newString("upsertKeyColumn"); //$NON-NLS-1$

    protected transient PropertyPathConnector REJECT_CONNECTOR = new PropertyPathConnector(Connector.REJECT_NAME, "rejectProperties.schemaReject");

    protected transient PropertyPathConnector FLOW_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "schemaFlow");

    // Have to use an explicit class to get the override of afterTableName(), an anonymous
    // class cannot be public and thus cannot be called.
    public class TableSubclass extends SnowflakeTableProperties {

        public TableSubclass(String name) {
            super(name);
        }

        @Override public ValidationResult afterTableName() throws Exception {
            ValidationResult validationResult = super.afterTableName();
            if (table.main.schema.getValue() != null) {
                List<String> fieldNames = getFieldNames(table.main.schema);
                upsertKeyColumn.setPossibleValues(fieldNames);
            }
            return validationResult;
        }
    }

    public TSnowflakeOutputRejectProperties rejectProperties = new TSnowflakeOutputRejectProperties("rejectProperties");

    public TSnowflakeOutputProperties(String name) {
        super(name);
    }

    @Override public ComponentProperties getInputComponentProperties() {
        return this;
    }

    @Override public ComponentProperties getOutputComponentProperties() {
        return rejectProperties;
    }

    @Override public void setupProperties() {
        super.setupProperties();

        outputAction.setValue(TSnowflakeOutputProperties.OutputAction.INSERT);

        table = new TSnowflakeOutputProperties.TableSubclass("table");
        table.connection = connection;
        table.setupProperties();

        table.setSchemaListener(new ISchemaListener() {

            @Override public void afterSchema() {
                beforeUpsertKeyColumn();
            }
        });
    }

    @Override public void setupLayout() {
        super.setupLayout();
        Form mainForm = getForm(Form.MAIN);
        mainForm.addRow(outputAction);
        mainForm.addColumn(widget(upsertKeyColumn).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
    }

    public void afterOutputAction() {
        refreshLayout(getForm(Form.MAIN));
    }

    @Override public void refreshLayout(Form form) {
        super.refreshLayout(form);

        if (form.getName().equals(Form.MAIN)) {
            Form advForm = getForm(Form.ADVANCED);
            if (advForm != null) {
                boolean isUpsert = TSnowflakeOutputProperties.OutputAction.UPSERT.equals(outputAction.getValue());
                form.getWidget(upsertKeyColumn.getName()).setHidden(!isUpsert);
                if (isUpsert) {
                    beforeUpsertKeyColumn();
                }
            }
        }
    }

    protected List<String> getFieldNames(Property schema) {
        Schema s = (Schema) schema.getValue();
        List<String> fieldNames = new ArrayList<>();
        for (Schema.Field f : s.getFields()) {
            fieldNames.add(f.name());
        }
        return fieldNames;
    }

    public void beforeUpsertKeyColumn() {
        if (table.main.schema.getValue() != null)
            upsertKeyColumn.setPossibleValues(getFieldNames(table.main.schema));
    }

    @Override protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        HashSet<PropertyPathConnector> connectors = new HashSet<>();
        if (isOutputConnection) {
            connectors.add(REJECT_CONNECTOR);
        } else {
            connectors.add(MAIN_CONNECTOR);
        }
        return connectors;
    }

}
