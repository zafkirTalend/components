package org.talend.components.cassandra;

import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.ConnectionPropertiesProvider;
import org.talend.components.api.service.AbstractComponentTest;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.test.SimpleComponentRegistry;
import org.talend.components.api.test.SimpleComponentService;
import org.talend.components.cassandra.connection.TCassandraConnectionDefinition;
import org.talend.components.cassandra.input.TCassandraInputDefinition;
import org.talend.components.cassandra.output.TCassandraOutputDefinition;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.presentation.Form;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class CassandraTestBase extends AbstractComponentTest {

    public static final String KS_NAME = CassandraTestBase.class.getSimpleName().toLowerCase();
    // start the embedded cassandra server and init with data
    @Rule
    public EmbeddedCassandraExampleDataResource mCass = new EmbeddedCassandraExampleDataResource(KS_NAME);

    protected static void initConnectionProps(ConnectionPropertiesProvider<CassandraConnectionProperties> props){
        props.getConnectionProperties().host.setValue(EmbeddedCassandraResource.HOST);
        props.getConnectionProperties().port.setValue(EmbeddedCassandraResource.PORT);
    }

    //used by ComponentTestUtils
    //FIXME(bchen) can be common
    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    private ComponentService componentService;
    // registry components you want to test
    @Override
    public ComponentService getComponentService() {
        if (componentService == null) {
            SimpleComponentRegistry simpleComponentRegistry = new SimpleComponentRegistry();
            simpleComponentRegistry.addComponent(TCassandraConnectionDefinition.COMPONENT_NAME, new TCassandraConnectionDefinition());
            simpleComponentRegistry.addComponent(TCassandraInputDefinition.COMPONENT_NAME, new TCassandraInputDefinition());
            simpleComponentRegistry.addComponent(TCassandraOutputDefinition.COMPONENT_NAME, new TCassandraOutputDefinition());
            componentService = new SimpleComponentService(simpleComponentRegistry);
        }
        return componentService;
    }

    //FIXME(bchen) can be common, used for salesforce also
    protected ComponentProperties checkAndAfter(Form form, String propName, ComponentProperties props) throws Throwable {
        assertTrue(form.getWidget(propName).isCallAfter());
        return getComponentService().afterProperty(propName, props);
    }

    /**
     * equals method of NamedThing is not compare the content
     */
    protected List<String> namedThingToString(List<NamedThing> names) {
        List<String> strs = new ArrayList<>();
        for (NamedThing keyspaceName : names) {
            strs.add(keyspaceName.getName());
        }
        return strs;
    }
}
