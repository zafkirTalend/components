package org.talend.components.cassandra;

import org.apache.avro.Schema;
import org.talend.daikon.schema.avro.AvroConverter;
import org.talend.daikon.schema.avro.util.CachedAdapterFactoryBase;
import org.talend.daikon.schema.type.ContainerReaderByIndex;
import org.talend.daikon.schema.type.ContainerWriterByIndex;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.SettableByIndexData;

/**
 * A common base class for an IndexedRecordAdapterFactory that uses exclusively Cassandra types and the specific
 * instances of {@link CassandraAvroRegistry}.
 */
public abstract class CassandraBaseAdapterFactory<GettableT extends GettableByIndexData, SettableT extends SettableByIndexData<SettableT>, ContainerDataSpecT>
        extends CachedAdapterFactoryBase<DataType, GettableT, SettableT, ContainerDataSpecT> {

    protected CassandraBaseAdapterFactory() {
        super(DataType.class);
    }

    @Override
    protected ContainerReaderByIndex<? super GettableT, ?> getFieldReader(DataType fieldType) {
        return CassandraAvroRegistry.get().getReader(fieldType);
    }

    @Override
    protected ContainerWriterByIndex<? super SettableT, ?> getFieldWriter(DataType fieldType) {
        return CassandraAvroRegistry.get().getWriter(fieldType);
    }

    @Override
    protected void setToNull(SettableT value, int fieldIndex) {
        value.setToNull(fieldIndex);
    }

    @Override
    protected void setSchemaFromContainerDataSpec(ContainerDataSpecT containerType) {
        setSchema(CassandraAvroRegistry.get().inferSchema(containerType));
    }

    @Override
    protected AvroConverter<?, ?> getConverter(DataType fieldType, Schema schema, Class<?> datumClass) {
        return CassandraAvroRegistry.get().getConverter(fieldType, schema, datumClass);
    }
}
