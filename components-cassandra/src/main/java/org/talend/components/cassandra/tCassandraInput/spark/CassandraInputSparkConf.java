package org.talend.components.cassandra.tCassandraInput.spark;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.rdd.CassandraTableScanJavaRDD;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.talend.components.api.component.runtime.input.Source;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.api.spark.MRInputFormat;
import org.talend.components.api.runtime.api.spark.SparkInputConf;
import org.talend.components.api.runtime.row.BaseRowStruct;
import org.talend.components.api.schema.SchemaElement;
import org.talend.components.cassandra.tCassandraInput.tCassandraInputSparkProperties;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bchen on 16-1-18.
 */
public class CassandraInputSparkConf implements SparkInputConf {
    @Override
    public JavaRDD<BaseRowStruct> invoke(JavaSparkContext jsc, ComponentProperties properties, Class<? extends Source> sourceClazz) {
        tCassandraInputSparkProperties props = (tCassandraInputSparkProperties) properties;
        if (props.useQuery.getBooleanValue()) {
            JobConf job = new JobConf();
            job.set("input.source", sourceClazz.getName());
            job.set("input.props", properties.toSerialized());
            JavaPairRDD<NullWritable, BaseRowStruct> pairRDD = jsc.hadoopRDD(job, MRInputFormat.class, NullWritable.class, BaseRowStruct.class);
            return pairRDD.map(new Function<Tuple2<NullWritable, BaseRowStruct>, BaseRowStruct>() {
                @Override
                public BaseRowStruct call(Tuple2<NullWritable, BaseRowStruct> row) throws Exception {
                    return row._2();
                }
            });
        } else {
            CassandraTableScanJavaRDD<String> rdd = CassandraJavaUtil
                    .javaFunctions(jsc)
                    .cassandraTable(props.keyspace.getStringValue(), props.columnFamily.getStringValue(),
                            CassandraJavaUtil.mapColumnTo(String.class))
                    .select(CassandraJavaUtil.column("name"));
            return rdd.map(new Function<String, BaseRowStruct>() {
                @Override
                public BaseRowStruct call(String v1) throws Exception {
                    Map<String, SchemaElement.Type> metadata = new HashMap<>();
                    metadata.put("name", SchemaElement.Type.STRING);
                    BaseRowStruct row = new BaseRowStruct(metadata);
                    row.put("name", v1);
                    return row;
                }
            });
        }
    }
}
