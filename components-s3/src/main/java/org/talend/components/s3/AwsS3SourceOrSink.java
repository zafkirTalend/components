package org.talend.components.s3;

import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;

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
    private AwsS3ConnectionProperties properties;

    public void initialize(RuntimeContainer container, ComponentProperties properties) {
        this.properties = (AwsS3ConnectionProperties) properties;
        // FIXME - this should be moved to the properties setup
    }

    public ValidationResult validate(RuntimeContainer adaptor) {
        return ValidationResult.OK;
    }

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

}
