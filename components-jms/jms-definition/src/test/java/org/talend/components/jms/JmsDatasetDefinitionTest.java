// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import org.junit.Test;

import org.talend.components.api.component.runtime.RuntimeInfo;

import static org.junit.Assert.assertEquals;

public class JmsDatasetDefinitionTest {
    private final JmsDatasetDefinition datasetDefinition = new JmsDatasetDefinition();

    /**
     * Check {@link JmsDatasetDefinition#getRuntimeInfo(JmsDatasetProperties properties, Object ctx)} returns RuntimeInfo,
     * which runtime class name is "org.talend.components.jms.runtime_1_1.DatasetRuntime"
     */
    @Test
    public void testGetRuntimeInfo(){
        RuntimeInfo runtimeInfo = datasetDefinition.getRuntimeInfo(null, null);
        assertEquals("org.talend.components.jms.runtime_1_1.DatasetRuntime", runtimeInfo.getRuntimeClassName());
    }

    /**
     * Check {@link JmsDatasetDefinition#createProperties()} returns JmsDatasetProperties, which canonical name is
     * "jms"
     */
    @Test
    public void testCreateProperties(){
        JmsDatasetProperties props = datasetDefinition.createProperties();
        assertEquals("jms", props.getName());
    }
}
