package org.talend.components.api.component;

import org.talend.components.api.runtime.output.Sink;
import org.talend.daikon.schema.type.TypesRegistry;

public interface OutputComponentDefinition {

    public Sink getOutputRuntime();

    public TypesRegistry getTypesRegistry();
}
