package org.talend.components.cassandra.runtime;

import org.talend.components.api.runtime.input.UnshardedInput;
import org.talend.components.cassandra.mako.tCassandraInputDIProperties;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 * A simple interface for reading all rows from a table.
 */
public class CassandraUnshardedInput implements UnshardedInput<Row> {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The specification for this input. */
    private final tCassandraInputDIProperties properties;

    private transient Session connection;

    private transient Cluster cluster;

    private transient ResultSet rs;

    private transient Row currentRow;

    CassandraUnshardedInput(tCassandraInputDIProperties properties) {
        this.properties = properties;
    }

    @Override
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
        rs = connection.execute(properties.query.getStringValue());
    }

    @Override
    public boolean hasNext() {
        if (currentRow != null)
            return true;
        currentRow = rs.one();
        return currentRow != null;
    }

    @Override
    public Row next() {
        Row current = currentRow;
        currentRow = null;
        return current;
    }

    @Override
    public void remove() {
        // Can be removed in Java 8.
        throw new UnsupportedOperationException("Unsupported.");
    }

    @Override
    public void close() {
        connection.close();
        cluster.close();
    }
}
