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
package org.talend.components.s3;

import java.util.Map;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;

/**
 * created by dmytro.chmyga on Jul 26, 2016
 */
public class ClientConfigurationBuilder {

    public static ClientConfiguration createClientConfiguration(Map<AwsS3ClientConfigFields, Object> configData) {
        ClientConfiguration clientConfig = new ClientConfiguration();
        boolean socketSizeHintsSet = false;
        int socketReceiveBufferSizeHint = 0;
        int socketSendBufferSizeHint = 0;
        for (Map.Entry<AwsS3ClientConfigFields, Object> configValue : configData.entrySet()) {
            AwsS3ClientConfigFields field = configValue.getKey();
            Object value = configValue.getValue();
            switch (field) {
            case CONNECTIONTIMEOUT:
                clientConfig.setConnectionTimeout((Integer) value);
                break;
            case MAXCONNECTIONS:
                clientConfig.setMaxConnections((Integer) value);
                break;
            case MAXERRORRETRY:
                clientConfig.setMaxErrorRetry((Integer) value);
                break;
            case PROTOCOL:
                String protocol = ((String) value).toUpperCase();
                clientConfig.setProtocol(Protocol.valueOf(protocol));
                break;
            case PROXYDOMAIN:
                clientConfig.setProxyDomain((String) value);
                break;
            case PROXYHOST:
                clientConfig.setProxyHost((String) value);
                break;
            case PROXYPASSWORD:
                clientConfig.setProxyPassword((String) value);
                break;
            case PROXYPORT:
                clientConfig.setProxyPort((Integer) value);
                break;
            case PROXYUSERNAME:
                clientConfig.setProxyUsername((String) value);
                break;
            case PROXYWORKSTATION:
                clientConfig.setProxyWorkstation((String) value);
                break;
            case SOCKETRECEIVEBUFFERSIZEHINT:
                socketSizeHintsSet = true;
                socketReceiveBufferSizeHint = (Integer) value;
                break;
            case SOCKETSENDBUFFERSIZEHINT:
                socketSizeHintsSet = true;
                socketSendBufferSizeHint = (Integer) value;
                break;
            case SOCKETTIMEOUT:
                clientConfig.setSocketTimeout((Integer) value);
                break;
            case USERAGENT:
                clientConfig.setUserAgent((String) value);
                break;
            default:
                break;
            }
        }
        if (socketSizeHintsSet) {
            clientConfig.setSocketBufferSizeHints(socketSendBufferSizeHint, socketReceiveBufferSizeHint);
        }
        return clientConfig;
    }

}
