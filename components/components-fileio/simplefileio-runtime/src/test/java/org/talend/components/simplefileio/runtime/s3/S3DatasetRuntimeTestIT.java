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

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.simplefileio.s3.S3DatasetProperties;
import org.talend.components.simplefileio.s3.S3DatastoreProperties;

/**
 * Unit tests for {@link S3DatasetRuntime}.
 */
public class S3DatasetRuntimeTestIT {

    /** Set up credentials for integration tests. */
    @Rule
    public S3TestResource s3 = S3TestResource.of();

    S3DatasetRuntime runtime;

    public static S3DatasetProperties createS3DatasetProperties(S3DatastoreProperties datastore) {
        S3DatasetProperties properties = new S3DatasetProperties(null);
        properties.init();
        properties.setDatastoreProperties(datastore);
        return properties;
    }

    @Before
    public void reset() {
        runtime = new S3DatasetRuntime();
    }

    @Test
    public void listBuckets() {
        runtime.initialize(null, s3.createS3DatasetProperties());
        Set<String> bucketNames = runtime.listBuckets();
        assertTrue(bucketNames.size() > 0);
    }
}
