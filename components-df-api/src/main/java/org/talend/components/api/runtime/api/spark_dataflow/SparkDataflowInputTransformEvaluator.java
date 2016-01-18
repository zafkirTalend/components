package org.talend.components.api.runtime.api.spark_dataflow;

import com.cloudera.dataflow.spark.EvaluationContext;
import com.cloudera.dataflow.spark.TransformEvaluator;
import org.talend.components.api.runtime.api.spark.SparkInputConf;

/**
 * Created by bchen on 16-1-18.
 */
public class SparkDataflowInputTransformEvaluator implements TransformEvaluator<SparkDataflowIO.Read.Bound<String>> {
    @Override
    public void evaluate(SparkDataflowIO.Read.Bound<String> transform, EvaluationContext context) {
        SparkInputConf sparkInputConf = null;
        try {
            sparkInputConf = transform.getSparkInputConfClazz().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        context.setOutputRDD(transform, sparkInputConf.invoke(context.getSparkContext(), transform.getProperties(), transform.getSourceClazz()));
    }
}