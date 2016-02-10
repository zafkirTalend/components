package org.talend.components.mysql.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.talend.daikon.schema.type.ExternalBaseType;

/**
 * Created by bchen on 16-1-18.
 */
public interface MysqlBaseType<AppType extends Object, TalendType extends Object>
        extends ExternalBaseType<AppType, TalendType, ResultSet, PreparedStatement> {

    public static final String FAMILY_NAME = "Mysql";

}
