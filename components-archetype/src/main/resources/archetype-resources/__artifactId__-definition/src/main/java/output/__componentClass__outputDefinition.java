package ${package}.output;

import java.util.Set;

import ${packageTalend}.api.component.AbstractComponentDefinition;
import ${packageTalend}.api.component.ConnectorTopology;
import ${packageTalend}.api.component.runtime.RuntimeInfo;
import ${packageTalend}.api.properties.ComponentProperties;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.property.Property;

public class ${componentClass}OutputDefinition extends AbstractComponentDefinition {

    public static final String COMPONENT_NAME = "t${componentClass}Output"; //$NON-NLS-1$

    public ${componentClass}OutputDefinition() {
        super(COMPONENT_NAME);
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return ${componentClass}OutputProperties.class;
    }

    public RuntimeInfo getRuntimeInfo(Properties properties, ConnectorTopology connectorTopology) {
        return null;
    }

    public Property[] getReturnProperties() {
        return new Property[0];
    }

    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return null;
    }
}
