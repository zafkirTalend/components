// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.file.tfileoutput;

import java.util.Set;

import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.file.RuntimeInfoProvider;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

public class FileOutputDefinition extends AbstractComponentDefinition {

    public static final String COMPONENT_NAME = "FileOutput";

    public FileOutputDefinition() {
        super(COMPONENT_NAME, ExecutionEngine.DI);
    }

    @Override
    public String[] getFamilies() {
        return new String[] { "File/Output" };
    }

    @Override
    public Property[] getReturnProperties() {
        return new Property[] { RETURN_TOTAL_RECORD_COUNT_PROP, RETURN_ERROR_MESSAGE_PROP };
    }

    @Override
    public RuntimeInfo getRuntimeInfo(ExecutionEngine engine, ComponentProperties properties,
            ConnectorTopology connectorTopology) {
        assertEngineCompatibility(engine);
        assertConnectorTopologyCompatibility(connectorTopology);
        return RuntimeInfoProvider.provideRuntimeInfo(connectorTopology);
    }

    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return ConnectorTopology.INCOMING_ONLY;
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return FileOutputProperties.class;
    }

}
