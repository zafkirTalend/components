package org.talend.components.cassandra;

import org.talend.components.api.runtime.input.Source;
import org.talend.components.api.runtime.output.Sink;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.bd.api.component.BDImplement;
import org.talend.components.bd.api.component.BDType;
import org.talend.components.bd.api.component.IBDImplement;
import org.talend.components.cassandra.tCassandraInput.CassandraSource;
import org.talend.components.cassandra.tCassandraInput.spark.CassandraInputSparkConf;
import org.talend.components.cassandra.tCassandraOutput.CassandraSink;

/**
 * Created by bchen on 16-1-25.
 */
public class CassandraProperties extends ComponentProperties implements IBDImplement {
    /**
     * named constructor to be used is these properties are nested in other properties. Do not subclass this method for
     * initialization, use {@link #init()} instead.
     *
     * @param name
     */
    public CassandraProperties(String name) {
        super(name);
    }

    //TODO this method should be in XXXDefinition?
    @Override
    public String getImplementClassName(BDType type) {
        if (type == BDType.SparkInputConf) {
            return CassandraInputSparkConf.class.getName();
        }
        return BDImplement.getImplementClassName(type);
    }

    @Override
    public Class<? extends Source> getSourceClass() {
        return CassandraSource.class;
    }

    @Override
    public Class<? extends Sink> getSinkClass() {
        return CassandraSink.class;
    }
}
