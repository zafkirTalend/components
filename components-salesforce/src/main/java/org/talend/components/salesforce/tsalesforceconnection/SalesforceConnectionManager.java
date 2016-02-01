package org.talend.components.salesforce.tsalesforceconnection;

import com.sforce.soap.partner.fault.LoginFault;
import org.apache.commons.pool2.ObjectPool;
import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.connection.ConnectionManager;
import org.talend.components.salesforce.SalesforceConnectionProperties;

/**
 * Created by bchen on 16-1-28.
 */
public class SalesforceConnectionManager extends ConnectionManager<SalesforceConnectionObject> {
    @Override
    public SalesforceConnectionObject newConnection(ComponentProperties props) throws TalendConnectionException {
        SalesforceConnectionFactory factory = new SalesforceConnectionFactory((SalesforceConnectionProperties) props);
        SalesforceConnectionObject connection = null;
        try {
            connection = factory.create();
        } catch (LoginFault loginFault) {
            throw new TalendConnectionException(loginFault.getExceptionCode().toString() + "-" + loginFault.getExceptionMessage());
        } catch (Exception e) {
            // FIXME - do a better job here
            throw new TalendConnectionException(e.getMessage());
        }
        return connection;
    }


    @Override
    public void destoryConnection(SalesforceConnectionObject partnerConnection) {
        //nothing to do;
    }

    @Override
    public ObjectPool<SalesforceConnectionObject> getConnectionPool(ComponentProperties props) {
        return SalesforceConnectionPool.get((SalesforceConnectionProperties) props);
    }
}
