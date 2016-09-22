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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ComponentDriverInitialization;
import org.talend.components.api.component.runtime.RuntimeInfo;
import org.talend.components.runtimeservice.RuntimeUtil;
import org.talend.components.s3.AwsS3Definition;
import org.talend.components.s3.runtime.TAwsS3PutComponentDriverRuntime;
import org.talend.daikon.sandbox.SandboxedInstance;

/**
 * Unit-tests for {@link TAwsS3PutDefinition} class
 */
public class TAwsS3PutDefinitionTest {

    /**
     * Check {@link TAwsS3PutDefinition#getFamilies()} returns string array, which contains "Business/JIRA"
     */
    @Test
    public void testGetFamilies() {
        AwsS3Definition definition = new TAwsS3PutDefinition();
        String[] families = definition.getFamilies();
        assertThat(families, arrayContaining("Cloud/Amazon/S3"));
    }

    /**
     * Check {@link TAwsS3PutDefinition#getName()} returns "tJIRAInput"
     */
    @Test
    public void testGetName() {
        AwsS3Definition definition = new TAwsS3PutDefinition();
        String componentName = definition.getName();
        assertEquals(componentName, "tAWSS3Put");
    }

    /**
     * Check {@link TAwsS3PutDefinition#getPropertyClass()} returns class, which canonical name is
     * "org.talend.components.s3.tawss3put.TAwsS3PutProperties"
     */
    @Test
    public void testGetPropertyClass() {
        TAwsS3PutDefinition definition = new TAwsS3PutDefinition();
        Class<?> propertyClass = definition.getPropertyClass();
        String canonicalName = propertyClass.getCanonicalName();
        assertThat(canonicalName, equalTo("org.talend.components.s3.tawss3put.TAwsS3PutProperties"));
    }

    /**
     * Check {@link TAwsS3PutDefinition#getRuntime()} returns instance of {@link TAwsS3PutComponentDriverRuntime}
     */
    @Test
    public void testGetRuntime() {
        TAwsS3PutDefinition definition = new TAwsS3PutDefinition();
        RuntimeInfo runtimeInfo = definition.getRuntimeInfo(null, ConnectorTopology.NONE);
        SandboxedInstance sandboxedInstance = RuntimeUtil.createRuntimeClass(runtimeInfo, definition.getClass().getClassLoader());
        ComponentDriverInitialization compInit = (ComponentDriverInitialization) sandboxedInstance.getInstance();
        assertThat(compInit, is(instanceOf(TAwsS3PutComponentDriverRuntime.class)));
    }
}
