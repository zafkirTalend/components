package org.talend.components.cassandra.runtime;

import org.apache.avro.SchemaBuilder;
import org.junit.Test;
import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.cassandra.CassandraIOBasedProperties;
import org.talend.components.cassandra.CassandraSchemaProperties;
import org.talend.components.cassandra.CassandraTestBase;
import org.talend.components.cassandra.input.TCassandraInputDefinition;
import org.talend.components.cassandra.input.TCassandraInputProperties;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.service.PropertiesServiceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CassandraReaderTestIT extends CassandraTestBase {

    @Test
    public void testRead() throws Throwable {
        TCassandraInputProperties props = (TCassandraInputProperties)getComponentService().getComponentProperties(TCassandraInputDefinition.COMPONENT_NAME);
        setupSchemaProps(props, false);
        setupQueryProps(props);
        CassandraSource cassandraSource = new CassandraSource();
        cassandraSource.initialize(null, props);
        cassandraSource.validate(null);
        BoundedReader reader = cassandraSource.createReader(null);
        assertTrue(reader.start());
        assertNotNull(reader.getCurrent());
        assertTrue(reader.advance());
        reader.close();
    }

    @Test
    public void testReadWithEmptySchema() throws Throwable {
    }

    private void setupQueryProps(TCassandraInputProperties props) {
        props.query.setValue("select * from " + props.schemaProperties.columnFamily.getStringValue());
    }

    public void setupSchemaProps(CassandraIOBasedProperties props, boolean includeAllFields) throws Throwable {
        initConnectionProps(props);
        props.getSchemaProperties().keyspace.setValue(KS_NAME);
        props.getSchemaProperties().columnFamily.setValue("example_src");
        if(includeAllFields){
            props.getSchemaProperties().schema.schema.setValue(SchemaBuilder.builder().record("test")
                    .prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true").fields().endRecord());
        }else {
            Form schemaRefForm = props.getSchemaProperties().getForm(Form.REFERENCE);
            PropertiesServiceTest.checkAndAfter(getComponentService(), schemaRefForm, CassandraSchemaProperties.COLUMN_FAMILY, schemaRefForm.getProperties());
        }
    }
}
