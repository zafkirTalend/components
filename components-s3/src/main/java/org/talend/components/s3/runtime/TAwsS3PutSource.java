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
import java.util.List;

import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.s3.tawss3put.TAwsS3PutProperties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;

/**
 * created by dmytro.chmyga on Jul 28, 2016
 */
public class TAwsS3PutSource extends AwsS3SourceOrSink implements BoundedSource {

    private long fileLength;

    @Override
    public ValidationResult validate(RuntimeContainer container) {
        ValidationResult result = new ValidationResult();
        String fileName = ((TAwsS3PutProperties) properties).fileBucketKeyProperties.filePath.getValue();
        if (fileName == null || fileName.isEmpty()) {
            result.setStatus(Result.ERROR);
            result.setMessage("File name cannot be empty.");
            return result;
        }
        File inputFile = new File(fileName);
        if (inputFile.exists()) {
            fileLength = inputFile.length();
            Integer partSize = ((TAwsS3PutProperties) properties).uploadPartSize.getValue();
            if (partSize == null || partSize == 0 || ((partSize * 1024 * 1024) < fileLength && partSize < 5)) {
                result.setStatus(Result.ERROR);
                result.setMessage("Upload part size should be more than 5 MB.");
                return result;
            }
            result = super.validate(container);
        } else {
            result.setMessage("File cannot be found.");
            result.setStatus(Result.ERROR);
        }
        return result;
    }

    @Override
    public List<? extends BoundedSource> splitIntoBundles(long desiredBundleSizeBytes, RuntimeContainer adaptor)
            throws Exception {
        return null;
    }

    @Override
    public long getEstimatedSizeBytes(RuntimeContainer adaptor) {
        return 0;
    }

    @Override
    public boolean producesSortedKeys(RuntimeContainer adaptor) {
        return false;
    }

    @Override
    public AwsS3Reader<TAwsS3PutProperties> createReader(RuntimeContainer adaptor) {
        TAwsS3PutProperties putProperties = (TAwsS3PutProperties) properties;
        final long partSize = putProperties.uploadPartSize.getValue() * 1024 * 1024;
        if (partSize < fileLength) {
            return new AwsS3MultipartPutReader(this, adaptor, putProperties, partSize);
        }
        return new AwsS3PutReader(this, adaptor, putProperties);
    }

}
