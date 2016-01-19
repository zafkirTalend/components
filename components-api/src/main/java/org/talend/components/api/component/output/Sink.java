package org.talend.components.api.component.output;

import org.talend.components.api.properties.ComponentProperties;

import java.io.Serializable;

/**
 * Created by bchen on 16-1-17.
 */
public interface Sink extends Serializable {
    public void init(ComponentProperties properties);

    public void close();

    public Writer getRecordWriter();

    /**
     * do something like create folder/create table
     */
    public void initDest();
}
