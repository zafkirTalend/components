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

package org.talend.components.jms;

import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.RuntimeInfo;
import org.talend.components.api.component.runtime.SimpleRuntimeInfo;
import org.talend.components.common.datastore.DatastoreDefinition;
import org.talend.daikon.SimpleNamedThing;

public class JmsDatastoreDefinition extends SimpleNamedThing implements DatastoreDefinition<JmsDatastoreProperties> {

    public static final String RUNTIME_1_1 = "org.talend.components.jms.runtime_1_1.DatastoreRuntime";

    @Override
    public JmsDatastoreProperties createProperties() {
        return new JmsDatastoreProperties(JmsComponentFamilyDefinition.NAME);
    }

    @Override
    public RuntimeInfo getRuntimeInfo(JmsDatastoreProperties properties, Object ctx) {
        return new SimpleRuntimeInfo(this.getClass().getClassLoader(),
                    DependenciesReader.computeDependenciesFilePath("org.talend.components", "components-jms/jms-runtime_1_1"),
                    RUNTIME_1_1 );
    }
}
