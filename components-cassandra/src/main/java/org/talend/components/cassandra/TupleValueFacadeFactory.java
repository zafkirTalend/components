package org.talend.components.cassandra;

import org.apache.avro.Schema.Field;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.schema.type.AvroConverter;
import org.talend.daikon.schema.type.ContainerWriterByIndex;
import org.talend.daikon.schema.type.IndexedRecordFacadeFactory;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.SettableByIndexData;
import com.datastax.driver.core.TupleType;
import com.datastax.driver.core.TupleValue;

/**
 * Creates an {@link IndexedRecordFacadeFactory} that knows how to interpret Cassandra {@link TupleValue} objects.
 */
public class TupleValueFacadeFactory extends GettableByIndexDataFacadeFactory<TupleValue> {

    private transient TupleType mType;

    @Override
    public Class<TupleValue> getSpecificClass() {
        return TupleValue.class;
    }

    @Override
    protected DataType getType(TupleValue udt, int i) {
        mType = udt.getType();
        return mType.getTypeArguments().get(i);
    }

    @Override
    public TupleValue convertFromAvro(IndexedRecord record) {
        TupleValue value = mType.newValue();

        // TODO(rskraba): common place for this, duplicated between
        // TupleValueFacadeFactory, UDTValueFacade
        for (Field f : getSchema().getFields()) {
            int fieldIndex = f.pos();
            DataType fieldType = mType.getTypeArguments().get(fieldIndex);
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
