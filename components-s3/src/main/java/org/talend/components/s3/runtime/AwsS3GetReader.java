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

import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.s3.AwsS3FileBucketKeyProperties;
import org.talend.components.s3.tawss3get.TAwsS3GetProperties;

import com.amazonaws.services.s3.model.GetObjectRequest;

public class AwsS3GetReader extends AwsS3Reader {

    private AwsS3FileBucketKeyProperties fileProperties;

    protected AwsS3GetReader(BoundedSource source, RuntimeContainer container, TAwsS3GetProperties properties) {
        super(source, container, properties);
    }

    @Override
    public boolean start() throws IOException {
        fileProperties = properties.fileBucketKeyProperties;
        return true;
    }

    @Override
    public boolean advance() throws IOException {
        GetObjectRequest request = new GetObjectRequest(fileProperties.bucket.getValue(), fileProperties.key.getValue());
        getConnection().getObject(request, new File(fileProperties.filePath.getValue()));
        return false;
    }

}
