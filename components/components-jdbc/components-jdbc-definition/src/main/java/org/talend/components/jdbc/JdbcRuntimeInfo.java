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
package org.talend.components.jdbc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.JarRuntimeInfo;
import org.talend.components.api.exception.ComponentException;
import org.talend.daikon.exception.error.CommonErrorCodes;

public class JdbcRuntimeInfo extends JarRuntimeInfo {

    /**
     * 
     */
    private final RuntimeSettingProvider props;

    /**
     * @param runtimeClassName
     * @param props
     */
    public JdbcRuntimeInfo(RuntimeSettingProvider props, String runtimeClassName) {
        // add the version to fix the issue, not good, could we avoid it?
        super(JDBCFamilyDefinition.getDIRuntimeMavenURI(), DependenciesReader.computeDependenciesFilePath(
                JDBCFamilyDefinition.getDIRuntimeGroupId(), JDBCFamilyDefinition.getDIRuntimeArtifactId()), runtimeClassName);
        this.props = props;
    }

    public JdbcRuntimeInfo(RuntimeSettingProvider props, String jarUrlString, String depTxtPath, String runtimeClassName) {
        super(jarUrlString, depTxtPath, runtimeClassName);
        this.props = props;
    }

    public JarRuntimeInfo cloneWithNewJarUrlString(String newJarUrlString) {
        return new JdbcRuntimeInfo(this.props, newJarUrlString, this.getDepTxtPath(), this.getRuntimeClassName());
    }

    @Override
    public List<URL> getMavenUrlDependencies() {
        List<URL> result = new ArrayList<>(super.getMavenUrlDependencies());
        // add user specific drivers to the dependency list
        try {
            if (this.props != null) {
                List<String> drivers = this.props.getRuntimeSetting().getDriverPaths();
                if (drivers != null) {
                    for (String driver : drivers) {
                        result.add(new URL(removeQuote(driver)));
                    }
                }
            }

        } catch (MalformedURLException e) {
            throw new ComponentException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }

        return result;
    }

    // @TODO this should not exists in TCOMP, this is Studio related.
    String removeQuote(String content) {
        if (content.startsWith("\"") && content.endsWith("\"")) {
            return content.substring(1, content.length() - 1);
        }

        return content;
    }

}
