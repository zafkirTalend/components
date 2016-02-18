// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.oracle.toracleinput;

import org.talend.components.oracle.DBInputProperties;
import org.talend.components.oracle.OracleConnectionProperties;

public class TOracleInputProperties extends DBInputProperties {

    public TOracleInputProperties(String name) {
        super(name);
        connection = new OracleConnectionProperties("connection");
    }

}
