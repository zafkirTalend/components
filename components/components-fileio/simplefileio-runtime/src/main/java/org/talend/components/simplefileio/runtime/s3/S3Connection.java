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

import org.apache.hadoop.fs.s3a.Constants;
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

    public static String getUriPath(S3DatasetProperties properties) {
        // Construct the path using the s3a schema.
        return Constants.FS_S3A + "://" + properties.bucket.getValue() + "/" + properties.object.getValue();
    }

    public static void setS3Configuration(ExtraHadoopConfiguration conf, S3DatastoreProperties properties) {
        conf.set(Constants.ACCESS_KEY, properties.accessKey.getValue());
        conf.set(Constants.SECRET_KEY, properties.secretKey.getValue());
    }

    public static void setS3Configuration(ExtraHadoopConfiguration conf, S3DatasetProperties properties) {
        // TODO encryption in motion and encryption at rest.
        // TODO: they probably want encryption in motion only for output.
        setS3Configuration(conf, properties.getDatastoreProperties());
    }
}
