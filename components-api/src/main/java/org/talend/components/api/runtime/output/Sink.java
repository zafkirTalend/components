package org.talend.components.api.runtime.output;

import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.schema.SchemaElement;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bchen on 16-1-17.
 */
public interface Sink extends Serializable {
    public void init(ComponentProperties properties) throws TalendConnectionException;

    public void close();

    public Writer getRecordWriter();

    /**
     * do something like create folder/create table
     */
    public void initDest();

    public List<SchemaElement> getSchema();
}
