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
import org.talend.components.s3.tawss3get.TAwsS3GetProperties;

import com.amazonaws.services.s3.AmazonS3Client;

/**
 * created by dmytro.chmyga on Jul 28, 2016
 */
public abstract class AwsS3Reader extends AbstractBoundedReader<IndexedRecord> {

    protected RuntimeContainer container;

    protected AmazonS3Client connection;

    protected TAwsS3GetProperties properties;

    /**
     * DOC dmytro.chmyga AwsS3Reader constructor comment.
     * 
     * @param source
     */
    protected AwsS3Reader(BoundedSource source, RuntimeContainer container, TAwsS3GetProperties properties) {
        super(source);
        this.container = container;
        this.properties = properties;
    }

    protected AmazonS3Client getConnection() throws IOException {
        if (connection == null) {
            connection = ((TAwsS3GetSource) getCurrentSource()).connect(container);
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
        if (properties.connectionProperties.getReferencedComponentId() != null
                && !properties.connectionProperties.getReferencedComponentId().isEmpty() && connection != null) {
            connection.shutdown();
        }
    }

}
