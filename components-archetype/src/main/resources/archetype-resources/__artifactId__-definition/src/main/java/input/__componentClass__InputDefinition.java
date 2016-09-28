package ${package}.input;

import java.util.Set;

import ${packageTalend}.api.component.AbstractComponentDefinition;
import ${packageTalend}.api.component.ConnectorTopology;
import ${packageTalend}.api.component.runtime.RuntimeInfo;
import ${packageTalend}.api.properties.ComponentProperties;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.property.Property;

public class ${componentClass}InputDefinition extends AbstractComponentDefinition {

    public static final String COMPONENT_NAME = "t${componentClass}Input"; //$NON-NLS-1$

    public ${componentClass}InputDefinition() {
        super(COMPONENT_NAME);
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return ${componentClass}InputProperties.class;
    }

    public RuntimeInfo getRuntimeInfo(Properties properties, ConnectorTopology connectorTopology) {
        ${componentClass}InputProperties prop = (${componentClass}InputProperties) properties;
        // Depending on the version, return a RuntimeInfo corresponding to ${componentClass}Source.
        return null;
    }

    public Property[] getReturnProperties() {
        return new Property[0];
    }

    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return null;
    }
}