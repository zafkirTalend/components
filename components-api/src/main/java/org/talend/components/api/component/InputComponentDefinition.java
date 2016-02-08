package org.talend.components.api.component;

import org.talend.components.api.runtime.input.Source;
import org.talend.daikon.schema.type.TypesRegistry;

public interface InputComponentDefinition {

    public Source getInputRuntime();

    public TypesRegistry getTypesRegistry();

}
