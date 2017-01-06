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
package org.talend.components.salesforce.runtime;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.ProxyAuthenticator;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.talend.components.api.component.runtime.Reader;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.salesforce.SalesforceConnectionProperties;
import org.talend.components.salesforce.runtime.SalesforceSource;
import org.talend.components.salesforce.runtime.SalesforceSourceOrSink;
import org.talend.components.salesforce.test.SalesforceRuntimeTestUtil;
import org.talend.components.salesforce.test.SalesforceTestBase;
import org.talend.components.salesforce.tsalesforceconnection.TSalesforceConnectionDefinition;
import org.talend.components.salesforce.tsalesforceinput.TSalesforceInputDefinition;
import org.talend.components.salesforce.tsalesforceinput.TSalesforceInputProperties;
import org.talend.components.salesforce.tsalesforceinput.TSalesforceInputProperties.QueryMode;
import org.talend.daikon.properties.ValidationResult;

public class SalesforceProxyTestIT extends SalesforceTestBase {

    private static HttpProxyServer server;

    private static final String proxyUsername = "talend_username";

    private static final String proxyPassword = "talend_password";

    private static final int proxyPort = 9999;

    @BeforeClass
    public static void setupProxy() {
        ProxyAuthenticator auth = new ProxyAuthenticator() {

            @Override
            public boolean authenticate(String username, String password) {
                return proxyUsername.equals(username) && proxyPassword.equals(password);
            }

            @Override
            public String getRealm() {
                return null;
            }

        };
        server = DefaultHttpProxyServer.bootstrap().withPort(proxyPort).withProxyAuthenticator(auth).start();

    }

    @AfterClass
    public static void closeProxy() {
        if (server != null) {
            server.stop();
        }
        server = null;
    }

    @Test
    public void testProxy() {
        TSalesforceConnectionDefinition definition = (TSalesforceConnectionDefinition) getComponentService()
                .getComponentDefinition(TSalesforceConnectionDefinition.COMPONENT_NAME);
        SalesforceConnectionProperties properties = (SalesforceConnectionProperties) definition.createRuntimeProperties();
        setProxySettingForClient(properties);

        properties.bulkConnection.setValue(true);
        properties.userPassword.userId.setValue(userId);
        properties.userPassword.password.setValue(password);
        properties.userPassword.securityKey.setValue(securityKey);

        SourceOrSink sourceOrSink = new SalesforceSourceOrSink();
        sourceOrSink.initialize(null, properties);
        org.talend.daikon.properties.ValidationResult vr = sourceOrSink.validate(null);
        Assert.assertEquals(ValidationResult.Result.OK, vr.getStatus());
    }

    @Test
    public void testProxyWithBulkQuery() {
        TSalesforceInputDefinition definition = (TSalesforceInputDefinition) getComponentService()
                .getComponentDefinition(TSalesforceInputDefinition.COMPONENT_NAME);
        TSalesforceInputProperties properties = (TSalesforceInputProperties) definition.createRuntimeProperties();
        setProxySettingForClient(properties.getConnectionProperties());

        properties.connection.bulkConnection.setValue(true);
        properties.queryMode.setValue(QueryMode.Bulk);

        SalesforceRuntimeTestUtil util = new SalesforceRuntimeTestUtil();
        properties.module.moduleName.setValue(util.getTestModuleName());
        properties.module.main.schema.setValue(util.getTestSchema1());

        properties.connection.userPassword.userId.setValue(userId);
        properties.connection.userPassword.password.setValue(password);
        properties.connection.userPassword.securityKey.setValue(securityKey);

        Source source = new SalesforceSource();
        source.initialize(null, properties);
        org.talend.daikon.properties.ValidationResult vr = source.validate(null);
        Assert.assertEquals(ValidationResult.Result.OK, vr.getStatus());

        Reader reader = source.createReader(null);

        try {
            reader.start();

            do {
                reader.getCurrent();
            } while (reader.advance());

            reader.close();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    private static void setProxySettingForClient(SalesforceConnectionProperties properties) {
        properties.proxy.useProxy.setStoredValue(Boolean.TRUE);
        properties.proxy.host.setStoredValue("127.0.0.1");
        properties.proxy.port.setStoredValue(proxyPort);
        properties.proxy.userPassword.userId.setStoredValue(proxyUsername);
        properties.proxy.userPassword.password.setStoredValue(proxyPassword);
    }

}
