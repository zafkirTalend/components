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
import org.talend.components.s3.tawss3get.TAwsS3GetProperties;

import com.amazonaws.services.s3.model.GetObjectRequest;

/**
 * Class used for Objects(files) downloading from Amazon S3 server.
 */
public class AwsS3GetDownloader extends AwsS3Loader<TAwsS3GetProperties> {

    private AwsS3FileBucketKeyProperties fileProperties;

    protected AwsS3GetDownloader(AwsS3ComponentRuntime<TAwsS3GetProperties> componentRuntime, RuntimeContainer container,
            TAwsS3GetProperties properties) {
        super(componentRuntime, container, properties);
    }

    public void doWork() throws IOException {
        fileProperties = properties.fileBucketKeyProperties;
        GetObjectRequest request = new GetObjectRequest(fileProperties.bucket.getValue(), fileProperties.key.getValue());
        getConnection().getObject(request, new File(fileProperties.filePath.getValue()));
    }

}
