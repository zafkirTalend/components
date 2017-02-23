package org.talend.components.file.runtime;

import java.util.Map;

import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.file.fileoutputdefinition.FileOutputProperties;

public class FileWriteOperation implements WriteOperation<Result> {

    private static final long serialVersionUID = 1L;

    private FileSink fsink;

    private FileOutputProperties properties;

    public FileWriteOperation(FileSink fsink, FileOutputProperties properties) {
        this.fsink = fsink;
        this.properties = properties;
    }

    @Override
    public void initialize(RuntimeContainer adaptor) {

    }

    @Override
    public Writer<Result> createWriter(RuntimeContainer adaptor) {
        return new FileOutputWriter(this, (String) properties.filename.getValue());
    }

    @Override
    public Map<String, Object> finalize(Iterable<Result> writerResults, RuntimeContainer adaptor) {
        return Result.accumulateAndReturnMap(writerResults);
    }

    @Override
    public Sink getSink() {
        return fsink;
    }

}
