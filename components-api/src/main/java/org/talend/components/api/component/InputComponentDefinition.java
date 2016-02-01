package org.talend.components.api.component;

import org.talend.components.api.runtime.input.Source;
import org.talend.components.api.schema.column.type.TypesRegistry;

/**
 * Created by bchen on 16-1-29.
 */
public interface InputComponentDefinition {

    public Source getInputRuntime();

    public TypesRegistry getTypesRegistry();

}
