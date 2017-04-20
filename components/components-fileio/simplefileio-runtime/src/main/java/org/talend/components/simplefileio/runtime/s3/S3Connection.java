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

package org.talend.components.simplefileio.runtime.s3;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.s3a.Constants;
import org.apache.hadoop.fs.s3a.S3AEncryptionMethods;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.talend.components.simplefileio.runtime.ExtraHadoopConfiguration;
import org.talend.components.simplefileio.s3.S3DatasetProperties;
import org.talend.components.simplefileio.s3.S3DatastoreProperties;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class S3Connection {

    public static AmazonS3 createClient(S3DatastoreProperties properties) {
        AWSCredentialsProvider basicCredentials = new StaticCredentialsProvider(new BasicAWSCredentials(
                properties.accessKey.getValue(), properties.secretKey.getValue()));
        AmazonS3 conn = new AmazonS3Client(basicCredentials);
        conn.setRegion(RegionUtils.getRegion(properties.region.getValue().getValue()));
        return conn;
    }

    public static S3AFileSystem createFileSystem(S3DatasetProperties properties) throws IOException {
        Configuration config = new Configuration(false);
        ExtraHadoopConfiguration extraConfig = new ExtraHadoopConfiguration();
        S3Connection.setS3Configuration(extraConfig, properties);
        extraConfig.addTo(config);
        try {
            return (S3AFileSystem) FileSystem.get(new URI(Constants.FS_S3A + "://" + properties.bucket.getValue()), config);
        } catch (URISyntaxException e) {
            // The URI is constant, so this exception should never occur.
            throw new RuntimeException(e);
        }
    }

    public static String getUriPath(S3DatasetProperties properties) {
        // Construct the path using the s3a schema.
        return Constants.FS_S3A + "://" + properties.bucket.getValue() + "/" + properties.object.getValue();
    }

    public static void setS3Configuration(ExtraHadoopConfiguration conf, S3DatastoreProperties properties) {
        conf.set(Constants.ACCESS_KEY, properties.accessKey.getValue());
        conf.set(Constants.SECRET_KEY, properties.secretKey.getValue());
    }

    public static void setS3Configuration(ExtraHadoopConfiguration conf, S3DatasetProperties properties) {
        if (properties.encryptDataAtRest.getValue()) {
            conf.set(Constants.SERVER_SIDE_ENCRYPTION_ALGORITHM, S3AEncryptionMethods.SSE_KMS.getMethod());
            conf.set(Constants.SERVER_SIDE_ENCRYPTION_KEY, properties.kmsForDataAtRest.getValue());
        }
        if (properties.encryptDataInMotion.getValue()) {
            // TODO: these don't exist yet...
            conf.set("fs.s3a.client-side-encryption-algorithm", S3AEncryptionMethods.SSE_KMS.getMethod());
            conf.set("fs.s3a.client-side-encryption-key", properties.kmsForDataInMotion.getValue());
        }
        setS3Configuration(conf, properties.getDatastoreProperties());
    }
}
