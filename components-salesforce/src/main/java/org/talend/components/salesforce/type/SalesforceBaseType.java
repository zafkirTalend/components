package org.talend.components.salesforce.type;

import org.talend.daikon.schema.type.ExternalBaseType;

import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by bchen on 16-1-27.
 */
public abstract class SalesforceBaseType<AppType extends Object, TalendType extends Object>
        extends ExternalBaseType<AppType, TalendType, SObject, SObject> {

    // TODO pull this up to Definition or Properties?
    public static final String FAMILY_NAME = "Salesforce";

    @Override
    public AppType readValue(SObject obj, String key) {
        return getAppValue((SObject) obj, key);
    }

    @Override
    public void writeValue(SObject app, String key, AppType value) {
        setAppValue((SObject) app, key, (AppType) value);
    }

    @Override
    public AppType convertFromKnown(TalendType value) {
        return convert2AType((TalendType) value);
    }

    @Override
    public TalendType convertToKnown(AppType value) {
        return convert2TType((AppType) value);
    }

    protected abstract AppType convert2AType(TalendType value);

    protected abstract TalendType convert2TType(AppType value);

    protected abstract AppType getAppValue(SObject app, String key);

    protected abstract void setAppValue(SObject app, String key, AppType value);
}
