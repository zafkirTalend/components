package org.talend.components.cassandra;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.talend.components.cassandra.RowReadOnlyWrapper.RowIndexedRecord;
import org.talend.daikon.schema.type.AvroConverter;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Row;

/**
 */
public class RowReadOnlyWrapper implements AvroConverter<Row, RowIndexedRecord> {

    @Override
    public Schema getAvroSchema() {
        // TODO(rskraba): set where the schema maps to the row object...
        return null;
    }

    @Override
    public Class<Row> getSpecificClass() {
        return Row.class;
    }

    @Override
    public Row convertFromAvro(RowIndexedRecord value) {
        throw new UnsupportedOperationException("Should not write to a read-only item.");
    }

    @Override
    public RowIndexedRecord convertToAvro(Row value) {
        return new RowIndexedRecord(value, getAvroSchema());
    }

    public static class RowIndexedRecord implements IndexedRecord {

        public Row mRow;

        public Schema mSchema;

        public RowIndexedRecord(Row row, Schema schema) {
            mRow = row;
            mSchema = schema;
        }

        public Row getRow() {
            return mRow;
        }

        public void setRow(Row r) {
            mRow = r;
        }

        public Schema getSchema() {
            return mSchema;
        }

        public void setSchema(Schema s) {
            // TODO: this must be a record.
            mSchema = s;
        }

        @Override
        public void put(int i, Object v) {
            throw new UnsupportedOperationException("Should not write to a read-only item.");
        }

        @Override
        public Object get(int i) {
            // TODO(rskraba): cache this info once instead of doing it per-row, per-column.
            // TODO(rskraba): error handling
            String name = mSchema.getFields().get(i).name();
            ColumnDefinitions columns = mRow.getColumnDefinitions();
            DataType type = columns.getType(name);

            return CassandraTypeRegistry.getReader(type).readValue(mRow, name);
        }
    }
}
