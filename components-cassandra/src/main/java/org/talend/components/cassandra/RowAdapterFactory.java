package org.talend.components.cassandra;

import org.talend.daikon.schema.avro.IndexedRecordAdapterFactory;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.TupleValue;

/**
 * Creates an {@link IndexedRecordAdapterFactory} that knows how to interpret Cassandra {@link Row} objects.
 */
public class RowAdapterFactory extends CassandraBaseAdapterFactory<Row, TupleValue, Row> {

    @Override
    public Class<Row> getDatumClass() {
        return Row.class;
    }

    @Override
    protected void setContainerDataSpecFromInstance(Row row) {
        setContainerDataSpec(row);
    }

    @Override
    public DataType getFieldDataSpec(int i) {
        return getContainerDataSpec().getColumnDefinitions().getType(i);
    }

    @Override
    protected TupleValue createOrGetInstance() {
        // This should never happen, there is never any reason to create a Row. They are always obtained from a
        // ResultSet.
        throw new UnmodifiableAdapterException();
    }
}
