package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_Percent implements SalesforceBaseType<Double, Double> {

    @Override
    public Double convertFromKnown(Double value) {
        return null;
    }

    @Override
    public Double convertToKnown(Double value) {
        return null;
    }

    @Override
    public Double readValue(SObject app, String key) {
        return null;
    }

    @Override
    public void writeValue(SObject app, String key, Double value) {

    }
}
