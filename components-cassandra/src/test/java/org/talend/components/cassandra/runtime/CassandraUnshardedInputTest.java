package org.talend.components.cassandra.runtime;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.talend.components.cassandra.mako.tCassandraInputDIProperties;
import org.talend.components.cassandra.mako.tCassandraInputSparkProperties;

import com.datastax.driver.core.Row;

/**
 * Unit tests for the {@link CassandraUnshardedInput}.
 */
public class CassandraUnshardedInputTest {

    @Rule
    public EmbeddedCassandraResource mCass = new EmbeddedCassandraResource(getClass().getSimpleName());

    /**
     * Tests the simplest case of this input.
     */
    @Test
    public void testBasic() {
        tCassandraInputDIProperties props = new tCassandraInputSparkProperties("tCassandraInput_1");
        props.initForRuntime();
        
        props.host.setValue(EmbeddedCassandraResource.HOST);
        props.port.setValue(EmbeddedCassandraResource.PORT);
        props.useAuth.setValue(false);
        props.keyspace.setValue(mCass.getKeySpace());
        props.columnFamily.setValue(mCass.getTableSrc());
        props.query.setValue("SELECT name FROM " + mCass.getKsTableSrc());

        try (CassandraUnshardedInput cIn = new CassandraUnshardedInput(props)) {
            cIn.setup();
            assertThat(cIn.hasNext(), is(true));
            Row r = cIn.next();
            assertThat(r.getString("name"), is("hello"));

            assertThat(cIn.hasNext(), is(true));
            r = cIn.next();
            assertThat(r.getString("name"), is("world"));

            assertThat(cIn.hasNext(), is(false));
        }
    }
}
