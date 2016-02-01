package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_Percent extends SalesforceBaseType<Double,Double> {
    @Override
    protected Double convert2AType(Double value) {
        return null;
    }

    @Override
    protected Double convert2TType(Double value) {
        return null;
    }

    @Override
    protected Double getAppValue(SObject app, String key) {
        return null;
    }

    @Override
    protected void setAppValue(SObject app, String key, Double value) {

    }
}
