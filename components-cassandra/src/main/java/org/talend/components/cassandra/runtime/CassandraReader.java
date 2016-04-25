package org.talend.components.cassandra.runtime;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import org.talend.components.api.component.runtime.AbstractBoundedReader;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.cassandra.input.TCassandraInputProperties;

import java.io.IOException;
import java.util.NoSuchElementException;

public class CassandraReader extends AbstractBoundedReader<Row> {

    private TCassandraInputProperties properties;

    private transient ResultSet rs;

    private transient Row current;

    protected CassandraReader(RuntimeContainer container, CassandraSource source, TCassandraInputProperties properties) {
        super(container, source);
        this.properties = properties;
    }

    @Override
    public boolean start() throws IOException {
        if (rs == null) {
            Session session = ((CassandraSource) getCurrentSource()).connect(container);
            String keyspace = properties.getSchemaProperties().keyspace.getStringValue();
            if (keyspace != null) {
                session.execute("USE " + keyspace);
            }
            rs = session.execute(properties.query.getStringValue());
        }
        current = rs.one();
        return current != null;
    }

    @Override
    public boolean advance() throws IOException {
        return start();
    }

    @Override
    public Row getCurrent() throws NoSuchElementException {
        return current;
    }
}
