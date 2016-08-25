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
package org.talend.components.dropbox.runtime.reader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.dropbox.runtime.DropboxGetSource;
import org.talend.components.dropbox.runtime.DropboxRuntimeTestBase;

/**
 * Unit-tests for {@link DropboxGetReader}
 */
public class DropboxGetReaderTest extends DropboxRuntimeTestBase {

    /**
     * Prepares required instances for tests
     */
    @Before
    public void setUp() {
        setupGetFileSchema();
        setupConnectionProperties();
        setupGetProperties();
        setupGetSource();
    }

    /**
     * Checks {@link DropboxGetReader#getCurrentTimestamp()} returns null
     * For now, this method returns null. Maybe it will be implemented later, when there will be more information
     * on how to implement it
     * @throws IOException 
     */
    @Test
    public void testGetCurrentTimestamp() throws IOException {
        DropboxGetReader reader = new DropboxGetStreamReader(getSource);
        Instant instant = reader.getCurrentTimestamp();
        assertThat(instant, nullValue());
        reader.close();
    }

    /**
     * Checks {@link DropboxGetReader#getCurrentSource()} returns {@link Source} without any changes
     * @throws IOException 
     */
    @Test
    public void testGetCurrentSource() throws IOException {
        DropboxGetReader reader = new DropboxGetStreamReader(getSource);
        DropboxGetSource actualSource = reader.getCurrentSource();
        assertEquals(getSource, actualSource);
        reader.close();
    }

    /**
     * Checks {@link DropboxGetReader#getReturnValues()} returns empty map,
     * because this component supports only ERROR_MESSAGE return value, which is computed in generated code outside component
     * @throws IOException 
     */
    @Test
    public void testGetReturnValues() throws IOException {
        DropboxGetReader reader = new DropboxGetStreamReader(getSource);
        Map<String, Object> returnValues = reader.getReturnValues();
        assertEquals(0, returnValues.size());
        reader.close();
    }

}
