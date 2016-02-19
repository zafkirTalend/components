package org.talend.components.cassandra;

import org.talend.daikon.schema.type.IndexedRecordFacadeFactory;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.TupleValue;

/**
 * Creates an {@link IndexedRecordFacadeFactory} that knows how to interpret Cassandra {@link Row} objects.
 */
public class RowFacadeFactory extends CassandraBaseFacadeFactory<Row, TupleValue, Row> {

    @Override
    public Class<Row> getDatumClass() {
        return Row.class;
    }

    @Override
    protected void setContainerTypeFromInstance(Row row) {
        setContainerType(row);
    }

    @Override
    protected DataType getFieldType(int i) {
        return getContainerType().getColumnDefinitions().getType(i);
    }

    @Override
    protected TupleValue createOrGetInstance() {
        // This should never happen, there is never any reason to create a Row.
        throw new UnmodifiableFacadeException();
    }
}
