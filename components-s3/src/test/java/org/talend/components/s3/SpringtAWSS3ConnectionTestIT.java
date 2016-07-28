package org.talend.components.s3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.test.AbstractComponentTest;
import org.talend.components.api.test.ComponentTestUtils;
import org.talend.components.api.test.SpringTestApp;
import org.talend.components.s3.tawss3connection.TAwsS3ConnectionDefinition;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringTestApp.class)
public class SpringtAWSS3ConnectionTestIT extends AbstractComponentTest {

    @Inject
    private ComponentService componentService;

    public ComponentService getComponentService() {
        return componentService;
    }

    @Test
    public void testAfterInheritFromAwsRole() throws Throwable {
        ComponentProperties props;

        props = new TAwsS3ConnectionDefinition().createProperties();
        ComponentTestUtils.checkSerialize(props, errorCollector);
        Property<Boolean> inheritFromAwsRole = (Property<Boolean>) props.getProperty("inheritFromAwsRole");
        assertEquals(false, inheritFromAwsRole.getValue());
        Form mainForm = props.getForm(Form.MAIN);
        assertFalse(mainForm.getWidget("accessSecretKeyProperties").isHidden());

        inheritFromAwsRole.setValue(true);
        props = (ComponentProperties) checkAndAfter(mainForm, "inheritFromAwsRole", props);
        mainForm = props.getForm(Form.MAIN);
        assertTrue(mainForm.isRefreshUI());

        assertTrue(mainForm.getWidget("accessSecretKeyProperties").isHidden());
    }

    @Test
    public void testEncryptionProperties() throws Throwable {
        ComponentProperties props;

        props = new TAwsS3ConnectionDefinition().createProperties();
        ComponentTestUtils.checkSerialize(props, errorCollector);
        Property<Boolean> encrypt = (Property<Boolean>) props.getProperty("encrypt");
        assertEquals(false, encrypt.getValue());
        Form mainForm = props.getForm(Form.MAIN);
        assertTrue(mainForm.getWidget("encryptionProperties").isHidden());

        encrypt.setValue(true);
        props = (ComponentProperties) checkAndAfter(mainForm, "encrypt", props);
        mainForm = props.getForm(Form.MAIN);
        assertTrue(mainForm.isRefreshUI());

        assertFalse(mainForm.getWidget("encryptionProperties").isHidden());

        AwsS3ConnectionEncryptionProperties encryptionProperties = (AwsS3ConnectionEncryptionProperties) props
                .getProperty("encryptionProperties");

        assertEquals(EncryptionKeyType.KMS_MANAGED_CUSTOMER_MASTER_KEY, encryptionProperties.encryptionKeyType.getValue());
        Form mainEncForm = encryptionProperties.getForm(Form.MAIN);
        assertFalse(mainEncForm.getWidget("kmsCmkProperties").isHidden());
        assertTrue(mainEncForm.getWidget("symmetricKeyProperties").isHidden());
        assertTrue(mainEncForm.getWidget("asymmetricKeyProperties").isHidden());

        encryptionProperties.encryptionKeyType.setValue(EncryptionKeyType.SYMMETRIC_MASTER_KEY);
        encryptionProperties = (AwsS3ConnectionEncryptionProperties) checkAndAfter(mainEncForm, "encryptionKeyType",
                encryptionProperties);
        assertTrue(mainEncForm.getWidget("kmsCmkProperties").isHidden());
        assertFalse(mainEncForm.getWidget("symmetricKeyProperties").isHidden());
        assertTrue(mainEncForm.getWidget("asymmetricKeyProperties").isHidden());

        AwsS3SymmetricKeyEncryptionProperties symmetricKeyPropeties = encryptionProperties.symmetricKeyProperties;
        assertEquals(Algorithm.AES, symmetricKeyPropeties.algorithm.getValue());
        assertEquals(Encoding.BASE_64, symmetricKeyPropeties.encoding.getValue());

        Form mainSymmetricForm = symmetricKeyPropeties.getForm(Form.MAIN);
        assertFalse(mainSymmetricForm.getWidget("key").isHidden());
        assertTrue(mainSymmetricForm.getWidget("keyFilePath").isHidden());

        symmetricKeyPropeties.encoding.setValue(Encoding.X509);
        symmetricKeyPropeties = (AwsS3SymmetricKeyEncryptionProperties) checkAndAfter(mainSymmetricForm, "encoding",
                symmetricKeyPropeties);
        assertTrue(mainSymmetricForm.getWidget("key").isHidden());
        assertFalse(mainSymmetricForm.getWidget("keyFilePath").isHidden());

        encryptionProperties.encryptionKeyType.setValue(EncryptionKeyType.ASYMMETRIC_MASTER_KEY);
        encryptionProperties = (AwsS3ConnectionEncryptionProperties) checkAndAfter(mainEncForm, "encryptionKeyType",
                encryptionProperties);
        assertTrue(mainEncForm.getWidget("kmsCmkProperties").isHidden());
        assertTrue(mainEncForm.getWidget("symmetricKeyProperties").isHidden());
        assertFalse(mainEncForm.getWidget("asymmetricKeyProperties").isHidden());

        AwsS3AsymmetricKeyEncryptionProperties asymmetricKeyPropeties = encryptionProperties.asymmetricKeyProperties;
        assertEquals(Algorithm.RSA, asymmetricKeyPropeties.algorithm.getValue());
    }

    @Test
    public void testAfterConfigClient() throws Throwable {
        ComponentProperties props;

        props = new TAwsS3ConnectionDefinition().createProperties();
        ComponentTestUtils.checkSerialize(props, errorCollector);
        Property<Boolean> configClient = (Property<Boolean>) props.getProperty("configClient");
        assertEquals(false, configClient.getValue());
        Form advForm = props.getForm(Form.ADVANCED);
        assertTrue(advForm.getWidget("configClientTable").isHidden());

        configClient.setValue(true);
        props = (ComponentProperties) checkAndAfter(advForm, "configClient", props);
        advForm = props.getForm(Form.ADVANCED);
        assertTrue(advForm.isRefreshUI());

        assertFalse(advForm.getWidget("configClientTable").isHidden());
    }

    @Test
    public void testClientConfigTable() {
        ComponentProperties props;

        props = new TAwsS3ConnectionDefinition().createProperties();
        ComponentTestUtils.checkSerialize(props, errorCollector);
        AwsS3ClientConfigTable configClient = (AwsS3ClientConfigTable) props.getProperty("configClientTable");
        List<AwsS3ClientConfigFields> fields = configClient.configField.getValue();
        assertTrue("fields should be still null", fields == null);

        List<AwsS3ClientConfigFields> newFields = new ArrayList<>();
        newFields.add(AwsS3ClientConfigFields.CONNECTIONTIMEOUT);
        newFields.add(AwsS3ClientConfigFields.MAXCONNECTIONS);

        configClient.configField.setValue(newFields);

        List<Object> newValues = new ArrayList<>();
        newValues.add(100);
        newValues.add(10);

        configClient.configValue.setValue(newValues);

        assertTrue(configClient.configField.getValue().size() == newFields.size());
        assertTrue(configClient.configField.getValue().get(0) == AwsS3ClientConfigFields.CONNECTIONTIMEOUT);
        assertTrue(configClient.configField.getValue().get(1) == AwsS3ClientConfigFields.MAXCONNECTIONS);

        assertTrue(configClient.configValue.getValue().size() == newValues.size());
        assertTrue((Integer) configClient.configValue.getValue().get(0) == 100);
        assertTrue((Integer) configClient.configValue.getValue().get(1) == 10);
    }

    @Test
    // this is an integration test to check that the dependencies file is properly generated.
    public void testDependencies() {
        ComponentTestUtils.testAllDesignDependenciesPresent(componentService, errorCollector);
    }

    protected Properties checkAndAfter(Form form, String propName, Properties props) throws Throwable {
        assertTrue(form.getWidget(propName).isCallAfter());
        return getComponentService().afterProperty(propName, props);
    }

}
