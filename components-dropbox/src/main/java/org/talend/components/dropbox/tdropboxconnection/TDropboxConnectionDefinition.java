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
package org.talend.components.dropbox.tdropboxconnection;

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.RuntimeInfo;
import org.talend.components.api.component.runtime.SimpleRuntimeInfo;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.dropbox.DropboxDefinition;
import org.talend.components.dropbox.runtime.DropboxSourceOrSink;
import org.talend.daikon.properties.Properties;

/**
 * Dropbox connection component definition
 */
public class TDropboxConnectionDefinition extends DropboxDefinition {

    /**
     * Dropbox connection component name
     */
    public static final String COMPONENT_NAME = "tDropboxConnection";

    /**
     * Constructor sets component name
     */
    public TDropboxConnectionDefinition() {
        super(COMPONENT_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TDropboxConnectionProperties.class;
    }

    /**
     * @return true in case it is startable component, false otherwise
     */
    @Override
    public boolean isStartable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuntimeInfo getRuntimeInfo(Properties properties, ConnectorTopology connectorTopology) {
        if (connectorTopology == ConnectorTopology.NONE) {
            return new SimpleRuntimeInfo(this.getClass().getClassLoader(), DEPENDENCIES_FILE_PATH,
                    DropboxSourceOrSink.class.getCanonicalName());
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.NONE);
    }

}
