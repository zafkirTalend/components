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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.io.PubsubIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.converter.ComparableIndexedRecordBase;

public class ExtractCsvSplit extends DoFn<PubsubIO.PubsubMessage, IndexedRecord> {

    private final String fieldDelimiter;

    private transient Schema schema;

    ExtractCsvSplit(String fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
    }

    @DoFn.ProcessElement
    public void processElement(ProcessContext c) {
        PubsubIO.PubsubMessage element = c.element();
        Map<String, String> attributeMap = element.getAttributeMap();
        String[] content = new String(element.getMessage(), Charset.forName("UTF-8")).split(fieldDelimiter);
        if (schema == null) {
            //TODO change to use built-in String[] infer
            schema = PubSubAvroRegistry.get().inferSchema(content);
            if (!attributeMap.isEmpty()) {
                List<Schema.Field> fields = new ArrayList<>();
                for (String attrName : attributeMap.keySet()) {
                    fields.add(new Schema.Field(attrName, SchemaBuilder.builder().stringBuilder().endString(), null, null));
                }
                schema = AvroUtils.addFields(schema, fields.toArray(new Schema.Field[] {}));
            }
        }

        int schemaSize = schema.getFields().size();
        int contentSize = content.length;
        if (schemaSize == contentSize) {
            c.output(new PubsubMessageIndexedRecord(schema, content));
        } else if (schemaSize == contentSize + attributeMap.size()) {
            String[] message = new String[schemaSize];
            for (Schema.Field field : schema.getFields()) {
                int pos = field.pos();
                if (content.length >= pos + 1) {
                    message[pos] = content[pos];
                } else {
                    message[pos] = attributeMap.get(field.name());
                }
            }
            c.output(new PubsubMessageIndexedRecord(schema, message));
        } else {
            throw new RuntimeException("Message is not match the schema.");
            // TODO how to handle this situation, we can image the content of message can be changed
        }
    }

    public static class PubsubMessageIndexedRecord extends ComparableIndexedRecordBase {

        private final Schema schema;

        private final String[] value;

        public PubsubMessageIndexedRecord(Schema schema, String[] value) {
            this.schema = schema;
            this.value = value;
        }

        @Override
        public Schema getSchema() {
            return schema;
        }

        @Override
        public Object get(int i) {
            return value[i];
        }

        @Override
        public void put(int i, Object v) {
            value[i] = v == null ? null : String.valueOf(v);
        }
    }
}
