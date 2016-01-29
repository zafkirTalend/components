package org.talend.components.api.runtime.metadata;

import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.NameAndLabel;

import java.util.List;

/**
 * Created by bchen on 16-1-18.
 */
public interface Metadata {
    public void initSchema(ComponentProperties properties) throws TalendConnectionException;

    public void initSchemaForDynamic(ComponentProperties properties) throws TalendConnectionException;

    public void initSchemaForDynamicWithFirstRow(ComponentProperties properties, Object firstRow) throws TalendConnectionException;

    public List<NameAndLabel> getSchemasName(ComponentProperties properties) throws TalendConnectionException;
}
