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

import org.talend.components.api.component.runtime.ComponentRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.s3.AmazonS3ClientProducerFactory;
import org.talend.components.s3.AwsS3ConnectionProperties;
import org.talend.components.s3.AwsS3ConnectionPropertiesProvider;
import org.talend.daikon.properties.ValidationResult;

import com.amazonaws.services.s3.AmazonS3Client;

/**
 * created by dmytro.chmyga on Sep 16, 2016
 */
public class AwsS3ComponentRuntime<T extends AwsS3ConnectionPropertiesProvider> implements ComponentRuntime {

    protected T properties;

    protected RuntimeContainer container;

    protected static final String KEY_CONNECTION = "Connection";

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        this.properties = (T) properties;
        this.container = container;
        return ValidationResult.OK;
    }

    public AmazonS3Client connect(RuntimeContainer container) throws IOException {
        AwsS3ConnectionProperties connProps = properties.getConnectionProperties();
        String refComponentId = connProps.getReferencedComponentId();
        AmazonS3Client sharedConn = null;
        // Using another component's connection
        if (refComponentId != null) {
            // In a runtime container
            if (container != null) {
                sharedConn = (AmazonS3Client) container.getComponentData(refComponentId, KEY_CONNECTION);
                if (sharedConn != null) {
                    return sharedConn;
                }
                throw new IOException("Referenced component: " + refComponentId + " not connected");
            }
            // Design time
            connProps = connProps.getReferencedConnectionProperties();
        }
        if (container != null) {
            sharedConn = (AmazonS3Client) container.getComponentData(container.getCurrentComponentId(), KEY_CONNECTION);
            if (sharedConn != null) {
                return sharedConn;
            }
        }
        sharedConn = createClient(properties.getConnectionProperties());
        if (container != null) {
            container.setComponentData(container.getCurrentComponentId(), KEY_CONNECTION, sharedConn);
        }
        return sharedConn;
    }

    private AmazonS3Client createClient(AwsS3ConnectionProperties connectionProperties) throws IOException {
        return AmazonS3ClientProducerFactory.createClientProducer(connectionProperties).createClient(connectionProperties);
    }

}
