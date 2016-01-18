package org.talend.components.api.runtime.api.spark;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.talend.components.api.component.runtime.input.Source;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.row.BaseRowStruct;

/**
 * Created by bchen on 16-1-18.
 */
public interface SparkInputConf {
    public JavaRDD<BaseRowStruct> invoke(JavaSparkContext jsc, ComponentProperties properties, Class<? extends Source> sourceClazz);
}
