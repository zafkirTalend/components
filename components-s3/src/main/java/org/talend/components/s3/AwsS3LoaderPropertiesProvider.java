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
package org.talend.components.s3;

/**
 * Common interface for Properties, which provide properties for file upload/download.
 */
public interface AwsS3LoaderPropertiesProvider extends AwsS3ConnectionPropertiesProvider {

    /**
     * Return properties containing local file path, S3 bucket and S3 object key values.
     */
    public AwsS3FileBucketKeyProperties getFileBucketKeyProperties();

}
