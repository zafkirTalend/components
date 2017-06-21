package org.talend.components.processing.normalize;

import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.component.runtime.JarRuntimeInfo;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.api.exception.error.ComponentsErrorCode;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.processing.ProcessingFamilyDefinition;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumSet;
import java.util.Set;

/**
 * Created by zafkir on 19/06/2017.
 */
public class NormalizeDefinition extends AbstractComponentDefinition {

    public static final String COMPONENT_NAME = "Normalize";

    public NormalizeDefinition() {
        super(COMPONENT_NAME, ExecutionEngine.BEAM);
    }

    @Override
    public Class<NormalizeProperties> getPropertyClass() {
        return NormalizeProperties.class;
    }

    @Override
    public String[] getFamilies() {
        return new String[] { ProcessingFamilyDefinition.NAME };
    }

    public Property[] getReturnProperties() {
        return new Property[] {};
    }

    @Override
    public String getIconKey() {
        return "filter-row";
    }

    @Override
    public RuntimeInfo getRuntimeInfo(ExecutionEngine engine, ComponentProperties properties, ConnectorTopology connectorTopology) {
        assertEngineCompatibility(engine);
        if (ConnectorTopology.INCOMING_AND_OUTGOING.equals(connectorTopology)) {
            try {
                return new JarRuntimeInfo(new URL("mvn:org.talend.components/processing-runtime"),
                        DependenciesReader.computeDependenciesFilePath("org.talend.components", "components-runtime"),
                        "org.talend.components.processing.runtime.normalize.NormalizeRuntime");
            } catch (MalformedURLException e) {
                throw new ComponentException(e);
            }
        } else {
            TalendRuntimeException.build(ComponentsErrorCode.WRONG_CONNECTOR) //
                    .put("component", getName()) //
                    .put("requested", connectorTopology == null ? "null" : connectorTopology.toString()) //
                    .put("available", ConnectorTopology.INCOMING_AND_OUTGOING.toString()).throwIt();
            return null;
        }
    }

    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.INCOMING_AND_OUTGOING);
    }

    @Override
    public boolean isConditionalInputs() {
        return true;
    }

    @Override
    public boolean isSchemaAutoPropagate() {
        return true;
    }
}
