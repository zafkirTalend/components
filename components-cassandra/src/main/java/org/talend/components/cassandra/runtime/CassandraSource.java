package org.talend.components.cassandra.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.container.RuntimeContainer;

import java.util.ArrayList;
import java.util.List;

public class CassandraSource extends CassandraSourceOrSink implements BoundedSource {

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
        return null;
    }
}
