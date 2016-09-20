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

import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.daikon.properties.property.Property;

/**
 * created by dmytro.chmyga on Jul 25, 2016
 */
public abstract class AwsS3Definition extends AbstractComponentDefinition {

    public AwsS3Definition(String componentName) {
        super(componentName);
    }

    @Override
    public Property[] getReturnProperties() {
        return new Property[] { RETURN_ERROR_MESSAGE_PROP };
    }

    @Override
    public String[] getFamilies() {
        return new String[] { "Cloud/Amazon/S3" }; //$NON-NLS-1$
    }

    @Override
    public boolean isStartable() {
        return true;
    }

}
