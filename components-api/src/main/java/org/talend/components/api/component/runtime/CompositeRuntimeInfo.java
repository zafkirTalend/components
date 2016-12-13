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
package org.talend.components.api.component.runtime;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.talend.components.api.exception.ComponentException;
import org.talend.components.api.exception.error.ComponentsApiErrorCode;
import org.talend.daikon.runtime.RuntimeInfo;

/**
 * Implements a {@link RuntimeInfo} that delegates to several other runtime infos.
 *
 * The methods are invoked sequentially on the list and the first one that responds without an exception is used.
 */
public class CompositeRuntimeInfo implements RuntimeInfo {

    private final RuntimeInfo[] delegates;

    /** @param delegates The list of delegates, in the order they should be attempted. */
    public CompositeRuntimeInfo(RuntimeInfo... delegates) {
        this.delegates = delegates;
    }

    @Override
    public List<URL> getMavenUrlDependencies() {
        ComponentException lastException = null;
        for (RuntimeInfo ri : delegates) {
            try {
                return ri.getMavenUrlDependencies();
            } catch (ComponentException e) {
                if (e.getCode() != ComponentsApiErrorCode.COMPUTE_DEPENDENCIES_FAILED)
                    throw e;
                lastException = e;
            }
        }
        throw lastException;
    }

    @Override
    public String getRuntimeClassName() {
        ComponentException lastException = null;
        for (RuntimeInfo ri : delegates) {
            try {
                return ri.getRuntimeClassName();
            } catch (ComponentException e) {
                if (e.getCode() != ComponentsApiErrorCode.COMPUTE_DEPENDENCIES_FAILED)
                    throw e;
                lastException = e;
            }
        }
        throw lastException;
    }

    /**
     * Create a CompositeRuntimeInfo of a {@link SimpleRuntimeInfo} using the system ClassLoader, followed by a
     * {@link JarRuntimeInfo} using the same runtime class that fetches the dependencies from a maven repository.
     *
     * @param mvnGroupId The maven group id if fetching the artifact from maven, also used to compute the dependency
     * file path when loading from the system ClassLoader.
     * @param mvnGroupId The maven artifact id if fetching the artifact from maven, also used to compute the dependency
     * file path when loading from the system ClassLoader.
     * @param runtimeClassName The runtime class name to use in all cases.
     * @return A CompositeRuntimeInfo that looks in the system classpath first, before attempting to download from the
     * maven repository.
     */
    public static RuntimeInfo of(String mvnGroupId, String mvnArtifactId, String runtimeClassName) {
        String filePath = DependenciesReader.computeDependenciesFilePath(mvnGroupId, mvnArtifactId);
        try {
            return new CompositeRuntimeInfo(
                    new SimpleRuntimeInfo(ClassLoader.getSystemClassLoader(), filePath, runtimeClassName), //
                    new JarRuntimeInfo(new URL("mvn:" + mvnGroupId + "/" + mvnArtifactId), filePath, runtimeClassName));
        } catch (MalformedURLException e) {
            throw new ComponentException(e);
        }
    }

}
