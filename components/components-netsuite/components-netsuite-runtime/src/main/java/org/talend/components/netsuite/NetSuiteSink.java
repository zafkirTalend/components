// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.components.netsuite;

import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.netsuite.client.NetSuiteException;
import org.talend.components.netsuite.output.NetSuiteOutputProperties;
import org.talend.components.netsuite.output.NetSuiteWriteOperation;

/**
 * Base class for NetSuite sinks.
 */
public class NetSuiteSink extends NetSuiteSourceOrSink implements Sink {

    @Override
    public WriteOperation<?> createWriteOperation() {
        if (properties instanceof NetSuiteOutputProperties) {
            return new NetSuiteWriteOperation(this, (NetSuiteOutputProperties) properties);
        }
        throw new NetSuiteException(new NetSuiteErrorCode(NetSuiteErrorCode.CLIENT_ERROR),
                NetSuiteRuntimeI18n.MESSAGES.getMessage("error.invalidComponentPropertiesClass",
                        NetSuiteOutputProperties.class, properties.getClass()));
    }

}
