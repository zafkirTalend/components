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
package org.talend.components.s3;

import org.talend.components.api.AbstractComponentFamilyDefinition;
import org.talend.components.api.ComponentInstaller;
import org.talend.components.api.Constants;
import org.talend.components.s3.tawss3connection.TAwsS3ConnectionDefinition;
import org.talend.components.s3.tawss3get.TAwsS3GetDefinition;
import org.talend.components.s3.tawss3put.TAwsS3PutDefinition;

import aQute.bnd.annotation.component.Component;

/**
 * AwsS3FamilyDefinition provides definition for Amazon S3 components family.
 */
@Component(name = Constants.COMPONENT_INSTALLER_PREFIX + AwsS3FamilyDefinition.NAME, provide = ComponentInstaller.class)
public class AwsS3FamilyDefinition extends AbstractComponentFamilyDefinition implements ComponentInstaller {

    public static final String NAME = "AwsS3";

    public AwsS3FamilyDefinition() {
        super(NAME,
                // Components
                new TAwsS3ConnectionDefinition(), new TAwsS3GetDefinition(), new TAwsS3PutDefinition());
    }

    @Override
    public void install(ComponentFrameworkContext ctx) {
        ctx.registerComponentFamilyDefinition(this);
    }

}
