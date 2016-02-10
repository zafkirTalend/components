package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_Boolean implements SalesforceBaseType<String, Boolean> {
    @Override
    public String convertFromKnown(Boolean value) {
        return value.toString();
    }

    @Override
    public Boolean convertToKnown(String value) {
        return Boolean.valueOf(value);
    }

    @Override
    public String readValue(SObject app, String key) {
        return app.getChild(key).getValue().toString();
    }

    @Override
    public void writeValue(SObject app, String key, String value) {
        app.setField(key, value);
    }
}
