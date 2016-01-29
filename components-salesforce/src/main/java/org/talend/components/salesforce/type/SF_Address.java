package org.talend.components.salesforce.type;

import com.sforce.soap.partner.Address;
import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_Address extends SalesforceBaseType<Address, String> {
    @Override
    protected Address convert2AType(String value) {
        return null;
    }

    @Override
    protected String convert2TType(Address value) {
        return null;
    }

    @Override
    protected Address getAppValue(SObject app, String key) {
        return null;
    }

    @Override
    protected void setAppValue(SObject app, String key, Address value) {

    }
}
