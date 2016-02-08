package org.talend.components.api.runtime.metadata;

import java.util.List;

import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.NamedThing;

public interface Metadata {

    public void initSchema(ComponentProperties properties) throws TalendConnectionException;

    public void initSchemaForDynamic(ComponentProperties properties) throws TalendConnectionException;

    public void initSchemaForDynamicWithFirstRow(ComponentProperties properties, Object firstRow)
            throws TalendConnectionException;

    public List<NamedThing> getSchemasName(ComponentProperties properties) throws TalendConnectionException;
}
