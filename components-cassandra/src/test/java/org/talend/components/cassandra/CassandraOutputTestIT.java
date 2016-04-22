package org.talend.components.cassandra;

import org.apache.avro.Schema;
import org.junit.Test;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.test.ComponentTestUtils;
import org.talend.components.cassandra.output.TCassandraOutputDefinition;
import org.talend.components.cassandra.output.TCassandraOutputProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.service.PropertiesServiceTest;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CassandraOutputTestIT extends CassandraTestBase {

    @Test
    public void testPropsAndi18n(){
        ComponentProperties props = getComponentService().getComponentProperties(TCassandraOutputDefinition.COMPONENT_NAME);
        //Basic test, it will check i18n also
        ComponentTestUtils.checkSerialize(props, errorCollector);
        assertThat(props.getForm(Form.MAIN).getName(), is(Form.MAIN));
    }

    @Test
    public void testSchema() throws Throwable {
        TCassandraOutputProperties props = (TCassandraOutputProperties)getComponentService().getComponentProperties(TCassandraOutputDefinition.COMPONENT_NAME);
        initConnectionProps(props);
        assertThat(props.getForms().size(), is(1));
        Form schemaRefForm = props.getSchemaProperties().getForm(Form.REFERENCE);

        PropertiesServiceTest.checkAndBeforeActivate(getComponentService(), schemaRefForm, CassandraSchemaProperties.KEYSPACE, schemaRefForm.getProperties());
        Property keyspace = (Property)schemaRefForm.getWidget(CassandraSchemaProperties.KEYSPACE).getContent();
        assertThat(keyspace.getPossibleValues().size(), is(6));
        List<String> kss = namedThingToString((List<NamedThing>) keyspace.getPossibleValues());
        assertThat(kss, hasItem(KS_NAME));
        keyspace.setValue(KS_NAME);

        PropertiesServiceTest.checkAndBeforeActivate(getComponentService(), schemaRefForm, CassandraSchemaProperties.COLUMN_FAMILY, schemaRefForm.getProperties());
        Property columnFamily = (Property) schemaRefForm.getWidget(CassandraSchemaProperties.COLUMN_FAMILY).getContent();
        assertThat(columnFamily.getPossibleValues().size(), is(3));
        List<String> cfs = namedThingToString((List<NamedThing>) columnFamily.getPossibleValues());
        assertThat(cfs, hasItem("example_src"));
        columnFamily.setValue("example_src");

        PropertiesServiceTest.checkAndAfter(getComponentService(), schemaRefForm, CassandraSchemaProperties.COLUMN_FAMILY, schemaRefForm.getProperties());
        assertThat(props.getSchemas().size(), is(1));
        Schema schema = props.getSchemas().get(0);
        System.out.println(schema);
        assertThat(schema.getFields().size(), is(EmbeddedCassandraExampleDataResource.sExampleSrcColumns.length));
    }

}
