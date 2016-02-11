package org.talend.components.api.runtime;

import org.talend.components.api.runtime.input.Source;
import org.talend.components.api.runtime.output.Sink;

/**
 * Created by bchen on 16-1-25.
 */
public interface IDIImplement {

    // TODO getSourceClass/getSinkClass interface for all components? Input/Output component only? Properties or
    // Definition?
    public Class<? extends Source> getSourceClass();

    public Class<? extends Sink> getSinkClass();
}
