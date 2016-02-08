package org.talend.components.cassandra.tCassandraInput;


import org.talend.daikon.properties.Property;

import static org.talend.daikon.properties.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.PropertyFactory.newEnum;
import static org.talend.daikon.properties.PropertyFactory.newLong;
import static org.talend.daikon.properties.PropertyFactory.newString;
import static org.talend.daikon.properties.PropertyFactory.newTable;

/**
 * Created by bchen on 16-1-13.
 */
public class tCassandraInputSparkProperties extends tCassandraInputDIProperties {
    /**
     * named constructor to be used is these properties are nested in other properties. Do not subclass this method for
     * initialization, use {@link #init()} instead.
     *
     * @param name
     */
    public tCassandraInputSparkProperties(String name) {
        super(name);
    }

    public Property useQuery = newBoolean("useQuery");
    //useQuery = true DI api
//    public Property query = newString("query", "select id, name from employee");
    //TODO how to hidden host/port in this component?
    //useQuery = false spark-cassandra-connector api
    public Property columnFunctionTables = newTable("columnFunctionTables", newString("COLUMN"), newEnum("FUNCTION", "TTL", "WRITETIME"));
    public Property filterConditionTables = newTable("filterConditionTables", newString("COLUMN"), newEnum("FUNCTION", "EQ", "LT", "GT", "LE", "GE", "CONTAINS", "CONTAINSKEY", "IN"), newString("VALUE"));
    public Property order = newEnum("order", "NONE", "ASC", "DESC");
    public Property useLimit = newBoolean("useLimit", false);
    public Property limit = newLong("limit", 100l);

}
