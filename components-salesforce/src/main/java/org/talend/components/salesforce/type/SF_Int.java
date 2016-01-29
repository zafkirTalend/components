package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_Int extends SalesforceBaseType<Integer, Integer> {
    @Override
    protected Integer convert2AType(Integer value) {
        return null;
    }

    @Override
    protected Integer convert2TType(Integer value) {
        return null;
    }

    @Override
    protected Integer getAppValue(SObject app, String key) {
        return null;
    }

    @Override
    protected void setAppValue(SObject app, String key, Integer value) {

    }
}
