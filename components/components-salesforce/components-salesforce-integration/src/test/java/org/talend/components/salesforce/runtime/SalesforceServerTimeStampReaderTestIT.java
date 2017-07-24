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
package org.talend.components.salesforce.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.apache.avro.generic.IndexedRecord;
import org.junit.Test;
import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.salesforce.integration.SalesforceTestBase;
import org.talend.components.salesforce.tsalesforcegetservertimestamp.TSalesforceGetServerTimestampProperties;

public class SalesforceServerTimeStampReaderTestIT extends SalesforceTestBase {

    @Test
    public void testGetServerTimestamp() throws Throwable {
        Calendar date = getServerTimestamp();
        Calendar now = Calendar.getInstance();
        assertEquals(now.get(Calendar.YEAR), date.get(Calendar.YEAR));
        assertEquals(now.get(Calendar.MONTH), date.get(Calendar.MONTH));
        // Handles case of server and local time difference
        assertTrue(Math.abs(now.get(Calendar.DATE) - date.get(Calendar.DATE)) <= 1);
    }

    public Calendar getServerTimestamp() throws Throwable {
        TSalesforceGetServerTimestampProperties props = (TSalesforceGetServerTimestampProperties) new TSalesforceGetServerTimestampProperties(
                "foo").init();
        setupProps(props.connection, !ADD_QUOTES);
        BoundedReader<IndexedRecord> bounderReader = createBoundedReader(props);
        try {
            assertTrue(bounderReader.start());
            assertFalse(bounderReader.advance());
            IndexedRecord record = bounderReader.getCurrent();
            assertNotNull(record);
            Long timestamp = (Long) record.get(0);
            Calendar ms = Calendar.getInstance();
            ms.setTimeInMillis(timestamp);
            return ms;
        } finally {
            bounderReader.close();
        }

    }

}
