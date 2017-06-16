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

package org.talend.components.salesforce;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.talend.components.salesforce.runtime.SalesforceBulkRuntime;
import org.talend.components.salesforce.tsalesforceinput.TSalesforceInputProperties;

import com.csvreader.CsvWriter;
import com.sforce.async.BatchInfo;
import com.sforce.async.BatchInfoList;
import com.sforce.async.BatchStateEnum;
import com.sforce.async.BulkConnection;
import com.sforce.async.ConcurrencyMode;
import com.sforce.async.JobInfo;
import com.sforce.async.JobStateEnum;

/**
 *
 */
public class SalesforceBulkRuntimeTest {

    private TSalesforceInputProperties inputProperties;

    private BulkConnection conn;

    private SalesforceBulkRuntime runtime;

    @Before
    public void setUp() throws Exception {
        inputProperties = new TSalesforceInputProperties("input");

        conn = mock(BulkConnection.class);

        runtime = new SalesforceBulkRuntime(conn);
        assertTrue(conn == runtime.getBulkConnection());
    }

    @Test
    public void testSetChunkingDefault() throws IOException {
        inputProperties.chunkSize.setValue(0);
        inputProperties.chunkSleepTime.setValue(0);

        runtime.setChunkProperties(inputProperties);

        assertEquals(TSalesforceInputProperties.DEFAULT_CHUNK_SIZE, runtime.getChunkSize());
        assertEquals(TSalesforceInputProperties.DEFAULT_CHUNK_SLEEP_TIME * 1000, runtime.getChunkSleepTime());
    }

    @Test
    public void testSetChunkingNormal() throws IOException {
        inputProperties.chunkSize.setValue(50000);
        inputProperties.chunkSleepTime.setValue(5);

        runtime.setChunkProperties(inputProperties);

        assertEquals(50000, runtime.getChunkSize());
        assertEquals(5 * 1000, runtime.getChunkSleepTime());
    }

    @Test
    public void testSetChunkingGreaterThanMax() throws IOException {
        inputProperties.chunkSize.setValue(TSalesforceInputProperties.MAX_CHUNK_SIZE + 10000);

        runtime.setChunkProperties(inputProperties);

        assertEquals(TSalesforceInputProperties.MAX_CHUNK_SIZE, runtime.getChunkSize());
    }

    @Test
    public void testSetConcurrencyMode() throws IOException {
        runtime.setConcurrencyMode(SalesforceBulkProperties.Concurrency.Serial);
        assertEquals(ConcurrencyMode.Serial, runtime.getConcurrencyMode());

        runtime.setConcurrencyMode(SalesforceBulkProperties.Concurrency.Parallel);
        assertEquals(ConcurrencyMode.Parallel, runtime.getConcurrencyMode());
    }

    @Test
    public void testExecuteBulk() throws Exception {
        File bulkFile = File.createTempFile("SalesforceBulkRuntimeTest-batch", "csv");
        bulkFile.deleteOnExit();

        final int recordCount = 3000;

        OutputStream out = new FileOutputStream(bulkFile);
        CsvWriter csvWriter = new CsvWriter(new BufferedOutputStream(out),
                ',', Charset.forName("UTF-8"));

        for (int i = 0; i < recordCount; i++) {
            csvWriter.writeRecord(new String[]{
                    "fieldValueA" + i,
                    "fieldValueB" + i,
                    "fieldValueC" + i
            });
        }
        csvWriter.close();

        final AtomicReference<JobInfo> jobInfo = new AtomicReference<>();
        final List<BatchInfo> batchInfoList = new ArrayList<>();
        final Map<String, BatchInfo> batchInfoMap = new HashMap<>();

        when(conn.createJob(any(JobInfo.class))).then(new Answer<JobInfo>() {

            @Override
            public JobInfo answer(InvocationOnMock invocation) throws Throwable {
                JobInfo job = (JobInfo) invocation.getArguments()[0];

                job.setId(UUID.randomUUID().toString());
                job.setState(JobStateEnum.InProgress);

                jobInfo.set(job);

                return job;
            }
        });

        when(conn.createBatchFromStream(any(JobInfo.class), any(InputStream.class))).then(new Answer<BatchInfo>() {

            @Override
            public BatchInfo answer(InvocationOnMock invocation) throws Throwable {
                JobInfo job = (JobInfo) invocation.getArguments()[0];
                InputStream input = (InputStream) invocation.getArguments()[1];

                BatchInfo batch = new BatchInfo();
                batch.setJobId(job.getId());
                batch.setId(UUID.randomUUID().toString());

                synchronized (batchInfoList) {
                    batchInfoList.add(batch);
                    batchInfoMap.put(batch.getId(), batch);
                }

                synchronized (batchInfoList) {
                    batch.setState(BatchStateEnum.Queued);
                }

                Thread.sleep(500);

                synchronized (batchInfoList) {
                    batch.setState(BatchStateEnum.Completed);
                }

                return batch;
            }
        });

        when(conn.getBatchInfoList(anyString())).then(new Answer<BatchInfoList>() {

            @Override
            public BatchInfoList answer(InvocationOnMock invocation) throws Throwable {
                BatchInfoList result = new BatchInfoList();
                synchronized (batchInfoList) {
                    result.setBatchInfo(batchInfoList.toArray(new BatchInfo[batchInfoList.size()]));
                }
                return result;
            }
        });

        runtime.executeBulk("Case", SalesforceOutputProperties.OutputAction.INSERT,
                null, "csv", bulkFile.getAbsolutePath(), 1024 * 1024, 1000);

        bulkFile.delete();
    }

    @Test(expected = RuntimeException.class)
    public void testNullConnection() throws IOException {
        new SalesforceBulkRuntime(null);
    }
}
