package org.talend.components.cassandra;

import static org.talend.daikon.schema.avro.util.AvroUtils.unwrapIfNullable;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificData;
import org.talend.daikon.schema.type.AvroConverter;
import org.talend.daikon.schema.type.IndexedRecordFacadeFactory;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;

/**
 * Creates an {@link IndexedRecordFacadeFactory} that knows how to interpret Cassandra {@link GettableByIndexData}
 * objects.
 */
public abstract class GettableByIndexDataFacadeFactory<T extends GettableByIndexData> implements
        IndexedRecordFacadeFactory<T, IndexedRecord> {

    /**
     * Every {@link IndexedRecord} specified by this factory should use the same schema.
     */
    private Schema mSchema;

    /**
     * @return the Schema used for {@link IndexedRecord}s created by this factory, or null if no Schema has been set or
     * inferred from existing data.
     */
    @Override
    public Schema getSchema() {
        return mSchema;
    }

    /**
     * Sets the known schema for this factory to the given value, which will be used for all subsequent generated
     * {@link IndexedRecord}s, or re-inferred from incoming data if null.
     */
    @Override
    public void setSchema(Schema schema) {
        mSchema = schema;
    }

    @Override
    public IndexedRecord convertToAvro(T gettable) {
        if (mSchema == null) {
            mSchema = CassandraAvroRegistry.get().inferSchema(gettable);
        }
        return new GettableFacadeIndexedRecord(gettable, getSchema());
    }

    protected abstract DataType getType(T gettable, int i);

    /**
     * A facade that maps the given {@link GettableByIndexData} to have the appearance of an Avro {@link IndexedRecord}.
     */
    public class GettableFacadeIndexedRecord implements IndexedRecord, Comparable<IndexedRecord> {

        public T mGettable;

        public Schema mSchema;

        public DataType[] mFieldType;

        @SuppressWarnings("rawtypes")
        public AvroConverter[] mFieldConverter;

        public GettableFacadeIndexedRecord(T gettable, Schema schema) {
            mGettable = gettable;
            mSchema = schema;
            mFieldType = new DataType[unwrapIfNullable(mSchema).getFields().size()];
            mFieldConverter = new AvroConverter[mFieldType.length];
        }

        public T getRow() {
            return mGettable;
        }

        public void setRow(T r) {
            mGettable = r;
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
            // TODO(rskraba): cache this info once instead of doing it per-row,
            // per-column.
            // TODO(rskraba): error handling
            DataType fieldType = getType(mGettable, i);

            Object value = CassandraAvroRegistry.get().getReader(fieldType).readValue(mGettable, i);
            if (value == null)
                return null;

            if (mFieldConverter[i] == null) {
                mFieldConverter[i] = CassandraAvroRegistry.get().getConverter(value.getClass(), fieldType,
                        mSchema.getFields().get(i).schema());
            }

            if (mFieldConverter[i] != null) {
                value = mFieldConverter[i].convertToAvro(value);
            }

            return value;
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
            return mGettable.toString();
        }

        @Override
        public int compareTo(IndexedRecord that) {
            return ReflectData.get().compare(this, that, getSchema());
        }

    }
}
