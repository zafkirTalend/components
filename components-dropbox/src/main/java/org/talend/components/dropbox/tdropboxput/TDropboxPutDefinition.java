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
package org.talend.components.dropbox.tdropboxput;

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.RuntimeInfo;
import org.talend.components.api.component.runtime.SimpleRuntimeInfo;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.dropbox.DropboxDefinition;
import org.talend.components.dropbox.runtime.DropboxPutSink;
import org.talend.daikon.properties.Properties;

/**
 * Dropbox put component definition
 */
public class TDropboxPutDefinition extends DropboxDefinition {

    /**
     * Dropbox put component name
     */
    public static final String COMPONENT_NAME = "tDropboxPut";

    /**
     * Constructor sets component name
     */
    public TDropboxPutDefinition() {
        super(COMPONENT_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TDropboxPutProperties.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuntimeInfo getRuntimeInfo(Properties properties, ConnectorTopology connectorTopology) {
        if (connectorTopology == ConnectorTopology.INCOMING) {
            return new SimpleRuntimeInfo(this.getClass().getClassLoader(), DEPENDENCIES_FILE_PATH,
                    DropboxPutSink.class.getCanonicalName());
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.INCOMING);
    }
}
