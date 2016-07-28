package org.talend.components.s3.runtime;

import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.s3.AmazonS3ClientProducerFactory;
import org.talend.components.s3.AwsS3ConnectionProperties;
import org.talend.components.s3.AwsS3ConnectionPropertiesProvider;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;

import com.amazonaws.services.s3.AmazonS3Client;

/**
 * The tAWSS3ConnectionSource provides the mechanism to supply data to other components at run-time.
 *
 * Based on the Apache Beam project, the Source mechanism is appropriate to describe distributed and non-distributed
 * data sources and can be adapted to scalable big data execution engines on a cluster, or run locally.
 *
 * This example component describes an input source that is guaranteed to be run in a single JVM (whether on a cluster
 * or locally), so:
 *
 * <ul>
 * <li>the simplified logic for reading is found in the {@link tAWSS3ConnectionReader}, and</li>
 * </ul>
 */
public class AwsS3SourceOrSink implements SourceOrSink {

    private static final long serialVersionUID = -5982819304499842835L;

    protected AwsS3ConnectionPropertiesProvider properties;

    protected static final String KEY_CONNECTION = "Connection";

    public void initialize(RuntimeContainer container, ComponentProperties properties) {
        this.properties = (AwsS3ConnectionPropertiesProvider) properties;
    }

    public ValidationResult validate(RuntimeContainer container) {
        ValidationResult validationResult = new ValidationResult();
        try {
            connect(container);
        } catch (IOException e) {
            validationResult.setStatus(Result.ERROR);
            validationResult.setMessage(e.getMessage());
        }
        return validationResult;
    }

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        return null;
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
        return null;
    }

    protected AmazonS3Client connect(RuntimeContainer container) throws IOException {
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
