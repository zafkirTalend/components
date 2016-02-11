package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-28.
 */
// TODO from API, it's byte?
public class SF_Byte implements SalesforceBaseType<Byte, Byte> {

    @Override
    public Byte convertFromKnown(Byte value) {
        return null;
    }

    @Override
    public Byte convertToKnown(Byte value) {
        return null;
    }

    @Override
    public Byte readValue(SObject app, String key) {
        return null;
    }

    @Override
    public void writeValue(SObject app, String key, Byte value) {

    }
}
