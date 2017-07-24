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
package org.talend.components.processing.definition.window;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.component.runtime.JarRuntimeInfo;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.processing.definition.ProcessingFamilyDefinition;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

public class WindowDefinition extends AbstractComponentDefinition {

    public static final String NAME = "Window";

    public WindowDefinition() {
        super(NAME, ExecutionEngine.BEAM);
    }

    @Override
    public Class<WindowProperties> getPropertyClass() {
        return WindowProperties.class;
    }

    @Override
    public String[] getFamilies() {
        return new String[] { ProcessingFamilyDefinition.NAME };
    }

    public Property[] getReturnProperties() {
        return new Property[] {};
    }

    @Override
    public String getIconKey() {
        return "window";
    }

    @Override
    public RuntimeInfo getRuntimeInfo(ExecutionEngine engine, ComponentProperties properties,
            ConnectorTopology connectorTopology) {
        assertEngineCompatibility(engine);
        assertConnectorTopologyCompatibility(connectorTopology);
        try {
            return new JarRuntimeInfo(new URL("mvn:org.talend.components/processing-runtime"),
                    DependenciesReader.computeDependenciesFilePath(ProcessingFamilyDefinition.MAVEN_GROUP_ID,
                            ProcessingFamilyDefinition.MAVEN_ARTIFACT_ID),
                    "org.talend.components.processing.runtime.WindowRuntime");
        } catch (MalformedURLException e) {
            throw new ComponentException(e);
        }
    }

    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.INCOMING_AND_OUTGOING);
    }
}
