package org.talend.components.cassandra;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;

import com.datastax.driver.core.Row;

/**
 * Created by bchen on 16-1-15.
 */
public class CassandraAvroAdapter {

    public Schema schema;

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema s) {
        schema = s;
    }

    public IndexedRecord convert(Row r) {
        return null;
    }
}
