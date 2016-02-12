package org.talend.components.api.runtime.output;

import java.io.Closeable;
import java.io.Serializable;

/**
 * A simplified interface for output sources.
 */
public interface Output extends Serializable, Closeable {

    public void setup();

    public void emit(Object obj);
}