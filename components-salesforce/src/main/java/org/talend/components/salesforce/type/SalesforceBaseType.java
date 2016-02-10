package org.talend.components.salesforce.type;

import org.talend.daikon.schema.type.ExternalBaseType;

import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-27.
 */
public interface SalesforceBaseType<AppType, TalendType> extends ExternalBaseType<AppType, TalendType, SObject, SObject> {

    // TODO pull this up to Definition or Properties?
    public static final String FAMILY_NAME = "Salesforce";
}
