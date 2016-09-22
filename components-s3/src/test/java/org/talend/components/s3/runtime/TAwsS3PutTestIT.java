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
package org.talend.components.s3.runtime;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.talend.components.s3.AmazonS3ClientProducerFactory;
import org.talend.components.s3.tawss3put.TAwsS3PutDefinition;
import org.talend.components.s3.tawss3put.TAwsS3PutProperties;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;

/**
 * Integration test for files upload to Amazon S3 server.
 */
public class TAwsS3PutTestIT {

    private final String accessKey = System.getProperty("s3.access.key");

    private final String secretKey = System.getProperty("s3.secret.key");

    private final String bucketName = "dchmyga-talend-comp";

    private final String s3FileKey = "it-put-test-file";

    // @Test
    public void testUploadingFile() throws IOException {
        TAwsS3PutProperties props = (TAwsS3PutProperties) new TAwsS3PutDefinition().createProperties();
        props.connectionProperties.encrypt.setValue(false);
        props.connectionProperties.inheritFromAwsRole.setValue(false);
        props.connectionProperties.accessSecretKeyProperties.accessKey.setValue(accessKey);
        props.connectionProperties.accessSecretKeyProperties.secretKey.setValue(secretKey);

        File f = File.createTempFile("testFile", null);
        String s = "Test Put";
        FileWriter fw = null;
        try {
            fw = new FileWriter(f);
            fw.write(s);
        } finally {
            if (fw != null) {
                fw.close();
            }
        }

        String filePath = f.getAbsolutePath();

        props.fileBucketKeyProperties.bucket.setValue(bucketName);
        props.fileBucketKeyProperties.key.setValue(s3FileKey);
        props.fileBucketKeyProperties.filePath.setValue(filePath);

        TAwsS3PutComponentDriverRuntime putSource = new TAwsS3PutComponentDriverRuntime();
        putSource.initialize(null, props);

        putSource.runAtDriver();

        f.delete();

        AmazonS3Client s3Client = AmazonS3ClientProducerFactory.createClientProducer(props.getConnectionProperties())
                .createClient(props.getConnectionProperties());
        GetObjectRequest request = new GetObjectRequest(bucketName, s3FileKey);
        s3Client.getObject(request, new File(filePath));

        File downloadedFile = new File(filePath);
        downloadedFile.deleteOnExit();
        FileReader fr = null;
        BufferedReader br = null;
        String downloadedData = null;

        try {
            fr = new FileReader(downloadedFile);
            br = new BufferedReader(fr);
            downloadedData = br.readLine();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                }
            }
        }
        assertEquals("Downloaded file content and uploaded data content are different!", s, downloadedData);
    }

}
