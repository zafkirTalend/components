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
package org.talend.components.dropbox.runtime;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.properties.ValidationResult;

/**
 * Unit-tests for {@link DropboxComponentSourceOrSink} class
 */
public class DropboxComponentSourceOrSinkTest extends DropboxRuntimeTestBase {

    /**
     * Prepares required instances for tests
     */
    @Before
    public void setUp() {
        setupConnectionProperties();
        setupCommonProperties();
    }

    /**
     * Checks {@link DropboxComponentSourceOrSink#initialize(RuntimeContainer, ComponentProperties)} sets required fields from
     * {@link ComponentProperties}
     */
    @Test
    public void testInitialize() {
        DropboxComponentSourceOrSink sourceOrSink = new DropboxComponentSourceOrSink();

        ValidationResult validation = sourceOrSink.initialize(null, commonProperties);
        assertEquals(ValidationResult.OK, validation);

        String path = sourceOrSink.getPath();
        assertEquals("/path/to/test/file.txt", path);
    }

}
