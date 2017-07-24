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

package org.talend.components.simplefileio;

import java.net.MalformedURLException;
import java.net.URL;

import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.JarRuntimeInfo;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.common.dataset.DatasetProperties;
import org.talend.components.common.datastore.DatastoreDefinition;
import org.talend.components.simplefileio.input.SimpleFileIOInputDefinition;
import org.talend.components.simplefileio.output.SimpleFileIOOutputDefinition;
import org.talend.daikon.definition.DefinitionImageType;
import org.talend.daikon.definition.I18nDefinition;
import org.talend.daikon.runtime.RuntimeInfo;

public class SimpleFileIODatastoreDefinition extends I18nDefinition
        implements DatastoreDefinition<SimpleFileIODatastoreProperties> {

    public static final String RUNTIME = "org.talend.components.simplefileio.runtime.SimpleFileIODatastoreRuntime";

    public static final String NAME = SimpleFileIOComponentFamilyDefinition.NAME + "Datastore";

    /** TODO: See {@link org.talend.components.simplefileio.SimpleFileIODatasetDefinition}. */
    public static final boolean IS_CLASSLOADER_REUSABLE = SimpleFileIODatasetDefinition.IS_CLASSLOADER_REUSABLE;

    public SimpleFileIODatastoreDefinition() {
        super(NAME);
    }

    @Override
    public Class<SimpleFileIODatastoreProperties> getPropertiesClass() {
        return SimpleFileIODatastoreProperties.class;
    }

    @Deprecated
    @Override
    public String getImagePath() {
        return null;
    }

    @Override
    public String getImagePath(DefinitionImageType type) {
        return null;
    }

    @Override
    public String getIconKey() {
        return "file-hdfs-o";
    }

    @Override
    public DatasetProperties createDatasetProperties(SimpleFileIODatastoreProperties storeProp) {
        SimpleFileIODatasetProperties setProp = new SimpleFileIODatasetProperties(SimpleFileIODatasetDefinition.NAME);
        setProp.init();
        setProp.setDatastoreProperties(storeProp);
        return setProp;
    }

    @Override
    public String getInputCompDefinitionName() {
        return SimpleFileIOInputDefinition.NAME;
    }

    @Override
    public String getOutputCompDefinitionName() {
        return SimpleFileIOOutputDefinition.NAME;
    }

    @Override
    public RuntimeInfo getRuntimeInfo(SimpleFileIODatastoreProperties properties) {
        try {
            return new JarRuntimeInfo(new URL(SimpleFileIOComponentFamilyDefinition.MAVEN_DEFAULT_RUNTIME_URI),
                    DependenciesReader.computeDependenciesFilePath(SimpleFileIOComponentFamilyDefinition.MAVEN_GROUP_ID,
                            SimpleFileIOComponentFamilyDefinition.MAVEN_DEFAULT_RUNTIME_ARTIFACT_ID),
                    RUNTIME, IS_CLASSLOADER_REUSABLE);
        } catch (MalformedURLException e) {
            throw new ComponentException(e);
        }
    }
}
