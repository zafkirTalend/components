package org.talend.components.cassandra.input;

import aQute.bnd.annotation.component.Component;
import org.talend.components.api.Constants;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.InputComponentDefinition;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.cassandra.CassandraDefinition;
import org.talend.components.cassandra.runtime.CassandraSource;

@Component(name = Constants.COMPONENT_BEAN_PREFIX
        + TCassandraInputDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class TCassandraInputDefinition extends CassandraDefinition implements InputComponentDefinition {

    public static final String COMPONENT_NAME = "tCassandraInput"; //$NON-NLS-1$

    public TCassandraInputDefinition() {
        super(COMPONENT_NAME);
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
