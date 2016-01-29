package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_AnyType extends SalesforceBaseType<Object, Object> {
    @Override
    protected Object convert2AType(Object value) {
        return value;
    }

    @Override
    protected Object convert2TType(Object value) {
        return value;
    }

    @Override
    protected Object getAppValue(SObject app, String key) {
        return app.getChild(key).getValue().toString();
    }

    @Override
    protected void setAppValue(SObject app, String key, Object value) {
        app.setField(key, value);
    }
}
