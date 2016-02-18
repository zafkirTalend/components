package org.talend.components.cassandra;

import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.schema.type.IndexedRecordFacadeFactory;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Row;

/**
 * Creates an {@link IndexedRecordFacadeFactory} that knows how to interpret Cassandra {@link Row} objects.
 */
public class RowFacadeFactory extends GettableByIndexDataFacadeFactory<Row> {

    @Override
    public Class<Row> getSpecificClass() {
        return Row.class;
    }

    @Override
    protected DataType getType(Row row, int i) {
        return row.getColumnDefinitions().getType(i);
    }

    @Override
    public Row convertFromAvro(IndexedRecord record) {
        // This should never happen.
        throw new UnsupportedOperationException("Should not convert back to a Cassandra Row object.");
    }
}
