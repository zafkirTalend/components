package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.util.Base64;

/**
 * Created by bchen on 16-1-27.
 */
public class SF_Base64 implements SalesforceBaseType<String, byte[]> {

    @Override
    public String convertFromKnown(byte[] value) {
        return new String(Base64.encode(value));
    }

    @Override
    public byte[] convertToKnown(String value) {
        return Base64.decode(value.getBytes());
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
