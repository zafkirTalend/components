package org.talend.components.cassandra.metadata;

import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.metadata.Metadata;
import org.talend.components.cassandra.CassandraTypeRegistry;
import org.talend.daikon.NamedThing;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnMetadata;
import com.datastax.driver.core.DataType;

/**
 * Created by bchen on 16-1-15.
 */
public class CassandraMetadata implements Metadata {

    CassandraTypeRegistry registry = new CassandraTypeRegistry();

    @Override
    public void initSchema(ComponentProperties properties) {
        CassandraMetadataProperties props = (CassandraMetadataProperties) properties;

        String source = props.columnFamily.getStringValue();

        Cluster cluster = Cluster.builder().addContactPoints(props.host.getStringValue())
                .withPort(Integer.valueOf(props.port.getStringValue())).build();
        List<ColumnMetadata> columns = cluster.getMetadata().getKeyspace(props.keyspace.getStringValue()).getTable(source)
                .getColumns();

        // Generate a schema from the columns in the column family.
        FieldAssembler<Schema> fa = SchemaBuilder.record(source + "Record").fields();
        for (ColumnMetadata column : columns) {
            DataType type = column.getType();
            fa = fa.name(column.getName()).type(registry.getSchema(type)).noDefault();
        }
        props.schema.schema.setValue(fa.endRecord());
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
