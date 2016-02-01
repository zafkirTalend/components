package org.talend.components.bd.api.component.spark;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.talend.components.api.runtime.input.Source;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.IDIImplement;
import org.talend.components.api.runtime.row.BaseRowStruct;
import scala.Tuple2;

import java.io.Serializable;

/**
 * Created by bchen on 16-1-18.
 */
public class SparkInputConf implements Serializable {
    //TODO need talendJobContext
    public JavaRDD<BaseRowStruct> invoke(JavaSparkContext jsc, ComponentProperties properties) {
        Class<? extends Source> sourceClazz = null;
        if (properties instanceof IDIImplement) {
            sourceClazz = ((IDIImplement) properties).getSourceClass();
        }
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
    }
}
