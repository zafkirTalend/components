package org.talend.components.cassandra.tCassandraInput.spark;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.rdd.CassandraTableScanJavaRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.row.BaseRowStruct;
import org.talend.components.bd.api.component.spark.SparkInputConf;
import org.talend.components.cassandra.tCassandraInput.tCassandraInputSparkProperties;
import org.talend.daikon.schema.SchemaElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bchen on 16-1-18.
 */
public class CassandraInputSparkConf extends SparkInputConf {
    @Override
    public JavaRDD<BaseRowStruct> invoke(JavaSparkContext jsc, ComponentProperties properties) {
        tCassandraInputSparkProperties props = (tCassandraInputSparkProperties) properties;
        if (props.useQuery.getBooleanValue()) {
            return super.invoke(jsc, properties);
        } else {
            //TODO create rowMapper for BaseRowStruct/avro
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
