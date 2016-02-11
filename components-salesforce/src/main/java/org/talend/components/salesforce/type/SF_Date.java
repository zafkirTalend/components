package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

import java.util.Date;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_Date implements SalesforceBaseType<Date, Date> {

    @Override
    public Date convertFromKnown(Date value) {
        return null;
    }

    @Override
    public Date convertToKnown(Date value) {
        return null;
    }

    @Override
    public Date readValue(SObject app, String key) {
        return null;
    }

    @Override
    public void writeValue(SObject app, String key, Date value) {

    }
}
