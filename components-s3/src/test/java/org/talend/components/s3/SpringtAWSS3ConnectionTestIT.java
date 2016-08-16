package org.talend.components.s3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
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
import org.talend.components.s3.AbstractAmazonS3ClientProducer.AsymmetricKeyEncryptionAmazonS3ClientProvider;
import org.talend.components.s3.AbstractAmazonS3ClientProducer.KmsCmkEncryptionAmazonS3ClientProducer;
import org.talend.components.s3.AbstractAmazonS3ClientProducer.NonEncryptedAmazonS3ClientProducer;
import org.talend.components.s3.AbstractAmazonS3ClientProducer.SymmetricKeyEncryptionAmazonS3ClientProvider;
import org.talend.components.s3.tawss3connection.TAwsS3ConnectionDefinition;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3EncryptionClient;

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
    public void testAfterAssumeRole() throws Throwable {
        ComponentProperties props;

        props = new TAwsS3ConnectionDefinition().createProperties();
        Property<Boolean> assumeRoleProp = (Property<Boolean>) props.getProperty("assumeRole");
        assertEquals(false, assumeRoleProp.getValue());
        Form mainForm = props.getForm(Form.MAIN);
        assertTrue(mainForm.getWidget("assumeRoleProps").isHidden());
        Form advForm = props.getForm(Form.ADVANCED);
        assertTrue(advForm.getWidget("setStsEndpoint").isHidden());
        assertTrue(advForm.getWidget("stsEndpoint").isHidden());

        assumeRoleProp.setValue(true);
        props = (ComponentProperties) checkAndAfter(mainForm, "assumeRole", props);
        assertTrue(mainForm.isRefreshUI());
        assertTrue(advForm.isRefreshUI());

        assertFalse(mainForm.getWidget("assumeRoleProps").isHidden());
        assertFalse(advForm.getWidget("setStsEndpoint").isHidden());

        Property<Boolean> setStsEndpoint = (Property<Boolean>) props.getProperty("setStsEndpoint");
        assertEquals(false, setStsEndpoint.getValue());
        assertTrue(advForm.getWidget("stsEndpoint").isHidden());

        setStsEndpoint.setValue(true);
        props = (ComponentProperties) checkAndAfter(advForm, "setStsEndpoint", props);
        assertTrue(advForm.isRefreshUI());

        assertFalse(advForm.getWidget("stsEndpoint").isHidden());
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
    public void testClientCreation() throws IOException, NoSuchAlgorithmException {
        TAwsS3ConnectionDefinition def = new TAwsS3ConnectionDefinition();
        AwsS3ConnectionProperties props = (AwsS3ConnectionProperties) def.createProperties();

        props.accessSecretKeyProperties.accessKey.setValue(null);
        props.accessSecretKeyProperties.secretKey.setValue(null);
        props.inheritFromAwsRole.setValue(true);
        props.encrypt.setValue(false);
        AbstractAmazonS3ClientProducer clientProducer = AmazonS3ClientProducerFactory.createClientProducer(props);
        assertTrue("ClientProducer should be an instance of NonEncryptedAmazonS3ClientProducer class",
                clientProducer instanceof NonEncryptedAmazonS3ClientProducer);
        AmazonS3Client s3Client = clientProducer.createClient(props);
        assertTrue("Client should be a simple amazon client", s3Client.getClass().equals(AmazonS3Client.class));
        s3Client.shutdown();

        props.accessSecretKeyProperties.accessKey.setValue("aaaa");
        props.accessSecretKeyProperties.secretKey.setValue("aaaa");
        props.inheritFromAwsRole.setValue(false);
        props.encrypt.setValue(false);
        clientProducer = AmazonS3ClientProducerFactory.createClientProducer(props);
        assertTrue("ClientProducer should be an instance of NonEncryptedAmazonS3ClientProducer class",
                clientProducer instanceof NonEncryptedAmazonS3ClientProducer);
        s3Client = clientProducer.createClient(props);
        assertTrue("Client should be a simple amazon client", s3Client.getClass().equals(AmazonS3Client.class));
        s3Client.shutdown();

        KeyPair pair = generateKeyPair();
        File privateKey = File.createTempFile("privateKey", null);
        privateKey.deleteOnExit();
        writeEncodedKeyToFile(pair.getPrivate().getEncoded(), privateKey);
        File publicKey = File.createTempFile("publicKey", null);
        writeEncodedKeyToFile(pair.getPublic().getEncoded(), publicKey);
        publicKey.deleteOnExit();
        props.encrypt.setValue(true);
        props.encryptionProperties.encryptionKeyType.setValue(EncryptionKeyType.ASYMMETRIC_MASTER_KEY);
        props.encryptionProperties.asymmetricKeyProperties.privateKeyFilePath.setValue(privateKey.getAbsolutePath());
        props.encryptionProperties.asymmetricKeyProperties.publicKeyFilePath.setValue(publicKey.getAbsolutePath());
        clientProducer = AmazonS3ClientProducerFactory.createClientProducer(props);
        assertTrue("ClientProducer should be an instance of AsymmetricKeyEncryptionAmazonS3ClientProvider class",
                clientProducer instanceof AsymmetricKeyEncryptionAmazonS3ClientProvider);
        s3Client = clientProducer.createClient(props);
        assertTrue("Client should be an encrypted amazon client", s3Client.getClass().equals(AmazonS3EncryptionClient.class));
        s3Client.shutdown();

        props.encrypt.setValue(true);
        props.encryptionProperties.encryptionKeyType.setValue(EncryptionKeyType.SYMMETRIC_MASTER_KEY);
        props.encryptionProperties.symmetricKeyProperties.key.setValue("aaaaaaa");
        clientProducer = AmazonS3ClientProducerFactory.createClientProducer(props);
        assertTrue("ClientProducer should be an instance of SymmetricKeyEncryptionAmazonS3ClientProvider class",
                clientProducer instanceof SymmetricKeyEncryptionAmazonS3ClientProvider);
        s3Client = clientProducer.createClient(props);
        assertTrue("Client should be an encrypted amazon client", s3Client.getClass().equals(AmazonS3EncryptionClient.class));
        s3Client.shutdown();

        File symmetricAesFile = File.createTempFile("symmetricKey", null);
        symmetricAesFile.deleteOnExit();
        byte[] myVeryWeakKey = new byte[8];
        myVeryWeakKey[6] = 10;
        myVeryWeakKey[7] = 10;
        writeEncodedKeyToFile(myVeryWeakKey, symmetricAesFile);
        props.encryptionProperties.symmetricKeyProperties.encoding.setValue(Encoding.X509);
        props.encryptionProperties.symmetricKeyProperties.keyFilePath.setValue(symmetricAesFile.getAbsolutePath());
        clientProducer = AmazonS3ClientProducerFactory.createClientProducer(props);
        assertTrue("ClientProducer should be an instance of SymmetricKeyEncryptionAmazonS3ClientProvider class",
                clientProducer instanceof SymmetricKeyEncryptionAmazonS3ClientProvider);
        s3Client = clientProducer.createClient(props);
        assertTrue("Client should be an encrypted amazon client", s3Client.getClass().equals(AmazonS3EncryptionClient.class));
        s3Client.shutdown();

        props.encrypt.setValue(true);
        props.encryptionProperties.encryptionKeyType.setValue(EncryptionKeyType.KMS_MANAGED_CUSTOMER_MASTER_KEY);
        props.encryptionProperties.kmsCmkProperties.key.setValue("aaaaaaa");
        clientProducer = AmazonS3ClientProducerFactory.createClientProducer(props);
        assertTrue("ClientProducer should be an instance of KmsCmkEncryptionAmazonS3ClientProducer class",
                clientProducer instanceof KmsCmkEncryptionAmazonS3ClientProducer);
        s3Client = clientProducer.createClient(props);
        assertTrue("Client should be an encrypted amazon client", s3Client.getClass().equals(AmazonS3EncryptionClient.class));
        s3Client.shutdown();
    }

    @Test
    public void testFailingClientCreation() throws IOException, NoSuchAlgorithmException {
        TAwsS3ConnectionDefinition def = new TAwsS3ConnectionDefinition();
        AwsS3ConnectionProperties props = (AwsS3ConnectionProperties) def.createProperties();

        props.accessSecretKeyProperties.accessKey.setValue(null);
        props.accessSecretKeyProperties.secretKey.setValue(null);
        props.inheritFromAwsRole.setValue(false);
        props.encrypt.setValue(false);
        AmazonS3Client s3Client = null;
        AbstractAmazonS3ClientProducer clientProducer = AmazonS3ClientProducerFactory.createClientProducer(props);
        try {
            s3Client = clientProducer.createClient(props);
            fail("non-encrypted client shouldn't be created if credentials are not inherited from aws role and Access key and secret key are null");
        } catch (Exception e) {
        }

        props.encrypt.setValue(true);
        props.encryptionProperties.encryptionKeyType.setValue(EncryptionKeyType.ASYMMETRIC_MASTER_KEY);
        props.encryptionProperties.asymmetricKeyProperties.privateKeyFilePath.setValue(null);
        props.encryptionProperties.asymmetricKeyProperties.publicKeyFilePath.setValue(null);
        try {
            clientProducer = AmazonS3ClientProducerFactory.createClientProducer(props);
            s3Client = clientProducer.createClient(props);
            fail("encrypted client shouldn't be created with Asymmetric key if Private and public keys are null");
        } catch (Exception e) {
        }

        props.encrypt.setValue(true);
        props.encryptionProperties.encryptionKeyType.setValue(EncryptionKeyType.SYMMETRIC_MASTER_KEY);
        props.encryptionProperties.symmetricKeyProperties.encoding.setValue(Encoding.BASE_64);
        props.encryptionProperties.symmetricKeyProperties.key.setValue(null);
        try {
            clientProducer = AmazonS3ClientProducerFactory.createClientProducer(props);
            s3Client = clientProducer.createClient(props);
            fail("encrypted client shouldn't be created with symmetric key and Base64 encoding if Key is null");
        } catch (Exception e) {
        }

        props.encryptionProperties.symmetricKeyProperties.encoding.setValue(Encoding.X509);
        props.encryptionProperties.symmetricKeyProperties.keyFilePath.setValue(null);
        clientProducer = AmazonS3ClientProducerFactory.createClientProducer(props);
        try {
            clientProducer = AmazonS3ClientProducerFactory.createClientProducer(props);
            s3Client = clientProducer.createClient(props);
            fail("encrypted client shouldn't be created with symmetric key and X509 encoding if Key file path is null");
        } catch (Exception e) {
        }

        props.encrypt.setValue(true);
        props.encryptionProperties.encryptionKeyType.setValue(EncryptionKeyType.KMS_MANAGED_CUSTOMER_MASTER_KEY);
        props.encryptionProperties.kmsCmkProperties.key.setValue(null);
        try {
            clientProducer = AmazonS3ClientProducerFactory.createClientProducer(props);
            s3Client = clientProducer.createClient(props);
            fail("encrypted client shouldn't be created with KMS if Key null");
        } catch (Exception e) {
        }
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        return keyGen.genKeyPair();
    }

    private void writeEncodedKeyToFile(byte[] key, File f) throws IOException {
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.close();
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
