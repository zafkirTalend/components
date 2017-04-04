package org.talend.components.snowflake.tsnowflakeinput;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.util.EnumSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.snowflake.SnowflakeConnectionProperties;
import org.talend.components.snowflake.SnowflakeTableProperties;
import org.talend.daikon.runtime.RuntimeInfo;

public class TSnowflakeInputDefinitionTest {

	TSnowflakeInputDefinition inputDefinition;
	
	@Before
	public void reset() {
		inputDefinition = new TSnowflakeInputDefinition();

    }
	
	@Test
	public void testIsStartable() {
		boolean isStartable;
		
		isStartable = inputDefinition.isStartable();
		
		assertTrue(isStartable);
	}
	
	@Test
	public void testGetPropertyClass() {
		Class<?> propertyClass;
		
		propertyClass = inputDefinition.getPropertiesClass();
		
		assertTrue(propertyClass.equals(TSnowflakeInputProperties.class));
	}
	
	@Test
	public void testGetNestedCompatibleComponentPropertiesClass() {
		Class<? extends ComponentProperties>[] componentPropertiesClasses;
		
		componentPropertiesClasses = inputDefinition.getNestedCompatibleComponentPropertiesClass();
		
		assertTrue(componentPropertiesClasses.length == 2);
		assertArrayEquals(componentPropertiesClasses, new Class[] {SnowflakeConnectionProperties.class, SnowflakeTableProperties.class});
		
	}
	
	 @Test
	 public void getRuntimeInfo() throws Exception {
		 RuntimeInfo runtimeInfo;
		 
		 runtimeInfo = inputDefinition.getRuntimeInfo(ExecutionEngine.DI, null, ConnectorTopology.OUTGOING);
		 
		 assertNotNull(runtimeInfo);
	 }
	 
	 @Test
	 public void testGetSupportedConnectorTopologies() {
		 Set<ConnectorTopology> supportedConnectorTopologies;
		 Set <ConnectorTopology> requiredConnectorTopologies;
		 
		 requiredConnectorTopologies = EnumSet.of(ConnectorTopology.OUTGOING);
		 supportedConnectorTopologies = inputDefinition.getSupportedConnectorTopologies();
		 
		 assertEquals(requiredConnectorTopologies, supportedConnectorTopologies);
	 }
}
