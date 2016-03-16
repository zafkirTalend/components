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

import java.io.InputStream;

import org.talend.components.api.Constants;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.InputComponentDefinition;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.oracle.DBInputDefinition;
import org.talend.components.oracle.OracleConnectionProperties;
import org.talend.components.oracle.runtime.OracleSource;

import aQute.bnd.annotation.component.Component;

/**
 * Component that can connect to a oracle system and get some data out of it.
 */

@Component(name = Constants.COMPONENT_BEAN_PREFIX + TOracleInputDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class TOracleInputDefinition extends DBInputDefinition implements InputComponentDefinition {

    public static final String COMPONENT_NAME = "tOracleInputNew";

    @Override
    protected String getComponentName() {
        return COMPONENT_NAME;
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TOracleInputProperties.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ComponentProperties>[] getNestedCompatibleComponentPropertiesClass() {
        return new Class[] { OracleConnectionProperties.class, TOracleInputProperties.class };
    }

    @Override
    public Source getRuntime() {
        return new OracleSource();
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
