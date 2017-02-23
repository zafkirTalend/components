
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
package org.talend.components.file.fileinputdefinition;

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.ComponentImageType;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.component.runtime.SimpleRuntimeInfo;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.file.runtime.FileInputSource;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

/**
 * The FileInputDefinition acts as an entry point for all of services that a component provides to integrate with the
 * Studio (at design-time) and other components (at run-time).
 */
public class FileInputDefinition extends AbstractComponentDefinition {

    public static final String COMPONENT_NAME = "FileReader"; //$NON-NLS-1$

    public FileInputDefinition() {
        super(COMPONENT_NAME, ExecutionEngine.DI, ExecutionEngine.BEAM);
    }

    @Override
    public String[] getFamilies() {
        return new String[] { "File/Input" }; //$NON-NLS-1$
    }

    @Override
    public Property[] getReturnProperties() {
        return new Property[] {};
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return FileInputProperties.class;
    }

    @Override
    public RuntimeInfo getRuntimeInfo(ExecutionEngine engine, ComponentProperties properties, ConnectorTopology componentType) {
        assertEngineCompatibility(engine);
        assertConnectorTopologyCompatibility(componentType);
        return new SimpleRuntimeInfo(this.getClass().getClassLoader(),
                DependenciesReader.computeDependenciesFilePath("org.talend.components", "file-input-output"),
                FileInputSource.class.getCanonicalName());
    }

    // public static RuntimeInfo getCommonRuntimeInfo(ClassLoader classLoader, Class<? extends SourceOrSink> clazz) {
    // return new SimpleRuntimeInfo(classLoader,
    // DependenciesReader.computeDependenciesFilePath("org.talend.components", "file-input"), clazz.getCanonicalName());
    // }

    @Override
    public String getPngImagePath(ComponentImageType imageType) {
        switch (imageType) {
        case PALLETE_ICON_32X32:
            return "fileReader_icon32.png"; //$NON-NLS-1$
        default:
            return "fileReader_icon32.png"; //$NON-NLS-1$
        }
    }

    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.OUTGOING);
    }

}
