package org.talend.components.cassandra.tCassandraInput;

import org.talend.components.cassandra.metadata.CassandraMetadataProperties;
import org.talend.daikon.properties.Property;

import static org.talend.daikon.properties.PropertyFactory.newString;

/**
 * Created by bchen on 16-1-14.
 */
public class tCassandraInputDIProperties extends CassandraMetadataProperties {
    /**
     * named constructor to be used is these properties are nested in other properties. Do not subclass this method for
     * initialization, use {@link #init()} instead.
     *
     * @param name
     */
    public tCassandraInputDIProperties(String name) {
        super(name);
    }

    public Property query = newString("query");

}
