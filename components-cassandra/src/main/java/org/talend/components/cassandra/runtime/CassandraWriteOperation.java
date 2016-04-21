package org.talend.components.cassandra.runtime;

import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.component.runtime.WriterResult;
import org.talend.components.api.container.RuntimeContainer;

public class CassandraWriteOperation implements WriteOperation<WriterResult> {
    private CassandraSink cassandraSink;
    public CassandraWriteOperation(CassandraSink cassandraSink) {
        this.cassandraSink = cassandraSink;
    }

    @Override
    public void initialize(RuntimeContainer container) {

    }

    @Override
    public Writer<WriterResult> createWriter(RuntimeContainer container) {
        return new CassandraWriter(this, container);
    }

    @Override
    public Sink getSink() {
        return cassandraSink;
    }

    @Override
    public void finalize(Iterable<WriterResult> writerResults, RuntimeContainer container) {

    }
}
