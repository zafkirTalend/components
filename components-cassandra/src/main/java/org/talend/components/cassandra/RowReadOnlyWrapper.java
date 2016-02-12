package org.talend.components.cassandra;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.schema.type.IndexedRecordFacadeFactory;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Row;

/**
 */
public class RowReadOnlyWrapper implements IndexedRecordFacadeFactory<Row> {

    private Schema mSchema;

    @Override
    public Class<Row> getSpecificClass() {
        return Row.class;
    }

    @Override
    public Schema getSchema() {
        return mSchema;
    }

    @Override
    public void setSchema(Schema schema) {
        mSchema = schema;
    }

    @Override
    public IndexedRecord createFacade(Row row) {
        if (mSchema == null) {
            mSchema = inferSchema(row);
        }
        return new RowIndexedRecord(row, getSchema());
    }

    public static Schema inferSchema(Row row) {
        ColumnDefinitions cd = row.getColumnDefinitions();

        // Generate a schema from the columns.
        String recordName = cd.size() == 0 ? "Record" : cd.getTable(0) + "Record";
        FieldAssembler<Schema> fa = SchemaBuilder.record(recordName).fields();
        for (int i = 0; i < cd.size(); i++) {
            DataType type = cd.getType(i);
            fa = fa.name(cd.getName(i)).type(CassandraAvroRegistry.getSchema(type)).noDefault();
        }
        return fa.endRecord();
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

            return CassandraAvroRegistry.getReader(type).readValue(mRow, name);
        }
    }
}
