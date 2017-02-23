package org.talend.components.file.runtime;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.file.fileoutputdefinition.FileOutputProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;

public class FileSink implements Sink {

    private static final long serialVersionUID = 1L;

    private FileOutputProperties properties;

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        return null;
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
        return null;
    }

    @Override
    public ValidationResult validate(RuntimeContainer container) {
        ValidationResult validate = ValidationResult.OK;
        String fileName = (String) properties.filename.getValue();

        File fileToWrite = new File(fileName);

        try {
            if (!fileToWrite.exists()) {
                fileToWrite.mkdirs();
                fileToWrite.createNewFile();
            }

            if (!fileToWrite.canWrite()) {
                throw new UnsupportedOperationException("Can not write to the file " + fileName);
            }

        } catch (Exception e) {
            validate.setStatus(Result.ERROR);
            validate.setMessage(e.getMessage());
        }
        return validate;
    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        this.properties = (FileOutputProperties) properties;
        return ValidationResult.OK;
    }

    @Override
    public WriteOperation<?> createWriteOperation() {
        return new FileWriteOperation(this, properties);
    }

}
