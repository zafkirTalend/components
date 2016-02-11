package org.talend.components.api.component;

import org.talend.components.api.runtime.connection.ConnectionManager;

/**
 * Created by bchen on 16-1-29.
 */
public interface ConnectionComponentDefinition {

    public ConnectionManager getConnectionManager();
}
