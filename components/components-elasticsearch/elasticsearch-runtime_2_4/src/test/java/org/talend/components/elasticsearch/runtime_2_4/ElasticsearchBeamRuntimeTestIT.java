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

package org.talend.components.elasticsearch.runtime_2_4;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.PCollection;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.adapter.beam.coders.LazyAvroCoder;
import org.talend.components.adapter.beam.transform.ConvertToIndexedRecord;
import org.talend.components.elasticsearch.ElasticsearchDatasetProperties;
import org.talend.components.elasticsearch.ElasticsearchDatastoreProperties;
import org.talend.components.elasticsearch.input.ElasticsearchInputProperties;
import org.talend.components.elasticsearch.output.ElasticsearchOutputProperties;
import org.talend.daikon.java8.Consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ElasticsearchBeamRuntimeTestIT implements Serializable {

    public static final String INDEX_NAME = "beam";

    @Rule
    public final TestPipeline pipeline = TestPipeline.create();

    ElasticsearchDatastoreProperties datastoreProperties;

    Client client;

    @Before
    public void init() throws IOException, ExecutionException, InterruptedException {
        client = ElasticsearchTestUtils.createClient(ElasticsearchTestConstants.HOSTS.split(":")[0],
                ElasticsearchTestConstants.TRANSPORT_PORT, ElasticsearchTestConstants.CLUSTER_NAME);

        datastoreProperties = new ElasticsearchDatastoreProperties("datastore");
        datastoreProperties.init();
        datastoreProperties.nodes.setValue(ElasticsearchTestConstants.HOSTS);
        RestClient restClient = ElasticsearchConnection.createClient(datastoreProperties);

        BasicHeader emptyHeader = new BasicHeader("", "");
        Map<String, String> emptyParams = new HashMap<>();

        ElasticsearchTestUtils.deleteIndex(INDEX_NAME, client);

        Response checkExistsResponse = restClient.performRequest("HEAD", "/" + INDEX_NAME, emptyParams);
        ElasticsearchResponse checkExists = new ElasticsearchResponse(checkExistsResponse);
        if (!checkExists.isOk()) {
            // create index for test, name is 'beam'
            restClient.performRequest("PUT", "/" + INDEX_NAME, emptyHeader);
        }
    }

    @Test
    public void getSampleTest() {
        final String TYPE_NAME = "getsampletest";

        List<String> records = Arrays.asList("r1", "r2", "r3");

        List<IndexedRecord> avroRecords = new ArrayList<>();
        for (String record : records) {
            avroRecords.add(ConvertToIndexedRecord.convertToAvro(record));
        }

        ElasticsearchDatasetProperties datasetProperties = new ElasticsearchDatasetProperties("datasetProperties");
        datasetProperties.init();
        datasetProperties.setDatastoreProperties(datastoreProperties);
        datasetProperties.index.setValue(INDEX_NAME);
        datasetProperties.type.setValue(TYPE_NAME);

        ElasticsearchOutputProperties outputProperties = new ElasticsearchOutputProperties("outputProperties");
        outputProperties.init();
        outputProperties.setDatasetProperties(datasetProperties);

        ElasticsearchOutputRuntime outputRuntime = new ElasticsearchOutputRuntime();
        outputRuntime.initialize(null, outputProperties);

        PCollection<IndexedRecord> inputRecords = (PCollection<IndexedRecord>) pipeline
                .apply(Create.of(avroRecords).withCoder(LazyAvroCoder.of()));
        inputRecords.apply(outputRuntime);

        pipeline.run();

        ElasticsearchTestUtils.upgradeIndexAndGetCurrentNumDocs(INDEX_NAME, TYPE_NAME, client);

        ElasticsearchDatasetRuntime datasetRuntime = new ElasticsearchDatasetRuntime();
        datasetRuntime.initialize(null, datasetProperties);
        final List<String> samples = new ArrayList<>();
        datasetRuntime.getSample(3, new Consumer<IndexedRecord>() {

            ExtractJson parse = new ExtractJson();

            @Override
            public void accept(IndexedRecord indexedRecord) {
                samples.add(parse.apply(indexedRecord));
            }
        });
        assertThat(samples, containsInAnyOrder(records.toArray()));

    }

    @Test
    public void basicTest() throws MalformedURLException {
        final String TYPE_NAME = "basictest";

        List<String> records = Arrays.asList("r1", "r2", "r3");

        List<IndexedRecord> avroRecords = new ArrayList<>();
        for (String record : records) {
            avroRecords.add(ConvertToIndexedRecord.convertToAvro(record));
        }

        ElasticsearchDatasetProperties datasetProperties = new ElasticsearchDatasetProperties("datasetProperties");
        datasetProperties.init();
        datasetProperties.setDatastoreProperties(datastoreProperties);
        datasetProperties.index.setValue(INDEX_NAME);
        datasetProperties.type.setValue(TYPE_NAME);

        ElasticsearchOutputProperties outputProperties = new ElasticsearchOutputProperties("outputProperties");
        outputProperties.init();
        outputProperties.setDatasetProperties(datasetProperties);

        ElasticsearchOutputRuntime outputRuntime = new ElasticsearchOutputRuntime();
        outputRuntime.initialize(null, outputProperties);

        PCollection<IndexedRecord> inputRecords = (PCollection<IndexedRecord>) pipeline
                .apply(Create.of(avroRecords).withCoder(LazyAvroCoder.of()));
        inputRecords.apply(outputRuntime);

        pipeline.run();

        ElasticsearchTestUtils.upgradeIndexAndGetCurrentNumDocs(INDEX_NAME, TYPE_NAME, client);

        // input pipeline start
        ElasticsearchInputProperties inputProperties = new ElasticsearchInputProperties("inputProperties");
        inputProperties.init();
        inputProperties.setDatasetProperties(datasetProperties);

        ElasticsearchInputRuntime inputRuntime = new ElasticsearchInputRuntime();
        inputRuntime.initialize(null, inputProperties);

        PCollection<IndexedRecord> outputRecords = pipeline.apply(inputRuntime);
        PCollection<String> out = outputRecords.apply(MapElements.via(new ExtractJson()));

        PAssert.that(out).containsInAnyOrder(records);
        pipeline.run();

    }

    @Test
    public void filterTest() throws MalformedURLException {
        final String TYPE_NAME = "filtertest";

        List<String> expectedRecords = Arrays.asList("r1", "r2", "r3");
        List<String> records = Arrays.asList("r1", "r2", "r3", "q1", "q2");

        List<IndexedRecord> avroRecords = new ArrayList<>();
        for (String record : records) {
            avroRecords.add(ConvertToIndexedRecord.convertToAvro(record));
        }

        ElasticsearchDatasetProperties datasetProperties = new ElasticsearchDatasetProperties("datasetProperties");
        datasetProperties.init();
        datasetProperties.setDatastoreProperties(datastoreProperties);
        datasetProperties.index.setValue(INDEX_NAME);
        datasetProperties.type.setValue(TYPE_NAME);

        ElasticsearchOutputProperties outputProperties = new ElasticsearchOutputProperties("outputProperties");
        outputProperties.init();
        outputProperties.setDatasetProperties(datasetProperties);

        ElasticsearchOutputRuntime outputRuntime = new ElasticsearchOutputRuntime();
        outputRuntime.initialize(null, outputProperties);

        PCollection<IndexedRecord> inputRecords = (PCollection<IndexedRecord>) pipeline
                .apply(Create.of(avroRecords).withCoder(LazyAvroCoder.of()));
        inputRecords.apply(outputRuntime);

        pipeline.run();

        ElasticsearchTestUtils.upgradeIndexAndGetCurrentNumDocs(INDEX_NAME, TYPE_NAME, client);

        // input pipeline start
        ElasticsearchInputProperties inputProperties = new ElasticsearchInputProperties("inputProperties");
        inputProperties.init();
        inputProperties.setDatasetProperties(datasetProperties);
        inputProperties.query.setValue("{\"query\":{\"regexp\":{\"field\":\"r[1-3]\"}}}");

        ElasticsearchInputRuntime inputRuntime = new ElasticsearchInputRuntime();
        inputRuntime.initialize(null, inputProperties);

        PCollection<IndexedRecord> outputRecords = pipeline.apply(inputRuntime);
        PCollection<String> out = outputRecords.apply(MapElements.via(new ExtractJson()));

        PAssert.that(out).containsInAnyOrder(expectedRecords);
        pipeline.run();

    }

    public static class ExtractJson extends SimpleFunction<IndexedRecord, String> {

        private static final ObjectMapper mapper = new ObjectMapper();

        @Override
        public String apply(IndexedRecord input) {
            try {
                JsonNode jsonNode = mapper.readValue(String.valueOf(input.get(0)), JsonNode.class);
                return jsonNode.path("field").asText();
            } catch (IOException e) {
                return null;
            }
        }
    }
}
