
package org.talend.components.s3.tawss3connection;

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.RuntimeInfo;
import org.talend.components.api.component.runtime.SimpleRuntimeInfo;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.s3.AwsS3ConnectionProperties;
import org.talend.components.s3.AwsS3Definition;
import org.talend.components.s3.runtime.AwsS3SourceOrSink;
import org.talend.daikon.properties.Properties;

/**
 * The TAwsS3ConnectionDefinition acts as an entry point for all of services that a component provides to integrate with
 * the Studio (at design-time) and other components (at run-time).
 */
public class TAwsS3ConnectionDefinition extends AwsS3Definition {

    public static final String COMPONENT_NAME = "tAWSS3Connection"; //$NON-NLS-1$

    public TAwsS3ConnectionDefinition() {
        super(COMPONENT_NAME);
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return AwsS3ConnectionProperties.class;
    }

    @Override
    public RuntimeInfo getRuntimeInfo(Properties properties, ConnectorTopology connectorTopology) {
        if (connectorTopology == ConnectorTopology.NONE) {
            return new SimpleRuntimeInfo(this.getClass().getClassLoader(),
                    DependenciesReader.computeDependenciesFilePath("org.talend.components", "components-s3"),
                    AwsS3SourceOrSink.class.getCanonicalName());
        }
        return null;
    }

    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.NONE);
    }
}
