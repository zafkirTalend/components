package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

import java.util.Date;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_Time implements SalesforceBaseType<String, Date> {

    @Override
    public String convertFromKnown(Date value) {
        return null;
    }

    @Override
    public Date convertToKnown(String value) {
        return null;
    }

    @Override
    public String readValue(SObject app, String key) {
        return null;
    }

    @Override
    public void writeValue(SObject app, String key, String value) {

    }
}
