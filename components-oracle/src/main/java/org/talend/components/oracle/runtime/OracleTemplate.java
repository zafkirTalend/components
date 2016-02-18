package org.talend.components.oracle.runtime;

import java.sql.Connection;

import org.talend.components.oracle.DBConnectionProperties;


public class OracleTemplate implements DBTemplate {

    @Override
    public Connection connect(DBConnectionProperties properties) throws Exception {
        String host = properties.host.getStringValue();
        String port = properties.port.getStringValue();
        String dbname = properties.database.getStringValue();
        String parameters = properties.jdbcparameter.getStringValue();

        String username = properties.userPassword.userId.getStringValue();
        String password = properties.userPassword.password.getStringValue();

        boolean autocommit = properties.autocommit.getBooleanValue();

        StringBuilder url = new StringBuilder();
        url.append("jdbc:oracle:thin:@").append(host).append(":").append(port).append(":").append(dbname);
        if (parameters != null) {
            url.append("?").append(parameters);
        }

        java.lang.Class.forName("oracle.jdbc.OracleDriver");
        
        Connection conn = java.sql.DriverManager.getConnection(url.toString(), username, password);
        conn.setAutoCommit(autocommit);
        
        return conn;
    }

}
