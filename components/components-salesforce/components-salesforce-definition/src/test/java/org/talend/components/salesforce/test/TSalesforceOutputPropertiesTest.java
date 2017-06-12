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
package org.talend.components.salesforce.test;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.talend.components.salesforce.tsalesforceoutput.TSalesforceOutputProperties.FIELD_ERROR_MESSAGE;
import static org.talend.components.salesforce.tsalesforceoutput.TSalesforceOutputProperties.FIELD_SALESFORCE_ID;
import static org.talend.components.salesforce.tsalesforceoutput.TSalesforceOutputProperties.FIELD_STATUS;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.talend.components.api.test.ComponentTestUtils;
import org.talend.components.salesforce.SalesforceOutputProperties;
import org.talend.components.salesforce.tsalesforceoutput.TSalesforceOutputProperties;
import org.talend.daikon.properties.PropertiesDynamicMethodHelper;
import org.talend.daikon.properties.presentation.Form;

public class TSalesforceOutputPropertiesTest {

    private TSalesforceOutputProperties properties = new TSalesforceOutputProperties("foo");

    public static Schema DEFAULT_SCHEMA = SchemaBuilder.builder().record("Schema").fields() //
            .name("Id").type().stringType().noDefault() //
            .name("Name").type().stringType().noDefault() //
            .endRecord();

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    @Before
    public void setUp() {
        properties = new TSalesforceOutputProperties("root");
    }

    @Test
    public void testValuesAndLayout() throws Throwable {
        properties.init();

        // check default
        Form mainForm = properties.getForm(Form.MAIN);
        assertEquals(Form.MAIN, mainForm.getName());
        Form advancedForm = properties.getForm(Form.ADVANCED);
        assertTrue(mainForm.getWidget(properties.outputAction.getName()).isVisible());
        assertEquals(TSalesforceOutputProperties.OutputAction.INSERT, properties.outputAction.getValue());
        Schema rejectSchema = properties.schemaReject.schema.getValue();
        assertNotNull(rejectSchema);
        assertEquals(0, rejectSchema.getFields().size());
        Schema flowSchema = properties.schemaFlow.schema.getValue();
        assertNotNull(flowSchema);
        assertEquals(0, flowSchema.getFields().size());

        assertTrue(advancedForm.getWidget(properties.extendInsert.getName()).isVisible());
        assertTrue(properties.extendInsert.getValue());
        assertTrue(advancedForm.getWidget(properties.ceaseForError.getName()).isVisible());
        assertTrue(properties.ceaseForError.getValue());
        assertTrue(advancedForm.getWidget(properties.commitLevel.getName()).isVisible());
        assertEquals(200, properties.commitLevel.getValue().intValue());
        assertTrue(advancedForm.getWidget(properties.logFileName.getName()).isVisible());
        assertNull(properties.logFileName.getValue());

        // 1.After schema changed
        properties.module.main.schema.setValue(DEFAULT_SCHEMA);
        properties.module.schemaListener.afterSchema();
        rejectSchema = properties.schemaReject.schema.getValue();
        assertNotNull(rejectSchema);
        assertEquals(5, rejectSchema.getFields().size());
        assertEquals(4, rejectSchema.getField(FIELD_ERROR_MESSAGE).pos());
        flowSchema = properties.schemaFlow.schema.getValue();
        assertNotNull(flowSchema);
        assertEquals(2, flowSchema.getFields().size());

        // 2.After extendInsert unchecked
        properties.extendInsert.setValue(false);
        assertTrue(advancedForm.getWidget(properties.extendInsert.getName()).isCallAfter());
        PropertiesDynamicMethodHelper.afterProperty(properties, properties.extendInsert.getName());
        assertTrue(advancedForm.getWidget(properties.retrieveInsertId.getName()).isVisible());
        assertFalse(properties.retrieveInsertId.getValue());

        // 3.After retrieveInsertId checked
        properties.retrieveInsertId.setValue(true);
        assertTrue(advancedForm.getWidget(properties.retrieveInsertId.getName()).isCallAfter());
        PropertiesDynamicMethodHelper.afterProperty(properties, properties.retrieveInsertId.getName());
        // check schema changes
        flowSchema = properties.schemaFlow.schema.getValue();
        assertNotNull(flowSchema);
        assertEquals(3, flowSchema.getFields().size());
        // Field "salesforce_id" is added
        assertEquals(2, flowSchema.getField(FIELD_SALESFORCE_ID).pos());

        // 3.After retrieveInsertId checked
        properties.outputAction.setValue(SalesforceOutputProperties.OutputAction.UPSERT);
        assertTrue(mainForm.getWidget(properties.outputAction.getName()).isCallAfter());
        PropertiesDynamicMethodHelper.afterProperty(properties, properties.outputAction.getName());
        flowSchema = properties.schemaFlow.schema.getValue();
        assertNotNull(flowSchema);
        assertEquals(4, flowSchema.getFields().size());
        // Field "salesforce_id" is added
        assertEquals(2, flowSchema.getField(FIELD_SALESFORCE_ID).pos());
        assertEquals(3, flowSchema.getField(FIELD_STATUS).pos());

    }

    @Test
    public void testSerialize() throws Throwable {
        properties.init();

        ComponentTestUtils.checkSerialize(properties, errorCollector);
    }
}
