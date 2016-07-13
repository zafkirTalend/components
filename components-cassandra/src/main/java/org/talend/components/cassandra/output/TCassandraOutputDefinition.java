package org.talend.components.cassandra.output;

import aQute.bnd.annotation.component.Component;
import org.talend.components.api.Constants;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.OutputComponentDefinition;
import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.cassandra.CassandraDefinition;
import org.talend.components.cassandra.runtime.CassandraSink;
import org.talend.daikon.properties.property.Property;

@Component(name = Constants.COMPONENT_BEAN_PREFIX
        + TCassandraOutputDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class TCassandraOutputDefinition extends CassandraDefinition implements OutputComponentDefinition {
    public static final String COMPONENT_NAME = "tCassandraOutput"; //$NON-NLS-1$

    public TCassandraOutputDefinition() {
        super(COMPONENT_NAME);
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
