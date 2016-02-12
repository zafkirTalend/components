package org.talend;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.runtime.metadata.Metadata;
import org.talend.components.api.runtime.row.BaseRowStruct;
import org.talend.components.bd.api.component.spark.SparkInputConf;
import org.talend.components.bd.api.component.spark.SparkOutputConf;
import org.talend.components.bd.api.component.x_dataflow.DataflowIO;
import org.talend.components.bd.api.component.x_dataflow.DataflowInputTransformEvaluator;
import org.talend.components.bd.api.component.x_dataflow.DataflowOutputTransformEvaluator;
import org.talend.components.cassandra.CassandraAvroRegistry;
import org.talend.components.cassandra.mako.tCassandraInputSparkProperties;
import org.talend.components.cassandra.mako.tCassandraOutputDIProperties;
import org.talend.components.cassandra.metadata.CassandraMetadata;
import org.talend.components.cassandra.runtime.spark.CassandraInputSparkConf;
import org.talend.daikon.schema.type.TypeMapping;

import com.cloudera.dataflow.spark.SparkPipelineOptions;
import com.cloudera.dataflow.spark.SparkPipelineOptionsFactory;
import com.cloudera.dataflow.spark.SparkPipelineRunner;
import com.cloudera.dataflow.spark.TransformTranslator;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.cloud.dataflow.sdk.Pipeline;

/**
 * Created by bchen on 16-1-9.
 */
public class CassandraSparkDataflowTest {

    private static final String HOST = "localhost";

    private static final String PORT = "9042";

    private static final String KEYSPACE = "ks";

    Session connect;

    @BeforeClass
    public static void init() throws Exception {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra("cassandra.yml");

        // TypeMapping.registryTypes(new CassandraAvroRegistry());
        TransformTranslator.addTransformEvaluator(DataflowIO.Read.Component.class, new DataflowInputTransformEvaluator());
        TransformTranslator.addTransformEvaluator(DataflowIO.Write.Component.class, new DataflowOutputTransformEvaluator());
    }

    @Before
    public void prepare() {
        Cluster cluster = new Cluster.Builder().addContactPoints(HOST).withPort(Integer.valueOf(PORT)).build();
        connect = cluster.connect();
        connect.execute("CREATE KEYSPACE ks WITH replication={'class' : 'SimpleStrategy', 'replication_factor':1}");
        connect.execute("CREATE TABLE ks.test (name text PRIMARY KEY)");
        connect.execute("insert into ks.test (name) values ('hello')");
        connect.execute("insert into ks.test (name) values ('world')");
        connect.execute("create TABLE ks.test2 (name text PRIMARY KEY)");
    }

    @After
    public void tearDown() {
        connect.execute("drop keyspace ks");
    }

    @Test
    public void testForSpark() throws TalendConnectionException {
        tCassandraInputSparkProperties props = gettCassandraInputSparkProperties();
        tCassandraOutputDIProperties outProps = gettCassandraOutputDIProperties();
        Metadata m = new CassandraMetadata();
        m.initSchema(props);
        m.initSchema(outProps);

        SparkConf conf = new SparkConf().setAppName("Test").setMaster("local[1]");
        // conf.set("spark.ui.port", "4041");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        SparkInputConf inputConf = new SparkInputConf();
        JavaRDD<BaseRowStruct> rdd = inputConf.invoke(jsc, props);
        SparkOutputConf outputConf = new SparkOutputConf();
        outputConf.invoke(rdd, outProps);
        jsc.stop();
        ResultSet rs = connect.execute("select name from ks.test2");
        List<String> result = new ArrayList<>();
        for (Row r : rs) {
            result.add(r.getString("name"));
        }
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("hello", result.get(0));
        Assert.assertEquals("world", result.get(1));
    }

    @Test
    public void testForSparkWithNativeAPI() throws TalendConnectionException {
        tCassandraInputSparkProperties props = gettCassandraInputSparkPropertiesForNativeAPI();
        tCassandraOutputDIProperties outProps = gettCassandraOutputDIProperties();
        Metadata m = new CassandraMetadata();
        m.initSchema(props);
        m.initSchema(outProps);

        SparkConf conf = new SparkConf().setAppName("Test").setMaster("local[1]");
        // conf.set("spark.ui.port", "4042");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        SparkInputConf inputConf = new CassandraInputSparkConf();
        JavaRDD<BaseRowStruct> rdd = inputConf.invoke(jsc, props);
        SparkOutputConf outputConf = new SparkOutputConf();
        outputConf.invoke(rdd, outProps);
        jsc.stop();
        ResultSet rs = connect.execute("select name from ks.test2");
        List<String> result = new ArrayList<>();
        for (Row r : rs) {
            result.add(r.getString("name"));
        }
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("hello", result.get(0));
        Assert.assertEquals("world", result.get(1));
    }

    // TODO can't stop spark in testForSparkDF, so can't test testForSparkDFWithNativeInputAPI

    /**
     * reuse the DI api
     */
    @Ignore
    @Test
    public void testForSparkDF() throws TalendConnectionException {
        tCassandraInputSparkProperties props = gettCassandraInputSparkProperties();

        tCassandraOutputDIProperties outProps = gettCassandraOutputDIProperties();

        Metadata m = new CassandraMetadata();
        m.initSchema(props);
        m.initSchema(outProps);

        SparkPipelineOptions options = SparkPipelineOptionsFactory.create();
        Pipeline p = Pipeline.create(options);
        p.apply(DataflowIO.Read.named("tCassandraInput_1").fromProperties(props)).apply(
                DataflowIO.Write.named("tCassandraOutput_1").fromProperties(outProps));
        SparkPipelineRunner.create().run(p);

        ResultSet rs = connect.execute("select name from ks.test2");
        List<String> result = new ArrayList<>();
        for (Row r : rs) {
            result.add(r.getString("name"));
        }
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("hello", result.get(0));
        Assert.assertEquals("world", result.get(1));
    }

    /**
     * use the spark-cassandra-connector api for input
     */
    @Ignore
    @Test
    public void testForSparkDFWithNativeInputAPI() throws TalendConnectionException {
        tCassandraInputSparkProperties props = gettCassandraInputSparkPropertiesForNativeAPI();

        tCassandraOutputDIProperties outProps = gettCassandraOutputDIProperties();

        Metadata m = new CassandraMetadata();
        m.initSchema(props);
        m.initSchema(outProps);

        SparkPipelineOptions options = SparkPipelineOptionsFactory.create();
        Pipeline p = Pipeline.create(options);
        p.apply(DataflowIO.Read.named("tCassandraInput_1").fromProperties(props)).apply(
                DataflowIO.Write.named("tCassandraOutput_1").fromProperties(outProps));
        SparkPipelineRunner.create().run(p);

        ResultSet rs = connect.execute("select name from ks.test2");
        List<String> result = new ArrayList<>();
        for (Row r : rs) {
            result.add(r.getString("name"));
        }
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("hello", result.get(0));
        Assert.assertEquals("world", result.get(1));
    }

    private tCassandraInputSparkProperties gettCassandraInputSparkPropertiesForNativeAPI() {
        tCassandraInputSparkProperties props = new tCassandraInputSparkProperties("tCassandraInput_1");
        props.initForRuntime();
        props.host.setValue(HOST);
        props.port.setValue(PORT);
        props.useAuth.setValue(false);
        props.keyspace.setValue(KEYSPACE);
        props.columnFamily.setValue("test");
        props.useQuery.setValue(false);
        return props;
    }

    private tCassandraInputSparkProperties gettCassandraInputSparkProperties() {
        tCassandraInputSparkProperties props = new tCassandraInputSparkProperties("tCassandraInput_1");
        props.initForRuntime();
        props.host.setValue(HOST);
        props.port.setValue(PORT);
        props.useAuth.setValue(false);
        props.keyspace.setValue(KEYSPACE);
        props.columnFamily.setValue("test");
        props.useQuery.setValue(true);
        props.query.setValue("select name from ks.test");
        return props;
    }

    private tCassandraOutputDIProperties gettCassandraOutputDIProperties() {
        tCassandraOutputDIProperties outProps = new tCassandraOutputDIProperties("tCassandraOutput_1");
        outProps.initForRuntime();
        outProps.host.setValue(HOST);
        outProps.port.setValue(PORT);
        outProps.keyspace.setValue(KEYSPACE);
        outProps.columnFamily.setValue("test2");
        outProps.dataAction.setValue("INSERT");
        outProps.useUnloggedBatch.setValue(false);
        return outProps;
    }

}
