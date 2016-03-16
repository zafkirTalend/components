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
package org.talend.components.oracle.toracleconnection;

import java.io.InputStream;

import org.talend.components.api.Constants;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.oracle.DBConnectionDefinition;
import org.talend.components.oracle.OracleConnectionProperties;

import aQute.bnd.annotation.component.Component;

@Component(name = Constants.COMPONENT_BEAN_PREFIX + TOracleConnectionDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class TOracleConnectionDefinition extends DBConnectionDefinition {

    public static final String COMPONENT_NAME = "tOracleConnectionNew";

    @Override
    protected String getComponentName() {
        return COMPONENT_NAME;
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return OracleConnectionProperties.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ComponentProperties>[] getNestedCompatibleComponentPropertiesClass() {
        return new Class[] { OracleConnectionProperties.class };
    }

    @Override
    public String[] getFamilies() {
        return new String[] { "Databases/Oracle" };
    }

    @Override
    public String getMavenGroupId() {
        return "org.talend.components";
    }

    @Override
    public String getMavenArtifactId() {
        return "components-oracle";
    }

}
