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

/**
 * Configuration fields for Client Configuration table.
 */
public enum AwsS3ClientConfigFields {

    CONNECTIONTIMEOUT("ConnectionTimeout"),
    MAXCONNECTIONS("MaxConnections"),
    MAXERRORRETRY("MaxErrorRetry"),
    PROTOCOL("Protocol"),
    PROXYDOMAIN("ProxyDomain"),
    PROXYHOST("ProxyHost"),
    PROXYPASSWORD("ProxyPassword"),
    PROXYPORT("ProxyPort"),
    PROXYUSERNAME("ProxyUsername"),
    PROXYWORKSTATION("ProxyWorkstation"),
    SOCKETTIMEOUT("SocketTimeout"),
    USERAGENT("UserAgent"),
    SOCKETRECEIVEBUFFERSIZEHINT("SocketReceiveBufferSizeHints"),
    SOCKETSENDBUFFERSIZEHINT("SocketSendBufferSizeHints");

    private final String name;

    private AwsS3ClientConfigFields(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static AwsS3ClientConfigFields getByName(String name) {
        for (AwsS3ClientConfigFields field : AwsS3ClientConfigFields.values()) {
            if (field.name.equals(name)) {
                return field;
            }
        }
        return null;
    }

}
