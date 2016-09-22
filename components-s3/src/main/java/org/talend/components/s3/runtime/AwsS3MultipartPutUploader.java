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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.s3.tawss3put.TAwsS3PutProperties;

import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;

/**
 * Worker class for Uploading files using multiple requests.
 */
public class AwsS3MultipartPutUploader extends AwsS3Loader<TAwsS3PutProperties> {

    private final long partSize;

    private String uploadId;

    private long fileLength;

    private int curPartNumber;

    private long filePosition = 0;

    private List<PartETag> partsTags;

    private File inputFile;

    private static final transient Logger LOGGER = LoggerFactory.getLogger(AwsS3MultipartPutUploader.class);

    /**
     * AwsS3MultipartPutReader constructor comment.
     * 
     * @param componentRuntime
     * @param container
     * @param properties
     * @param partSize - size of a single uploading part. This parameter will be used to upload parts of that side from
     * local machine to Amazon S3 server. This value shouldn't be less than 5(MB).
     */
    protected AwsS3MultipartPutUploader(AwsS3ComponentRuntime<TAwsS3PutProperties> componentRuntime, RuntimeContainer container,
            TAwsS3PutProperties properties, long partSize) {
        super(componentRuntime, container, properties);
        this.partSize = partSize;
    }

    @Override
    public void doWork() throws IOException {
        inputFile = new File(properties.fileBucketKeyProperties.filePath.getValue());
        fileLength = inputFile.length();

        partsTags = new ArrayList<>();
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
                properties.fileBucketKeyProperties.bucket.getValue(), properties.fileBucketKeyProperties.key.getValue());
        if (properties.enableServerSideEncryption.getValue()) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
            initRequest.setObjectMetadata(objectMetadata);
        }
        InitiateMultipartUploadResult initResponse = getConnection().initiateMultipartUpload(initRequest);
        uploadId = initResponse.getUploadId();

        while (filePosition < fileLength) {
            long curPartSize = Math.min(partSize, fileLength - filePosition);
            UploadPartRequest uploadRequest = new UploadPartRequest()
                    .withBucketName(properties.fileBucketKeyProperties.bucket.getValue())
                    .withKey(properties.fileBucketKeyProperties.key.getValue()).withUploadId(uploadId)
                    .withPartNumber(curPartNumber).withFileOffset(filePosition).withFile(inputFile).withPartSize(curPartSize);

            // Upload part and add response to our list.
            partsTags.add(getConnection().uploadPart(uploadRequest).getPartETag());

            filePosition += curPartSize;
        }
    }

    /**
     * If there was an Exception during the uploading process, we should abort the uploading process. It is done by
     * sending the {@link AbortMultipartUploadRequest}. Otherwise, in case the uploading is successful, we will send
     * {@link CompleteMultipartUploadRequest} to finish the uploading process.
     */
    @Override
    public void close() throws IOException {
        if (filePosition >= fileLength) {
            LOGGER.debug("File uploading process finished successfully. Sending the complete upload request.");
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
                    properties.fileBucketKeyProperties.bucket.getValue(), properties.fileBucketKeyProperties.key.getValue(),
                    uploadId, partsTags);

            getConnection().completeMultipartUpload(compRequest);
        } else {
            LOGGER.debug("File uploading process was not finished successfully. Aborting upload. Sending abort upload request.");
            getConnection()
                    .abortMultipartUpload(new AbortMultipartUploadRequest(properties.fileBucketKeyProperties.bucket.getValue(),
                            properties.fileBucketKeyProperties.key.getValue(), uploadId));
        }
        super.close();
    }

}
