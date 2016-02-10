package org.talend.components.mysql.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by bchen on 16-1-18.
 */
public class Mysql_VARCHAR implements MysqlBaseType<String, String> {

    @Override
    public String convertFromKnown(String value) {
        return value;
    }

    @Override
    public String convertToKnown(String value) {
        return value;
    }

    @Override
    public String readValue(ResultSet app, String key) {
        // TODO throw Talend exception
        String value = null;
        try {
            value = app.getString(key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public void writeValue(PreparedStatement app, String key, String value) {
        // TODO throw Talend exception
        try {
            app.setString(Integer.valueOf(key), value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
