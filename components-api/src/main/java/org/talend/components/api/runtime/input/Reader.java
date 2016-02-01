package org.talend.components.api.runtime.input;

import org.talend.components.api.schema.SchemaElement;

import java.util.List;

/**
 * Created by bchen on 16-1-13.
 */
public interface Reader<T> {

    public boolean start();

    public boolean advance();

    public T getCurrent();

    public void close();

    public List<SchemaElement> getSchema();
}
