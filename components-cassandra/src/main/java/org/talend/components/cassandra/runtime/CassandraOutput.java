package org.talend.components.cassandra.runtime;

/**
 * A simple interface to write all incoming rows.
 */
public class CassandraOutput {

    // /** Default serial version UID. */
    // private static final long serialVersionUID = 1L;
    //
    // /** The specification for this output. */
    // private final tCassandraOutputDIProperties properties;
    //
    // private transient Session connection;
    //
    // private transient Cluster cluster;
    //
    // private transient ResultSet rs;
    //
    // private transient Row currentRow;
    //
    // CassandraOutput(tCassandraOutputDIProperties properties) {
    // this.properties = properties;
    // }
    //
    // @Override
    // public void setup() {
    // // TODO connection pool
    // Cluster.Builder clusterBuilder = Cluster.builder() //
    // .addContactPoints(properties.host.getStringValue().split(",")) //
    // .withPort(Integer.valueOf(properties.port.getStringValue())); //
    // if (properties.useAuth.getBooleanValue()) {
    // clusterBuilder.withCredentials(properties.username.getStringValue(), properties.password.getStringValue());
    // }
    //
    // // Generate the state for the current connection.
    // cluster = clusterBuilder.build();
    // connection = cluster.connect();
    // rs = connection.execute(properties.query.getStringValue());
    // }
    //
    // @Override
    // public boolean hasNext() {
    // if (currentRow != null)
    // return true;
    // currentRow = rs.one();
    // return currentRow != null;
    // }
    //
    // @Override
    // public Void next() {
    // Row current = currentRow;
    // currentRow = null;
    // return null;
    // }
    //
    // @Override
    // public void remove() {
    // // Can be removed in Java 8.
    // throw new UnsupportedOperationException("Unsupported.");
    // }
    //
    // @Override
    // public void close() {
    // connection.close();
    // cluster.close();
    // }
}
