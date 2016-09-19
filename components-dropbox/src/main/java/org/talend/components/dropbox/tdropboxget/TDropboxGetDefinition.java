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

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.RuntimeInfo;
import org.talend.components.api.component.runtime.SimpleRuntimeInfo;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.dropbox.DropboxDefinition;
import org.talend.components.dropbox.runtime.DropboxGetSource;
import org.talend.daikon.properties.Properties;

/**
 * Dropbox get component definition
 */
public class TDropboxGetDefinition extends DropboxDefinition {

    /**
     * Dropbox get component name
     */
    public static final String COMPONENT_NAME = "tDropboxGet";

    /**
     * Constructor sets component name
     */
    public TDropboxGetDefinition() {
        super(COMPONENT_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TDropboxGetProperties.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuntimeInfo getRuntimeInfo(Properties properties, ConnectorTopology connectorTopology) {
        if (connectorTopology == ConnectorTopology.OUTGOING) {
            return new SimpleRuntimeInfo(this.getClass().getClassLoader(), DEPENDENCIES_FILE_PATH,
                    DropboxGetSource.class.getCanonicalName());
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.OUTGOING);
    }

}
