package org.talend.components.simplefileio.runtime.s3;

import static org.junit.Assert.assertEquals;
import static org.talend.components.test.SimpleFileIOTestConstants.S3AccessKey;
import static org.talend.components.test.SimpleFileIOTestConstants.S3Region;
import static org.talend.components.test.SimpleFileIOTestConstants.S3SecretKey;

import org.junit.Before;
import org.junit.Test;
import org.talend.components.simplefileio.s3.S3DatastoreProperties;
import org.talend.daikon.properties.ValidationResult;

public class S3DatastoreRuntimeTestIT {

    S3DatastoreRuntime runtime;

    public static S3DatastoreProperties createS3DatastoreProperties() {
        S3DatastoreProperties properties = new S3DatastoreProperties(null);
        properties.init();
        properties.accessKey.setValue(S3AccessKey);
        properties.secretKey.setValue(S3SecretKey);
        properties.region.setValue(org.talend.components.simplefileio.s3.S3Region.valueOf(S3Region));
        return properties;
    }

    private static S3DatastoreProperties createS3DatastoreProperties_wrongAccess() {
        S3DatastoreProperties properties = createS3DatastoreProperties();
        properties.accessKey.setValue("wrong");
        return properties;
    }

    private static S3DatastoreProperties createS3DatastoreProperties_wrongSecret() {
        S3DatastoreProperties properties = createS3DatastoreProperties();
        properties.secretKey.setValue("wrong");
        return properties;
    }

    @Before
    public void reset() {
        runtime = new S3DatastoreRuntime();
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
