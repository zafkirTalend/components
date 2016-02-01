package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_ID extends SalesforceBaseType<String, String> {
    @Override
    protected String convert2AType(String value) {
        return value;
    }

    @Override
    protected String convert2TType(String value) {
        return value;
    }

    @Override
    protected String getAppValue(SObject app, String key) {
        return app.getChild(key).getValue().toString();
    }

    @Override
    protected void setAppValue(SObject app, String key, String value) {
        app.setField(key, value);
    }
}
