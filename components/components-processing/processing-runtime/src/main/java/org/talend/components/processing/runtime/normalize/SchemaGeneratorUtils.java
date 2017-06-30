package org.talend.components.processing.runtime.normalize;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.avro.AvroUtils;

public class SchemaGeneratorUtils {

    public static Schema getUnwrappedSchema(Field field) {
        return AvroUtils.unwrapIfNullable(field.schema());
    }

    public static Schema getUnwrappedSchema(IndexedRecord record) {
        return AvroUtils.unwrapIfNullable(record.getSchema());
    }

    /**
     * Use a Schema to generate a hierarchical GenericRecord that contains only null values.
     *
     * @param schema the parent schema of the field to set as null
     * @param fieldName the name of the field to set as null
     * @return if fieldName is a Record of the schema, the method will return a GenericRecord with any leaf set as null,
     * otherwise return null
     */
    public static IndexedRecord generateEmptyRecord(Schema schema, String fieldName) {
        if (schema.getType().equals(Type.RECORD)) {
            Schema unwrappedSchema = getUnwrappedSchema(schema.getField(fieldName));
            if (unwrappedSchema.getType().equals(Type.RECORD)) {
                GenericRecordBuilder outputRecord = new GenericRecordBuilder(unwrappedSchema);
                for (Field field : unwrappedSchema.getFields()) {
                    IndexedRecord value = generateEmptyRecord(unwrappedSchema, field.name());
                    outputRecord.set(field.name(), value);
                }
                return outputRecord.build();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
