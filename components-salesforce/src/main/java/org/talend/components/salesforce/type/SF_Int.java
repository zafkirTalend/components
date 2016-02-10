package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_Int implements SalesforceBaseType<Integer, Integer> {
    @Override
    public Integer convertFromKnown(Integer value) {
        return null;
    }

    @Override
    public Integer convertToKnown(Integer value) {
        return null;
    }

    @Override
    public Integer readValue(SObject app, String key) {
        return null;
    }

    @Override
    public void writeValue(SObject app, String key, Integer value) {

    }
}
