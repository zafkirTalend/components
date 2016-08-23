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
package org.talend.components.dropbox.tdropboxget;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

/**
 * Unit-tests for {@link ChunkModeProperties}
 */
public class ChunkModePropertiesTest {

    /**
     * Checks {@link ChunkModeProperties#afterChunkMode()} shows Chunk Size widget, when Chunk Mode checkbox is checked
     */
    @Test
    public void testAfterChunkModeTrue() {
        ChunkModeProperties properties = new ChunkModeProperties("chunkMode");
        properties.init();
        properties.chunkMode.setValue(true);

        properties.afterChunkMode();

        boolean chunkSizeIsHidden = properties.getForm(Form.MAIN).getWidget("chunkSize").isHidden();
        assertFalse(chunkSizeIsHidden);
    }

    /**
     * Checks {@link ChunkModeProperties#afterChunkMode()} hides Chunk Size widget, when Chunk Mode checkbox is unchecked
     */
    @Test
    public void testAfterChunkModeFalse() {
        ChunkModeProperties properties = new ChunkModeProperties("chunkMode");
        properties.init();
        properties.chunkMode.setValue(false);

        properties.afterChunkMode();

        boolean chunkSizeIsHidden = properties.getForm(Form.MAIN).getWidget("chunkSize").isHidden();
        assertTrue(chunkSizeIsHidden);
    }

    /**
     * Checks {@link ChunkModeProperties#setupProperties()} sets correct initial property values
     */
    @Test
    public void testSetupProperties() {
        ChunkModeProperties properties = new ChunkModeProperties("chunkMode");
        properties.setupProperties();

        boolean chunkMode = properties.chunkMode.getValue();
        int chunkSize = properties.chunkSize.getValue();

        assertFalse(chunkMode);
        assertThat(chunkSize, equalTo(8192));
    }

    /**
     * Checks {@link ChunkModeProperties#setupLayout()} creates Main form,
     * which contains 2 widgets: Chunk Mode and Chunk Size
     */
    @Test
    public void testSetupLayout() {
        ChunkModeProperties properties = new ChunkModeProperties("chunkMode");
        properties.setupLayout();

        Form main = properties.getForm(Form.MAIN);
        assertThat(main, notNullValue());
        Form advanced = properties.getForm(Form.ADVANCED);
        assertThat(advanced, nullValue());

        Collection<Widget> mainWidgets = main.getWidgets();
        assertThat(mainWidgets, hasSize(2));

        Widget chunkModeWidget = main.getWidget("chunkMode");
        assertThat(chunkModeWidget, notNullValue());
        Widget chunkSizeWidget = main.getWidget("chunkSize");
        assertThat(chunkSizeWidget, notNullValue());
    }

    /**
     * Checks {@link ChunkModeProperties#refreshLayout(Form)} hides Chunk Size widget in initial state
     */
    @Test
    public void testRefreshLayoutMainInitial() {
        ChunkModeProperties properties = new ChunkModeProperties("chunkMode");
        properties.init();

        properties.refreshLayout(properties.getForm(Form.MAIN));

        boolean chunkModeIsHidden = properties.getForm(Form.MAIN).getWidget("chunkMode").isHidden();
        boolean chunkSizeIsHidden = properties.getForm(Form.MAIN).getWidget("chunkSize").isHidden();
        assertFalse(chunkModeIsHidden);
        assertTrue(chunkSizeIsHidden);
    }
}
