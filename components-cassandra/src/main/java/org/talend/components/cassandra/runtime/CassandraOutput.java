package org.talend.components.cassandra.runtime;

import java.io.Closeable;
import java.io.Serializable;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.runtime.output.Output;
import org.talend.components.cassandra.CassandraAvroRegistry;
import org.talend.components.cassandra.mako.tCassandraOutputDIProperties;
import org.talend.daikon.schema.type.AvroConverter;
import org.talend.daikon.schema.type.ContainerWriterByIndex;
import org.talend.daikon.schema.type.DatumRegistry;
import org.talend.daikon.schema.type.IndexedRecordFacadeFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SettableByIndexData;

/**
 * A simple interface to write all incoming rows.
 */
public class CassandraOutput implements Output, Closeable, Serializable {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The specification for this output. */
    private final tCassandraOutputDIProperties properties;

    private transient Session connection;

    private transient Cluster cluster;

    private transient BoundStatement boundStatement;

    private transient ColumnDefinitions mColumns;

    private transient IndexedRecordFacadeFactory<Object, ? extends IndexedRecord> mFactory;

    public CassandraOutput(tCassandraOutputDIProperties properties) {
        this.properties = properties;
    }

    public void setup() {
        // TODO connection pool
        Cluster.Builder clusterBuilder = Cluster.builder() //
                .addContactPoints(properties.host.getStringValue().split(",")) //
                .withPort(Integer.valueOf(properties.port.getStringValue())); //
        if (properties.useAuth.getBooleanValue()) {
            clusterBuilder.withCredentials(properties.username.getStringValue(), properties.password.getStringValue());
        }

        // Generate the state for the current connection.
        cluster = clusterBuilder.build();
        connection = cluster.connect();

        CQLManager cqlManager = new CQLManager(properties, (Schema) properties.schema.schema.getValue());
        PreparedStatement preparedStatement = connection.prepare(cqlManager.generatePreActionSQL());
        mColumns = preparedStatement.getVariables();
        if (properties.useUnloggedBatch.getBooleanValue()) {
            // TODO
        } else {
            boundStatement = new BoundStatement(preparedStatement);
        }
    }

    @SuppressWarnings("unchecked")
    public void emit(Object datum) {
        if (datum == null)
            return;

        if (mFactory == null)
            mFactory = (IndexedRecordFacadeFactory<Object, ? extends IndexedRecord>) DatumRegistry.getFacadeFactory(datum
                    .getClass());

        IndexedRecord ir = mFactory.convertToAvro(datum);

        for (Field f : ir.getSchema().getFields()) {
            int fieldIndex = f.pos();
            DataType fieldType = mColumns.getType(fieldIndex);
            Object fieldValue = ir.get(fieldIndex);

            if (fieldValue == null) {
                boundStatement.setToNull(fieldIndex);
                continue;
            }

            AvroConverter<Object, Object> valueConverter = (AvroConverter<Object, Object>) CassandraAvroRegistry.get()
                    .getConverter(fieldValue.getClass(), fieldType, f.schema());

            ContainerWriterByIndex<SettableByIndexData<?>, Object> writer = (ContainerWriterByIndex<SettableByIndexData<?>, Object>) CassandraAvroRegistry
                    .get().getWriter(fieldType);

            writer.writeValue(boundStatement, f.pos(), valueConverter.convertFromAvro(fieldValue));
        }
        connection.execute(boundStatement);
    }

    @Override
    public void close() {
        connection.close();
        cluster.close();
    }
}
