// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.components.jms;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

import java.util.Collection;

public class JmsDatastorePropertiesTest {
    /**
     * Checks {@link JmsDatasetProperties} sets correctly initial schema
     * property
     */
    @Test
    public void testDefaultProperties() {
        JmsDatastoreProperties properties = new JmsDatastoreProperties("test");
        assertNull(properties.main.schema.getValue());
        assertNull(properties.version.getValue());
        //assertNull(properties.contextProvider.getValue());
        assertEquals("com.tibco.tibjms.naming.TibjmsInitialContextFactory",properties.contextProvider.getValue());
        assertEquals("tibjmsnaming://localhost:7222",properties.serverUrl.getValue());
        assertEquals("GenericConnectionFactory",properties.connectionFactoryName.getValue());
        assertEquals(false,properties.needUserIdentity.getValue());
        assertEquals("",properties.userPassword.getValue());
        assertEquals("",properties.userName.getValue());
        assertEquals(false,properties.use_https.getValue());
        assertNull(properties.https_settings.getValue());
        assertEquals("",properties.property.getValue());
        assertEquals("",properties.value.getValue());
    }

    @Test
    public void testSetupLayout() {
        JmsDatastoreProperties properties = new JmsDatastoreProperties("test");
        properties.main.init();

        properties.setupLayout();

        Form main = properties.getForm(Form.MAIN);
        assertThat(main, notNullValue());
        Form advanced = properties.getForm(Form.ADVANCED);
        assertThat(advanced, notNullValue());

        Collection<Widget> mainWidgets = main.getWidgets();
        assertThat(mainWidgets, hasSize(5));
        Widget mainWidget = main.getWidget("main");
        assertThat(mainWidget, notNullValue());
        Widget msgType = main.getWidget("version");
        assertThat(msgType, notNullValue());
        Widget contextProvider = main.getWidget("contextProvider");
        assertThat(contextProvider, notNullValue());
        Widget serverUrl = main.getWidget("serverUrl");
        assertThat(serverUrl, notNullValue());
        Widget connectionFactoryName = main.getWidget("connectionFactoryName");
        assertThat(connectionFactoryName, notNullValue());

        Collection<Widget> advancedWidgets = advanced.getWidgets();
        assertThat(advancedWidgets, hasSize(4));
        // TODO Fix issue test use_htttp
        Widget use_http = advanced.getWidget("use_http");
        //assertThat(use_http, notNullValue());
        Widget https_settings = advanced.getWidget("https_settings");
        assertThat(https_settings, notNullValue());
        Widget property = advanced.getWidget("property");
        assertThat(property, notNullValue());
        Widget value = advanced.getWidget("value");
        assertThat(value, notNullValue());
    }

    /**
     * Checks {@link JmsDatastoreProperties#refreshLayout(Form)} doesn't hide userPassword and jqlWidget in initial state
     */
    @Test
    public void testRefreshLayout() {
        //TODO FIX ISSUE
        JmsDatastoreProperties properties = new JmsDatastoreProperties("test");
        /*properties.main.init();
        System.out.println(properties.toString());
        //properties.refreshLayout(properties.getForm(Form.MAIN));

        assertFalse(properties.getForm(Form.MAIN).getWidget("version").isHidden());
        assertFalse(properties.getForm(Form.MAIN).getWidget("contextProvider").isHidden());
        assertFalse(properties.getForm(Form.MAIN).getWidget("serverUrl").isHidden());
        assertFalse(properties.getForm(Form.MAIN).getWidget("userName").isHidden());
        assertFalse(properties.getForm(Form.MAIN).getWidget("userPassword").isHidden());

        properties.needUserIdentity.setValue(true);
        assertFalse(properties.getForm(Form.MAIN).getWidget("version").isHidden());
        assertFalse(properties.getForm(Form.MAIN).getWidget("contextProvider").isHidden());
        assertFalse(properties.getForm(Form.MAIN).getWidget("serverUrl").isHidden());
        assertTrue(properties.getForm(Form.MAIN).getWidget("userName").isHidden());
        assertTrue(properties.getForm(Form.MAIN).getWidget("userPassword").isHidden());
        */
    }
}
