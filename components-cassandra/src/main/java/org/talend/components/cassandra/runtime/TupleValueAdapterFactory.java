package org.talend.components.cassandra.runtime;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.TupleType;
import com.datastax.driver.core.TupleValue;
import org.talend.daikon.avro.IndexedRecordAdapterFactory;

/**
 * Creates an {@link IndexedRecordAdapterFactory} that knows how to interpret Cassandra {@link TupleValue} objects.
 */
public class TupleValueAdapterFactory extends CassandraBaseAdapterFactory<TupleValue, TupleValue, TupleType> {

    @Override
    public Class<TupleValue> getDatumClass() {
        return TupleValue.class;
    }

    @Override
    protected void setContainerTypeFromInstance(TupleValue tuple) {
        setContainerType(tuple.getType());
    }

    @Override
    protected DataType getFieldType(int i) {
        return getContainerType().getComponentTypes().get(i);
    }

    @Override
    protected TupleValue createOrGetInstance() {
        return getContainerType().newValue();
    }
}
