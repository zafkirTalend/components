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

package org.talend.components.salesforce.datastore;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.talend.components.api.component.runtime.JarRuntimeInfo;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.common.dataset.DatasetProperties;
import org.talend.components.salesforce.SalesforceDefinition;
import org.talend.components.salesforce.TestFixture;
import org.talend.components.salesforce.common.SalesforceRuntimeSourceOrSink;
import org.talend.components.salesforce.dataprep.SalesforceInputDefinition;
import org.talend.components.salesforce.dataprep.SalesforceInputProperties;
import org.talend.components.salesforce.schema.SalesforceSchemaHelper;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.definition.DefinitionImageType;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.runtime.RuntimeInfo;
import org.talend.daikon.sandbox.SandboxedInstance;

/**
 *
 */
public class SalesforceDatastoreDefinitionTest {

    private SalesforceDatastoreDefinition definition;
    private SalesforceDatastoreProperties properties;

    @Before
    public void setUp() {
        definition = new SalesforceDatastoreDefinition();

        properties = new SalesforceDatastoreProperties("root");
        properties.init();
    }

    @Test
    public void testRuntimeInfo() {
        RuntimeInfo runtimeInfo = definition.getRuntimeInfo(properties);
        assertRuntimeInfo(runtimeInfo);
    }

    private void assertRuntimeInfo(RuntimeInfo runtimeInfo) {
        assertNotNull(runtimeInfo);
        assertThat(runtimeInfo, instanceOf(JarRuntimeInfo.class));

        JarRuntimeInfo jarRuntimeInfo = (JarRuntimeInfo) runtimeInfo;
        assertNotNull(jarRuntimeInfo.getJarUrl());
        assertNotNull(jarRuntimeInfo.getDepTxtPath());
        assertEquals("org.talend.components.salesforce.runtime.dataprep.SalesforceDatastoreRuntime",
                jarRuntimeInfo.getRuntimeClassName());
    }

    @Test
    public void testCreateDatasetProperties() throws Exception {

        SandboxedInstanceTestFixture sandboxedInstanceTestFixture = new SandboxedInstanceTestFixture();
        sandboxedInstanceTestFixture.setUp();

        DatasetProperties datasetProperties = definition.createDatasetProperties(properties);
        assertEquals(properties, datasetProperties.getDatastoreProperties());

        Form mainForm = properties.getForm(Form.MAIN);
        assertNotNull(mainForm);
        assertNotNull(mainForm.getWidget(properties.userId.getName()));
        assertNotNull(mainForm.getWidget(properties.password.getName()));
        assertNotNull(mainForm.getWidget(properties.securityKey.getName()));
    }

    @Test
    public void testInputComponentDefinitionName() {
        assertEquals(SalesforceInputDefinition.NAME, definition.getInputCompDefinitionName());
    }

    @Test
    public void testOutputComponentDefinitionName() {
        assertNull(definition.getOutputCompDefinitionName());
    }

    @Test
    public void testImagePath() {
        assertNotNull(definition.getImagePath(DefinitionImageType.PALETTE_ICON_32X32));
        assertNull(definition.getImagePath(DefinitionImageType.SVG_ICON));
    }

    class SandboxedInstanceTestFixture implements TestFixture {

        SandboxedInstance sandboxedInstance;
        SalesforceRuntimeSourceOrSink runtimeSourceOrSink;

        @Override
        public void setUp() throws Exception {
            SalesforceDefinition.SandboxedInstanceProvider sandboxedInstanceProvider = mock(
                    SalesforceDefinition.SandboxedInstanceProvider.class);
            SalesforceDefinition.setSandboxedInstanceProvider(sandboxedInstanceProvider);

            sandboxedInstance = mock(SandboxedInstance.class);
            when(sandboxedInstanceProvider.getSandboxedInstance(anyString(), anyBoolean()))
                    .thenReturn(sandboxedInstance);

            runtimeSourceOrSink = mock(SalesforceRuntimeSourceOrSink.class,
                    withSettings().extraInterfaces(SalesforceSchemaHelper.class));
            doReturn(runtimeSourceOrSink).when(sandboxedInstance).getInstance();
            when(runtimeSourceOrSink.initialize(any(RuntimeContainer.class), any(SalesforceInputProperties.class)))
                    .thenReturn(ValidationResult.OK);
            when(runtimeSourceOrSink.validate(any(RuntimeContainer.class)))
                    .thenReturn(ValidationResult.OK);

            List<NamedThing> moduleNames = Arrays.<NamedThing>asList(
                    new SimpleNamedThing("Account"),
                    new SimpleNamedThing("Customer")
            );

            when(runtimeSourceOrSink.getSchemaNames(any(RuntimeContainer.class)))
                    .thenReturn(moduleNames);
        }

        @Override
        public void tearDown() throws Exception {
            SalesforceDefinition.setSandboxedInstanceProvider(
                    SalesforceDefinition.SandboxedInstanceProvider.INSTANCE);
        }
    }
}
