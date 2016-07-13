package org.talend.components.cassandra.beam;

import org.apache.beam.sdk.io.cassandra.CassandraConfig;
import org.apache.beam.sdk.io.cassandra.CassandraIO;
import org.apache.beam.sdk.io.cassandra.CassandraRow;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.talend.bigdata.BeamIOGenrator;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.cassandra.input.TCassandraInputProperties;
import org.talend.components.cassandra.output.TCassandraOutputProperties;

import java.util.HashMap;
import java.util.Map;

public class CassandraIOGenerator implements BeamIOGenrator<CassandraRow, CassandraRow> {

    public PTransform<PBegin, PCollection<CassandraRow>> genInput(ComponentProperties
                                                                          properties) {
        TCassandraInputProperties props = (TCassandraInputProperties)properties;

        Map<String, String> config = new HashMap<>();
        config.put(CassandraConfig.PORT, props.getConnectionProperties().port.getValue());

        return CassandraIO.read().withHost(props.getConnectionProperties().host.getValue())
                .withKeyspace(props.getSchemaProperties().keyspace.getValue()).withTable
                        (props.getSchemaProperties().columnFamily.getValue()).withConfig(config);
    }

    public PTransform<PCollection<CassandraRow>, PDone> genOutput(ComponentProperties
                                                                          properties){
        TCassandraOutputProperties props = (TCassandraOutputProperties)properties;

        Map<String, String> config = new HashMap<>();
        config.put(CassandraConfig.PORT, props.getConnectionProperties().port.getValue());


        return CassandraIO.write().withHost(props.getConnectionProperties().host.getValue())
                .withKeyspace(props.getSchemaProperties().keyspace.getValue())
                .withTable(props.getSchemaProperties().columnFamily.getValue()).withConfig(config);
    }

}
