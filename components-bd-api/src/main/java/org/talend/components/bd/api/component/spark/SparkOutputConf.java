package org.talend.components.bd.api.component.spark;

import org.apache.hadoop.mapred.JobConf;
import org.apache.spark.api.java.JavaRDD;
import org.talend.components.api.component.output.Sink;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.row.BaseRowStruct;

import java.io.Serializable;

/**
 * Created by bchen on 16-1-19.
 */
public class SparkOutputConf implements Serializable {
    public void invoke(JavaRDD<BaseRowStruct> rdd, ComponentProperties properties, Class<? extends Sink> sinkClazz) {
        JobConf job = new JobConf();
        job.set("output.sink", sinkClazz.getName());
        job.set("output.props", properties.toSerialized());
        rdd.foreachPartition(new SparkOutputFunction(job));
    }
}
