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
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.component.runtime.AbstractBoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.s3.AwsS3ConnectionPropertiesProvider;

import com.amazonaws.services.s3.AmazonS3Client;

/**
 * created by dmytro.chmyga on Jul 28, 2016
 */
public abstract class AwsS3Reader<T extends AwsS3ConnectionPropertiesProvider> extends AbstractBoundedReader<IndexedRecord> {

    protected RuntimeContainer container;

    protected AmazonS3Client connection;

    protected T properties;

    /**
     * DOC dmytro.chmyga AwsS3Reader constructor comment.
     * 
     * @param source
     */
    protected AwsS3Reader(BoundedSource source, RuntimeContainer container, T properties) {
        super(source);
        this.container = container;
        this.properties = properties;
    }

    protected AmazonS3Client getConnection() throws IOException {
        if (connection == null) {
            connection = ((AwsS3SourceOrSink) getCurrentSource()).connect(container);
        }
        return connection;
    }

    @Override
    public IndexedRecord getCurrent() throws NoSuchElementException {
        return null;
    }

    @Override
    public Map getReturnValues() {
        return new HashMap<>();
    }

    @Override
    public void close() throws IOException {
        boolean useReferencedConnection = properties.getConnectionProperties().getReferencedComponentId() != null
                && !properties.getConnectionProperties().getReferencedComponentId().isEmpty();
        if (!useReferencedConnection && connection != null) {
            connection.shutdown();
            connection = null;
        }
    }

}
