package org.talend.components.cassandra.beam;

import static org.talend.daikon.avro.AvroUtils.unwrapIfNullable;

import org.apache.beam.sdk.io.cassandra.CassandraColumnDefinition;
import org.apache.beam.sdk.io.cassandra.CassandraRow;
import com.datastax.driver.core.ColumnMetadata;
import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.TableMetadata;
import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificData;
import org.talend.bigdata.Converter;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.cassandra.input.TCassandraInputProperties;
import org.talend.components.cassandra.runtime.CassandraAvroRegistry;
import org.talend.components.cassandra.runtime.CassandraSourceOrSink;
import org.talend.daikon.avro.converter.AvroConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CassandraIORowAdapterFactory implements Converter<CassandraRow,
        IndexedRecord> {

    private Schema mSchema;

    private ComponentProperties props;

    @Override
    public Schema getSchema() {
        return null;
    }

    @Override
    public void setSchema(Schema schema) {
        this.mSchema = schema;
    }

    @Override
    public Class<CassandraRow> getDatumClass() {
        return CassandraRow.class;
    }

    @Override
    public CassandraRow convertToDatum(IndexedRecord value) {
        CassandraRow row = new CassandraRow();
        Map<String, CassandraColumnDefinition.Type> types = getTypes();
        for (Schema.Field f : unwrapIfNullable(getSchema()).getFields()) {
            int fieldIndex = f.pos();
            Object fieldValue = value.get(fieldIndex);
            AvroConverter converter = CassandraAvroRegistry.get().getConverter(types.get
                    (f.name()), f.schema(), fieldValue.getClass());
            row.add(f.name(), types.get(f.name()), converter.convertToDatum(fieldValue));
        }
        return row;
    }

    @Override
    public IndexedRecord convertToAvro(CassandraRow value) {
        if (getSchema() == null) {
            Schema schema = CassandraAvroRegistry.get().inferSchema(value);
            setSchema(schema);
        }
        GettableAdapterIndexedRecord record = new GettableAdapterIndexedRecord(value);
        return record;
    }

    @Override
    public ComponentProperties getProps() {
        return props;
    }

    @Override
    public void setProps(ComponentProperties componentProperties) {
        this.props = componentProperties;
    }

    private Map<String, CassandraColumnDefinition.Type> getTypes() {
        CassandraSourceOrSink sos = new CassandraSourceOrSink();
        sos.initialize(null, props);
        TableMetadata cassandraMetadata = null;
        try {
            cassandraMetadata = sos.getCassandraMetadata(null, (
                    (TCassandraInputProperties) props)
                    .getSchemaProperties().keyspace
                    .getValue(), ((TCassandraInputProperties) props).getSchemaProperties()
                    .columnFamily
                    .getValue());
        } catch (IOException e) {
            //TODO how to throw exception here
        }
        Map<String, CassandraColumnDefinition.Type> types = new HashMap<>();
        List<ColumnMetadata> columns = cassandraMetadata.getColumns();
        for (ColumnMetadata column : columns) {
            types.put(column.getName(), CassandraRow.getType(column.getType()));
        }

        return types;


    }

    /**
     * A adapter that maps the given {@link GettableByIndexData} to have the appearance of an
     * Avro {@link IndexedRecord}.
     */
    public class GettableAdapterIndexedRecord implements IndexedRecord, Comparable<IndexedRecord> {

        /**
         * The wrapped GettableByIndexData object.
         */
        public CassandraRow row;

        public GettableAdapterIndexedRecord(CassandraRow row) {
            this.row = row;
        }

        public CassandraRow getRow() {
            return row;
        }

        public void setRow(CassandraRow r) {
            row = r;
        }

        public Schema getSchema() {
            return mSchema;
        }

        @Override
        public void put(int i, Object v) {
            // This should never happen.
            throw new UnsupportedOperationException("Should not write to a read-only item.");
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object get(int i) {
            Object value = row.getValue(i);
            if (value == null) {
                return null;
            }
            //TODO cache converter
            AvroConverter converter = CassandraAvroRegistry.get().getConverter(row
                    .getDefinitions().get(i).getColType(), unwrapIfNullable(mSchema).getFields()
                    .get(i).schema(), value.getClass());

            return converter.convertToAvro(value);
        }

        @Override
        public int hashCode() {
            // Base the hash code only on the schema.
            return SpecificData.get().hashCode(this, getSchema());
        }

        @Override
        public boolean equals(Object that) {
            if (that == this)
                return true; // identical object
            if (!(that instanceof IndexedRecord))
                return false; // not a record
            return compareTo((IndexedRecord) that) == 0;
        }

        @Override
        public String toString() {
            return row.toString();
        }

        @Override
        public int compareTo(IndexedRecord that) {
            return ReflectData.get().compare(this, that, getSchema());
        }

    }
}
