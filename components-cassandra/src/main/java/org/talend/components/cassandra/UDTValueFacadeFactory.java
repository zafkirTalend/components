package org.talend.components.cassandra;

import org.apache.avro.Schema.Field;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.schema.type.AvroConverter;
import org.talend.daikon.schema.type.ContainerWriterByIndex;
import org.talend.daikon.schema.type.IndexedRecordFacadeFactory;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.SettableByIndexData;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;

/**
 * Creates an {@link IndexedRecordFacadeFactory} that knows how to interpret Cassandra {@link UDTValue} objects.
 */
public class UDTValueFacadeFactory extends GettableByIndexDataFacadeFactory<UDTValue> {

    private transient UserType mType;

    @Override
    public Class<UDTValue> getSpecificClass() {
        return UDTValue.class;
    }

    @Override
    protected DataType getType(UDTValue udt, int i) {
        mType = udt.getType();
        return mType.getFieldType(getSchema().getFields().get(i).name());
    }

    @Override
    public UDTValue convertFromAvro(IndexedRecord record) {
        UDTValue value = mType.newValue();

        // TODO(rskraba): common place for this, duplicated between
        // TupleValueFacadeFactory, UDTValueFacadeFactory and
        // CassandraOutput.
        for (Field f : record.getSchema().getFields()) {
            int fieldIndex = f.pos();
            DataType fieldType = mType.getFieldType(f.name());
            Object fieldValue = record.get(fieldIndex);

            if (fieldValue == null) {
                value.setToNull(fieldIndex);
                continue;
            }

            AvroConverter<Object, Object> valueConverter = (AvroConverter<Object, Object>) CassandraAvroRegistry.get()
                    .getConverter(fieldValue.getClass(), fieldType, f.schema());

            ContainerWriterByIndex<SettableByIndexData<?>, Object> writer = (ContainerWriterByIndex<SettableByIndexData<?>, Object>) CassandraAvroRegistry
                    .get().getWriter(fieldType);

            writer.writeValue(value, f.pos(), valueConverter.convertFromAvro(fieldValue));
        }
        return value;
    }
}
