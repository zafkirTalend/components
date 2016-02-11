package org.talend.components.api.runtime.connection;

import org.apache.commons.pool2.ObjectPool;
import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.properties.ComponentProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bchen on 16-1-28.
 */
public abstract class ConnectionManager<T> {

    public abstract T newConnection(ComponentProperties props) throws TalendConnectionException;

    public abstract void destoryConnection(T t);

    protected static Map<String, Object> cache = new HashMap<>();

    // TODO need to separate to two method? create and get?
    public synchronized T getConnectionByKey(String key, ComponentProperties props) throws TalendConnectionException {
        if (key == null) {
            return newConnection(props);
        }
        T connection = (T) cache.get(key);
        if (connection == null) {
            connection = newConnection(props);
            cache.put(key, connection);
        }
        return connection;
    }

    /**
     * should only be called by tXXXClose
     *
     * @param key
     */
    public synchronized void destoryConnectionByKey(String key) {
        destoryConnection((T) cache.get(key));
        cache.remove(key);
    }

    public abstract ObjectPool<T> getConnectionPool(ComponentProperties props);

    // TODO implement getConnectionPoolByKey

}
