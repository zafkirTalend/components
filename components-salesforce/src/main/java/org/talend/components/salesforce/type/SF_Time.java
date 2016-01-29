package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

import java.util.Date;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_Time extends SalesforceBaseType<String, Date> {
    @Override
    protected String convert2AType(Date value) {
        return null;
    }

    @Override
    protected Date convert2TType(String value) {
        return null;
    }

    @Override
    protected String getAppValue(SObject app, String key) {
        return null;
    }

    @Override
    protected void setAppValue(SObject app, String key, String value) {

    }
}
