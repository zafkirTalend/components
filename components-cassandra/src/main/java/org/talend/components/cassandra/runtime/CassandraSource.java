package org.talend.components.cassandra.runtime;

import org.apache.beam.sdk.io.cassandra.CassandraRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.bigdata.Coder;
import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.cassandra.input.TCassandraInputProperties;
import org.talend.daikon.properties.Properties;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CassandraSource extends CassandraSourceOrSink implements BoundedSource, Coder {

    private static final Logger LOG = LoggerFactory.getLogger(CassandraSource.class);

    public CassandraSource(){}

    @Override
    public List<? extends BoundedSource> splitIntoBundles(long desiredBundleSizeBytes, RuntimeContainer container) throws Exception {
        List<BoundedSource> list = new ArrayList<>();
        list.add(this);
        return list;
    }

    @Override
    public long getEstimatedSizeBytes(RuntimeContainer container) {
        return 0;
    }

    @Override
    public boolean producesSortedKeys(RuntimeContainer container) {
        return false;
    }

    @Override
    public BoundedReader createReader(RuntimeContainer container) {
        return new CassandraReader(container, this, (TCassandraInputProperties)properties);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeUTF(((ComponentProperties)properties).toSerialized());
    }

    private void readObject(ObjectInputStream in) throws IOException {
        properties = (TCassandraInputProperties)Properties.Helper.fromSerializedPersistent(in
                .readUTF(),
                TCassandraInputProperties.class).object;
    }

    @Override
    public Class getSerializableClass() {
        return CassandraRow.class;
    }
}
