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
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.runtime.RuntimeInfo;

public class CompositeRuntimeInfo implements RuntimeInfo {

    private RuntimeInfo[] infos;

    public CompositeRuntimeInfo(RuntimeInfo... infos) {
        this.infos = infos;
    }

    @Override
    public List<URL> getMavenUrlDependencies() {
        ComponentException lastException = null;
        for (RuntimeInfo ri : infos) {
            try {
                return ri.getMavenUrlDependencies();
            } catch (TalendRuntimeException e) {
                lastException = null;
            }
        }
        throw lastException;
    }

    @Override
    public String getRuntimeClassName() {
        ComponentException lastException = null;
        for (RuntimeInfo ri : infos) {
            try {
                return ri.getRuntimeClassName();
            } catch (TalendRuntimeException e) {
                lastException = null;
            }
        }
        throw lastException;
    }

    public static CompositeRuntimeInfo of(String mvnUrl, String mvnGroupId, String mvnArtifactId, String runtimeClassName) {
        try {
            String filePath = DependenciesReader.computeDependenciesFilePath(mvnGroupId, mvnArtifactId);
            return new CompositeRuntimeInfo(
                    new SimpleRuntimeInfo(ClassLoader.getSystemClassLoader(), filePath, runtimeClassName), new JarRuntimeInfo(
                            new URL(mvnUrl), filePath, runtimeClassName));
        } catch (MalformedURLException e) {
            throw new ComponentException(e);
        }

    }

}
