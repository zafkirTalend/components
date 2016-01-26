package org.talend.components.bd.api.component.x_dataflow;

import com.cloudera.dataflow.spark.EvaluationContext;
import com.cloudera.dataflow.spark.TransformEvaluator;
import com.cloudera.dataflow.spark.WindowingHelpers;
import com.google.cloud.dataflow.sdk.util.WindowedValue;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaRDDLike;
import org.apache.spark.api.java.function.Function;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.row.BaseRowStruct;
import org.talend.components.bd.api.component.BDType;
import org.talend.components.bd.api.component.IBDImplement;
import org.talend.components.bd.api.component.spark.SparkOutputConf;

/**
 * Created by bchen on 16-1-25.
 */
public class DataflowOutputTransformEvaluator implements TransformEvaluator<DataflowIO.Write.Component<String>>{
    @Override
    public void evaluate(DataflowIO.Write.Component<String> transform, EvaluationContext context) {
        ComponentProperties properties = transform.getProperties();
        IBDImplement bdProps = null;
        //TODO check if it's default, should be done in SparkInputConf, not in here, refactor later
        boolean isDefault = true;
        if (properties instanceof IBDImplement) {
            bdProps = (IBDImplement) properties;
            isDefault = false;
        }
        SparkOutputConf sparkOutputConf = null;
        if (isDefault) {
            sparkOutputConf = new SparkOutputConf();
        } else {
            try {
                sparkOutputConf = (SparkOutputConf) Class.forName(bdProps.getImplementClassName(BDType.SparkOutputConf)).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
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
        sparkOutputConf.invoke(last, transform.getProperties());
    }
}
