
package org.talend.components.files.tfileinputpositional;

import org.talend.components.api.Constants;
import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.ComponentImageType;
import org.talend.components.api.component.InputComponentDefinition;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.properties.property.Property;

import aQute.bnd.annotation.component.Component;

/**
 * The TestInputDefinition acts as an entry point for all of services that 
 * a component provides to integrate with the Studio (at design-time) and other 
 * components (at run-time).
 */
@Component(name = Constants.COMPONENT_BEAN_PREFIX + TFileInputPositionalDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class TFileInputPositionalDefinition extends AbstractComponentDefinition implements InputComponentDefinition {

    public static final String COMPONENT_NAME = "tFileInputPositional"; //$NON-NLS-1$

    public TFileInputPositionalDefinition() {
        super(COMPONENT_NAME);
    }

    @Override
    public String[] getFamilies() {
        return new String[] { "File/Input" }; //$NON-NLS-1$
    }

    @Override
    public Property[] getReturnProperties() {
        return new Property[] { RETURN_ERROR_MESSAGE_PROP, RETURN_TOTAL_RECORD_COUNT_PROP};
    }

    @Override
    public String getPngImagePath(ComponentImageType imageType) {
        switch (imageType) {
        case PALLETE_ICON_32X32:
            return "tFileInputPositional_icon32.png"; //$NON-NLS-1$
        default:
            return "tFileInputPositional_icon32.png"; //$NON-NLS-1$
        }
    }

    public String getMavenGroupId() {
        return "org.talend.components";
    }

    @Override
    public String getMavenArtifactId() {
        return "components-filePositional";
    }
    
    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TFileInputPositionalProperties.class;
    }

    @Override
    public Source getRuntime() {
        return new TFileInputPositionalSource();
    }
}
