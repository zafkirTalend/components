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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.container.DefaultComponentRuntimeContainerImpl;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.service.common.ComponentServiceImpl;
import org.talend.components.api.service.common.DefinitionRegistry;
import org.talend.components.api.test.AbstractComponentTest;
import org.talend.components.salesforce.SalesforceConnectionModuleProperties;
import org.talend.components.salesforce.SalesforceConnectionProperties;
import org.talend.components.salesforce.SalesforceFamilyDefinition;
import org.talend.components.salesforce.SalesforceModuleProperties;
import org.talend.components.salesforce.SalesforceOutputProperties.OutputAction;
import org.talend.components.salesforce.runtime.SalesforceSink;
import org.talend.components.salesforce.runtime.SalesforceSource;
import org.talend.components.salesforce.runtime.SalesforceWriteOperation;
import org.talend.components.salesforce.tsalesforceinput.TSalesforceInputProperties;
import org.talend.components.salesforce.tsalesforceoutput.TSalesforceOutputProperties;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.test.PropertiesTestUtils;

@SuppressWarnings("nls")
public class SalesforceTestBase extends AbstractComponentTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesforceTestBase.class);

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    private ComponentService componentService;

    protected static RuntimeContainer adaptor = new DefaultComponentRuntimeContainerImpl();

    public static final boolean ADD_QUOTES = true;

    static public final String userId = System.getProperty("salesforce.user");

    static public final String password = System.getProperty("salesforce.password");

    static public final String securityToken = System.getProperty("salesforce.key");

    public SalesforceTestBase() {
    }

    public static String createNewRandom() {
        return Integer.toString(ThreadLocalRandom.current().nextInt(1, 1000000));
    }

    @Before
    public void initializeComponentRegistryAndService() {
        // reset the component service
        componentService = null;
    }

    // default implementation for pure java test. Shall be overriden of Spring or OSGI tests
    @Override
    public ComponentService getComponentService() {
        if (componentService == null) {
            DefinitionRegistry testComponentRegistry = new DefinitionRegistry();
            // register component
            testComponentRegistry.registerComponentFamilyDefinition(new SalesforceFamilyDefinition());
            componentService = new ComponentServiceImpl(testComponentRegistry);
        }
        return componentService;
    }

    protected ComponentProperties checkAndAfter(Form form, String propName, ComponentProperties props) throws Throwable {
        assertTrue(form.getWidget(propName).isCallAfter());
        ComponentProperties afterProperty = (ComponentProperties) getComponentService().afterProperty(propName, props);
        assertEquals(
                "ComponentProperties after failed[" + props.getClass().getCanonicalName() + "/after"
                        + StringUtils.capitalize(propName) + "] :" + afterProperty.getValidationResult().getMessage(),
                ValidationResult.Result.OK, afterProperty.getValidationResult().getStatus());
        return afterProperty;
    }

    static public SalesforceConnectionProperties setupProps(SalesforceConnectionProperties props, boolean addQuotes) {
        if (props == null) {
            props = (SalesforceConnectionProperties) new SalesforceConnectionProperties("foo").init();
        }
        Properties userPassword = (Properties) props.getProperty("userPassword");
        ((Property) userPassword.getProperty("userId")).setValue(addQuotes ? "\"" + userId + "\"" : userId);
        ((Property) userPassword.getProperty("password")).setValue(addQuotes ? "\"" + password + "\"" : password);
        ((Property) userPassword.getProperty("securityToken")).setValue(addQuotes ? "\"" + securityToken + "\"" : securityToken);
        return props;
    }

    public static final String EXISTING_MODULE_NAME = "Account";

    public static final String NOT_EXISTING_MODULE_NAME = "foobar";

    public static final String TEST_KEY = "Address2 456";

    protected void setupModule(SalesforceModuleProperties moduleProps, String module) throws Throwable {
        Form f = moduleProps.getForm(Form.REFERENCE);
        moduleProps = (SalesforceModuleProperties) PropertiesTestUtils.checkAndBeforeActivate(getComponentService(), f,
                "moduleName", moduleProps);
        moduleProps.moduleName.setValue(module);
        moduleProps = (SalesforceModuleProperties) checkAndAfter(f, "moduleName", moduleProps);
    }

    protected void setupModuleWithEmptySchema(SalesforceModuleProperties moduleProps, String module) throws Throwable {
        Form f = moduleProps.getForm(Form.REFERENCE);
        moduleProps = (SalesforceModuleProperties) PropertiesTestUtils.checkAndBeforeActivate(getComponentService(), f,
                "moduleName", moduleProps);
        moduleProps.moduleName.setValue(module);
        Schema emptySchema = Schema.createRecord(module, null, null, false);
        emptySchema.setFields(new ArrayList<Schema.Field>());
        emptySchema = AvroUtils.setIncludeAllFields(emptySchema, true);
        moduleProps.main.schema.setValue(emptySchema);
    }

    public static Schema getSchema(boolean isDynamic) {
        SchemaBuilder.FieldAssembler<Schema> fa = SchemaBuilder.builder().record("MakeRowRecord").fields() //
                .name("Id").type().nullable().stringType().noDefault() //
                .name("Name").type().nullable().stringType().noDefault() //
                .name("ShippingStreet").type().nullable().stringType().noDefault() //
                .name("ShippingPostalCode").type().nullable().intType().noDefault() //
                .name("BillingStreet").type().nullable().stringType().noDefault() //
                .name("BillingState").type().nullable().stringType().noDefault() //
                .name("BillingPostalCode").type().nullable().stringType().noDefault();
        if (isDynamic) {
            fa = fa.name("ShippingState").type().nullable().stringType().noDefault();
        }

        return fa.endRecord();
    }

    public Schema getMakeRowSchema(boolean isDynamic) {
        return getSchema(isDynamic);
    }

    public List<IndexedRecord> makeRows(String random, int count, boolean isDynamic) {
        List<IndexedRecord> outputRows = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            GenericData.Record row = new GenericData.Record(getMakeRowSchema(isDynamic));
            row.put("Name", "TestName");
            row.put("ShippingStreet", TEST_KEY);
            row.put("ShippingPostalCode", Integer.toString(i));
            row.put("BillingStreet", "123 Main Street");
            row.put("BillingState", "CA");
            row.put("BillingPostalCode", random);
            if (isDynamic) {
                row.put("ShippingState", "CA");
            }
            LOGGER.debug("Row to insert: " + row.get("Name") //
                    + " id: " + row.get("Id") //
                    + " shippingPostalCode: " + row.get("ShippingPostalCode") //
                    + " billingPostalCode: " + row.get("BillingPostalCode") //
                    + " billingStreet: " + row.get("BillingStreet"));
            outputRows.add(row);
        }
        return outputRows;
    }

    protected List<IndexedRecord> checkRows(String random, List<IndexedRecord> rows, int count) {
        List<IndexedRecord> checkedRows = new ArrayList<>();

        Schema rowSchema = null;
        int iName = 0;
        int iId = 0;
        int iBillingPostalCode = 0;
        int iBillingStreet = 0;
        int iShippingStreet = 0;
        int iShippingState = 0;
        int iBillingState = 0;

        int checkCount = 0;
        for (IndexedRecord row : rows) {
            if (rowSchema == null) {
                rowSchema = row.getSchema();
                iName = rowSchema.getField("Name").pos();
                iId = rowSchema.getField("Id").pos();
                iBillingPostalCode = rowSchema.getField("BillingPostalCode").pos();
                iBillingStreet = rowSchema.getField("BillingStreet").pos();
                iBillingState = rowSchema.getField("BillingState").pos();
                iShippingStreet = rowSchema.getField("ShippingStreet").pos();
                if (rowSchema.getField("ShippingState") != null) {
                    iShippingState = rowSchema.getField("ShippingState").pos();
                }
            }

            LOGGER.debug("check: " + row.get(iName) + " id: " + row.get(iId) + " post: " + row.get(iBillingPostalCode) + " st: "
                    + " post: " + row.get(iBillingStreet));
            String check = (String) row.get(iShippingStreet);
            if (check == null || !check.equals(SalesforceTestBase.TEST_KEY)) {
                continue;
            }
            check = (String) row.get(iBillingPostalCode);
            if (check == null || !check.equals(random)) {
                continue;
            }
            checkCount++;
            if (rowSchema.getField("ShippingState") != null) {
                assertEquals("CA", row.get(iShippingState));
            }
            assertEquals("TestName", row.get(iName));
            assertEquals("123 Main Street", row.get(iBillingStreet));
            assertEquals("CA", row.get(iBillingState));
            checkedRows.add(row);
        }
        assertEquals(count, checkCount);
        return checkedRows;
    }

    public List<String> getDeleteIds(List<IndexedRecord> rows) {
        List<String> ids = new ArrayList<>();
        for (IndexedRecord row : rows) {
            LOGGER.debug("del: " + row.get(row.getSchema().getField("Name").pos()) + " id: "
                    + row.get(row.getSchema().getField("Id").pos()) + " post: "
                    + row.get(row.getSchema().getField("BillingPostalCode").pos()) + " st: " + " post: "
                    + row.get(row.getSchema().getField("BillingStreet").pos()));
            String check = (String) row.get(row.getSchema().getField("ShippingStreet").pos());
            if (check == null || !check.equals(SalesforceTestBase.TEST_KEY)) {
                continue;
            }
            ids.add((String) row.get(row.getSchema().getField("Id").pos()));
        }
        return ids;
    }

    /**
     * @return the list of row match the TEST_KEY, and if a random values it specified it also filter row against the
     */
    public List<IndexedRecord> filterAllTestRows(String random, List<IndexedRecord> rows) {
        List<IndexedRecord> checkedRows = new ArrayList<>();

        for (IndexedRecord row : rows) {
            String check = (String) row.get(row.getSchema().getField("ShippingStreet").pos());
            if (check == null || !check.equals(TEST_KEY)) {
                continue;
            }
            if (random != null) {// check the random value if specified
                check = (String) row.get(row.getSchema().getField("BillingPostalCode").pos());
                if (check == null || !check.equals(random)) {
                    continue;
                }
            }
            LOGGER.debug("Found match: " + row.get(row.getSchema().getField("Name").pos()) //
                    + " id: " + row.get(row.getSchema().getField("Id").pos()) //
                    + " shippingPostalCode: " + row.get(row.getSchema().getField("ShippingPostalCode").pos()) //
                    + " billingPostalCode: " + row.get(row.getSchema().getField("BillingPostalCode").pos()) //
                    + " billingStreet: " + row.get(row.getSchema().getField("BillingStreet").pos())); //
            checkedRows.add(row);
        }
        return checkedRows;
    }

    static public List<IndexedRecord> getAllTestRows(List<IndexedRecord> rows) {
        List<IndexedRecord> checkedRows = new ArrayList<>();

        for (IndexedRecord row : rows) {
            String check = (String) row.get(row.getSchema().getField("ShippingStreet").pos());
            if (check == null || !check.equals(TEST_KEY)) {
                continue;
            }
            LOGGER.debug("Test row is: " + row.get(row.getSchema().getField("Name").pos()) + " id: "
                    + row.get(row.getSchema().getField("Id").pos()) + " post: "
                    + row.get(row.getSchema().getField("BillingPostalCode").pos()) + " st: " + " post: "
                    + row.get(row.getSchema().getField("BillingStreet").pos()));
            checkedRows.add(row);
        }
        return checkedRows;
    }

    protected List<IndexedRecord> readRows(TSalesforceInputProperties inputProps) throws IOException {
        BoundedReader<IndexedRecord> reader = createBoundedReader(inputProps);
        boolean hasRecord = reader.start();
        List<IndexedRecord> rows = new ArrayList<>();
        while (hasRecord) {
            org.apache.avro.generic.IndexedRecord unenforced = reader.getCurrent();
            rows.add(unenforced);
            hasRecord = reader.advance();
        }
        return rows;
    }

    protected List<IndexedRecord> readRows(SalesforceConnectionModuleProperties props) throws IOException {
        TSalesforceInputProperties inputProps = (TSalesforceInputProperties) new TSalesforceInputProperties("bar").init();
        inputProps.connection = props.connection;
        inputProps.module = props.module;
        inputProps.batchSize.setValue(200);
        inputProps.queryMode.setValue(TSalesforceInputProperties.QueryMode.Query);
        List<IndexedRecord> inputRows = readRows(inputProps);
        return inputRows;
    }

    List<IndexedRecord> readAndCheckRows(String random, SalesforceConnectionModuleProperties props, int count) throws Exception {
        List<IndexedRecord> inputRows = readRows(props);
        return checkRows(random, inputRows, count);
    }

    protected void checkRows(List<IndexedRecord> outputRows, SalesforceConnectionModuleProperties props) throws Exception {
        List<IndexedRecord> inputRows = readRows(props);
        assertThat(inputRows, containsInAnyOrder(outputRows.toArray()));
    }

    protected void checkAndDelete(String random, SalesforceConnectionModuleProperties props, int count) throws Exception {
        List<IndexedRecord> inputRows = readAndCheckRows(random, props, count);
        deleteRows(inputRows, props);
        readAndCheckRows(random, props, 0);
    }

    public static <T> T writeRows(Writer<T> writer, List<IndexedRecord> outputRows) throws IOException {
        T result;
        writer.open("foo");
        try {
            for (IndexedRecord row : outputRows) {
                writer.write(row);
            }
        } finally {
            result = writer.close();
        }
        return result;
    }

    protected static void writeRows(List<IndexedRecord> outputRows) throws Exception {
        TSalesforceOutputProperties outputProps = createSalesforceoutputProperties(EXISTING_MODULE_NAME);// $NON-NLS-1$
        outputProps.outputAction.setValue(TSalesforceOutputProperties.OutputAction.INSERT);
        doWriteRows(outputProps, outputRows);
    }

    // Returns the rows written (having been re-read so they have their Ids)
    protected static void doWriteRows(SalesforceConnectionModuleProperties props, List<IndexedRecord> outputRows) throws Exception {
        SalesforceSink salesforceSink = new SalesforceSink();
        salesforceSink.initialize(adaptor, props);
        salesforceSink.validate(adaptor);
        SalesforceWriteOperation writeOperation = salesforceSink.createWriteOperation();
        Writer<Result> saleforceWriter = writeOperation.createWriter(adaptor);
        writeRows(saleforceWriter, outputRows);
    }
    // Returns the rows written (having been re-read so they have their Ids)
    protected List<IndexedRecord> writeRows(String random, SalesforceConnectionModuleProperties props,
            List<IndexedRecord> outputRows) throws Exception {
        TSalesforceOutputProperties outputProps = new TSalesforceOutputProperties("output"); //$NON-NLS-1$
        outputProps.copyValuesFrom(props);
        outputProps.outputAction.setValue(TSalesforceOutputProperties.OutputAction.INSERT);
        doWriteRows(outputProps, outputRows);
        return readAndCheckRows(random, props, outputRows.size());
    }

    protected void deleteRows(List<IndexedRecord> rows, SalesforceConnectionModuleProperties props) throws Exception {
        TSalesforceOutputProperties deleteProperties = new TSalesforceOutputProperties("delete"); //$NON-NLS-1$
        deleteProperties.copyValuesFrom(props);
        deleteProperties.outputAction.setValue(OutputAction.DELETE);
        LOGGER.debug("deleting " + rows.size() + " rows");
        doWriteRows(deleteProperties, rows);
    }

    public <T> BoundedReader<T> createSalesforceInputReaderFromModule(String moduleName, TSalesforceInputProperties properties) {
        if (null == properties) {
            properties = (TSalesforceInputProperties) new TSalesforceInputProperties("foo").init(); //$NON-NLS-1$
        }
        setupProps(properties.connection, !ADD_QUOTES);
        properties.batchSize.setValue(200);
        properties.module.moduleName.setValue(moduleName);
        properties.module.main.schema.setValue(
                SchemaBuilder.builder().record("test").prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true").fields().endRecord());
        return createBoundedReader(properties);
    }

    public <T> BoundedReader<T> createBoundedReader(ComponentProperties tsip) {
        SalesforceSource salesforceSource = new SalesforceSource();
        salesforceSource.initialize(null, tsip);
        salesforceSource.validate(null);
        return salesforceSource.createReader(null);
    }

    public static void deleteAllAccountTestRows(String condition) throws Exception {
        TSalesforceInputProperties properties = (TSalesforceInputProperties) new TSalesforceInputProperties("foo").init();
        properties.condition.setValue("Name = '" + condition + "'");
        BoundedReader<?> salesforceInputReader = new SalesforceTestBase()
                .createSalesforceInputReaderFromModule(EXISTING_MODULE_NAME, properties);
        // getting all rows

        List<IndexedRecord> rows = new ArrayList<>();
        try {
            salesforceInputReader.start();
            rows.add((IndexedRecord) salesforceInputReader.getCurrent());
            while (salesforceInputReader.advance()) {
                rows.add((IndexedRecord) salesforceInputReader.getCurrent());
            }
        } finally {
            salesforceInputReader.close();
        }
        // filtering rows
        TSalesforceOutputProperties salesforceoutputProperties = createSalesforceoutputProperties(EXISTING_MODULE_NAME);
        setupProps(salesforceoutputProperties.connection, !ADD_QUOTES);
        new SalesforceTestBase().deleteRows(rows, salesforceoutputProperties);
    }

    private static TSalesforceOutputProperties createSalesforceoutputProperties(String moduleName) throws Exception {
        TSalesforceOutputProperties props = (TSalesforceOutputProperties) new TSalesforceOutputProperties("foo").init();
        setupProps(props.connection, !ADD_QUOTES);
        props.module.moduleName.setValue(moduleName);
        props.module.afterModuleName();// to setup schema.
        return props;
    }

}
