package org.talend.components.processing.definition.window;

import org.junit.Test;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class WindowPropertiesTest {

    /**
     * Checks {@link WindowProperties} sets correctly initial schema property
     */
    @Test
    public void testDefaultProperties() {
        WindowProperties properties = new WindowProperties("test");
        assertNull(properties.windowDurationLength.getValue());
        assertNull(properties.windowSlideLength.getValue());
    }

    /**
     * Checks {@link WindowProperties} sets correctly initial layout properties
     */
    @Test
    public void testSetupLayout() {
        WindowProperties properties = new WindowProperties("test");

        properties.setupLayout();

        Form main = properties.getForm(Form.MAIN);
        assertThat(main, notNullValue());

        Collection<Widget> mainWidgets = main.getWidgets();
        assertThat(mainWidgets, hasSize(3));
        Widget windowDuration = main.getWidget("windowDurationLength");
        assertThat(windowDuration, notNullValue());
        Widget slideWindow = main.getWidget("windowSlideLength");
        assertThat(slideWindow, notNullValue());
        Widget windowType = main.getWidget("windowType");
        assertThat(windowType, notNullValue());
    }

    /**
     * Checks {@link WindowProperties#refreshLayout(Form)}
     */
    @Test
    public void testRefreshLayout() {
        WindowProperties properties = new WindowProperties("test");
        properties.init();
        properties.refreshLayout(properties.getForm(Form.MAIN));

        assertTrue(properties.getForm(Form.MAIN).getWidget("windowDurationLength").isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget("windowSlideLength").isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget("windowType").isVisible());
    }
}
