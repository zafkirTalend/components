package org.talend.components.bd.api.component.spark_dataflow;

import com.cloudera.dataflow.spark.EvaluationContext;
import com.cloudera.dataflow.spark.TransformEvaluator;
import com.cloudera.dataflow.spark.WindowingHelpers;
import com.google.cloud.dataflow.sdk.util.WindowedValue;
import org.apache.spark.api.java.JavaRDD;
import org.talend.components.bd.api.component.spark.SparkInputConf;
import org.talend.components.api.runtime.row.BaseRowStruct;

/**
 * Created by bchen on 16-1-18.
 */
public class SparkDataflowInputTransformEvaluator implements TransformEvaluator<SparkDataflowIO.Read.Bound<String>> {
    @Override
    public void evaluate(SparkDataflowIO.Read.Bound<String> transform, EvaluationContext context) {
        SparkInputConf sparkInputConf = null;
        if (transform.getSparkInputConfClazz() == null) {
            sparkInputConf = new SparkInputConf();
        } else {
            try {
                sparkInputConf = transform.getSparkInputConfClazz().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        JavaRDD<BaseRowStruct> last =
                sparkInputConf.invoke(context.getSparkContext(), transform.getProperties(), transform.getSourceClazz());
        JavaRDD<WindowedValue<BaseRowStruct>> rdd = last
                .map(WindowingHelpers.<BaseRowStruct>windowFunction());
        context.setOutputRDD(transform, rdd);
    }
}