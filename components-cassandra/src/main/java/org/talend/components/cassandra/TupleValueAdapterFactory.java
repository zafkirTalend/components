package org.talend.components.cassandra;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.TupleType;
import com.datastax.driver.core.TupleValue;

/**
 * Creates an {@link IndexedRecordAdapterFactory} that knows how to interpret Cassandra {@link TupleValue} objects.
 */
public class TupleValueAdapterFactory extends CassandraBaseAdapterFactory<TupleValue, TupleValue, TupleType> {

    @Override
    public Class<TupleValue> getDatumClass() {
        return TupleValue.class;
    }

    @Override
    protected void setContainerDataSpecFromInstance(TupleValue tuple) {
        setContainerDataSpec(tuple.getType());
    }

    @Override
    public DataType getFieldDataSpec(int i) {
        return getContainerDataSpec().getComponentTypes().get(i);
    }

    @Override
    protected TupleValue createOrGetInstance() {
        return getContainerDataSpec().newValue();
    }
}
