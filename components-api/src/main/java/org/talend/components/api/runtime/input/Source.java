package org.talend.components.api.runtime.input;

import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.properties.ComponentProperties;

/**
 * Created by bchen on 16-1-10.
 */
public interface Source<T> {

    /**
     * this method in-charge to open resource
     *
     * @param properties
     */
    public void init(ComponentProperties properties) throws TalendConnectionException;

    public void close();

    public Reader<T> getRecordReader(Split split) throws TalendConnectionException;

    public boolean supportSplit();

    public Split[] getSplit(int num);

    public String getFamilyName();

}
