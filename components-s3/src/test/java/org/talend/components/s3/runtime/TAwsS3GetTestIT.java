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
import org.talend.components.s3.tawss3get.TAwsS3GetDefinition;
import org.talend.components.s3.tawss3get.TAwsS3GetProperties;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * created by dmytro.chmyga on Jul 29, 2016
 */
public class TAwsS3GetTestIT {

    private final String accessKey = System.getProperty("s3.access.key");

    private final String secretKey = System.getProperty("s3.secret.key");

    private final String bucketName = "dchmyga-talend-comp";

    private final String s3FileKey = "it-get-test-file";

    // @Test
    public void testDownloadingFile() throws IOException {
        File f = File.createTempFile("testFile", null);
        String s = "Test Get";
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

        TAwsS3GetProperties props = (TAwsS3GetProperties) new TAwsS3GetDefinition().createProperties();
        props.connectionProperties.encrypt.setValue(false);
        props.connectionProperties.inheritFromAwsRole.setValue(false);
        props.connectionProperties.accessSecretKeyProperties.accessKey.setValue(accessKey);
        props.connectionProperties.accessSecretKeyProperties.secretKey.setValue(secretKey);

        props.fileBucketKeyProperties.bucket.setValue(bucketName);
        props.fileBucketKeyProperties.key.setValue(s3FileKey);
        props.fileBucketKeyProperties.filePath.setValue(filePath);

        PutObjectRequest putRequest = new PutObjectRequest(bucketName, s3FileKey, new File(filePath));
        AmazonS3Client s3Client = AmazonS3ClientProducerFactory.createClientProducer(props.getConnectionProperties())
                .createClient(props.getConnectionProperties());
        s3Client.putObject(putRequest);

        f.delete();

        TAwsS3GetSource getSource = new TAwsS3GetSource();
        getSource.initialize(null, props);

        AwsS3GetReader reader = getSource.createReader(null);
        reader.start();
        reader.close();

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
