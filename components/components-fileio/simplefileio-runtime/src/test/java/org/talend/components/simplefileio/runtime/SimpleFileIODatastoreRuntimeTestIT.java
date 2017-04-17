package org.talend.components.simplefileio.runtime;

import static org.junit.Assert.*;
import static org.talend.components.simplefileio.runtime.SimpleFileIODatastoreRuntimeTest
        .createDatastoreProperties;
import static org.talend.components.test.SimpleFileIOTestConstants.S3AccessKey;
import static org.talend.components.test.SimpleFileIOTestConstants.S3Region;
import static org.talend.components.test.SimpleFileIOTestConstants.S3SecretKey;

import org.junit.Before;
import org.junit.Test;
import org.talend.components.simplefileio.FileSystemType;
import org.talend.components.simplefileio.S3Region;
import org.talend.components.simplefileio.SimpleFileIODatastoreProperties;
import org.talend.daikon.properties.ValidationResult;

public class SimpleFileIODatastoreRuntimeTestIT {

    SimpleFileIODatastoreRuntime runtime;

    @Before
    public void reset() {
        runtime = new SimpleFileIODatastoreRuntime();
    }

    public static SimpleFileIODatastoreProperties createS3DatastoreProperties() {
        SimpleFileIODatastoreProperties properties = createDatastoreProperties();
        properties.fileSystemType.setValue(FileSystemType.S3);
        properties.accessKey.setValue(S3AccessKey);
        properties.secretKey.setValue(S3SecretKey);
        properties.region.setValue(org.talend.components.simplefileio.S3Region.valueOf(S3Region));
        return properties;
    }

    private static SimpleFileIODatastoreProperties createS3DatastoreProperties_wrongAccess() {
        SimpleFileIODatastoreProperties properties = createS3DatastoreProperties();
        properties.accessKey.setValue("wrong");
        return properties;
    }

    private static SimpleFileIODatastoreProperties createS3DatastoreProperties_wrongSecret() {
        SimpleFileIODatastoreProperties properties = createS3DatastoreProperties();
        properties.secretKey.setValue("wrong");
        return properties;
    }

    @Test
    public void doHealthChecksTest_s3() {
        runtime.initialize(null, createS3DatastoreProperties());
        Iterable<ValidationResult> validationResults = runtime.doHealthChecks(null);
        assertEquals(ValidationResult.OK, validationResults.iterator().next());

        runtime.initialize(null, createS3DatastoreProperties_wrongAccess());
        validationResults = runtime.doHealthChecks(null);
        assertEquals(ValidationResult.Result.ERROR, validationResults.iterator().next().getStatus());

        runtime.initialize(null, createS3DatastoreProperties_wrongSecret());
        validationResults = runtime.doHealthChecks(null);
        assertEquals(ValidationResult.Result.ERROR, validationResults.iterator().next().getStatus());
    }
}
