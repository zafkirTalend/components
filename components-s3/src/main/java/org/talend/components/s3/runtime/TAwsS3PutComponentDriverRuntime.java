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

import org.talend.components.s3.tawss3put.TAwsS3PutProperties;

/**
 * Class for Uploading files to Amazon S3 servers.
 */
public class TAwsS3PutComponentDriverRuntime extends TAwsS3FilesLoaderRuntime<TAwsS3PutProperties> {

    @Override
    public AwsS3Loader<TAwsS3PutProperties> getWorker() {
        String fileName = properties.fileBucketKeyProperties.filePath.getValue();
        File inputFile = new File(fileName);
        long fileLength;
        if (inputFile.exists()) {
            fileLength = inputFile.length();
            Integer partSize = ((TAwsS3PutProperties) properties).uploadPartSize.getValue();
            if (partSize == null || partSize == 0 || ((partSize * 1024 * 1024) < fileLength && partSize < 5)) {
                throw new RuntimeException("Upload part size should be more than 5 MB.");
            }
        } else {
            throw new RuntimeException("File cannot be found.");
        }
        final long partSize = properties.uploadPartSize.getValue() * 1024 * 1024;
        if (partSize < fileLength) {
            return new AwsS3MultipartPutUploader(this, container, properties, partSize);
        }
        return new AwsS3PutUploader(this, container, properties);
    }

}
