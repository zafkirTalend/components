package org.talend.components.pubsub.runtime;

import static org.talend.components.pubsub.runtime.PubSubTestConstants.addSubscriptionForDataset;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDataset;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDatasetFromAvro;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDatasetFromCSV;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createDatastore;
import static org.talend.components.pubsub.runtime.PubSubTestConstants.createInput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.runners.spark.SparkContextOptions;
import org.apache.beam.runners.spark.SparkRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.values.PCollection;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.adapter.beam.transform.ConvertToIndexedRecord;
import org.talend.components.pubsub.PubSubDatasetProperties;
import org.talend.components.pubsub.PubSubDatastoreProperties;
import org.talend.daikon.avro.AvroUtils;

import com.google.cloud.ByteArray;
import com.google.cloud.pubsub.Message;
import com.google.cloud.pubsub.PubSub;
import com.google.cloud.pubsub.SubscriptionInfo;
import com.google.cloud.pubsub.TopicInfo;

public class PubSubInputRuntimeTest {

    final static String uuid = UUID.randomUUID().toString();

    final static String topicName = "tcomp-pubsub-inputtest" + uuid;

    final static String subscriptionName = "tcomp-pubsub-inputtest-sub1" + uuid;

    static PubSub client = PubSubConnection.createClient(createDatastore());

    static {
        PubSubAvroRegistry.get();
    }

    @Rule
    public final TestPipeline pipeline = TestPipeline.create();

    Integer maxRecords = 10;

    PubSubDatastoreProperties datastoreProperties;

    PubSubDatasetProperties datasetProperties;

    @BeforeClass
    public static void initTopic() {
        client.create(TopicInfo.of(topicName));
        client.create(SubscriptionInfo.of(topicName, subscriptionName));
    }

    @AfterClass
    public static void cleanTopic() throws Exception {
        client.deleteTopic(topicName);
        client.deleteSubscription(subscriptionName);
        client.close();
    }

    // TODO extract this to utils
    private Pipeline createSparkRunnerPipeline() {
        JavaSparkContext jsc = new JavaSparkContext("local[2]", this.getClass().getName());
        PipelineOptions o = PipelineOptionsFactory.create();
        SparkContextOptions options = o.as(SparkContextOptions.class);
        options.setProvidedSparkContext(jsc);
        options.setUsesProvidedSparkContext(true);
        options.setRunner(SparkRunner.class);

        return Pipeline.create(options);
    }

    @Before
    public void init() {
        datastoreProperties = createDatastore();
        datasetProperties = createDataset(datastoreProperties, topicName);
    }

    @Test
    public void inputCsv_Local() {
        inputCsv(pipeline);
    }

    private void inputCsv(Pipeline pipeline) {
        String testID = "csvBasicTest" + new Random().nextInt();
        final String fieldDelimited = ";";

        List<Person> expectedPersons = Person.genRandomList(testID, maxRecords);
        List<Message> messages = new ArrayList<>();
        for (Person person : expectedPersons) {
            messages.add(Message.of(person.toCSV(fieldDelimited)));
        }
        client.publish(topicName, messages);

        PubSubInputRuntime inputRuntime = new PubSubInputRuntime();
        inputRuntime.initialize(null, createInput(
                addSubscriptionForDataset(createDatasetFromCSV(createDatastore(), topicName, fieldDelimited), subscriptionName),
                null, maxRecords));

        PCollection<IndexedRecord> readMessages = pipeline.apply(inputRuntime);

        List<IndexedRecord> expected = new ArrayList<>();
        for (Person person : expectedPersons) {
            expected.add(ConvertToIndexedRecord.convertToAvro(person.toCSV(fieldDelimited).split(fieldDelimited)));
        }
        PAssert.that(readMessages).containsInAnyOrder(expected);

        pipeline.run().waitUntilFinish();
    }

    @Test
    @Ignore("Can not run together with inputAvro_Spark, JavaSparkContext can't modify in same jvm"
            + " error, or PAssert check with wrong data issue")
    public void inputCsv_Spark() {
        inputCsv(createSparkRunnerPipeline());
    }

    @Test
    public void inputAvro_Local() throws IOException {
        inputAvro(pipeline);
    }

    @Test
    public void inputAvro_Spark() throws IOException {
        inputAvro(createSparkRunnerPipeline());
    }

    private void inputAvro(Pipeline pipeline) throws IOException {
        String testID = "avroBasicTest" + new Random().nextInt();

        List<Person> expectedPersons = Person.genRandomList(testID, maxRecords);
        List<Message> messages = new ArrayList<>();
        for (Person person : expectedPersons) {
            messages.add(Message.of(ByteArray.copyFrom(person.serToAvroBytes())));
        }
        client.publish(topicName, messages);

        PubSubInputRuntime inputRuntime = new PubSubInputRuntime();
        inputRuntime
                .initialize(null,
                        createInput(addSubscriptionForDataset(
                                createDatasetFromAvro(createDatastore(), topicName, Person.schema.toString()), subscriptionName),
                                null, maxRecords));

        PCollection<IndexedRecord> readMessages = pipeline.apply(inputRuntime);

        List<IndexedRecord> expected = new ArrayList<>();
        for (Person person : expectedPersons) {
            expected.add(person.toAvroRecord());
        }
        PAssert.that(readMessages).containsInAnyOrder(expected);

        pipeline.run().waitUntilFinish();
    }

    @Test
    public void inputCsvWithAttrs_Local() throws IOException {
        inputCsvWithAttrs(pipeline);
    }

    private void inputCsvWithAttrs(Pipeline pipeline) throws IOException {
        String testID = "csvWithAttrsTest" + new Random().nextInt();
        final String fieldDelimited = ";";

        Map<String, String> attrMapping = new HashMap<>();
        attrMapping.put("attr_name", "name");
        attrMapping.put("attr_group", "group");
        attrMapping.put("attr_age", "age");

        List<Person> expectedPersons = Person.genRandomList(testID, maxRecords);
        List<Message> messages = new ArrayList<>();
        for (Person person : expectedPersons) {
            messages.add(Message.newBuilder(person.toCSV(fieldDelimited)).addAttribute("attr_name", person.name)
                    .addAttribute("attr_group", person.group).addAttribute("attr_age", person.age.toString()).build());
        }
        client.publish(topicName, messages);

        PubSubInputRuntime inputRuntime = new PubSubInputRuntime();
        PubSubDatasetProperties dataset = addSubscriptionForDataset(
                createDatasetFromCSV(createDatastore(), topicName, fieldDelimited), subscriptionName);
        dataset.attributes.attributeName.setValue(Arrays.asList("attr_name", "attr_age"));
        dataset.attributes.columnName.setValue(Arrays.asList("", "attrAge"));
        inputRuntime.initialize(null, createInput(dataset, null, maxRecords));

        PCollection<IndexedRecord> readMessages = pipeline.apply(inputRuntime);

        List<IndexedRecord> expected = new ArrayList<>();

        Map<String, String> attrsMap = dataset.attributes.genAttributesMap();
        List<Schema.Field> fields = new ArrayList<>();
        // for (String attrName : attrsMap.keySet()) {
        fields.add(new Schema.Field("attrAge", SchemaBuilder.builder().stringBuilder().endString(), null, null));
        fields.add(new Schema.Field("attr_name", SchemaBuilder.builder().stringBuilder().endString(), null, null));
        // }
        Schema schemaWithAttrs = AvroUtils.appendFields(ConvertToIndexedRecord
                .convertToAvro(expectedPersons.get(0).toCSV(fieldDelimited).split(fieldDelimited)).getSchema(),
                fields.toArray(new Schema.Field[] {}));
        for (Person person : expectedPersons) {
            GenericData.Record recordWithAttrs = new GenericData.Record(schemaWithAttrs);
            for (String attrName : attrsMap.keySet()) {
                recordWithAttrs.put(attrsMap.get(attrName), person.toAvroRecord().get(attrMapping.get(attrName)).toString());
            }
            for (Schema.Field field : Person.schema.getFields()) {
                recordWithAttrs.put(field.pos(), person.toAvroRecord().get(field.name()).toString());
            }

            expected.add(recordWithAttrs);
        }
        PAssert.that(readMessages).containsInAnyOrder(expected);

        pipeline.run().waitUntilFinish();
    }

    @Test
    public void inputAvroWithAttrs_Local() throws IOException {
        inputAvroWithAttrs(pipeline);
    }

    private void inputAvroWithAttrs(Pipeline pipeline) throws IOException {
        String testID = "avroWithAttrsTest" + new Random().nextInt();

        Map<String, String> attrMapping = new HashMap<>();
        attrMapping.put("attr_name", "name");
        attrMapping.put("attr_group", "group");
        attrMapping.put("attr_age", "age");

        List<Person> expectedPersons = Person.genRandomList(testID, maxRecords);
        List<Message> messages = new ArrayList<>();
        for (Person person : expectedPersons) {
            messages.add(Message.newBuilder(ByteArray.copyFrom(person.serToAvroBytes())).addAttribute("attr_name", person.name)
                    .addAttribute("attr_group", person.group).addAttribute("attr_age", person.age.toString()).build());
        }
        client.publish(topicName, messages);

        PubSubInputRuntime inputRuntime = new PubSubInputRuntime();
        PubSubDatasetProperties dataset = addSubscriptionForDataset(
                createDatasetFromAvro(createDatastore(), topicName, Person.schema.toString()), subscriptionName);
        dataset.attributes.attributeName.setValue(Arrays.asList("attr_name", "attr_age"));
        dataset.attributes.columnName.setValue(Arrays.asList("", "attrAge"));
        inputRuntime.initialize(null, createInput(dataset, null, maxRecords));

        PCollection<IndexedRecord> readMessages = pipeline.apply(inputRuntime);

        List<IndexedRecord> expected = new ArrayList<>();
        Map<String, String> attrsMap = dataset.attributes.genAttributesMap();
        List<Schema.Field> fields = new ArrayList<>();
        // for (String attrName : attrsMap.keySet()) {
        fields.add(new Schema.Field("attrAge", SchemaBuilder.builder().stringBuilder().endString(), null, null));
        fields.add(new Schema.Field("attr_name", SchemaBuilder.builder().stringBuilder().endString(), null, null));
        // }
        Schema schemaWithAttrs = AvroUtils.appendFields(Person.schema, fields.toArray(new Schema.Field[] {}));
        for (Person person : expectedPersons) {
            GenericData.Record recordWithAttrs = new GenericData.Record(schemaWithAttrs);
            for (String attrName : attrsMap.keySet()) {
                recordWithAttrs.put(attrsMap.get(attrName), person.toAvroRecord().get(attrMapping.get(attrName)).toString());
            }
            for (Schema.Field field : Person.schema.getFields()) {
                recordWithAttrs.put(field.name(), person.toAvroRecord().get(field.name()));
            }

            expected.add(recordWithAttrs);
        }
        PAssert.that(readMessages).containsInAnyOrder(expected);

        pipeline.run().waitUntilFinish();
    }
}
