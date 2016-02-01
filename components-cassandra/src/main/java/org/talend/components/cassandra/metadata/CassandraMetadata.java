package org.talend.components.cassandra.metadata;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnMetadata;
import com.datastax.driver.core.DataType;
import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.properties.NameAndLabel;
import org.talend.components.api.runtime.metadata.Metadata;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.schema.SchemaFactory;
import org.talend.components.cassandra.type.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.datastax.driver.core.DataType.Name.*;

/**
 * Created by bchen on 16-1-15.
 */
public class CassandraMetadata implements Metadata {
    private static Map<DataType.Name, Class<? extends CassandraBaseType>> mapping = new HashMap<>();

    static {
        mapping.put(ASCII, Cassandra_ASCII.class);
        mapping.put(BIGINT, Cassandra_BIGINT.class);
        mapping.put(BLOB, Cassandra_BLOB.class);
        mapping.put(BOOLEAN, Cassandra_BOOLEAN.class);
        mapping.put(COUNTER, Cassandra_COUNTER.class);
        mapping.put(DECIMAL, Cassandra_DECIMAL.class);
        mapping.put(DOUBLE, Cassandra_DOUBLE.class);
        mapping.put(FLOAT, Cassandra_FLOAT.class);
        mapping.put(INET, Cassandra_INET.class);
        mapping.put(INT, Cassandra_INT.class);
        mapping.put(LIST, Cassandra_LIST.class);
        mapping.put(MAP, Cassandra_MAP.class);
        mapping.put(SET, Cassandra_SET.class);
        mapping.put(TEXT, Cassandra_TEXT.class);
        mapping.put(TIMESTAMP, Cassandra_TIMESTAMP.class);
        mapping.put(TIMEUUID, Cassandra_TIMEUUID.class);
        mapping.put(UUID, Cassandra_UUID.class);
        mapping.put(VARCHAR, Cassandra_VARCHAR.class);
        mapping.put(VARINT, Cassandra_VARINT.class);
    }

    @Override
    public void initSchema(ComponentProperties properties) {
        CassandraMetadataProperties props = (CassandraMetadataProperties) properties;

        Cluster cluster = Cluster.builder().addContactPoints(props.host.getStringValue()).withPort(Integer.valueOf(props.port.getStringValue())).build();
        List<ColumnMetadata> columns = cluster.getMetadata().getKeyspace(props.keyspace.getStringValue()).getTable(props.columnFamily.getStringValue()).getColumns();
        for (ColumnMetadata column : columns) {
            DataType type = column.getType();
            props.schema.addSchemaChild(SchemaFactory.newDataSchemaElement(CassandraBaseType.FAMILY_NAME, column.getName(), mapping.get(type.getName())));
        }
    }

    @Override
    public void initSchemaForDynamic(ComponentProperties properties) throws TalendConnectionException {

    }

    @Override
    public void initSchemaForDynamicWithFirstRow(ComponentProperties properties, Object firstRow) throws TalendConnectionException {

    }

    @Override
    public List<NameAndLabel> getSchemasName(ComponentProperties properties) throws TalendConnectionException {
        return null;
    }
}
