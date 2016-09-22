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
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.s3.AwsS3ConnectionPropertiesProvider;

import com.amazonaws.services.s3.AmazonS3Client;

/**
 * Common worker class for workers used to upload/download files to/from Amazon S3 servers. Instances of this class are
 * used in {@link TAwsS3FilesLoaderRuntime} to do all the work related to downloading/uploading files.
 */
public abstract class AwsS3Loader<T extends AwsS3ConnectionPropertiesProvider> {

    protected AwsS3ComponentRuntime<T> componentRuntime;

    protected RuntimeContainer container;

    protected AmazonS3Client connection;

    protected T properties;

    private static final transient Logger LOGGER = LoggerFactory.getLogger(AwsS3Loader.class);

    /**
     * AwsS3Reader constructor comment.
     * 
     * @param componentRuntime - runtime, calling this class doWork() method
     * @param container - runtime container
     * @param properties - properties to use for files loading.
     */
    protected AwsS3Loader(AwsS3ComponentRuntime<T> componentRuntime, RuntimeContainer container, T properties) {
        this.componentRuntime = componentRuntime;
        this.container = container;
        this.properties = properties;
    }

    /**
     * Method to perform the files loading job.
     */
    public abstract void doWork() throws IOException;

    protected AmazonS3Client getConnection() throws IOException {
        if (connection == null) {
            LOGGER.debug("Trying to get the connection.");
            connection = componentRuntime.connect(container);
            LOGGER.debug("Connection retrieved.");
        }
        return connection;
    }

    /**
     * Close existing connection, if local components connection is used. Otherwise, leave the connection opened for
     * next components.
     */
    public void close() throws IOException {
        LOGGER.debug("Trying to close the connection.");
        boolean useReferencedConnection = properties.getConnectionProperties().getReferencedComponentId() != null
                && !properties.getConnectionProperties().getReferencedComponentId().isEmpty();
        if (!useReferencedConnection && connection != null) {
            LOGGER.debug("Component uses its own connection. Closing the connection.");
            connection.shutdown();
            connection = null;
            LOGGER.debug("Connection closed.");
        } else {
            LOGGER.debug("Component uses shared connection. Connection won't be closed.");
        }
    }

}
