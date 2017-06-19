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

package org.talend.components.salesforce;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Before;
import org.junit.Test;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.salesforce.common.SalesforceRuntimeSourceOrSink;
import org.talend.components.salesforce.schema.SalesforceSchemaHelper;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.service.PropertiesService;
import org.talend.daikon.properties.service.PropertiesServiceImpl;
import org.talend.daikon.properties.service.Repository;
import org.talend.daikon.sandbox.SandboxedInstance;

/**
 *
 */
public class SalesforceModuleListPropertiesTest {

    public static final Schema DEFAULT_SCHEMA_1 = SchemaBuilder.builder().record("Schema").fields() //
            .name("Id").prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type().stringType().noDefault() //
            .name("Name").type().stringType().noDefault() //
            .endRecord();

    public static final Schema DEFAULT_SCHEMA_2 = SchemaBuilder.builder().record("Schema").fields() //
            .name("Id").prop(SchemaConstants.TALEND_COLUMN_IS_KEY, "true").type().stringType().noDefault() //
            .name("FirstName").type().stringType().noDefault() //
            .name("LastName").type().stringType().noDefault() //
            .endRecord();

    private final String connectionName = "___TEST";

    private final String repoLocation = "___DRI";

    private PropertiesService propertiesService;

    private SalesforceConnectionProperties connectionProperties;

    private SalesforceModuleListProperties properties;

    @Before
    public void setUp() {
        propertiesService = new PropertiesServiceImpl();

        connectionProperties = new SalesforceConnectionProperties("connection");
        connectionProperties.name.setValue(connectionName);

        properties = new SalesforceModuleListProperties("root");
        properties.setConnection(connectionProperties);
        properties.setRepositoryLocation(repoLocation);
    }

    @Test
    public void testSetupProperties() {
        properties.setupProperties();

        assertNotNull(properties.getConnectionProperties());
        assertEquals(connectionProperties, properties.getConnectionProperties());
        assertNotNull(properties.getRepositoryLocation());
        assertEquals(repoLocation, properties.getRepositoryLocation());
    }

    @Test
    public void testSetupLayout() {
        properties.init();

        Form mainForm = properties.getForm(Form.MAIN);
        assertNotNull(mainForm.getWidget(properties.selectedModuleNames.getName()));
    }

    @Test
    public void testRefreshLayout() {
        properties.init();

        properties.refreshLayout(properties.getForm(Form.MAIN));
        Form mainForm = properties.getForm(Form.MAIN);
        assertNotNull(mainForm.getWidget(properties.selectedModuleNames.getName()));
    }

    @Test
    public void testBeforeFormPresentMain() throws Throwable {
        properties.init();

        try (SandboxedInstanceTestFixture sandboxedInstanceTestFixture = new SandboxedInstanceTestFixture()) {
            sandboxedInstanceTestFixture.setUp();

            propertiesService.beforeFormPresent("Main", properties);

            assertThat((Iterable<NamedThing>) properties.selectedModuleNames.getPossibleValues(),
                    containsInAnyOrder((NamedThing) new SimpleNamedThing("Account"), new SimpleNamedThing("Customer")));

            Form mainForm = properties.getForm(Form.MAIN);
            assertTrue(mainForm.isAllowBack());
            assertTrue(mainForm.isAllowFinish());
        }
    }

    @Test
    public void testAfterFormFinishMain() throws Throwable {
        properties.init();

        List<TestRepository.Entry> repoEntries = new ArrayList<>();
        TestRepository repository = new TestRepository(repoEntries);
        propertiesService.setRepository(repository);

        try (SandboxedInstanceTestFixture sandboxedInstanceTestFixture = new SandboxedInstanceTestFixture()) {
            sandboxedInstanceTestFixture.setUp();

            properties.selectedModuleNames.setValue(Arrays.<NamedThing>asList(
                    new SimpleNamedThing("Account"), new SimpleNamedThing("Customer")));

            propertiesService.afterFormFinish("Main", properties);

            assertEquals(3, repoEntries.size());

            // Connection entry

            TestRepository.Entry connRepoEntry = repoEntries.get(0);
            assertEquals(connectionName, connRepoEntry.getName());
            assertThat(connRepoEntry.getProperties(), instanceOf(SalesforceConnectionProperties.class));

            // Module entry 1

            TestRepository.Entry repoEntry1 = repoEntries.get(1);

            assertEquals("Account", repoEntry1.getName());
            assertEquals("main.schema", repoEntry1.getSchemaPropertyName());
            assertEquals(DEFAULT_SCHEMA_1, repoEntry1.getSchema());
            assertThat(repoEntry1.getProperties(), instanceOf(SalesforceModuleProperties.class));

            SalesforceModuleProperties modProps1 = (SalesforceModuleProperties) repoEntry1.getProperties();
            assertEquals(connectionProperties, modProps1.getConnectionProperties());
            assertEquals("Account", modProps1.moduleName.getValue());
            assertEquals(DEFAULT_SCHEMA_1, modProps1.main.schema.getValue());
            assertNotNull(modProps1.getForm(Form.MAIN));

            // Module entry 2

            TestRepository.Entry repoEntry2 = repoEntries.get(2);

            assertEquals("Customer", repoEntry2.getName());
            assertEquals("main.schema", repoEntry2.getSchemaPropertyName());
            assertEquals(DEFAULT_SCHEMA_2, repoEntry2.getSchema());
            assertThat(repoEntry2.getProperties(), instanceOf(SalesforceModuleProperties.class));

            SalesforceModuleProperties modProps2 = (SalesforceModuleProperties) repoEntry2.getProperties();
            assertEquals(connectionProperties, modProps2.getConnectionProperties());
            assertEquals("Customer", modProps2.moduleName.getValue());
            assertEquals(DEFAULT_SCHEMA_2, modProps2.main.schema.getValue());
            assertNotNull(modProps2.getForm(Form.MAIN));
        }
    }

    class SandboxedInstanceTestFixture extends TestFixtureBase {

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
            when(runtimeSourceOrSink.initialize(any(RuntimeContainer.class), eq(properties)))
                    .thenReturn(ValidationResult.OK);
            when(runtimeSourceOrSink.validate(any(RuntimeContainer.class)))
                    .thenReturn(ValidationResult.OK);

            List<NamedThing> moduleNames = Arrays.<NamedThing>asList(
                    new SimpleNamedThing("Account"),
                    new SimpleNamedThing("Customer")
            );

            when(runtimeSourceOrSink.getSchemaNames(any(RuntimeContainer.class)))
                    .thenReturn(moduleNames);

            when(runtimeSourceOrSink.getEndpointSchema(any(RuntimeContainer.class), eq("Account")))
                    .thenReturn(DEFAULT_SCHEMA_1);

            when(runtimeSourceOrSink.getEndpointSchema(any(RuntimeContainer.class), eq("Customer")))
                    .thenReturn(DEFAULT_SCHEMA_2);
        }

        @Override
        public void tearDown() throws Exception {
            SalesforceDefinition.setSandboxedInstanceProvider(
                    SalesforceDefinition.SandboxedInstanceProvider.INSTANCE);
        }
    }

    static class TestRepository implements Repository {

        private int locationNum;

        private String componentIdToCheck;

        private ComponentProperties properties;

        private List<Entry> repoEntries;

        TestRepository(List<Entry> repoEntries) {
            this.repoEntries = repoEntries;
        }

        public String getComponentIdToCheck() {
            return componentIdToCheck;
        }

        public void setComponentIdToCheck(String componentIdToCheck) {
            this.componentIdToCheck = componentIdToCheck;
        }

        public void setProperties(ComponentProperties properties) {
            this.properties = properties;
        }

        public void setRepoEntries(List<Entry> repoEntries) {
            this.repoEntries = repoEntries;
        }

        public ComponentProperties getProperties() {
            return properties;
        }

        public List<Entry> getRepoEntries() {
            return repoEntries;
        }

        @Override
        public String storeProperties(Properties properties, String name, String repositoryLocation, String schemaPropertyName) {
            Entry entry = new Entry(properties, name, repositoryLocation, schemaPropertyName);
            repoEntries.add(entry);
            return repositoryLocation + ++locationNum;
        }

        static class Entry {

            private Properties properties;

            private String name;

            private String repoLocation;

            private Schema schema;

            private String schemaPropertyName;

            Entry(Properties properties, String name, String repoLocation, String schemaPropertyName) {
                this.properties = properties;
                this.name = name;
                this.repoLocation = repoLocation;
                this.schemaPropertyName = schemaPropertyName;
                if (schemaPropertyName != null) {
                    this.schema = (Schema) properties.getValuedProperty(schemaPropertyName).getValue();
                }
            }

            public Properties getProperties() {
                return properties;
            }

            public String getName() {
                return name;
            }

            public String getRepoLocation() {
                return repoLocation;
            }

            public Schema getSchema() {
                return schema;
            }

            public String getSchemaPropertyName() {
                return schemaPropertyName;
            }

            @Override
            public String toString() {
                return "Entry: " + repoLocation + "/" + name + " properties: " + properties;
            }
        }
    }

}
