package org.talend.components.salesforce.type;

import com.sforce.soap.partner.Address;
import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_Address implements SalesforceBaseType<Address, String> {

    @Override
    public Address convertFromKnown(String value) {
        return null;
    }

    @Override
    public String convertToKnown(Address value) {
        return null;
    }

    @Override
    public Address readValue(SObject app, String key) {
        return null;
    }

    @Override
    public void writeValue(SObject app, String key, Address value) {

    }
}
