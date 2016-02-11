package org.talend.components.salesforce.tsalesforceconnection;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BulkConnection;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SessionHeader_element;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.SessionRenewer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.talend.components.salesforce.SalesforceConnectionProperties;
import org.talend.components.salesforce.connection.oauth.SalesforceOAuthConnection;

import javax.xml.namespace.QName;

/**
 * Created by bchen on 16-1-28.
 */
public class SalesforceConnectionFactory extends BasePooledObjectFactory<SalesforceConnectionObject> {

    private SalesforceConnectionProperties props;

    private static final String API_VERSION = "34.0";// TODO should not fixed in here

    public SalesforceConnectionFactory(SalesforceConnectionProperties props) {
        this.props = props;
    }

    @Override
    public SalesforceConnectionObject create() throws ConnectionException {
        ConnectorConfig config = new ConnectorConfig();
        config.setUsername(StringUtils.strip(props.userPassword.userId.getStringValue(), "\""));
        config.setPassword(StringUtils.strip(props.userPassword.password.getStringValue(), "\"")
                + StringUtils.strip(props.userPassword.securityKey.getStringValue(), "\""));

        // Notes on how to test this
        // http://thysmichels.com/2014/02/15/salesforce-wsc-partner-connection-session-renew-when-session-timeout/

        final SalesforceConnectionProperties finalProps = props;
        config.setSessionRenewer(new SessionRenewer() {

            @Override
            public SessionRenewalHeader renewSession(ConnectorConfig connectorConfig) throws ConnectionException {
                SessionRenewalHeader header = new SessionRenewalHeader();
                // FIXME - session id need to be null for trigger the login?
                // connectorConfig.setSessionId(null);
                PartnerConnection connection = doConnection(finalProps, connectorConfig).getPartnerConnection();
                SessionHeader_element h = connection.getSessionHeader();
                // FIXME - one or the other, I have seen both
                // header.name = new QName("urn:partner.soap.sforce.com", "X-SFDC-Session");
                header.name = new QName("urn:partner.soap.sforce.com", "SessionHeader");
                header.headerElement = h.getSessionId();

                return header;
            }
        });

        if (props.timeout.getIntValue() > 0) {
            config.setConnectionTimeout(props.timeout.getIntValue());
        }
        config.setCompression(props.needCompression.getBooleanValue());
        if (false) {
            config.setTraceMessage(true);
        }

        return doConnection(props, config);

    }

    protected SalesforceConnectionObject doConnection(SalesforceConnectionProperties properties, ConnectorConfig config)
            throws ConnectionException {
        if (SalesforceConnectionProperties.LOGIN_OAUTH.equals(properties.loginType.getValue())) {
            new SalesforceOAuthConnection(properties.oauth, SalesforceConnectionProperties.OAUTH_URL, API_VERSION).login(config);
        } else {
            config.setAuthEndpoint(SalesforceConnectionProperties.URL);
        }
        PartnerConnection partnerConnection = new PartnerConnection(config);

        /*
         * The endpoint for the Bulk API service is the same as for the normal SOAP uri until the /Soap/ part. From here
         * it's '/async/versionNumber'
         */
        ConnectorConfig bulkConfig = new ConnectorConfig();
        bulkConfig.setSessionId(config.getSessionId());
        String soapEndpoint = config.getServiceEndpoint();
        // FIXME - fix hardcoded version
        String restEndpoint = soapEndpoint.substring(0, soapEndpoint.indexOf("Soap/")) + "async/" + API_VERSION;
        bulkConfig.setRestEndpoint(restEndpoint);
        // This should only be false when doing debugging.
        bulkConfig.setCompression(true);
        bulkConfig.setTraceMessage(false);
        BulkConnection bulkConnection = null;
        try {
            bulkConnection = new BulkConnection(bulkConfig);
        } catch (AsyncApiException e) {
            e.printStackTrace();
        }
        return new SalesforceConnectionObject(partnerConnection, bulkConnection);
    }

    @Override
    public PooledObject<SalesforceConnectionObject> wrap(SalesforceConnectionObject o) {
        return new DefaultPooledObject(o);
    }

    @Override
    public void destroyObject(PooledObject<SalesforceConnectionObject> o) throws Exception {
        super.destroyObject(o);
    }
}
