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

package org.talend.components.simplefileio.runtime;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.talend.components.simplefileio.runtime.SimpleFileIODatasetRuntimeTest.createDatasetProperties;


import static org.talend.components.simplefileio.runtime.SimpleFileIODatastoreRuntimeTestIT
        .createS3DatastoreProperties;

import org.junit.Before;
import org.junit.Test;
import org.talend.components.simplefileio.SimpleFileIODatasetProperties;
import org.talend.components.simplefileio.SimpleFileIODatastoreProperties;

import java.util.Set;

public class SimpleFileIODatasetRuntimeTestIT {

    SimpleFileIODatasetRuntime runtime;

    public static SimpleFileIODatasetProperties createS3DatasetProperties(SimpleFileIODatastoreProperties datastore) {
        SimpleFileIODatasetProperties properties = createDatasetProperties();
        properties.setDatastoreProperties(datastore);
        return properties;
    }

    @Before
    public void reset() {
        runtime = new SimpleFileIODatasetRuntime();
    }

    @Test
    public void listBuckets() {
        runtime.initialize(null, createS3DatasetProperties(createS3DatastoreProperties()));
        Set<String> bucketNames = runtime.listBuckets();
        assertTrue(bucketNames.size() > 0);
    }
}
