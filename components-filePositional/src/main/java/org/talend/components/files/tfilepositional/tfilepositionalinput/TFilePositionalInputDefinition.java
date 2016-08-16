
package org.talend.components.files.tfilepositional.tfilepositionalinput;

import org.talend.components.api.Constants;
import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.ComponentImageType;
import org.talend.components.api.component.InputComponentDefinition;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.files.tfilepositionalinput.runtime.TFilePositionalInputSource;
import org.talend.daikon.properties.property.Property;

import aQute.bnd.annotation.component.Component;

/**
 * The TestInputDefinition acts as an entry point for all of services that 
 * a component provides to integrate with the Studio (at design-time) and other 
 * components (at run-time).
 */
@Component(name = Constants.COMPONENT_BEAN_PREFIX + TFilePositionalInputDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class TFilePositionalInputDefinition extends AbstractComponentDefinition implements InputComponentDefinition {

    public static final String COMPONENT_NAME = "tFileInputPositional"; //$NON-NLS-1$

    public TFilePositionalInputDefinition() {
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
            return "tFilePositionalInput_icon32.png"; //$NON-NLS-1$
        default:
            return "tFilePositionalInput_icon32.png"; //$NON-NLS-1$
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
        return TFilePositionalInputProperties.class;
    }

    @Override
    public Source getRuntime() {
        return new TFilePositionalInputSource();
    }
}
