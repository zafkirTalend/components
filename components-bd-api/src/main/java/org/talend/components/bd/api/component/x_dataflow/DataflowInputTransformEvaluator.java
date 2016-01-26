package org.talend.components.bd.api.component.x_dataflow;

import com.cloudera.dataflow.spark.EvaluationContext;
import com.cloudera.dataflow.spark.TransformEvaluator;
import com.cloudera.dataflow.spark.WindowingHelpers;
import com.cloudera.dataflow.spark.streaming.StreamingEvaluationContext;
import com.google.cloud.dataflow.sdk.util.WindowedValue;
import org.apache.spark.api.java.JavaRDD;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.row.BaseRowStruct;
import org.talend.components.bd.api.component.BDType;
import org.talend.components.bd.api.component.IBDImplement;
import org.talend.components.bd.api.component.spark.SparkInputConf;

/**
 * Created by bchen on 16-1-25.
 */
public class DataflowInputTransformEvaluator implements TransformEvaluator<DataflowIO.Read.Component<String>> {
    @Override
    public void evaluate(DataflowIO.Read.Component<String> transform, EvaluationContext context) {
        ComponentProperties properties = transform.getProperties();
        IBDImplement bdProps = null;
        //TODO check if it's default, should be done in SparkInputConf, not in here, refactor later
        boolean isDefault = true;
        if (properties instanceof IBDImplement) {
            bdProps = (IBDImplement) properties;
            isDefault = false;
        }

        if (context instanceof StreamingEvaluationContext) {

        } else {
            //Spark Batch
            SparkInputConf sparkInputConf = null;
            if (isDefault) {
                sparkInputConf = new SparkInputConf();
            } else {
                try {
                    sparkInputConf = (SparkInputConf) Class.forName(bdProps.getImplementClassName(BDType.SparkInputConf)).newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            JavaRDD<BaseRowStruct> last =
                    sparkInputConf.invoke(context.getSparkContext(), transform.getProperties());
            JavaRDD<WindowedValue<BaseRowStruct>> rdd = last
                    .map(WindowingHelpers.<BaseRowStruct>windowFunction());
            context.setOutputRDD(transform, rdd);
        }
    }
}
