package org.talend.components.cassandra;

import java.util.Arrays;

import org.apache.avro.Schema;
import org.talend.daikon.schema.avro.util.AvroUtils;
import org.talend.daikon.schema.type.AvroConverter;
import org.talend.daikon.schema.type.IndexedRecordFacadeFactory;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;

/**
 * Creates an {@link IndexedRecordFacadeFactory} that knows how to interpret Cassandra {@link UDTValue} objects.
 */
public class UDTValueFacadeFactory extends CassandraBaseFacadeFactory<UDTValue, UDTValue, UserType> {

    @Override
    public Class<UDTValue> getDatumClass() {
        return UDTValue.class;
    }

    @Override
    protected void setContainerTypeFromInstance(UDTValue udt) {
        setContainerType(udt.getType());
    }

    @Override
    protected DataType getFieldType(int i) {
        return getContainerType().getFieldType(AvroUtils.unwrapIfNullable(getSchema()).getFields().get(i).name());
    }

    @Override
    protected UDTValue createOrGetInstance() {
        return getContainerType().newValue();
    }

}
