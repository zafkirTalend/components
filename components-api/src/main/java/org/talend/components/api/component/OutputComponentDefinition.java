package org.talend.components.api.component;

import org.talend.components.api.runtime.output.Sink;
import org.talend.components.api.schema.column.type.TypesRegistry;

/**
 * Created by bchen on 16-1-29.
 */
public interface OutputComponentDefinition {
    public Sink getOutputRuntime();

    public TypesRegistry getTypesRegistry();
}
