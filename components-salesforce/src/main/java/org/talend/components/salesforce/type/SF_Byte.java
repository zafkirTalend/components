package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-28.
 */
//TODO from API, it's byte?
public class SF_Byte extends SalesforceBaseType<Byte, Byte> {
    @Override
    protected Byte convert2AType(Byte value) {
        return null;
    }

    @Override
    protected Byte convert2TType(Byte value) {
        return null;
    }

    @Override
    protected Byte getAppValue(SObject app, String key) {
        return null;
    }

    @Override
    protected void setAppValue(SObject app, String key, Byte value) {

    }
}
