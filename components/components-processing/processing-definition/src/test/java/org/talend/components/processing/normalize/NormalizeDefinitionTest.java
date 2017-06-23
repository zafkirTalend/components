package org.talend.components.processing.normalize;

import org.junit.Ignore;
import org.junit.Test;
import org.talend.components.api.component.ComponentImageType;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.processing.ProcessingFamilyDefinition;
import org.talend.daikon.runtime.RuntimeInfo;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class NormalizeDefinitionTest {

    private final NormalizeDefinition definition = new NormalizeDefinition();

    /**
     * Check {@link NormalizeDefinition#getFamilies()} returns string array, which contains "Processing"
     */
    @Test
    public void testGetFamilies() {
        String[] families = definition.getFamilies();
        assertThat(families, arrayContaining(ProcessingFamilyDefinition.NAME));
    }

    /**
     * Check {@link NormalizeDefinition#getName()} returns "Normalize"
     */
    @Test
    public void testGetName() {
        String componentName = definition.getName();
        assertEquals(componentName, "Normalize");
    }

    /**
     * Check {@link NormalizeDefinition#getPropertyClass()} returns class, which canonical name is
     * "org.talend.components.processing.normalize.NormalizeProperties"
     */
    @Test
    public void testGetPropertyClass() {
        Class<?> propertyClass = definition.getPropertyClass();
        String canonicalName = propertyClass.getCanonicalName();
        assertThat(canonicalName, equalTo("org.talend.components.processing.normalize.NormalizeProperties"));
    }

    @Test
    @Ignore
    public void testGetPngImagePath() {
        assertEquals("Normalize_icon32.png", definition.getPngImagePath(ComponentImageType.PALLETE_ICON_32X32));
    }

    @Test(expected = org.talend.daikon.exception.TalendRuntimeException.class)
    public void testGetRuntimeInfoIncoming() {
        assertNull(definition.getRuntimeInfo(ExecutionEngine.BEAM, null, ConnectorTopology.INCOMING));
    }

    @Test(expected = org.talend.daikon.exception.TalendRuntimeException.class)
    public void testGetRuntimeInfoOutgoing() {
        assertNull(definition.getRuntimeInfo(ExecutionEngine.BEAM, null, ConnectorTopology.OUTGOING));
    }

    @Test(expected = org.talend.daikon.exception.TalendRuntimeException.class)
    public void testGetRuntimeInfoNone() {
        assertNull(definition.getRuntimeInfo(ExecutionEngine.BEAM, null, ConnectorTopology.NONE));
    }

    @Test
    public void testGetRuntimeInfo() {
        RuntimeInfo runtimeInfo = definition.getRuntimeInfo(ExecutionEngine.BEAM, null, ConnectorTopology.INCOMING_AND_OUTGOING);
        assertEquals("org.talend.components.processing.runtime.normalize.NormalizeRuntime", runtimeInfo.getRuntimeClassName());
    }

    @Test
    public void testGetSupportedConnectorTopologies() {
        Set<ConnectorTopology> connector = definition.getSupportedConnectorTopologies();
        assertEquals(1, connector.size());
        assertTrue(connector.contains(ConnectorTopology.INCOMING_AND_OUTGOING));
    }

    /**
     * Check {@link NormalizeDefinition#isSchemaAutoPropagate()} returns <code>false</code>
     */
    @Test
    public void testIsSchemaAutoPropagate() {
        boolean result = definition.isSchemaAutoPropagate();
        assertTrue(result);
    }
}
