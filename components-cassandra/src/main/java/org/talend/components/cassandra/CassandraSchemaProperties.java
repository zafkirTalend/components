package org.talend.components.cassandra;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.properties.Property;

import static org.talend.daikon.properties.PropertyFactory.newEnum;

public class CassandraSchemaProperties extends ComponentProperties {

    /**
     * named constructor to be used is these properties are nested in other properties. Do not subclass this method for
     * initialization, use {@link #init()} instead.
     *
     * @param name
     */
    public CassandraSchemaProperties(String name) {
        super(name);
    }

    public Property keyspace = newEnum("keyspace");
    public Property columnFamily = newEnum("columnFamily");
    public SchemaProperties schema = new SchemaProperties("schema");

}
