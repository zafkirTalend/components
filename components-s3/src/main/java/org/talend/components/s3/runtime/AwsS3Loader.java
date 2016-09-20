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

import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.s3.AwsS3ConnectionPropertiesProvider;

import com.amazonaws.services.s3.AmazonS3Client;

/**
 * created by dmytro.chmyga on Jul 28, 2016
 */
public abstract class AwsS3Loader<T extends AwsS3ConnectionPropertiesProvider> {

    protected AwsS3ComponentRuntime<T> componentRuntime;

    protected RuntimeContainer container;

    protected AmazonS3Client connection;

    protected T properties;

    /**
     * DOC dmytro.chmyga AwsS3Reader constructor comment.
     * 
     * @param source
     */
    protected AwsS3Loader(AwsS3ComponentRuntime<T> componentRuntime, RuntimeContainer container, T properties) {
        this.componentRuntime = componentRuntime;
        this.container = container;
        this.properties = properties;
    }

    public abstract void doWork() throws IOException;

    protected AmazonS3Client getConnection() throws IOException {
        if (connection == null) {
            connection = componentRuntime.connect(container);
        }
        return connection;
    }

    public void close() throws IOException {
        boolean useReferencedConnection = properties.getConnectionProperties().getReferencedComponentId() != null
                && !properties.getConnectionProperties().getReferencedComponentId().isEmpty();
        if (!useReferencedConnection && connection != null) {
            connection.shutdown();
            connection = null;
        }
    }

}
