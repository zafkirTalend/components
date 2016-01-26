package org.talend.components.bd.api.component;

import org.talend.components.api.runtime.IDIImplement;

/**
 * Created by bchen on 16-1-25.
 */
public interface IBDImplement extends IDIImplement {
    public String getImplementClassName(BDType type);
}
