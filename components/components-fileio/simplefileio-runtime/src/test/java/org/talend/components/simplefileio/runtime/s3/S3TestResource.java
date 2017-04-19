// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.simplefileio.runtime.s3;

import java.util.UUID;

import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.talend.components.simplefileio.s3.S3DatasetProperties;
import org.talend.components.simplefileio.s3.S3DatastoreProperties;
import org.talend.components.simplefileio.s3.S3Region;

/**
 * Reusable for creating S3 properties preconfigured from environment variables for integration tests.
 *
 * <pre>
 * <profile>
 *   <id>amazon_credentials</id>
 *   <properties>
 *     <s3.accesskey>ACCESS_KEY</s3.accesskey>
 *     <s3.secretkey>SECRETY_KEY</s3.secretkey>
 *     <s3.region>EU_WEST_1</s3.region>
 *     <s3.bucket>testbucket</s3.bucket>
 *     <s3.ssekmskey>KEY1</s3.ssekmskey>
 *     <s3.csekmskey>KEY2</s3.csekmskey>
 *   </properties>
 *   <activation>
 *     <activeByDefault>true</activeByDefault>
 *   </activation>
 * </profile>
 * </pre>
 */
public class S3TestResource extends ExternalResource {

    /** The currently running test. */
    protected String name = null;

    private S3TestResource() {
    }

    public static S3TestResource of() {
        return new S3TestResource();
    }

    /**
     * @return An S3DatastoreProperties with all of the defaults, but filled with security credentials from the system
     * environment.
     */
    public S3DatastoreProperties createS3DatastoreProperties() {
        S3DatastoreProperties properties = new S3DatastoreProperties(null);
        properties.init();
        properties.accessKey.setValue(System.getProperty("s3.accesskey"));
        properties.secretKey.setValue(System.getProperty("s3.secretkey"));
        properties.region.setValue(S3Region.valueOf(System.getProperty("s3.region")));
        return properties;
    }

    /**
     * @return An S3DatasetProperties with credentials in the datastore, not configured for encryption. The region are
     * bucket are taken from the environment, and a unique "object" property is created for this unit test.
     */
    public S3DatasetProperties createS3DatasetProperties() {
        return createS3DatasetProperties(false, false);
    }

    /**
     * Return an S3DatasetProperties potentially configured for encryption.
     * 
     * @param sseKms Whether server-side encryption is used. The KMS key is taken from the system environment.
     * @param sseKms Whether client-side encryption is used. The KMS key is taken from the system environment.
     * @return An S3DatasetProperties with credentials in the datastore, configured for the specified encryption. The
     * region are bucket are taken from the environment, and a unique "object" property is created for this unit test.
     */
    public S3DatasetProperties createS3DatasetProperties(boolean sseKms, boolean cseKms) {
        S3DatasetProperties properties = new S3DatasetProperties(null);
        properties.init();
        properties.bucket.setValue(System.getProperty("s3.bucket"));
        properties.object.setValue("output/" + getName() + "_" + UUID.randomUUID());
        properties.setDatastoreProperties(createS3DatastoreProperties());
        if (sseKms) {
            properties.encryptDataAtRest.setValue(true);
            properties.kmsForDataAtRest.setValue("s3.ssekmskey");
        }
        if (cseKms) {
            properties.encryptDataInMotion.setValue(true);
            properties.kmsForDataInMotion.setValue("s3.csekmskey");
        }
        return properties;
    }

    /**
     * Return the name of the currently executing test.
     *
     * @return the name of the currently executing test.
     */
    public String getName() {
        return name;
    }

    @Override
    public Statement apply(Statement base, Description desc) {
        name = desc.getMethodName();
        return super.apply(base, desc);
    }
}
