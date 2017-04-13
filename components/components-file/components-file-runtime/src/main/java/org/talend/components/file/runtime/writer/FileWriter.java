/*******************************************************************************
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
 ******************************************************************************/
package org.talend.components.file.runtime.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.file.avro.DelimitedStringConverter;
import org.talend.components.file.tfileoutput.FileOutputProperties;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;


public class FileWriter implements Writer<Result> {

    private final WriteOperation<Result> writeOperation;

    private final FileOutputProperties outputProperties;

    private final RuntimeContainer adaptor;

    private Result result;

    private OutputStream os;

    private DelimitedStringConverter converter;

    public FileWriter(WriteOperation<Result> writeOperation,
            ComponentProperties outputProperties, RuntimeContainer adaptor) {
        this.writeOperation = writeOperation;
        this.outputProperties = (FileOutputProperties) outputProperties;
        this.adaptor = adaptor;
    }

    @Override
    public void open(String uId) throws IOException {
        this.result = new Result(uId);
        converter = new DelimitedStringConverter(outputProperties.schema.schema.getValue(),
                outputProperties.delimiter.getValue().getDelimiter());
        File file = new File(outputProperties.fileToSave.getValue());
        file.getParentFile().mkdirs();
        os = new FileOutputStream(file);
    }

    @Override
    public void write(Object object) throws IOException {
        try {
            if (object instanceof IndexedRecord) {
                IndexedRecord indexedRecord = (IndexedRecord) object;
                String record = converter.convertToDatum(indexedRecord);
                record += outputProperties.lineSeparator.getValue().getLineSeparator();
                byte[] bytes = record.getBytes();
                os.write(bytes);
                result.successCount++;
            } else {
                result.rejectCount++;
                throw new TalendRuntimeException(CommonErrorCodes.UNABLE_TO_READ_CONTENT);
            }
        } finally {
            result.totalCount++;
        }
    }

    @Override
    public Result close() throws IOException {
        try {
            os.flush();
            os.close();
        } finally {
        }
        return result;
    }

    @Override
    public WriteOperation<Result> getWriteOperation() {
        return this.writeOperation;
    }

}
