package org.talend.components.cassandra;

import org.talend.daikon.schema.avro.IndexedRecordAdapterFactory;
import org.talend.daikon.schema.avro.util.AvroUtils;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;

/**
 * Creates an {@link IndexedRecordAdapterFactory} that knows how to interpret Cassandra {@link UDTValue} objects.
 */
public class UDTValueAdapterFactory extends CassandraBaseAdapterFactory<UDTValue, UDTValue, UserType> {

    @Override
    public Class<UDTValue> getDatumClass() {
        return UDTValue.class;
    }

    @Override
    protected void setContainerDataSpecFromInstance(UDTValue udt) {
        setContainerDataSpec(udt.getType());
    }

    @Override
    public DataType getFieldDataSpec(int i) {
        return getContainerDataSpec().getFieldType(AvroUtils.unwrapIfNullable(getSchema()).getFields().get(i).name());
    }

    @Override
    protected UDTValue createOrGetInstance() {
        return getContainerDataSpec().newValue();
    }

}
