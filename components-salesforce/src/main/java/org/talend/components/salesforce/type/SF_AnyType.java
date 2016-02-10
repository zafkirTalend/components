package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_AnyType implements SalesforceBaseType<Object, Object> {
    @Override
    public Object convertFromKnown(Object value) {
        return value;
    }

    @Override
    public Object convertToKnown(Object value) {
        return value;
    }

    @Override
    public Object readValue(SObject app, String key) {
        return app.getChild(key).getValue().toString();
    }

    @Override
    public void writeValue(SObject app, String key, Object value) {
        app.setField(key, value);
    }
}
