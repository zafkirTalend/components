
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
package org.talend.components.file.runtime;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.avro.Schema.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.file.fileoutputdefinition.FileOutputDefinition;

/**
 * Simple implementation of a writer.
 */
public class FileOutputWriter implements Writer<Result> {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileOutputDefinition.class);

    private final String fileName;

    private final String fieldSeparator;

    private String uId;

    private BufferedWriter writer = null;

    private int totalCount;

    private int successCount;

    private int rejectCount;

    private final FileWriteOperation fileWriteOperation;

    public FileOutputWriter(FileWriteOperation fileWriteOperation, String fileName, String fieldSeparator) {
        this.fileWriteOperation = fileWriteOperation;
        this.fileName = fileName;
        this.fieldSeparator = fieldSeparator;
        totalCount = 0;
        successCount = 0;
        rejectCount = 0;
    }

    @Override
    public void open(String uId) throws IOException {
        this.uId = uId;
        this.writer = new BufferedWriter(new FileWriter(fileName));

    }

    @Override
    public void write(Object datum) throws IOException {
        if (null == datum)
            return;
        org.apache.avro.generic.GenericData.Record record = (org.apache.avro.generic.GenericData.Record) datum;

        String line = "";
        boolean first = true;
        for (Field f : record.getSchema().getFields()) {
            if (!first) {
                line += fieldSeparator;
            } else {
                first = false;
            }

            line += record.get(f.pos());

        }

        totalCount++;
        try {
            writer.write(line);
            writer.newLine();
            successCount++;
            writer.flush();
        } catch (IOException e) {
            LOGGER.error("Cannot write the line " + line, e);
            rejectCount++;
        }

    }

    @Override
    public Result close() throws IOException {
        writer.flush();
        writer.close();
        return new Result(uId, totalCount, successCount, rejectCount);
    }

    @Override
    public WriteOperation<Result> getWriteOperation() {
        return fileWriteOperation;
    }

}
