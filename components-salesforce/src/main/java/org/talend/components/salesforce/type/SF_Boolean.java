package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_Boolean extends SalesforceBaseType<String, Boolean> {
    @Override
    protected String convert2AType(Boolean value) {
        return value.toString();
    }

    @Override
    protected Boolean convert2TType(String value) {
        return Boolean.valueOf(value);
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
