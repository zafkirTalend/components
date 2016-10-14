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

import org.junit.Test;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

import java.util.Collection;

import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class JmsDatasetPropertiesTest {
    /**
     * Checks {@link JmsDatasetProperties} sets correctly initial schema
     * property
     */
    @Test
    public void testDefaultProperties() {
        JmsDatasetProperties properties = new JmsDatasetProperties("test");
        assertNull(properties.main.schema.getValue());
        assertNull(properties.msgType.getValue());
        assertNull(properties.processingMode.getValue());
    }

    /**
     * Checks {@link JmsDatasetProperties} sets correctly initial layout
     * properties
     */
    @Test
    public void testSetupLayout() {
        JmsDatasetProperties properties = new JmsDatasetProperties("test");
        properties.main.init();

        properties.setupLayout();

        Form main = properties.getForm(Form.MAIN);
        assertThat(main, notNullValue());

        Collection<Widget> mainWidgets = main.getWidgets();
        assertThat(mainWidgets, hasSize(3));
        Widget mainWidget = main.getWidget("main");
        assertThat(mainWidget, notNullValue());
        Widget msgType = main.getWidget("msgType");
        assertThat(msgType, notNullValue());
        Widget processingMode = main.getWidget("processingMode");
        assertThat(processingMode, notNullValue());
    }

    /**
     * Checks {@link JmsDatasetProperties#refreshLayout(Form)}
     */
    @Test
    public void testRefreshLayout() {
        JmsDatasetProperties properties = new JmsDatasetProperties("test");
        properties.init();
        properties.refreshLayout(properties.getForm(Form.MAIN));

        assertFalse(properties.getForm(Form.MAIN).getWidget("msgType").isHidden());
        assertFalse(properties.getForm(Form.MAIN).getWidget("processingMode").isHidden());
    }
}
