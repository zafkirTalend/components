
package org.talend.components.s3.tawss3connection;

import org.talend.components.api.Constants;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.EndpointComponentDefinition;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.s3.AwsS3ConnectionProperties;
import org.talend.components.s3.AwsS3Definition;
import org.talend.components.s3.runtime.AwsS3SourceOrSink;

import aQute.bnd.annotation.component.Component;

/**
 * The tAWSS3ConnectionDefinition acts as an entry point for all of services that a component provides to integrate with
 * the Studio (at design-time) and other components (at run-time).
 */
@Component(name = Constants.COMPONENT_BEAN_PREFIX
        + TAwsS3ConnectionDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class TAwsS3ConnectionDefinition extends AwsS3Definition implements EndpointComponentDefinition {

    public static final String COMPONENT_NAME = "tAWSS3Connection"; //$NON-NLS-1$

    public TAwsS3ConnectionDefinition() {
        super(COMPONENT_NAME);
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return AwsS3ConnectionProperties.class;
    }

    @Override
    public SourceOrSink getRuntime() {
        return new AwsS3SourceOrSink();
    }
}
