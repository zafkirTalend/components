package org.talend.components.salesforce.tsalesforceconnection;

import com.sforce.async.BulkConnection;
import com.sforce.soap.partner.PartnerConnection;

/**
 * Created by bchen on 16-1-29.
 */
public class SalesforceConnectionObject {
    PartnerConnection partnerConnection;

    BulkConnection bulkConnection;

    public SalesforceConnectionObject(PartnerConnection partnerConnection, BulkConnection bulkConnection) {
        this.partnerConnection = partnerConnection;
        this.bulkConnection = bulkConnection;
    }

    public PartnerConnection getPartnerConnection() {
        return partnerConnection;
    }

    public BulkConnection getBulkConnection() {
        return bulkConnection;
    }
}
