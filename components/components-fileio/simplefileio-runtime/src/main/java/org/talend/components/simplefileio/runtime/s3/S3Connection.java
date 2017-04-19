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

import com.amazonaws.regions.RegionUtils;
import org.talend.components.simplefileio.SimpleFileIODatastoreProperties;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.talend.components.simplefileio.s3.S3DatastoreProperties;

public class S3Connection {

    public static AmazonS3 createClient(S3DatastoreProperties properties) {
        AWSCredentialsProvider basicCredentials = new StaticCredentialsProvider(
                new BasicAWSCredentials(properties.accessKey.getValue(), properties.secretKey.getValue()));
        AmazonS3 conn = new AmazonS3Client(basicCredentials);
        conn.setRegion(RegionUtils.getRegion(properties.region.getValue().getValue()));
        return conn;
    }

}
