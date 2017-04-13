// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.file.runtime.writer;

import java.util.Map;

import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.container.RuntimeContainer;

public class FileWriteOperation implements WriteOperation<Result> {

    private static final long serialVersionUID = 1L;

    private final FileOutputSink sink;
    
    
    public FileWriteOperation(Sink sink) {
        this.sink = (FileOutputSink) sink;
    }
    
    @Override
    public void initialize(RuntimeContainer adaptor) {
        
    }

    @Override
    public Map<String, Object> finalize(Iterable<Result> writerResults, RuntimeContainer adaptor) {
        return null;
    }

    @Override
    public Writer<Result> createWriter(RuntimeContainer adaptor) {
        return new FileWriter(this, sink.getComponentProperties(), adaptor);
    }

    @Override
    public Sink getSink() {
        return sink;
    }

}
