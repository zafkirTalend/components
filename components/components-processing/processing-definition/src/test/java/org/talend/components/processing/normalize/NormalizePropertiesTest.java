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
package org.talend.components.processing.normalize;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.apache.avro.Schema;
import org.junit.Test;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.daikon.avro.AvroRegistry;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

public class NormalizePropertiesTest {

    /**
     * Checks {@link NormalizeProperties} sets correctly initial schema property
     */
    @Test
    public void testDefaultProperties() {
        NormalizeProperties properties = new NormalizeProperties("test");
        assertNull(properties.main.schema.getValue());
        assertNull(properties.schemaFlow.schema.getValue());

        assertEquals("", properties.columnToNormalize.getValue());
        assertEquals(false, properties.isList.getValue());
        assertEquals(NormalizeConstant.Delimiter.SEMICOLON, properties.fieldSeparator.getValue());
        assertEquals("", properties.otherSeparator.getValue());
        assertEquals(false, properties.discardTrailingEmptyStr.getValue());
        assertEquals(false, properties.trim.getValue());

        properties.init();
        assertEquals("EmptyRecord", properties.main.schema.getValue().getName());
        assertEquals("EmptyRecord", properties.schemaFlow.schema.getValue().getName());

        assertEquals("", properties.columnToNormalize.getValue());
        assertEquals(false, properties.isList.getValue());
        assertEquals(NormalizeConstant.Delimiter.SEMICOLON, properties.fieldSeparator.getValue());
        assertEquals("", properties.otherSeparator.getValue());
        assertEquals(false, properties.discardTrailingEmptyStr.getValue());
        assertEquals(false, properties.trim.getValue());
    }

    /**
     * Checks {@link NormalizeProperties} update correctly * schema property
     */
    @Test
    public void testSetupSchema() {
        NormalizeProperties properties = new NormalizeProperties("test");
        properties.init();
        AvroRegistry registry = new AvroRegistry();
        Schema stringSchema = registry.getConverter(String.class).getSchema();
        Schema.Field inputValue1Field = new Schema.Field("inputValue1", stringSchema, null, null, Schema.Field.Order.ASCENDING);
        Schema.Field inputValue2Field = new Schema.Field("inputValue2", stringSchema, null, null, Schema.Field.Order.ASCENDING);
        Schema inputSchema = Schema.createRecord("inputSchema", null, null, false,
                Arrays.asList(inputValue1Field, inputValue2Field));
        properties.main.schema.setValue(inputSchema);

        properties.columnToNormalize.setValue("testColumnToNormalize");
        properties.isList.setValue(false);
        properties.fieldSeparator.setValue(NormalizeConstant.Delimiter.OTHER);
        properties.otherSeparator.setValue("|");
        properties.discardTrailingEmptyStr.setValue(false);
        properties.trim.setValue(false);

        // Direct call since we are directly using the component property
        // instead of using PropertiesDynamicMethodHelper
        properties.schemaListener.afterSchema();

        assertThat(properties.main.schema.getValue(), equalTo(inputSchema));
        assertThat(properties.schemaFlow.schema.getValue(), equalTo(inputSchema));

        // the afterScheam trigger an update to the columnName
        assertEquals("testColumnToNormalize", properties.columnToNormalize.getValue());
        assertEquals(false, properties.isList.getValue());
        assertEquals(NormalizeConstant.Delimiter.OTHER, properties.fieldSeparator.getValue());
        assertEquals("|", properties.otherSeparator.getValue());
        assertEquals(false, properties.discardTrailingEmptyStr.getValue());
        assertEquals(false, properties.trim.getValue());
    }

    /**
     * Checks {@link NormalizeProperties#refreshLayout(Form)}
     */
    @Test
    public void testRefreshLayoutMainInitial() {
        NormalizeProperties properties = new NormalizeProperties("test");
        properties.init();
        AvroRegistry registry = new AvroRegistry();
        Schema stringSchema = registry.getConverter(String.class).getSchema();
        Schema.Field inputValue1Field = new Schema.Field("inputValue1", stringSchema, null, null, Schema.Field.Order.ASCENDING);
        Schema.Field inputValue2Field = new Schema.Field("inputValue2", stringSchema, null, null, Schema.Field.Order.ASCENDING);
        Schema inputSchema = Schema.createRecord("inputSchema", null, null, false,
                Arrays.asList(inputValue1Field, inputValue2Field));
        properties.main.schema.setValue(inputSchema);

        properties.refreshLayout(properties.getForm(Form.MAIN));
        assertTrue(properties.getForm(Form.MAIN).getWidget("columnToNormalize").isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget("isList").isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget("fieldSeparator").isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget("otherSeparator").isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget("discardTrailingEmptyStr").isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget("trim").isVisible());
        // The refreshLayout will change the columnName
        assertEquals("", properties.columnToNormalize.getValue());
        assertEquals(false, properties.isList.getValue());
        assertEquals(NormalizeConstant.Delimiter.SEMICOLON, properties.fieldSeparator.getValue());
        assertEquals("", properties.otherSeparator.getValue());
        assertEquals(false, properties.discardTrailingEmptyStr.getValue());
        assertEquals(false, properties.trim.getValue());

        properties.refreshLayout(properties.getForm(Form.MAIN));
        assertTrue(properties.getForm(Form.MAIN).getWidget("columnToNormalize").isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget("isList").isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget("fieldSeparator").isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget("otherSeparator").isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget("discardTrailingEmptyStr").isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget("trim").isVisible());
        // The refreshLayout will change the columnName
        assertEquals("", properties.columnToNormalize.getValue());
        assertEquals(false, properties.isList.getValue());
        assertEquals(NormalizeConstant.Delimiter.SEMICOLON, properties.fieldSeparator.getValue());
        assertEquals("", properties.otherSeparator.getValue());
        assertEquals(false, properties.discardTrailingEmptyStr.getValue());
        assertEquals(false, properties.trim.getValue());
    }

    @Test
    public void testSetupLayout() {
        NormalizeProperties properties = new NormalizeProperties("test");
        properties.schemaFlow.init();
        properties.main.init();

        properties.setupLayout();

        Form main = properties.getForm(Form.MAIN);
        assertThat(main, notNullValue());

        Collection<Widget> mainWidgets = main.getWidgets();
        assertThat(mainWidgets, hasSize(6));

        Widget columnToNormalizeWidget = main.getWidget("columnToNormalize");
        assertThat(columnToNormalizeWidget, notNullValue());

        Widget isListWidget = main.getWidget("isList");
        assertThat(isListWidget, notNullValue());

        Widget fieldSeparatorWidget = main.getWidget("fieldSeparator");
        assertThat(fieldSeparatorWidget, notNullValue());

        Widget otherSeparatorWidget = main.getWidget("otherSeparator");
        assertThat(fieldSeparatorWidget, notNullValue());

        Widget discardTrailingEmptyStrWidget = main.getWidget("discardTrailingEmptyStr");
        assertThat(discardTrailingEmptyStrWidget, notNullValue());

        Widget trimWidget = main.getWidget("trim");
        assertThat(trimWidget, notNullValue());
    }

    /**
     * Checks {@link NormalizeProperties#getAllSchemaPropertiesConnectors(boolean)} returns a {@link Set} with the main
     * link, for input
     */
    @Test
    public void testGetAllSchemaPropertiesConnectorsInput() {
        NormalizeProperties properties = new NormalizeProperties("test");

        Set<PropertyPathConnector> inputConnectors = properties.getAllSchemaPropertiesConnectors(false);
        assertEquals(1, inputConnectors.size());
        assertTrue(inputConnectors.contains(properties.MAIN_CONNECTOR));

    }

    @Test
    public void testGetAllSchemaPropertiesConnectorsOutput() {
        NormalizeProperties properties = new NormalizeProperties("test");

        Set<PropertyPathConnector> outputConnectors = properties.getAllSchemaPropertiesConnectors(true);
        assertEquals(1, outputConnectors.size());
        assertTrue(outputConnectors.contains(properties.FLOW_CONNECTOR));
    }
}
