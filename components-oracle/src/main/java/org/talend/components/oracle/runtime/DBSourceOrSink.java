// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.oracle.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.oracle.DBProvideConnectionProperties;
import org.talend.daikon.properties.ValidationResult;

public abstract class DBSourceOrSink implements SourceOrSink {

    private transient static final Logger   LOG = LoggerFactory.getLogger(DBSourceOrSink.class);

    protected DBProvideConnectionProperties properties;

    @Override
    public void initialize(RuntimeContainer adaptor, ComponentProperties properties) {
        this.properties = (DBProvideConnectionProperties) properties;
    }

    public abstract ValidationResult validate(RuntimeContainer adaptor);

    protected static ValidationResult fillValidationResult(ValidationResult vr, Exception ex) {
        if (vr == null) {
            return null;
        }
        vr.setMessage(ex.getMessage());
        vr.setStatus(ValidationResult.Result.ERROR);
        return vr;
    }
    
}
