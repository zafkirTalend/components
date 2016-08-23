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

import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

/**
 * Chunk mode properties. It contains 2 widgets:
 * Chunk Mode checkbox and Chunk Size
 */
public class ChunkModeProperties extends PropertiesImpl {

    /**
     * Default value of chunk size
     */
    public static final int DEFAULT_CHUNK_SIZE = 8192;

    /**
     * Flag, which indicates whether to use chunkMode
     */
    public Property<Boolean> chunkMode = PropertyFactory.newBoolean("chunkMode");

    /**
     * Specifies size of data chunk
     */
    public Property<Integer> chunkSize = PropertyFactory.newInteger("chunkSize");

    /**
     * Constructor sets {@link Properties} name
     * 
     * @param name {@link Properties} name
     */
    public ChunkModeProperties(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupProperties() {
        super.setupProperties();
        chunkSize.setValue(DEFAULT_CHUNK_SIZE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(chunkMode);
        mainForm.addColumn(chunkSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (form.getName().equals(Form.MAIN)) {
            boolean chunkModeValue = chunkMode.getValue();
            if (chunkModeValue) {
                form.getWidget(chunkSize.getName()).setHidden(false);
            } else {
                form.getWidget(chunkSize.getName()).setHidden(true);
            }
        }
    }

    /**
     * Refreshes layout after Chunk Mode checkbox is changed
     */
    public void afterChunkMode() {
        refreshLayout(getForm(Form.MAIN));
    }
}
