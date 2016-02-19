package org.talend.components.cassandra.runtime;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.cassandra.CassandraAvroRegistry;
import org.talend.components.cassandra.EmbeddedCassandraExampleDataResource;
import org.talend.components.cassandra.EmbeddedCassandraResource;
import org.talend.components.cassandra.mako.tCassandraInputDIProperties;
import org.talend.components.cassandra.mako.tCassandraInputSparkProperties;
import org.talend.components.cassandra.mako.tCassandraOutputDIProperties;
import org.talend.components.cassandra.metadata.CassandraMetadata;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

/**
 * Unit tests for the {@link CassandraOutput}.
 */
public class CassandraOutputTest {

    @Rule
    public EmbeddedCassandraExampleDataResource mCass = new EmbeddedCassandraExampleDataResource(getClass().getSimpleName());

    private static final CassandraAvroRegistry sRegistry = CassandraAvroRegistry.get();

    /**
     * Tests a simple case.
     * 
     * The data struct being passed into the output component is a simple String (no wrapping).
     */
    @Test
    public void testBasic() {
        // Setup.
        mCass.execute("CREATE TABLE helloworld_dst (name text PRIMARY KEY)");

        FieldAssembler<Schema> fa = SchemaBuilder.record("Record").fields();
        fa = fa.name("name").type(sRegistry.inferSchema(DataType.text())).noDefault();
        Schema schema = fa.endRecord();

        // Set up the properties to write to a table.
        tCassandraOutputDIProperties props = new tCassandraOutputDIProperties("tCassandraOutput_1");
        props.initForRuntime();
        props.host.setValue(EmbeddedCassandraResource.HOST);
        props.port.setValue(EmbeddedCassandraResource.PORT);
        props.useAuth.setValue(false);
        props.keyspace.setValue(mCass.getKeySpace());
        props.columnFamily.setValue("helloworld_dst");
        props.dataAction.setValue("INSERT");
        props.schema.schema.setValue(schema);

        // Use the output component to write.
        try (CassandraOutput cOut = new CassandraOutput(props)) {
            cOut.setup();
            cOut.emit("one");
            cOut.emit("two");
            cOut.emit("three");
        }

        // Check the expected results.
        ResultSet rs = mCass.execute("SELECT name FROM helloworld_dst");
        List<String> result = new ArrayList<>();
        for (Row r : rs) {
            result.add(r.getString("name"));
        }
        assertThat(result, hasSize(3));
        assertThat(result, containsInAnyOrder("one", "two", "three"));
    }

    /**
     * Test using the input together with the output.
     * 
     * The data struct being passed into the output component is a Cassandra {@link Row}.
     */
    @Test
    public void testCassandraBasicInAndOut() {
        // Setup.
        mCass.execute("CREATE TABLE helloworld (name text PRIMARY KEY)");
        mCass.execute("INSERT INTO helloworld (name) values ('hello')");
        mCass.execute("INSERT INTO helloworld (name) values ('world')");
        mCass.execute("CREATE TABLE helloworld_dst (name text PRIMARY KEY)");

        FieldAssembler<Schema> fa = SchemaBuilder.record("Record").fields();
        fa = fa.name("name").type(sRegistry.inferSchema(DataType.text())).noDefault();
        Schema schema = fa.endRecord();

        // Set up the properties to write to a table.
        tCassandraOutputDIProperties outProps = new tCassandraOutputDIProperties("tCassandraOutput_1");
        outProps.initForRuntime();
        outProps.host.setValue(EmbeddedCassandraResource.HOST);
        outProps.port.setValue(EmbeddedCassandraResource.PORT);
        outProps.useAuth.setValue(false);
        outProps.keyspace.setValue(mCass.getKeySpace());
        outProps.columnFamily.setValue("helloworld_dst");
        outProps.dataAction.setValue("INSERT");
        outProps.schema.schema.setValue(schema);

        tCassandraInputDIProperties props = new tCassandraInputSparkProperties("tCassandraInput_1");
        props.initForRuntime();

        props.host.setValue(EmbeddedCassandraResource.HOST);
        props.port.setValue(EmbeddedCassandraResource.PORT);
        props.useAuth.setValue(false);
        props.keyspace.setValue(mCass.getKeySpace());
        props.columnFamily.setValue("helloworld");
        props.query.setValue("SELECT name FROM helloworld");

        try (CassandraUnshardedInput cIn = new CassandraUnshardedInput(props);
                CassandraOutput cOut = new CassandraOutput(outProps);) {
            cIn.setup();
            cOut.setup();

            while (cIn.hasNext()) {
                Row r = cIn.next();
                cOut.emit(r);
            }
        }

        // Check the expected results.
        ResultSet rs = mCass.execute("SELECT name FROM helloworld_dst");
        List<String> result = new ArrayList<>();
        for (Row r : rs) {
            result.add(r.getString("name"));
        }
        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder("hello", "world"));
    }

    /**
     * Test using the input together with the output.
     * 
     * The data struct being passed into the output component is a Cassandra {@link Row}.
     */
    @Test
    public void testCassandraExampleInAndOut() {

        // Set up the properties to write to a table.
        tCassandraOutputDIProperties outProps = new tCassandraOutputDIProperties("tCassandraOutput_1");
        outProps.initForRuntime();
        outProps.host.setValue(EmbeddedCassandraResource.HOST);
        outProps.port.setValue(EmbeddedCassandraResource.PORT);
        outProps.useAuth.setValue(false);
        outProps.keyspace.setValue(mCass.getKeySpace());
        outProps.columnFamily.setValue(mCass.getTableDst());
        outProps.dataAction.setValue("INSERT");
        new CassandraMetadata().initSchema(outProps);

        tCassandraInputDIProperties props = new tCassandraInputSparkProperties("tCassandraInput_1");
        props.initForRuntime();

        props.host.setValue(EmbeddedCassandraResource.HOST);
        props.port.setValue(EmbeddedCassandraResource.PORT);
        props.useAuth.setValue(false);
        props.keyspace.setValue(mCass.getKeySpace());
        props.columnFamily.setValue(mCass.getTableSrc());
        props.query.setValue("SELECT * FROM " + mCass.getTableSrc());

        try (CassandraUnshardedInput cIn = new CassandraUnshardedInput(props);
                CassandraOutput cOut = new CassandraOutput(outProps);) {
            cIn.setup();
            cOut.setup();

            while (cIn.hasNext()) {
                Row r = cIn.next();
                cOut.emit(r);
            }
        }

        // Check the expected results.
        ResultSet rs = mCass.execute("SELECT * FROM " + mCass.getTableDst());
        List<String> result = new ArrayList<>();
        for (Row r : rs) {
            result.add(r.getString("key1"));
        }
        assertThat(result, hasSize(6));
        assertThat(result, containsInAnyOrder(mCass.sExampleSrcRowKeys));
    }

}
