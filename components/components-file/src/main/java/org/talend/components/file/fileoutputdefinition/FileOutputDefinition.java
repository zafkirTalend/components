
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
package org.talend.components.file.fileoutputdefinition;

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.ComponentImageType;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.component.runtime.SimpleRuntimeInfo;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.file.runtime.FileSink;
import org.talend.daikon.definition.DefinitionImageType;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

/**
 * The FileInputDefinition acts as an entry point for all of services that a component provides to integrate with the
 * Studio (at design-time) and other components (at run-time).
 */
public class FileOutputDefinition extends AbstractComponentDefinition {

    public static final String COMPONENT_NAME = "SimpleFileOutputWriter"; //$NON-NLS-1$

    public FileOutputDefinition() {
        super(COMPONENT_NAME, ExecutionEngine.DI, ExecutionEngine.BEAM);
    }

    @Override
    public String[] getFamilies() {
        return new String[] { "File/Output" }; //$NON-NLS-1$
    }

    @Override
    public Property[] getReturnProperties() {
        return new Property[] {};
    }

    @Override
    public String getPngImagePath(ComponentImageType imageType) {
        switch (imageType) {
        case PALLETE_ICON_32X32:
            return "fileWriter_icon32.png"; //$NON-NLS-1$
        default:
            return "fileWriter_icon32.png"; //$NON-NLS-1$
        }
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return FileOutputProperties.class;
    }

    @Override
    public RuntimeInfo getRuntimeInfo(ExecutionEngine engine, ComponentProperties properties, ConnectorTopology componentType) {
        assertEngineCompatibility(engine);
        if (componentType == ConnectorTopology.INCOMING || componentType == ConnectorTopology.INCOMING_AND_OUTGOING) {
            return new SimpleRuntimeInfo(this.getClass().getClassLoader(),
                    DependenciesReader.computeDependenciesFilePath("org.talend.components", "file-input-output"),
                    FileSink.class.getName());
        } else {
            return null;
        }
    }

    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.INCOMING, ConnectorTopology.INCOMING_AND_OUTGOING);
    }

    @Override
    public String getImagePath(DefinitionImageType type) {
        return type.name();
    }

    @Override
    public String getIconKey() {
        return null;
    }

}
