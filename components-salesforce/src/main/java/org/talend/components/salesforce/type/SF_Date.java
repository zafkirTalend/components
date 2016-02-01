package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

import java.util.Date;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_Date extends SalesforceBaseType<Date, Date> {
    @Override
    protected Date convert2AType(Date value) {
        return null;
    }

    @Override
    protected Date convert2TType(Date value) {
        return null;
    }

    @Override
    protected Date getAppValue(SObject app, String key) {
        return null;
    }

    @Override
    protected void setAppValue(SObject app, String key, Date value) {

    }
}
