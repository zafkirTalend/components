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
package org.talend.components.pubsub.runtime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.io.PubsubIO;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.talend.components.adapter.beam.coders.LazyAvroCoder;
import org.talend.components.adapter.beam.gcp.GcpServiceAccountOptions;
import org.talend.components.adapter.beam.gcp.ServiceAccountCredentialFactory;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.pubsub.PubSubDatasetProperties;
import org.talend.components.pubsub.PubSubDatastoreProperties;
import org.talend.components.pubsub.input.PubSubInputProperties;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.properties.ValidationResult;

import com.google.cloud.pubsub.PubSub;
import com.google.cloud.pubsub.ReceivedMessage;

public class PubSubInputRuntime extends PTransform<PBegin, PCollection<IndexedRecord>>
        implements RuntimableRuntime<PubSubInputProperties> {

    /**
     * The component instance that this runtime is configured for.
     */
    private PubSubInputProperties properties = null;

    @Override
    public ValidationResult initialize(RuntimeContainer container, PubSubInputProperties properties) {
        this.properties = properties;
        return ValidationResult.OK;
    }

    @Override
    public PCollection<IndexedRecord> expand(PBegin in) {
        PubSubDatasetProperties dataset = properties.getDatasetProperties();
        PubSubDatastoreProperties datastore = dataset.getDatastoreProperties();

        PCollection<PubsubIO.PubsubMessage> pubsubMessages = null;

        if (properties.noACK.getValue()) {// getSample
            pubsubMessages = in.apply(Create.of(dataset.subscription.getValue())).apply(ParDo.of(new SampleFn(properties)))
                    .setCoder(PubSubMessageCoder.of())
                    .apply(MapElements.via(new ExtractAttributes(dataset.attributes.genAttributesMap())));
        } else {// normal
            GcpServiceAccountOptions gcpOptions = in.getPipeline().getOptions().as(GcpServiceAccountOptions.class);
            gcpOptions.setProject(datastore.projectName.getValue());
            if (datastore.serviceAccountFile.getValue() != null) {
                gcpOptions.setCredentialFactoryClass(ServiceAccountCredentialFactory.class);
                gcpOptions.setServiceAccountFile(datastore.serviceAccountFile.getValue());
                gcpOptions.setGcpCredential(PubSubConnection.createCredentials(datastore));
            }

            PubsubIO.Read<PubsubIO.PubsubMessage> pubsubRead = PubsubIO.<PubsubIO.PubsubMessage> read().subscription(String
                    .format("projects/%s/subscriptions/%s", datastore.projectName.getValue(), dataset.subscription.getValue()));
            if (properties.useMaxReadTime.getValue()) {
                pubsubRead = pubsubRead.maxReadTime(new Duration(properties.maxReadTime.getValue()));
            }
            if (properties.useMaxNumRecords.getValue()) {
                pubsubRead = pubsubRead.maxNumRecords(properties.maxNumRecords.getValue());
            }

            if (properties.idLabel.getValue() != null && !"".equals(properties.idLabel.getValue())) {
                pubsubRead.idLabel(properties.idLabel.getValue());
            }
            if (properties.timestampLabel.getValue() != null && !"".equals(properties.timestampLabel.getValue())) {
                pubsubRead.timestampLabel(properties.timestampLabel.getValue());
            }

            pubsubMessages = in.apply(pubsubRead.withAttributes(new ExtractAttributes(dataset.attributes.genAttributesMap()))
                    .withCoder(PubSubMessageCoder.of()));
        }

        switch (dataset.valueFormat.getValue()) {
        case AVRO: {
            return pubsubMessages.apply(ParDo.of(new ConvertToAvro(dataset.avroSchema.getValue())))
                    .setCoder(getDefaultOutputCoder());
        }
        case CSV: {
            return (PCollection<IndexedRecord>) pubsubMessages
                    .apply(ParDo.of(new ExtractCsvSplit(dataset.fieldDelimiter.getValue())));
        }
        default:
            throw new RuntimeException("To be implemented: " + dataset.valueFormat.getValue());
        }
    }

    @Override
    public Coder getDefaultOutputCoder() {
        return LazyAvroCoder.of();
    }

    /**
     * Generate new PubsubMessage which only contains the attributes user defined.
     *
     * If there is no key provided, set it with null value.
     *
     * And the key of attributes be changed to avro column name, which helpfully for following function.
     */
    public static class ExtractAttributes extends SimpleFunction<PubsubIO.PubsubMessage, PubsubIO.PubsubMessage> {

        private Map<String, String> attrsMap;

        public ExtractAttributes(Map<String, String> attrsMap) {
            this.attrsMap = attrsMap;
        }

        @Override
        public PubsubIO.PubsubMessage apply(PubsubIO.PubsubMessage pubsubMessage) {
            Map<String, String> attributes = new HashMap<>();
            if (!attrsMap.isEmpty()) {
                // only need the attributes which user defined
                for (String attrKey : attrsMap.keySet()) {
                    attributes.put(attrsMap.get(attrKey), pubsubMessage.getAttributeMap().get(attrKey));
                }
            }
            return new PubsubIO.PubsubMessage(pubsubMessage.getMessage(), attributes);
        }
    }

    public static class ConvertToAvro extends DoFn<PubsubIO.PubsubMessage, IndexedRecord> {

        private final String schemaStr;

        private transient Schema schema;

        private transient Schema schemaWithAttrs;

        private transient DatumReader<GenericRecord> datumReader;

        private transient BinaryDecoder decoder;

        ConvertToAvro(String schemaStr) {
            this.schemaStr = schemaStr;
        }

        @DoFn.ProcessElement
        public void processElement(ProcessContext c) throws IOException {
            if (schema == null) {
                schema = new Schema.Parser().parse(schemaStr);
                datumReader = new GenericDatumReader<GenericRecord>(schema);
            }
            PubsubIO.PubsubMessage element = c.element();
            Map<String, String> attributeMap = element.getAttributeMap();
            decoder = DecoderFactory.get().binaryDecoder(element.getMessage(), decoder);

            if (!attributeMap.isEmpty()) {
                if (schemaWithAttrs == null) {
                    List<Schema.Field> fields = new ArrayList<>();
                    for (String attrName : attributeMap.keySet()) {
                        fields.add(new Schema.Field(attrName, SchemaBuilder.builder().stringBuilder().endString(), null, null));
                    }
                    schemaWithAttrs = AvroUtils.appendFields(schema, fields.toArray(new Schema.Field[] {}));
                }
                GenericRecord record = datumReader.read(null, decoder);

                GenericData.Record recordWithAttrs = new GenericData.Record(schemaWithAttrs);
                for (String attrName : attributeMap.keySet()) {
                    recordWithAttrs.put(attrName, attributeMap.get(attrName));
                }
                for (Schema.Field field : schema.getFields()) {
                    recordWithAttrs.put(field.name(), record.get(field.name()));
                }
                c.output(recordWithAttrs);
            } else {
                GenericRecord record = datumReader.read(null, decoder);
                c.output(record);
            }
        }
    }

    static class SampleFn extends DoFn<String, PubsubIO.PubsubMessage> {

        private PubSubInputProperties spec;

        private PubSub client;

        private int maxNum = 100;

        private long maxTime = 1000l;// 1 second

        private SampleFn(PubSubInputProperties spec) {
            this.spec = spec;
        }

        @Setup
        public void setup() {
            client = PubSubConnection.createClient(spec.getDatasetProperties().getDatastoreProperties());
            if (spec.useMaxNumRecords.getValue()) {
                maxNum = spec.maxNumRecords.getValue();
            }
            if (spec.useMaxReadTime.getValue()) {
                maxTime = spec.maxReadTime.getValue();
            }
        }

        @ProcessElement
        public void processElement(ProcessContext context) {
            int num = 0;
            Instant endTime = Instant.now().plus(maxTime);
            while (true) {
                Iterator<ReceivedMessage> messageIterator = client.pull(context.element(), maxNum);
                while (messageIterator.hasNext()) {
                    ReceivedMessage next = messageIterator.next();
                    context.output(new PubsubIO.PubsubMessage(next.getPayload().toByteArray(), next.getAttributes()));
                    // no next.ack() for getSample, if call ack then the message will be removed
                    num++;
                    if (num >= maxNum) {
                        break;
                    }
                }
                if (Instant.now().isAfter(endTime)) {
                    break;
                }
            }
        }

        @Teardown
        public void teardown() throws Exception {
            if (client != null) {
                client.close();
            }
        }
    }

}
