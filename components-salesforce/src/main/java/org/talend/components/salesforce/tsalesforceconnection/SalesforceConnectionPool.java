package org.talend.components.salesforce.tsalesforceconnection;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.talend.components.salesforce.SalesforceConnectionProperties;

/**
 * Created by bchen on 16-1-28.
 */
public class SalesforceConnectionPool {
    private static volatile ObjectPool<SalesforceConnectionObject> pool;

    private SalesforceConnectionPool() {
    }

    public static ObjectPool<SalesforceConnectionObject> get(SalesforceConnectionProperties props) {
        if (pool == null) {
            synchronized (SalesforceConnectionPool.class) {
                if (pool == null) {
                    pool = createPool(props);
                }
            }
        }
        return pool;
    }

    private static ObjectPool<SalesforceConnectionObject> createPool(SalesforceConnectionProperties props) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        //TODO how to config pool for DI/BD?
        return new GenericObjectPool<SalesforceConnectionObject>(new SalesforceConnectionFactory(props), config);
    }
}
