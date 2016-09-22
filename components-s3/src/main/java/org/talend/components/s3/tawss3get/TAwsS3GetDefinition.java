// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.s3.tawss3get;

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.RuntimeInfo;
import org.talend.components.api.component.runtime.SimpleRuntimeInfo;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.s3.AwsS3Definition;
import org.talend.components.s3.runtime.TAwsS3GetComponentDriverRuntime;
import org.talend.daikon.properties.Properties;

/**
 * The TAwsS3GetDefinition acts as an entry point for all of services that a component provides to integrate with the
 * Studio (at design-time) and other components (at run-time).
 */
public class TAwsS3GetDefinition extends AwsS3Definition {

    public static final String COMPONENT_NAME = "tAWSS3Get"; //$NON-NLS-1$

    public TAwsS3GetDefinition() {
        super(COMPONENT_NAME);
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TAwsS3GetProperties.class;
    }

    @Override
    public RuntimeInfo getRuntimeInfo(Properties properties, ConnectorTopology connectorTopology) {
        if (connectorTopology == ConnectorTopology.NONE) {
            return new SimpleRuntimeInfo(this.getClass().getClassLoader(),
                    DependenciesReader.computeDependenciesFilePath("org.talend.components", "components-s3"),
                    TAwsS3GetComponentDriverRuntime.class.getCanonicalName());
        }
        return null;
    }

    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.NONE);
    }

}
