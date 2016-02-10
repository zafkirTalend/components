package org.talend.components.mysql.type;

import org.talend.daikon.schema.type.ExternalBaseType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by bchen on 16-1-18.
 */
public abstract class MysqlBaseType<AppType extends Object, TalendType extends Object> extends ExternalBaseType<AppType, TalendType, ResultSet, PreparedStatement> {
    public static final String FAMILY_NAME = "Mysql";

    @Override
    public AppType readValue(ResultSet obj, String key) {
        return getAppValue((ResultSet) obj, key);
    }

    @Override
    public void writeValue(PreparedStatement app, String key, AppType value) {
        setAppValue((PreparedStatement) app, key, (AppType) value);
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

    protected abstract AppType getAppValue(ResultSet app, String key);

    protected abstract void setAppValue(PreparedStatement app, String key, AppType value);
}
