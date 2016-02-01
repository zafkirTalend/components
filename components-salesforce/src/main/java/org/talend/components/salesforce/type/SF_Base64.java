package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.util.Base64;

/**
 * Created by bchen on 16-1-27.
 */
public class SF_Base64 extends SalesforceBaseType<String, byte[]> {

    @Override
    protected String convert2AType(byte[] value) {
        return new String(Base64.encode(value));
    }

    @Override
    protected byte[] convert2TType(String value) {
        return Base64.decode(value.getBytes());
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
