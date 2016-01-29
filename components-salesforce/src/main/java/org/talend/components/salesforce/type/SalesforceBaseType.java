package org.talend.components.salesforce.type;

import com.sforce.soap.partner.sobject.SObject;
import org.talend.components.api.schema.column.type.ExternalBaseType;

/**
 * Created by bchen on 16-1-27.
 */
public abstract class SalesforceBaseType<AppType extends Object, TalendType extends Object> extends ExternalBaseType {
    //TODO pull this up to Definition or Properties?
    public static final String FAMILY_NAME = "Salesforce";
    @Override
    protected Object getValue(Object obj, String key) {
        return getAppValue((SObject) obj, key);
    }

    @Override
    protected void setValue(Object app, String key, Object value) {
        setAppValue((SObject) app, key, (AppType) value);
    }

    @Override
    protected Object c2AType(Object value) {
        return convert2AType((TalendType) value);
    }

    @Override
    protected TalendType c2TType(Object value) {
        return convert2TType((AppType) value);
    }

    protected abstract AppType convert2AType(TalendType value);

    protected abstract TalendType convert2TType(AppType value);

    protected abstract AppType getAppValue(SObject app, String key);

    protected abstract void setAppValue(SObject app, String key, AppType value);
}
