package org.talend.components.cassandra.connection;

import aQute.bnd.annotation.component.Component;
import org.talend.components.api.Constants;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.EndpointComponentDefinition;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.cassandra.CassandraConnectionProperties;
import org.talend.components.cassandra.CassandraDefinition;
import org.talend.components.cassandra.runtime.CassandraSourceOrSink;

@Component(name = Constants.COMPONENT_BEAN_PREFIX
        + TCassandraConnectionDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class TCassandraConnectionDefinition extends CassandraDefinition implements EndpointComponentDefinition {

    public static final String COMPONENT_NAME = "tCassandraConnection"; //$NON-NLS-1$

    public TCassandraConnectionDefinition() {
        super(COMPONENT_NAME);
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return CassandraConnectionProperties.class;
    }

    @Override
    public SourceOrSink getRuntime() {
        return new CassandraSourceOrSink();
    }
}
