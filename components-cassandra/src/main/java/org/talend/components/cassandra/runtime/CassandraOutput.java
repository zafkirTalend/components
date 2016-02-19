package org.talend.components.cassandra.runtime;

import java.io.Closeable;
import java.io.Serializable;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.runtime.output.Output;
import org.talend.components.cassandra.BoundStatementFacadeFactory;
import org.talend.components.cassandra.CassandraAvroRegistry;
import org.talend.components.cassandra.mako.tCassandraOutputDIProperties;
import org.talend.daikon.schema.type.IndexedRecordFacadeFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
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

    private transient IndexedRecordFacadeFactory<Object, ? extends IndexedRecord> mFactory;

    private transient BoundStatementFacadeFactory mOutFactory = new BoundStatementFacadeFactory();

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
        if (properties.useUnloggedBatch.getBooleanValue()) {
            // TODO
        } else {
            mOutFactory.setContainerType(new BoundStatement(preparedStatement));
            // Cassandra has some particular constraints where the factory facades need to be fully built to know their
            // DataType before writing. This only needs to be done once when the factory is built.
            CassandraAvroRegistry.get().buildFacadesUsingDataType(mOutFactory, null);
        }
    }

    @SuppressWarnings("unchecked")
    public void emit(Object datum) {
        // Ignore empty rows.
        if (datum == null)
            return;

        // This is all we need to do in order to ensure that we can process the incoming value as an IndexedRecord.
        if (mFactory == null)
            mFactory = (IndexedRecordFacadeFactory<Object, ? extends IndexedRecord>) CassandraAvroRegistry.get()
                    .createFacadeFactory(datum.getClass());
        IndexedRecord input = mFactory.convertToAvro(datum);

        // We're use the output factory to write the data into the bound statement.
        BoundStatement bs = mOutFactory.convertToDatum(input);

        // Executing the bound statement inserts the row.
        connection.execute(bs);
    }

    @Override
    public void close() {
        connection.close();
        cluster.close();
    }
}
