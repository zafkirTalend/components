package org.talend.components.cassandra.runtime;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

/**
 * A simple unit test that just ensures that the cassandra embedded database is running.
 * 
 * It is important that the embedded database is compatible with the spark connector.
 */
public class CassandraBasicTest {

    @Rule
    public EmbeddedCassandraResource mCass = new EmbeddedCassandraResource(getClass().getSimpleName());

    /**
     * Tests the set-up of the embedded Cassandra server. This can be complicated since the embedded server needs to
     * match the versions of cassandra used within the spark connector...
     */
    @Test
    public void testEmbeddedCassandra() {
        // Just get the result set from the automatically created table.
        ResultSet rs = mCass.getConnection().execute("SELECT name FROM " + mCass.getKsTableSrc());
        List<String> result = new ArrayList<>();
        for (Row r : rs) {
            result.add(r.getString("name"));
        }
        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder("hello", "world"));
    }
}
