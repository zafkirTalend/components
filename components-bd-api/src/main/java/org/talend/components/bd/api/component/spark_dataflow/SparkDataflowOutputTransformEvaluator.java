package org.talend.components.bd.api.component.spark_dataflow;

import com.cloudera.dataflow.spark.EvaluationContext;
import com.cloudera.dataflow.spark.TransformEvaluator;
import com.cloudera.dataflow.spark.WindowingHelpers;
import com.google.cloud.dataflow.sdk.util.WindowedValue;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaRDDLike;
import org.apache.spark.api.java.function.Function;
import org.talend.components.bd.api.component.spark.SparkOutputConf;
import org.talend.components.api.runtime.row.BaseRowStruct;

/**
 * Created by bchen on 16-1-19.
 */
public class SparkDataflowOutputTransformEvaluator implements TransformEvaluator<SparkDataflowIO.Write.Bound<String>> {
    @Override
    public void evaluate(SparkDataflowIO.Write.Bound<String> transform, EvaluationContext context) {
        SparkOutputConf sparkOutputConf = null;
        if (transform.getSparkOutputConfClazz() == null) {
            sparkOutputConf = new SparkOutputConf();
        } else {
            try {
                sparkOutputConf = transform.getSparkOutputConfClazz().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        JavaRDD<BaseRowStruct> last =
                ((JavaRDDLike<WindowedValue<BaseRowStruct>, ?>) context.getInputRDD(transform))
                        .map(WindowingHelpers.<BaseRowStruct>unwindowFunction())
                        .map(new Function<BaseRowStruct, BaseRowStruct>() {
                            @Override
                            public BaseRowStruct call(BaseRowStruct t) throws Exception {
                                return t;
                            }
                        });
        sparkOutputConf.invoke(last, transform.getProperties(), transform.getSinkClazz());
    }
}