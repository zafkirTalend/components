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
package org.talend.components.file;

import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.JarRuntimeInfo;
import org.talend.daikon.runtime.RuntimeInfo;

/**
 * Service class, which provides {@link RuntimeInfo} instances
 */
public final class RuntimeInfoProvider {

    public static final String MAVEN_GROUP_ID = "org.talend.components";

    public static final String MAVEN_RUNTIME_ARTIFACT_ID = "components-file-runtime";

    public static final String MAVEN_RUNTIME_URI = "mvn:" + MAVEN_GROUP_ID + "/" + MAVEN_RUNTIME_ARTIFACT_ID;

    public static final String INPUT_RUNTIME_CLASS_NAME = "org.talend.components.file.runtime.reader.FileInputSource"; //$NON-NLS-1$

    public static final String OUTPUT_RUNTIME_CLASS_NAME = "org.talend.components.file.runtime.writer.FileOutputSink";

    private RuntimeInfoProvider() {
        // Class provides static utility methods and shouldn't be instantiated
    }

    /**
     * Returns instance of {@link RuntimeInfo} for File component with runtime class name FileInputSource
     * or FileSink
     *
     * @return {@link RuntimeInfo} for File component
     */
    public static RuntimeInfo provideRuntimeInfo(ConnectorTopology connector) {
        if (connector == ConnectorTopology.OUTGOING) {
            return new JarRuntimeInfo(MAVEN_RUNTIME_URI,
                    DependenciesReader.computeDependenciesFilePath(MAVEN_GROUP_ID, MAVEN_RUNTIME_ARTIFACT_ID),
                    INPUT_RUNTIME_CLASS_NAME);
        }
        return null;
    }

    public static RuntimeInfo provideOutputRuntimeInfo(ConnectorTopology connector) {
        if (connector == ConnectorTopology.INCOMING) {
        return new JarRuntimeInfo(MAVEN_RUNTIME_URI,
                    DependenciesReader.computeDependenciesFilePath(MAVEN_GROUP_ID, MAVEN_RUNTIME_ARTIFACT_ID),
                    OUTPUT_RUNTIME_CLASS_NAME);
        }
        return null;
    }
}
