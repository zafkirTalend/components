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
package org.talend.components.s3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3EncryptionClient;
import com.amazonaws.services.s3.model.CryptoConfiguration;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.model.EncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.KMSEncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.StaticEncryptionMaterialsProvider;

/**
 * created by dmytro.chmyga on Jul 26, 2016
 */
public abstract class AbstractAmazonS3ClientProducer {

    public AmazonS3Client createClient(AwsS3ConnectionProperties connectionProperties) throws IOException {
        AWSCredentialsProvider credProvider = createCredentialsProvider(connectionProperties);
        boolean isClientConfig = connectionProperties.configClient.getValue();
        Map<AwsS3ClientConfigFields, Object> clientConfigData = null;
        if (isClientConfig) {
            clientConfigData = connectionProperties.configClientTable.getConfig();
        }
        AmazonS3Client client = null;
        if (clientConfigData != null && !clientConfigData.isEmpty()) {
            ClientConfiguration clientConfig = ClientConfigurationBuilder.createClientConfiguration(clientConfigData);
            client = doCreateClientWithClientConfig(credProvider, clientConfig, connectionProperties);
        } else {
            client = doCreateClient(credProvider, connectionProperties);
        }
        if (connectionProperties.isRegionSet()) {
            client.setRegion(RegionUtils.getRegion(connectionProperties.region.getValue().getAwsRegionCode()));
        }
        return client;
    }

    private AWSCredentialsProvider createCredentialsProvider(AwsS3ConnectionProperties connectionProperties) {
        AWSCredentialsProvider credProvider;
        if (connectionProperties.inheritFromAwsRole.getValue()) {
            credProvider = new InstanceProfileCredentialsProvider();
        } else {
            credProvider = new StaticCredentialsProvider(
                    new BasicAWSCredentials(connectionProperties.accessSecretKeyProperties.accessKey.getValue(),
                            connectionProperties.accessSecretKeyProperties.secretKey.getValue()));
        }
        return credProvider;
    }

    protected abstract AmazonS3Client doCreateClient(AWSCredentialsProvider credentialsProvider,
            AwsS3ConnectionProperties connectionProperties) throws IOException;

    protected abstract AmazonS3Client doCreateClientWithClientConfig(AWSCredentialsProvider credentialsProvider,
            ClientConfiguration clientConfig, AwsS3ConnectionProperties connectionProperties) throws IOException;

    public static class NonEncryptedAmazonS3ClientProducer extends AbstractAmazonS3ClientProducer {

        @Override
        protected AmazonS3Client doCreateClient(AWSCredentialsProvider credentialsProvider,
                AwsS3ConnectionProperties connectionProperties) {
            return new AmazonS3Client(credentialsProvider);
        }

        @Override
        protected AmazonS3Client doCreateClientWithClientConfig(AWSCredentialsProvider credentialsProvider,
                ClientConfiguration clientConfig, AwsS3ConnectionProperties connectionProperties) {
            return new AmazonS3Client(credentialsProvider, clientConfig);
        }

    }

    protected static abstract class EncryptedAmazonS3ClientProducer extends AbstractAmazonS3ClientProducer {

        @Override
        protected AmazonS3EncryptionClient doCreateClient(AWSCredentialsProvider credentialsProvider,
                AwsS3ConnectionProperties connectionProperties) throws IOException {
            return new AmazonS3EncryptionClient(credentialsProvider, createEncryptionMaterials(connectionProperties),
                    createCryptoConfig(connectionProperties));
        }

        @Override
        protected AmazonS3Client doCreateClientWithClientConfig(AWSCredentialsProvider credentialsProvider,
                ClientConfiguration clientConfig, AwsS3ConnectionProperties connectionProperties) throws IOException {
            return new AmazonS3EncryptionClient(credentialsProvider, createEncryptionMaterials(connectionProperties),
                    clientConfig, createCryptoConfig(connectionProperties));
        }

        protected CryptoConfiguration createCryptoConfig(AwsS3ConnectionProperties connectionProperties) {
            return new CryptoConfiguration();
        }

        protected abstract EncryptionMaterialsProvider createEncryptionMaterials(AwsS3ConnectionProperties connectionProperties)
                throws IOException;

    }

    public static class KmsCmkEncryptionAmazonS3ClientProducer extends EncryptedAmazonS3ClientProducer {

        @Override
        protected CryptoConfiguration createCryptoConfig(AwsS3ConnectionProperties connectionProperties) {
            CryptoConfiguration cryptoConfig = super.createCryptoConfig(connectionProperties);
            if (connectionProperties.isRegionSet()) {
                cryptoConfig.withAwsKmsRegion(RegionUtils.getRegion(connectionProperties.region.getValue().getAwsRegionCode()));
            }
            return cryptoConfig;
        }

        @Override
        protected EncryptionMaterialsProvider createEncryptionMaterials(AwsS3ConnectionProperties connectionProperties) {
            return new KMSEncryptionMaterialsProvider(connectionProperties.encryptionProperties.kmsCmkProperties.key.getValue());
        }

    }

    public static class SymmetricKeyEncryptionAmazonS3ClientProvider extends EncryptedAmazonS3ClientProducer {

        @Override
        protected EncryptionMaterialsProvider createEncryptionMaterials(AwsS3ConnectionProperties connectionProperties)
                throws IOException {
            SecretKeySpec symmetricKey = null;
            AwsS3SymmetricKeyEncryptionProperties encryptionParameters = connectionProperties.encryptionProperties.symmetricKeyProperties;
            if (encryptionParameters.encoding.getValue() == Encoding.BASE_64) {
                symmetricKey = new SecretKeySpec(Base64.decodeBase64(encryptionParameters.key.getValue().getBytes("UTF-8")),
                        "AES");
            } else if (encryptionParameters.encoding.getValue() == Encoding.X509) {
                File keyFile = new File(encryptionParameters.keyFilePath.getValue());
                FileInputStream keyInputStream = null;
                try {
                    keyInputStream = new FileInputStream(keyFile);
                    byte[] encodedPrivateKey = new byte[(int) keyFile.length()];
                    keyInputStream.read(encodedPrivateKey);
                    symmetricKey = new SecretKeySpec(encodedPrivateKey, "AES");
                } finally {
                    if (keyInputStream != null) {
                        keyInputStream.close();
                    }
                }
            }
            EncryptionMaterials encryptionMaterials = new EncryptionMaterials(symmetricKey);
            return new StaticEncryptionMaterialsProvider(encryptionMaterials);
        }

    }

    public static class AsymmetricKeyEncryptionAmazonS3ClientProvider extends EncryptedAmazonS3ClientProducer {

        @Override
        protected EncryptionMaterialsProvider createEncryptionMaterials(AwsS3ConnectionProperties connectionProperties)
                throws IOException {
            AwsS3AsymmetricKeyEncryptionProperties encryptionProps = connectionProperties.encryptionProperties.asymmetricKeyProperties;
            File filePublicKey = new File(encryptionProps.publicKeyFilePath.getValue());
            FileInputStream fis = null;
            byte[] encodedPublicKey = null;
            try {
                fis = new FileInputStream(filePublicKey);
                encodedPublicKey = new byte[(int) filePublicKey.length()];
                fis.read(encodedPublicKey);
            } finally {
                if (fis != null) {
                    fis.close();
                    fis = null;
                }
            }

            File filePrivateKey = new File(encryptionProps.privateKeyFilePath.getValue());
            byte[] encodedPrivateKey = null;
            try {
                fis = new java.io.FileInputStream(filePrivateKey);
                encodedPrivateKey = new byte[(int) filePrivateKey.length()];
                fis.read(encodedPrivateKey);
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }

            KeyFactory keyFactory;
            KeyPair asymmetricKey = null;
            try {
                keyFactory = KeyFactory.getInstance(encryptionProps.algorithm.toString());

                X509EncodedKeySpec publicKeySpec = new java.security.spec.X509EncodedKeySpec(encodedPublicKey);
                PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

                PKCS8EncodedKeySpec privateKeySpec = new java.security.spec.PKCS8EncodedKeySpec(encodedPrivateKey);
                PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

                asymmetricKey = new KeyPair(publicKey, privateKey);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new IOException(e);
            }
            EncryptionMaterials encryptionMaterials = new EncryptionMaterials(asymmetricKey);
            return new StaticEncryptionMaterialsProvider(encryptionMaterials);
        }

    }

}
