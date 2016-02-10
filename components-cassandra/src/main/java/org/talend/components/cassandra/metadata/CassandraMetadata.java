package org.talend.components.cassandra.metadata;

import java.util.List;

import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.metadata.Metadata;
import org.talend.components.cassandra.CassandraProperties;
import org.talend.components.cassandra.type.CassandraTalendTypesRegistry;
import org.talend.daikon.NamedThing;
import org.talend.daikon.schema.SchemaFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnMetadata;
import com.datastax.driver.core.DataType;

/**
 * Created by bchen on 16-1-15.
 */
public class CassandraMetadata implements Metadata {

    CassandraTalendTypesRegistry registry = new CassandraTalendTypesRegistry();

    @Override
    public void initSchema(ComponentProperties properties) {
        CassandraMetadataProperties props = (CassandraMetadataProperties) properties;

        Cluster cluster = Cluster.builder().addContactPoints(props.host.getStringValue())
                .withPort(Integer.valueOf(props.port.getStringValue())).build();
        List<ColumnMetadata> columns = cluster.getMetadata().getKeyspace(props.keyspace.getStringValue())
                .getTable(props.columnFamily.getStringValue()).getColumns();
        for (ColumnMetadata column : columns) {
            DataType type = column.getType();
            props.schema.addSchemaChild(SchemaFactory.newDataSchemaElement(CassandraProperties.FAMILY_NAME, column.getName(),
                    registry.getConverter().get(type.getName())));
        }
    }

    @Override
    public void initSchemaForDynamic(ComponentProperties properties) throws TalendConnectionException {

    }

    @Override
    public void initSchemaForDynamicWithFirstRow(ComponentProperties properties, Object firstRow)
            throws TalendConnectionException {

    }

    @Override
    public List<NamedThing> getSchemasName(ComponentProperties properties) throws TalendConnectionException {
        return null;
    }
}
