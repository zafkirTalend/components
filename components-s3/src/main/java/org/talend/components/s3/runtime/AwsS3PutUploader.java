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

import java.io.File;
import java.io.IOException;

import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.s3.AwsS3FileBucketKeyProperties;
import org.talend.components.s3.tawss3put.TAwsS3PutProperties;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * created by dmytro.chmyga on Jul 28, 2016
 */
public class AwsS3PutUploader extends AwsS3Loader<TAwsS3PutProperties> {

    protected AwsS3PutUploader(AwsS3ComponentRuntime<TAwsS3PutProperties> componentRuntime, RuntimeContainer container,
            TAwsS3PutProperties properties) {
        super(componentRuntime, container, properties);
    }

    @Override
    public void doWork() throws IOException {
        AwsS3FileBucketKeyProperties props = properties.fileBucketKeyProperties;
        PutObjectRequest putRequest = new PutObjectRequest(props.bucket.getValue(), props.key.getValue(),
                new File(props.filePath.getValue()));

        if (properties.enableServerSideEncryption.getValue()) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
            putRequest.setMetadata(objectMetadata);
        }

        getConnection().putObject(putRequest);
    }

}
