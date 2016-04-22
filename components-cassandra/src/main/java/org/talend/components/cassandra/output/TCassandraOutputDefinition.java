package org.talend.components.cassandra.output;

import aQute.bnd.annotation.component.Component;
import org.talend.components.api.Constants;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.OutputComponentDefinition;
import org.talend.components.api.component.Trigger;
import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.cassandra.CassandraDefinition;
import org.talend.components.cassandra.runtime.CassandraSink;

@Component(name = Constants.COMPONENT_BEAN_PREFIX
        + TCassandraOutputDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class TCassandraOutputDefinition extends CassandraDefinition implements OutputComponentDefinition {
    public static final String COMPONENT_NAME = "tCassandraOutputNew"; //$NON-NLS-1$

    public TCassandraOutputDefinition() {
        super(COMPONENT_NAME);
        //FIXME(bchen) in DI output component can have output connector, but for spark not, need two definition? and how to work with output connector?
        setConnectors(new Connector(Connector.ConnectorType.FLOW, 1, 0), new Connector(Connector.ConnectorType.MAIN, 0, 1));
        //FIXME(bchen) no need actually, but you have to init trigger list
        setTriggers(new Trigger(Trigger.TriggerType.SUBJOB_OK, 0, 0));
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TCassandraOutputProperties.class;
    }

    @Override
    public Sink getRuntime() {
        return new CassandraSink();
    }
}
