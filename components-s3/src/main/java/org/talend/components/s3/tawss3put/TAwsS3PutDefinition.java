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
package org.talend.components.s3.tawss3put;

import org.talend.components.api.Constants;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.InputComponentDefinition;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.s3.AwsS3Definition;
import org.talend.components.s3.runtime.TAwsS3PutSource;

import aQute.bnd.annotation.component.Component;

/**
 * created by dmytro.chmyga on Jul 28, 2016
 */
@Component(name = Constants.COMPONENT_BEAN_PREFIX + TAwsS3PutDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class TAwsS3PutDefinition extends AwsS3Definition implements InputComponentDefinition {

    public static final String COMPONENT_NAME = "tAWSS3Put"; //$NON-NLS-1$

    public TAwsS3PutDefinition() {
        super(COMPONENT_NAME);
    }

    @Override
    public Source getRuntime() {
        return new TAwsS3PutSource();
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TAwsS3PutProperties.class;
    }

}
