// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.file;

import org.talend.components.api.AbstractComponentFamilyDefinition;
import org.talend.components.api.ComponentInstaller;
import org.talend.components.api.Constants;
import org.talend.components.file.tfileinput.FileInputDefinition;
import org.talend.components.file.tfileoutput.FileOutputDefinition;

import com.google.auto.service.AutoService;

import aQute.bnd.annotation.component.Component;

/**
 * Install all of the definitions provided for the FileInput family of components.
 */
@AutoService(ComponentInstaller.class)
@Component(name = Constants.COMPONENT_INSTALLER_PREFIX + FileFamilyDefinition.NAME, provide = ComponentInstaller.class)
public class FileFamilyDefinition extends AbstractComponentFamilyDefinition implements ComponentInstaller {

    public static final String NAME = "FileComponent";

    public FileFamilyDefinition() {
        super(NAME, new FileInputDefinition(),
                new FileOutputDefinition());

    }

    @Override
    public void install(ComponentFrameworkContext ctx) {
        ctx.registerComponentFamilyDefinition(this);
    }
}
