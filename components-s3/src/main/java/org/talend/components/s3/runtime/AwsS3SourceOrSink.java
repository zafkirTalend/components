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

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Configuration extracted from the input properties. */
    private AwsS3ConnectionPropertiesProvider properties;

    public void initialize(RuntimeContainer container, ComponentProperties properties) {
        this.properties = (AwsS3ConnectionPropertiesProvider) properties;
    }

    public ValidationResult validate(RuntimeContainer adaptor) {
        ValidationResult validationResult = new ValidationResult();
        try {
            connect(properties.getConnectionProperties());
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

    protected AmazonS3Client connect(AwsS3ConnectionProperties connectionProperties) throws IOException {
        return createClient(connectionProperties);
    }

    private AmazonS3Client createClient(AwsS3ConnectionProperties connectionProperties) throws IOException {
        return AmazonS3ClientProducerFactory.createClientProducer(connectionProperties).createClient(connectionProperties);
    }

}
