package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;

import java.util.List;

/**
 * Created by bchen on 16-1-28.
 */
public class SF_JunctionIdList implements SalesforceBaseType<List, List> {

    @Override
    public List convertFromKnown(List value) {
        return null;
    }

    @Override
    public List convertToKnown(List value) {
        return null;
    }

    @Override
    public List readValue(SObject app, String key) {
        return null;
    }

    @Override
    public void writeValue(SObject app, String key, List value) {

    }
}
