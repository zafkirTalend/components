package org.talend.components.cassandra.input;

import aQute.bnd.annotation.component.Component;
import org.talend.components.api.Constants;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.InputComponentDefinition;
import org.talend.components.api.component.Trigger;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.cassandra.CassandraDefinition;
import org.talend.components.cassandra.runtime.CassandraSource;

@Component(name = Constants.COMPONENT_BEAN_PREFIX
        + TCassandraInputDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class TCassandraInputDefinition extends CassandraDefinition implements InputComponentDefinition {

    public static final String COMPONENT_NAME = "tCassandraInputNew"; //$NON-NLS-1$

    public TCassandraInputDefinition() {
        super(COMPONENT_NAME);

        //FIXME what's the default value of these connector max/min for in/out and if we don't define what's the value, and how to unlimited
        setTriggers(new Trigger(Trigger.TriggerType.ITERATE, 1, 0), new Trigger(Trigger.TriggerType.SUBJOB_OK, 1, 1),
            new Trigger(Trigger.TriggerType.SUBJOB_ERROR, 1, 1));
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TCassandraInputProperties.class;
    }

    @Override
    public Source getRuntime() {
        return new CassandraSource();
    }
}
