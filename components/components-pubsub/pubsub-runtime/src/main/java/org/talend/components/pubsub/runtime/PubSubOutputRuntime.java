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

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.io.PubsubIO;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.apache.commons.collections.MapUtils;
import org.talend.components.adapter.beam.gcp.GcpServiceAccountOptions;
import org.talend.components.adapter.beam.gcp.ServiceAccountCredentialFactory;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.pubsub.PubSubDatasetProperties;
import org.talend.components.pubsub.PubSubDatastoreProperties;
import org.talend.components.pubsub.output.PubSubOutputProperties;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.properties.ValidationResult;

import com.google.cloud.pubsub.PubSub;
import com.google.cloud.pubsub.PubSubException;
import com.google.cloud.pubsub.SubscriptionInfo;
import com.google.cloud.pubsub.TopicInfo;

public class PubSubOutputRuntime extends PTransform<PCollection<IndexedRecord>, PDone>
        implements RuntimableRuntime<PubSubOutputProperties> {

    /**
     * The component instance that this runtime is configured for.
     */
    private PubSubOutputProperties properties;

    @Override
    public ValidationResult initialize(RuntimeContainer container, PubSubOutputProperties properties) {
        this.properties = properties;
        return ValidationResult.OK;
    }

    @Override
    public PDone expand(PCollection<IndexedRecord> in) {
        PubSubDatasetProperties dataset = properties.getDatasetProperties();
        PubSubDatastoreProperties datastore = dataset.getDatastoreProperties();

        GcpServiceAccountOptions gcpOptions = in.getPipeline().getOptions().as(GcpServiceAccountOptions.class);
        gcpOptions.setProject(datastore.projectName.getValue());
        if (datastore.serviceAccountFile.getValue() != null) {
            gcpOptions.setCredentialFactoryClass(ServiceAccountCredentialFactory.class);
            gcpOptions.setServiceAccountFile(datastore.serviceAccountFile.getValue());
            gcpOptions.setGcpCredential(PubSubConnection.createCredentials(datastore));
        }

        createTopicSubscriptionIfNeeded(properties);

        PubsubIO.Write<PubsubIO.PubsubMessage> pubsubWrite = PubsubIO.<PubsubIO.PubsubMessage> write()
                .topic(String.format("projects/%s/topics/%s", datastore.projectName.getValue(), dataset.topic.getValue()));

        if (properties.idLabel.getValue() != null && !"".equals(properties.idLabel.getValue())) {
            pubsubWrite.idLabel(properties.idLabel.getValue());
        }
        if (properties.timestampLabel.getValue() != null && !"".equals(properties.timestampLabel.getValue())) {
            pubsubWrite.timestampLabel(properties.timestampLabel.getValue());
        }

        pubsubWrite = pubsubWrite.withAttributes(new SimpleFunction<PubsubIO.PubsubMessage, PubsubIO.PubsubMessage>() {

            @Override
            public PubsubIO.PubsubMessage apply(PubsubIO.PubsubMessage message) {
                return message;
            }
        });

        switch (dataset.valueFormat.getValue()) {
        case CSV: {
            return (PDone) in
                    .apply(MapElements
                            .via(new FormatCsv(dataset.fieldDelimiter.getValue(), dataset.attributes.genAttributesMap())))
                    .setCoder(PubSubMessageCoder.of()).apply(pubsubWrite);
        }
        case AVRO: {
            return in.apply(MapElements.via(new SplitAvro(dataset.attributes.genAttributesMap())))
                    .setCoder(PubSubMessageCoder.of()).apply(pubsubWrite);
        }
        default:
            throw new RuntimeException("To be implemented: " + dataset.valueFormat.getValue());
        }

    }

    private void createTopicSubscriptionIfNeeded(PubSubOutputProperties properties) {
        PubSubOutputProperties.TopicOperation topicOperation = properties.topicOperation.getValue();
        if (topicOperation == PubSubOutputProperties.TopicOperation.NONE) {
            return;
        }
        PubSubDatasetProperties dataset = properties.getDatasetProperties();

        validateForCreateTopic(dataset);

        PubSub client = PubSubConnection.createClient(dataset.getDatastoreProperties());

        if (topicOperation == PubSubOutputProperties.TopicOperation.DROP_IF_EXISTS_AND_CREATE) {
            dropTopic(client, dataset);
        }

        createTopic(client, dataset);
    }

    private void createTopic(PubSub client, PubSubDatasetProperties dataset) {
        try {
            client.create(TopicInfo.of(dataset.topic.getValue()));
        } catch (PubSubException e) {
            // ignore. no check before create, so the topic may exists
        }
        client.create(SubscriptionInfo.of(dataset.topic.getValue(), dataset.subscription.getValue()));
    }

    private void dropTopic(PubSub client, PubSubDatasetProperties dataset) {
        client.deleteSubscription(dataset.subscription.getValue());
        client.deleteTopic(dataset.topic.getValue());
    }

    private void validateForCreateTopic(PubSubDatasetProperties dataset) {
        if (dataset.subscription.getValue() == null || "".equals(dataset.subscription.getValue())) {
            TalendRuntimeException.build(CommonErrorCodes.UNEXPECTED_EXCEPTION)
                    .setAndThrow("Subscription required when create topic");
        }
        if (dataset.topic.getValue() == null || "".equals(dataset.topic.getValue())) {
            TalendRuntimeException.build(CommonErrorCodes.UNEXPECTED_EXCEPTION).setAndThrow("Topic required when create topic");
        }
    }

    public static class FormatCsvFunction implements SerializableFunction<IndexedRecord, PubsubIO.PubsubMessage> {

        public final String fieldDelimiter;

        private final Map<String, String> attrsMap; // key is avro column name, value is attr name

        private StringBuilder sb = new StringBuilder();

        public FormatCsvFunction(String fieldDelimiter, Map<String, String> attrsMap) {
            this.fieldDelimiter = fieldDelimiter;
            this.attrsMap = MapUtils.invertMap(attrsMap);
        }

        @Override
        public PubsubIO.PubsubMessage apply(IndexedRecord input) {
            List<Schema.Field> fields = input.getSchema().getFields();
            Map<String, String> attrs = new HashMap<>();
            for (int i = 0; i < fields.size(); i++) {
                String attrName = attrsMap.get(fields.get(i).name());
                if (attrName != null) {
                    attrs.put(attrName, input.get(i) == null ? null : String.valueOf(input.get(i)));
                } else {
                    if (sb.length() != 0)
                        sb.append(fieldDelimiter);
                    sb.append(input.get(i));
                }
            }
            byte[] bytes = sb.toString().getBytes(Charset.forName("UTF-8"));
            sb.setLength(0);
            return new PubsubIO.PubsubMessage(bytes, attrs);
        }
    }

    public static class FormatCsv extends SimpleFunction<IndexedRecord, PubsubIO.PubsubMessage> {

        public final FormatCsvFunction function;

        public FormatCsv(String fieldDelimiter, Map<String, String> attrsMap) {
            function = new FormatCsvFunction(fieldDelimiter, attrsMap);
        }

        @Override
        public PubsubIO.PubsubMessage apply(IndexedRecord input) {
            return function.apply(input);
        }
    }

    public static class SplitAvroFunction implements SerializableFunction<IndexedRecord, PubsubIO.PubsubMessage> {

        private final Map<String, String> attrsMap; // key is avro column name, value is attr name

        private transient Schema schemaWithoutAttrs;

        public SplitAvroFunction(Map<String, String> attrsMap) {
            this.attrsMap = MapUtils.invertMap(attrsMap);
        }

        @Override
        public PubsubIO.PubsubMessage apply(IndexedRecord indexedRecord) {
            if (attrsMap.isEmpty()) {
                // All the indexedRecord should be GenericRecord after coder stage of pipeline
                try {
                    return new PubsubIO.PubsubMessage(
                            CoderUtils.encodeToByteArray(AvroCoder.of(indexedRecord.getSchema()), (GenericRecord) indexedRecord),
                            Collections.<String, String> emptyMap());
                } catch (CoderException e) {
                    throw new RuntimeException(e.getMessage());
                }
            } else {
                Map<String, String> attrs = new HashMap<>();
                if (schemaWithoutAttrs == null) {
                    schemaWithoutAttrs = AvroUtils.removeFields(indexedRecord.getSchema(), attrsMap.keySet());
                }
                GenericData.Record recordWithoutAttrs = new GenericData.Record(schemaWithoutAttrs);
                for (Schema.Field field : indexedRecord.getSchema().getFields()) {
                    if (attrsMap.keySet().contains(field.name())) {
                        Object value = indexedRecord.get(field.pos());
                        attrs.put(field.name(), value == null ? null : String.valueOf(value));
                        continue;
                    }
                    recordWithoutAttrs.put(field.name(), indexedRecord.get(field.pos()));
                }
                try {
                    return new PubsubIO.PubsubMessage(
                            CoderUtils.encodeToByteArray(AvroCoder.of(schemaWithoutAttrs), recordWithoutAttrs), attrs);
                } catch (CoderException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    }

    public static class SplitAvro extends SimpleFunction<IndexedRecord, PubsubIO.PubsubMessage> {

        public final SplitAvroFunction function; // key is avro column name, value is attr name

        public SplitAvro(Map<String, String> attrsMap) {
            function = new SplitAvroFunction(attrsMap);
        }

        @Override
        public PubsubIO.PubsubMessage apply(IndexedRecord input) {
            return function.apply(input);
        }
    }

}
