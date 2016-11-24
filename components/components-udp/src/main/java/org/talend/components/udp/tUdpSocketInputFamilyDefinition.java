
// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.udp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.talend.components.api.AbstractComponentFamilyDefinition;
import org.talend.components.api.ComponentInstaller;
import org.talend.components.api.Constants;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.wizard.ComponentWizardDefinition;
import org.talend.daikon.i18n.I18nMessages;

import aQute.bnd.annotation.component.Component;

/**
 * Install all of the definitions provided for the tUdpSocketInput family of components.
 */
@Component(name = Constants.COMPONENT_INSTALLER_PREFIX + tUdpSocketInputFamilyDefinition.NAME, provide = ComponentInstaller.class)
public class tUdpSocketInputFamilyDefinition extends AbstractComponentFamilyDefinition implements ComponentInstaller {

    public static final String NAME = "tUdpSocketInput";

    public tUdpSocketInputFamilyDefinition() {
        super(NAME, new tUdpSocketInputDefinition());

    }

    @Override
    public void install(ComponentFrameworkContext ctx) {
        ctx.registerComponentFamilyDefinition(this);
    }
}
