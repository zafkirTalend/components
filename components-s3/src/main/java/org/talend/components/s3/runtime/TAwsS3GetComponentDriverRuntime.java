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

import org.talend.components.s3.tawss3get.TAwsS3GetProperties;

/**
 * created by dmytro.chmyga on Sep 16, 2016
 */
public class TAwsS3GetComponentDriverRuntime extends TAwsS3FilesLoaderRuntime<TAwsS3GetProperties> {

    @Override
    public AwsS3Loader<TAwsS3GetProperties> getWorker() {
        return new AwsS3GetDownloader(this, container, properties);
    }

}
