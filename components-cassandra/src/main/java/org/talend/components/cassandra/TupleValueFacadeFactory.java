package org.talend.components.cassandra;

import org.talend.daikon.schema.type.IndexedRecordFacadeFactory;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.TupleType;
import com.datastax.driver.core.TupleValue;

/**
 * Creates an {@link IndexedRecordFacadeFactory} that knows how to interpret Cassandra {@link TupleValue} objects.
 */
public class TupleValueFacadeFactory extends CassandraBaseFacadeFactory<TupleValue, TupleValue, TupleType> {

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
