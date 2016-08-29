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
package org.talend.components.dropbox.tdropboxget;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.talend.daikon.avro.SchemaConstants.JAVA_CLASS_FLAG;
import static org.talend.daikon.avro.SchemaConstants.TALEND_IS_LOCKED;
import static org.talend.daikon.di.DiSchemaConstants.TALEND6_COLUMN_TALEND_TYPE;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field.Order;
import org.junit.Test;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.daikon.avro.AvroRegistry;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

/**
 * Unit-tests for {@link TDropboxGetProperties} class
 */
public class TDropboxGetPropertiesTest {

    /**
     * Checks {@link TDropboxGetProperties#afterSaveAsFile()} shows Save To widget, when Save As File checkbox is checked
     */
    @Test
    public void testAfterSaveAsFileTrue() {
        TDropboxGetProperties properties = new TDropboxGetProperties("root");
        properties.init();
        properties.saveAsFile.setValue(true);

        properties.afterSaveAsFile();

        boolean saveToHidden = properties.getForm(Form.MAIN).getWidget("saveTo").isHidden();
        assertFalse(saveToHidden);
    }

    /**
     * Checks {@link TDropboxGetProperties#afterSaveAsFile()} hides Save To widget, when Save As File checkbox is unchecked
     */
    @Test
    public void testAfterSaveAsFileFalse() {
        TDropboxGetProperties properties = new TDropboxGetProperties("root");
        properties.init();
        properties.saveAsFile.setValue(false);

        properties.afterSaveAsFile();

        boolean saveToHidden = properties.getForm(Form.MAIN).getWidget("saveTo").isHidden();
        assertTrue(saveToHidden);
    }

    /**
     * Checks {@link TDropboxGetProperties#setupProperties()} sets correct initial property values
     */
    @Test
    public void testSetupProperties() {
        AvroRegistry registry = new AvroRegistry();
        Schema stringSchema = registry.getConverter(String.class).getSchema();
        Schema streamSchema = registry.getConverter(ByteBuffer.class).getSchema();
        streamSchema.addProp(JAVA_CLASS_FLAG, "java.io.InputStream");

        Schema.Field fileNameField = new Schema.Field("fileName", stringSchema, null, null, Order.ASCENDING);
        Schema.Field contentField = new Schema.Field("content", streamSchema, null, null, Order.ASCENDING);
        List<Schema.Field> fields = Arrays.asList(fileNameField, contentField);
        Schema expectedSchema = Schema.createRecord("dropbox", null, null, false, fields);
        expectedSchema.addProp(TALEND_IS_LOCKED, "true");

        TDropboxGetProperties properties = new TDropboxGetProperties("root");
        properties.setupProperties();

        String pathValue = properties.path.getValue();
        boolean saveAsFileValue = properties.saveAsFile.getValue();
        String saveToValue = properties.saveTo.getValue();
        OutgoingContentType contentTypeValue = properties.contentType.getValue();
        Schema schemaValue = properties.schema.schema.getValue();

        assertThat(pathValue, equalTo(""));
        assertFalse(saveAsFileValue);
        assertThat(saveToValue, equalTo(""));
        assertThat(contentTypeValue, equalTo(OutgoingContentType.INPUT_STREAM));
        assertThat(schemaValue, equalTo(expectedSchema));
    }

    /**
     * Checks {@link TDropboxGetProperties#refreshLayout(Form)} hides saveTo and chunkMode widgets in initial state
     */
    @Test
    public void testRefreshLayoutMainInitial() {
        TDropboxGetProperties properties = new TDropboxGetProperties("root");
        properties.init();

        properties.refreshLayout(properties.getForm(Form.MAIN));
        properties.refreshLayout(properties.getForm(Form.ADVANCED));

        boolean connectionIsHidden = properties.getForm(Form.MAIN).getWidget("connection").isHidden();
        boolean pathIsHidden = properties.getForm(Form.MAIN).getWidget("path").isHidden();
        boolean saveAsFileIsHidden = properties.getForm(Form.MAIN).getWidget("saveAsFile").isHidden();
        boolean saveToIsHidden = properties.getForm(Form.MAIN).getWidget("saveTo").isHidden();
        boolean chunkModeIsHidden = properties.getForm(Form.ADVANCED).getWidget("chunkMode").isHidden();
        boolean schemaIsHidden = properties.getForm(Form.MAIN).getWidget("schema").isHidden();
        assertFalse(connectionIsHidden);
        assertFalse(pathIsHidden);
        assertFalse(saveAsFileIsHidden);
        assertTrue(saveToIsHidden);
        assertTrue(chunkModeIsHidden);
        assertFalse(schemaIsHidden);
    }

    /**
     * Checks {@link TDropboxGetProperties#refreshLayout(Form)} doesn't refresh anything if non-existent form passed as
     * parameter
     */
    @Test
    public void testRefreshLayoutWrongForm() {
        TDropboxGetProperties properties = new TDropboxGetProperties("root");
        properties.init();

        boolean connectionExpected = properties.getForm(Form.MAIN).getWidget("connection").isHidden();
        boolean pathExpected = properties.getForm(Form.MAIN).getWidget("path").isHidden();
        boolean saveAsFileExpected = properties.getForm(Form.MAIN).getWidget("saveAsFile").isHidden();
        boolean saveToExpected = properties.getForm(Form.MAIN).getWidget("saveTo").isHidden();
        boolean chunkModeExpected = properties.getForm(Form.ADVANCED).getWidget("chunkMode").isHidden();
        boolean schemaExpected = properties.getForm(Form.MAIN).getWidget("schema").isHidden();

        properties.refreshLayout(new Form(properties, "NotMain"));

        boolean connectionActual = properties.getForm(Form.MAIN).getWidget("connection").isHidden();
        boolean pathActual = properties.getForm(Form.MAIN).getWidget("path").isHidden();
        boolean saveAsFileActual = properties.getForm(Form.MAIN).getWidget("saveAsFile").isHidden();
        boolean saveToActual = properties.getForm(Form.MAIN).getWidget("saveTo").isHidden();
        boolean chunkModeActual = properties.getForm(Form.ADVANCED).getWidget("chunkMode").isHidden();
        boolean schemaActual = properties.getForm(Form.MAIN).getWidget("schema").isHidden();

        assertEquals(connectionExpected, connectionActual);
        assertEquals(pathExpected, pathActual);
        assertEquals(saveAsFileExpected, saveAsFileActual);
        assertEquals(saveToExpected, saveToActual);
        assertEquals(chunkModeExpected, chunkModeActual);
        assertEquals(schemaExpected, schemaActual);
    }

    /**
     * Checks {@link TDropboxGetProperties#setupLayout()} creates Main form,
     * which contains 6 widgets: Connection, Path, Save As File, Save To, Content Type and Schema
     * and Advanced form, which contains 1 widget: Chunk Mode
     */
    @Test
    public void testSetupLayout() {
        TDropboxGetProperties properties = new TDropboxGetProperties("root");
        properties.connection.setupLayout();
        properties.schema.setupLayout();
        properties.chunkMode.setupLayout();
        properties.setupLayout();

        Form main = properties.getForm(Form.MAIN);
        assertThat(main, notNullValue());
        Form advanced = properties.getForm(Form.ADVANCED);
        assertThat(advanced, notNullValue());

        Collection<Widget> mainWidgets = main.getWidgets();
        assertThat(mainWidgets, hasSize(6));

        Widget connectionWidget = main.getWidget("connection");
        assertThat(connectionWidget, notNullValue());
        Widget pathWidget = main.getWidget("path");
        assertThat(pathWidget, notNullValue());
        Widget saveAsFileWidget = main.getWidget("saveAsFile");
        assertThat(saveAsFileWidget, notNullValue());
        Widget saveToWidget = main.getWidget("saveTo");
        assertThat(saveToWidget, notNullValue());
        Widget contentTypeWidget = main.getWidget("contentType");
        assertThat(contentTypeWidget, notNullValue());
        Widget schemaWidget = main.getWidget("schema");
        assertThat(schemaWidget, notNullValue());

        Collection<Widget> advancedWidgets = advanced.getWidgets();
        assertThat(advancedWidgets, hasSize(1));

        Widget chunkModeWidget = advanced.getWidget("chunkMode");
        assertThat(chunkModeWidget, notNullValue());
    }

    /**
     * Checks {@link TDropboxGetProperties#getAllSchemaPropertiesConnectors(boolean)} returns empty {@link Set}, when
     * false is passed
     */
    @Test
    public void testGetAllSchemaPropertiesConnectorsInput() {
        TDropboxGetProperties properties = new TDropboxGetProperties("root");

        Set<PropertyPathConnector> connectors = properties.getAllSchemaPropertiesConnectors(false);
        assertThat(connectors, is(empty()));
    }

    /**
     * Checks {@link TDropboxGetProperties#getAllSchemaPropertiesConnectors(boolean)} returns {@link Set} with 1
     * connector, when true is passed
     */
    @Test
    public void testGetAllSchemaPropertiesConnectorsOutput() {
        TDropboxGetProperties properties = new TDropboxGetProperties("root");

        Set<PropertyPathConnector> connectors = properties.getAllSchemaPropertiesConnectors(true);
        assertThat(connectors, hasSize(1));
    }
}
