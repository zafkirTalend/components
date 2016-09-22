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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.ComponentDriverInitialization;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.s3.AwsS3LoaderPropertiesProvider;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;

/**
 * Basic runtime class for components, which load the files(upload or download) to or from Amazon S3 servers.
 */
public abstract class TAwsS3FilesLoaderRuntime<T extends AwsS3LoaderPropertiesProvider> extends AwsS3ComponentRuntime<T>
        implements ComponentDriverInitialization {

    private static final long serialVersionUID = 3252346356479766553L;

    private static final transient Logger LOGGER = LoggerFactory.getLogger(TAwsS3FilesLoaderRuntime.class);

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        if (!(properties instanceof AwsS3LoaderPropertiesProvider)) {
            LOGGER.error("Properties must be of type " + AwsS3LoaderPropertiesProvider.class.getCanonicalName());
            return new ValidationResult().setMessage("Wrong properties type.").setStatus(Result.ERROR);
        }
        ValidationResult result = super.initialize(container, properties);
        if (result.getStatus() != Result.OK) {
            return result;
        }
        String fileName = ((AwsS3LoaderPropertiesProvider) properties).getFileBucketKeyProperties().filePath.getValue();
        if (fileName == null || fileName.isEmpty()) {
            LOGGER.error("File name is empty. Check component properties.");
            result.setStatus(Result.ERROR);
            result.setMessage("File name cannot be empty.");
            return result;
        }
        return result;
    }

    @Override
    public void runAtDriver() {
        AwsS3Loader<T> worker = getWorker();
        try {
            worker.doWork();
        } catch (IOException e) {
            LOGGER.error("Could not finish the loading process.", e);
            throw new RuntimeException(e);
        } finally {
            try {
                LOGGER.debug("Closing loader.");
                worker.close();
            } catch (IOException e) {
                LOGGER.error("Could not close the connection.", e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Create the worker for loading files.
     */
    protected abstract AwsS3Loader<T> getWorker();

}
