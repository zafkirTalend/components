package org.talend.components.cassandra.runtime;

import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

/**
 * Unit tests for the {@link CassandraUnshardedInput}.
 */
public class EmbeddedCassandraResource extends ExternalResource {

    public static final String HOST = "localhost";

    /** Must match cassandra.yml */
    public static final String PORT = "59042";

    private Session mConnection;

    private final String mKeySpace;

    /** The test name. */
    private String mName;

    EmbeddedCassandraResource(String keySpace) {
        mKeySpace = keySpace;
        try {
            EmbeddedCassandraServerHelper.startEmbeddedCassandra("cassandra.yml", //
                    EmbeddedCassandraServerHelper.DEFAULT_TMP_DIR, //
                    EmbeddedCassandraServerHelper.DEFAULT_STARTUP_TIMEOUT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** @return A connection to the embedded Cassandra server. */
    public Session getConnection() {
        return mConnection;
    }

    /** @return An input test table name in the form 'keyspace.' + testMethodName + "Src". */
    public String getKeySpace() {
        return mKeySpace;
    }

    /** @return An input test table name in the form 'keyspace.' + testMethodName + "Src". */
    public String getKsTableSrc() {
        return mKeySpace + "." + getTableSrc();
    }

    /** @return The output test table name in the form 'keyspace.' + testMethodName + "Dst". */
    public String getKsTableDst() {
        return mKeySpace + "." + getTableDst();
    }

    /** @return An input test table name in the form testMethodName + "Src". */
    public String getTableSrc() {
        return mName + "Src";
    }

    /** @return The output test table name in the form testMethodName + "Dst". */
    public String getTableDst() {
        return mName + "Dst";
    }

    @Override
    public Statement apply(Statement base, Description d) {
        mName = d.getMethodName();
        return super.apply(base, d);
    }

    @Override
    protected void before() throws Throwable {
        Cluster cluster = new Cluster.Builder().addContactPoints(HOST).withPort(Integer.valueOf(PORT)).build();
        mConnection = cluster.connect();
        mConnection.execute(
                "CREATE KEYSPACE " + getKeySpace() + " WITH replication={'class' : 'SimpleStrategy', 'replication_factor':1}");
        mConnection.execute("CREATE TABLE " + getKsTableSrc() + " (name text PRIMARY KEY)");
        mConnection.execute("INSERT INTO " + getKsTableSrc() + " (name) values ('hello')");
        mConnection.execute("INSERT INTO " + getKsTableSrc() + " (name) values ('world')");
        mConnection.execute("CREATE TABLE " + getKsTableDst() + " (name text PRIMARY KEY)");
    };

    @Override
    protected void after() {
        mConnection.execute("DROP KEYSPACE " + getKeySpace());
        mConnection.close();
    };

}
