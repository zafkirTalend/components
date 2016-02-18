package org.talend.components.oracle.runtime;

import java.sql.Connection;

import org.talend.components.oracle.DBConnectionProperties;

interface DBTemplate {

    Connection connect(DBConnectionProperties properties) throws Exception;

}
