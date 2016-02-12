package org.talend.components.cassandra.runtime;

import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.input.Reader;
import org.talend.components.api.runtime.input.Source;
import org.talend.components.api.runtime.input.Split;
import org.talend.components.cassandra.CassandraAvroRegistry;
import org.talend.components.cassandra.mako.tCassandraInputDIProperties;

import com.datastax.driver.core.Row;

/**
 * Created by bchen on 16-1-10.
 */
public class CassandraSource implements Source<Row> {

    private CassandraUnshardedInput input;

    @Override
    public void init(ComponentProperties properties) throws TalendConnectionException {
        input = new CassandraUnshardedInput((tCassandraInputDIProperties) properties);
    }

    @Override
    public void close() {
        input.close();
    }

    @Override
    public Reader<Row> getRecordReader(Split split) {
        return new CassandraReader();
    }

    @Override
    public boolean supportSplit() {
        return false;
    }

    @Override
    public Split[] getSplit(int num) {
        return new Split[0];
    }

    @Override
    public String getFamilyName() {
        return CassandraAvroRegistry.FAMILY_NAME;
    }

    public class CassandraReader implements Reader<Row> {

        private Row row;

        @Override
        public boolean start() {
            input.setup();
            return input.hasNext();
        }

        @Override
        public boolean advance() {
            if (!input.hasNext())
                return false;
            row = input.next();
            return true;
        }

        @Override
        public Row getCurrent() {
            return row;
        }

        @Override
        public void close() {
        }
    }

}
