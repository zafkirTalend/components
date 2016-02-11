package org.talend.components.bd.api.component;

import org.talend.components.bd.api.component.spark.SparkInputConf;
import org.talend.components.bd.api.component.spark.SparkOutputConf;

/**
 * Created by bchen on 16-1-25.
 */
public class BDImplement {

    public static String getImplementClassName(BDType type) {
        if (type == BDType.SparkInputConf) {
            return SparkInputConf.class.getName();
        } else if (type == BDType.SparkOutputConf) {
            return SparkOutputConf.class.getName();
        }
        return "";
    }
}
