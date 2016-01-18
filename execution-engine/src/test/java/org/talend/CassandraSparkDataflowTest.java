package org.talend;

import com.cloudera.dataflow.spark.SparkPipelineOptions;
import com.cloudera.dataflow.spark.SparkPipelineOptionsFactory;
import com.cloudera.dataflow.spark.SparkPipelineRunner;
import com.cloudera.dataflow.spark.TransformTranslator;
import com.google.cloud.dataflow.sdk.Pipeline;
import com.google.cloud.dataflow.sdk.io.TextIO;
import org.junit.Before;
import org.junit.Test;
import org.talend.components.api.runtime.api.spark_dataflow.SparkDataflowIO;
import org.talend.components.api.runtime.api.spark_dataflow.SparkDataflowInputTransformEvaluator;
import org.talend.components.api.schema.column.type.common.TypeMapping;
import org.talend.components.cassandra.metadata.CassandraMetadata;
import org.talend.components.cassandra.tCassandraInput.CassandraSource;
import org.talend.components.cassandra.tCassandraInput.spark.CassandraInputSparkConf;
import org.talend.components.cassandra.tCassandraInput.tCassandraInputSparkProperties;
import org.talend.components.cassandra.type.CassandraTalendTypesRegistry;

/**
 * Created by bchen on 16-1-9.
 */
public class CassandraSparkDataflowTest {
    @Before
    public void prepare() {
        TypeMapping.registryTypes(new CassandraTalendTypesRegistry());
        TransformTranslator.addTransformEvaluator(SparkDataflowIO.Read.Bound.class, new SparkDataflowInputTransformEvaluator());
    }

    @Test
    public void testType1() {
        tCassandraInputSparkProperties properties = new tCassandraInputSparkProperties("tCassandraInput_1");
        properties.initForRuntime();
        properties.host.setValue("localhost");
        properties.port.setValue("9042");
        properties.useAuth.setValue(false);
        properties.keyspace.setValue("ks");
        properties.columnFamily.setValue("test");
        properties.useQuery.setValue(false);

        CassandraMetadata m = new CassandraMetadata();
        m.initSchema(properties);

        SparkPipelineOptions options = SparkPipelineOptionsFactory.create();
        Pipeline p = Pipeline.create(options);
        p.apply(SparkDataflowIO.Read.named("ReadCassandraRow").fromProperties(properties).withSparkConf(CassandraInputSparkConf.class).withSource(CassandraSource.class)).apply(TextIO.Write.named("WriteDone").to("/tmp/out"));
        SparkPipelineRunner.create().run(p);
    }

    @Test
    public void testType2() {
        tCassandraInputSparkProperties properties = new tCassandraInputSparkProperties("tCassandraInput_1");
        properties.initForRuntime();
        properties.host.setValue("localhost");
        properties.port.setValue("9042");
        properties.useAuth.setValue(false);
        properties.keyspace.setValue("ks");
        properties.columnFamily.setValue("test");
        properties.useQuery.setValue(true);
        properties.query.setValue("select name from ks.test");

        CassandraMetadata m = new CassandraMetadata();
        m.initSchema(properties);

        SparkPipelineOptions options = SparkPipelineOptionsFactory.create();
        Pipeline p = Pipeline.create(options);
        p.apply(SparkDataflowIO.Read.named("ReadCassandraRow").fromProperties(properties).withSparkConf(CassandraInputSparkConf.class).withSource(CassandraSource.class)).apply(TextIO.Write.named("WriteDone").to("/tmp/out"));
        SparkPipelineRunner.create().run(p);
    }


}
