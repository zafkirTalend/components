package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

import java.util.List;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_JunctionIdList extends SalesforceBaseType<List, List> {
    @Override
    protected List convert2AType(List value) {
        return null;
    }

    @Override
    protected List convert2TType(List value) {
        return null;
    }

    @Override
    protected List getAppValue(SObject app, String key) {
        return null;
    }

    @Override
    protected void setAppValue(SObject app, String key, List value) {

    }
}
