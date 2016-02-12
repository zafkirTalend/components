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
import org.talend.daikon.schema.type.ContainerWriter;
import org.talend.daikon.schema.type.DatumRegistry;
import org.talend.daikon.schema.type.IndexedRecordFacadeFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

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

    private transient IndexedRecordFacadeFactory<Object> mFactory;

    CassandraOutput(tCassandraOutputDIProperties properties) {
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
            mFactory = (IndexedRecordFacadeFactory<Object>) DatumRegistry.getFacadeFactory(datum.getClass());

        IndexedRecord ir = mFactory.createFacade(datum);

        for (Field f : ir.getSchema().getFields()) {
            int index = f.pos();
            Object value = ir.get(index);

            if (value == null) {
                boundStatement.setToNull(index);
                continue;
            }

            @SuppressWarnings("unchecked")
            AvroConverter<Object, Object> valueConverter = (AvroConverter<Object, Object>) CassandraAvroRegistry
                    .getConverter(value.getClass());

            DataType columnType = CassandraAvroRegistry.getDataType(f.schema());
            @SuppressWarnings("unchecked")
            ContainerWriter<BoundStatement, Object> writer = (ContainerWriter<BoundStatement, Object>) CassandraAvroRegistry
                    .getWriter(columnType);

            writer.writeValue(boundStatement, f.pos(), valueConverter.convertFromAvro(value));
        }
        connection.execute(boundStatement);
    }

    @Override
    public void close() {
        connection.close();
        cluster.close();
    }
}
