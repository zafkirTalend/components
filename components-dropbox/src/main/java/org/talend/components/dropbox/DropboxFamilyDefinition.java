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
package org.talend.components.dropbox;

import org.talend.components.api.AbstractComponentFamilyDefinition;
import org.talend.components.api.ComponentInstaller;
import org.talend.components.api.Constants;
import org.talend.components.dropbox.tdropboxconnection.TDropboxConnectionDefinition;
import org.talend.components.dropbox.tdropboxget.TDropboxGetDefinition;
import org.talend.components.dropbox.tdropboxput.TDropboxPutDefinition;

import aQute.bnd.annotation.component.Component;

/**
 * Installs all definitions provided for the Dropbox components family
 */
@Component(name = Constants.COMPONENT_INSTALLER_PREFIX + DropboxFamilyDefinition.NAME, provide = ComponentInstaller.class)
public class DropboxFamilyDefinition extends AbstractComponentFamilyDefinition implements ComponentInstaller {

    /**
     * Dropbox family name
     */
    public static final String NAME = "Dropbox";

    /**
     * Constructor sets family name and adds all definitions
     */
    public DropboxFamilyDefinition() {
        super(NAME, new TDropboxConnectionDefinition(), new TDropboxGetDefinition(), new TDropboxPutDefinition());
    }

    /**
     * Registers Dropbox components family into the system
     */
    @Override
    public void install(ComponentFrameworkContext ctx) {
        ctx.registerComponentFamilyDefinition(this);
    }

}
