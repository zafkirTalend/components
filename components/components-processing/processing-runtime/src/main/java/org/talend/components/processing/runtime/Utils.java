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
package org.talend.components.processing.runtime;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;

public class Utils {

    public static List<Object> getInputFields(IndexedRecord inputRecord, String columnName) {
        // TODO current implementation will only extract one element, but
        // further implementation may
        ArrayList<Object> inputFields = new ArrayList<Object>();
        String[] path = columnName.split("\\.");
        Schema schema = inputRecord.getSchema();

        for (Integer i = 0; i < path.length; i++) {
            // The column was existing on the input record, we forward it to the
            // output record.
            if (schema.getField(path[i]) == null) {
                throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_ARGUMENT,
                        new Throwable(String.format("The field %s is not present on the input record", columnName)));
            }
            Object inputValue = inputRecord.get(schema.getField(path[i]).pos());

            // The current column can be a Record (an hierarchical sub-object)
            // or directly a value.
            if (inputValue instanceof GenericData.Record) {
                // If we are on a record, we need to recursively do the process
                inputRecord = (IndexedRecord) inputValue;

                // The sub-schema at this level is a union of "empty" and a
                // record, so we need to get the true
                // sub-schema
                if (schema.getField(path[i]).schema().getType().equals(Schema.Type.RECORD)) {
                    schema = schema.getField(path[i]).schema();
                } else if (schema.getField(path[i]).schema().getType().equals(Schema.Type.UNION)) {
                    for (Schema childSchema : schema.getField(path[i]).schema().getTypes()) {
                        if (childSchema.getType().equals(Schema.Type.RECORD)) {
                            schema = childSchema;
                            break;
                        }
                    }
                }
            } else if (inputValue instanceof List) {
                for (int j = 0; j < ((List) inputValue).size(); j++) {
                    inputFields.add(((List) inputValue).get(j));
                }
                break;
            } else {
                // if we are on a object, then this is or the expected value of
                // an error.
                if (i == path.length - 1) {
                    inputFields.add(inputValue);
                }
                /*
                 * else {
                 * // No need to go further, return an empty list
                 * break;
                 * }
                 */
            }
        }

        return inputFields;
    }
}
