package org.talend.components.salesforce;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.bind.XmlObject;

import java.util.Iterator;

/**
 * Created by bchen on 16-1-27.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        ConnectorConfig config = new ConnectorConfig();
        config.setUsername("bchen2@talend.com");
        config.setPassword("talend123sfYYBBe4aZN0TcDVDV7Ylzb6Ku");
        config.setAuthEndpoint("https://www.salesforce.com/services/Soap/u/34.0");
        PartnerConnection connection = new PartnerConnection(config);
//        DescribeSObjectResult account = connection.describeSObject("Account");
//        Field[] fields = account.getFields();
//        for (Field field : fields) {
//            System.out.println(field.getName() + ":" + field.getType() + ":" + field.getSoapType());
//        }

        QueryResult queryResult = connection.query("select Id,IsDeleted,MasterRecordId,Name,Type,ParentId,BillingStreet,BillingCity,BillingState,BillingPostalCode,BillingCountry,BillingLatitude,BillingLongitude,BillingAddress,ShippingStreet,ShippingCity,ShippingState,ShippingPostalCode,ShippingCountry,ShippingLatitude,ShippingLongitude,ShippingAddress,Phone,Fax,AccountNumber,Website,PhotoUrl,Sic,Industry,AnnualRevenue,NumberOfEmployees,Ownership,TickerSymbol,Description,Rating,Site,OwnerId,CreatedDate,CreatedById,LastModifiedDate,LastModifiedById,SystemModstamp,LastActivityDate,LastViewedDate,LastReferencedDate,Jigsaw,JigsawCompanyId,AccountSource,SicDesc,CustomerPriority__c,SLA__c,Active__c,NumberofLocations__c,UpsellOpportunity__c,SLASerialNumber__c,SLAExpirationDate__c from Account");
//        QueryResult queryResult = connection.query("select ContentData from ContactFeed");
        SObject[] records = queryResult.getRecords();
        for (SObject record : records) {
            Iterator<XmlObject> children = record.getChildren();
            while (children.hasNext()) {
                XmlObject next = children.next();
                System.out.println(next.getName() + ":" + next.getValue());
            }
        }


    }
}
