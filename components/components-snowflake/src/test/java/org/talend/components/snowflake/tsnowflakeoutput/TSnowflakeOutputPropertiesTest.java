package org.talend.components.snowflake.tsnowflakeoutput;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.api.test.ComponentTestUtils;
import org.talend.components.snowflake.tsnowflakeoutput.TSnowflakeOutputProperties.OutputAction;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.di.DiSchemaConstants;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.SchemaProperty;

public class TSnowflakeOutputPropertiesTest {

	TSnowflakeOutputProperties outputProperties;
	
	@Rule
    public ErrorCollector errorCollector = new ErrorCollector();
	
	@Before
	public void reset() {
		outputProperties = new TSnowflakeOutputProperties("output");
		outputProperties.init();
	}
	
	@Test
	public void testI18N() {
        ComponentTestUtils.checkAllI18N(outputProperties, errorCollector);
    }
	
	@Test
	public void testVisible() {
		Form main;
		boolean isOutputActionPropertyVisible;
		boolean isUpsertKeyColumnVisible;
		boolean isUpsertKeyColumnVisibleWhenOutputActionIsUpsert;
		
		main = outputProperties.getForm(Form.MAIN);
		isOutputActionPropertyVisible = main.getWidget(outputProperties.outputAction).isVisible();
		isUpsertKeyColumnVisible = main.getWidget(outputProperties.upsertKeyColumn).isVisible();
		
		outputProperties.outputAction.setValue(OutputAction.UPSERT);
		outputProperties.refreshLayout(main);
		
		isUpsertKeyColumnVisibleWhenOutputActionIsUpsert = main.getWidget(outputProperties.upsertKeyColumn).isVisible();
		
		assertTrue(isOutputActionPropertyVisible);
		assertFalse(isUpsertKeyColumnVisible);
		assertTrue(isUpsertKeyColumnVisibleWhenOutputActionIsUpsert);
	}
	
	@Test
	public void testDefaultValue() {
		OutputAction defaultValueOutputAction;
		
		defaultValueOutputAction = outputProperties.outputAction.getValue();
		
		assertEquals(defaultValueOutputAction, OutputAction.INSERT);
	}
	
	@Test
	public void testTriggers() {
		Form main;
		boolean isOutputActionCalledAfter;
		
		main = outputProperties.getForm(Form.MAIN);
		isOutputActionCalledAfter = main.getWidget(outputProperties.outputAction).isCallAfter();
		
		
		assertTrue(isOutputActionCalledAfter);
	}
	
	@Test
	public void testGetAllSchemaPropertiesConnectors() {
		Set<PropertyPathConnector> schemaPropertyForOutputConnection;
		Set<PropertyPathConnector> schemaPropertyForInputConnection;
		
		schemaPropertyForOutputConnection = outputProperties.getAllSchemaPropertiesConnectors(false);
		schemaPropertyForInputConnection = outputProperties.getAllSchemaPropertiesConnectors(true);
		
		
		//BUG THERE??? Method sets MAIN_CONNECTOR instead of FLOW_CONNECTOR
		assertTrue(schemaPropertyForOutputConnection.contains(outputProperties.FLOW_CONNECTOR));  
		assertTrue(schemaPropertyForInputConnection.contains(outputProperties.REJECT_CONNECTOR));
		
		assertTrue(schemaPropertyForInputConnection.size() == 1);
		assertTrue(schemaPropertyForOutputConnection.size() == 1);
		//TODO
	}
	
	@Test
	public void testGetFieldNames() {
		Schema runtimeSchema;
		
	    runtimeSchema = SchemaBuilder.builder().record("Record").fields() //
                .name("logicalTime").type(AvroUtils._logicalTime()).noDefault() //
                .name("logicalDate").type(AvroUtils._logicalDate()).noDefault() //
                .name("logicalTimestamp").type(AvroUtils._logicalTimestamp()).noDefault() //
                .name("id").type().intType().noDefault() //
                .name("name").type().stringType().noDefault() //
                .name("age").type().intType().noDefault() //
                .name("valid").type().booleanType().noDefault() //
                .name("address").type().stringType().noDefault() //
                .name("comment").prop(DiSchemaConstants.TALEND6_COLUMN_LENGTH, "255").type().stringType().noDefault() //
                .name("createdDate").prop(DiSchemaConstants.TALEND6_COLUMN_TALEND_TYPE, "id_Date") //
                .prop(DiSchemaConstants.TALEND6_COLUMN_PATTERN, "yyyy-MM-dd'T'HH:mm:ss'000Z'").type().nullable().longType() //
                .noDefault() //
                .endRecord(); //
	
	    Property schema = new SchemaProperty("schema");
	    schema.setValue(runtimeSchema);
	    
	    List<String> test = outputProperties.getFieldNames(schema);
	    System.out.println(test);
		//TODO make schema, test fields names;
	}
}
