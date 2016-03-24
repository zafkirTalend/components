package org.talend.components.cassandra.tcassandraconnection;

import aQute.bnd.annotation.component.Component;
import org.talend.components.api.Constants;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.Trigger;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.cassandra.CassandraDefinition;

@Component(name = Constants.COMPONENT_BEAN_PREFIX
        + TCassandraConnectionDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class TCassandraConnectionDefinition extends CassandraDefinition {

    public static final String COMPONENT_NAME = "tSalesforceConnectionNew"; //$NON-NLS-1$

    public TCassandraConnectionDefinition() {
        super(COMPONENT_NAME);
        setConnectors(new Connector(Connector.ConnectorType.FLOW, 0, 0));
        setTriggers(new Trigger(Trigger.TriggerType.ITERATE, 1, 0), new Trigger(Trigger.TriggerType.SUBJOB_OK, 1, 1),
                new Trigger(Trigger.TriggerType.SUBJOB_ERROR, 1, 1));
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TCassandraConnectionProperties.class;
    }
}
