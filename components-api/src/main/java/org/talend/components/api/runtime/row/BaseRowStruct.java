package org.talend.components.api.runtime.row;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;

/**
 * Created by bchen on 16-1-10.
 */
public class BaseRowStruct {

    private Map<String, Object> content = new HashMap<>();

    private Schema mSchema;

    public BaseRowStruct() {
    }

    public void setSchema(Schema schema) {
        mSchema = schema;
    }

    public Schema getType(String key) {
        return mSchema.getField(key).schema();
    }

    public Object get(String key) {
        return content.get(key);
    }

    public void put(String key, Object value) {
        // if (metadata.get(key).equals(value)) {
        content.put(key, value);
        // } else {
        // // FIXME use talend exception
        // throw new RuntimeException("unsupport set " + value.getClass() + " type to " + metadata.get(key));
        // }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (String k : content.keySet()) {
            s.append(k);
            s.append(":");
            s.append(content.get(k));
            s.append("\n");
        }
        return s.toString();
    }
}
