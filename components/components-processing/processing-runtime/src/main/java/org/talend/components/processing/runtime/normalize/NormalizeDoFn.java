package org.talend.components.processing.runtime.normalize;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.commons.lang3.StringUtils;
import org.talend.components.processing.filterrow.ConditionsRowConstant;
import org.talend.components.processing.normalize.NormalizeProperties;
import org.talend.daikon.avro.AvroRegistry;
import org.talend.daikon.avro.converter.IndexedRecordConverter;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zafkir on 19/06/2017.
 */
public class NormalizeDoFn extends DoFn<Object, IndexedRecord> {

    private NormalizeProperties properties = null;

    private IndexedRecordConverter converter = null;

    @Setup
    public void setup() throws Exception {
    }

    @ProcessElement
    public void processElement(ProcessContext context) {
        if (converter == null) {
            AvroRegistry registry = new AvroRegistry();
            converter = registry.createIndexedRecordConverter(context.element().getClass());
        }
        IndexedRecord inputRecord = (IndexedRecord) converter.convertToAvro(context.element());
        Schema schema = inputRecord.getSchema();

        String columnToNormalize = properties.columnName.getValue();
        //boolean useCSV = properties.csvOption.getValue();
        String delim = properties.fieldSeparator.getValue();
        boolean discardTrailingEmptyStr = properties.discardTrailingEmptyStr.getValue();
        boolean trim = properties.trim.getValue();

        if (!StringUtils.isEmpty(columnToNormalize)) {

            int indexColumnToNormalize = inputRecord.getSchema().getField(columnToNormalize).pos();

            List<Object> inputValues = getInputFields(inputRecord, columnToNormalize);
            if(inputValues.size() == 1) {
                inputRecord.put(indexColumnToNormalize, delimit(inputValues.get(0), delim, discardTrailingEmptyStr, trim));
            }
        }
        System.out.println(inputRecord);
        context.output(inputRecord);
    }

    private String[] delimit(Object obj, String delim, boolean discardTrailingEmptyStr, boolean trim) {
        String str = (String) obj;
        String[] strDelimited = str.split(delim);
        for(int i=0; i<strDelimited.length; i++) {
            if(discardTrailingEmptyStr) {
                strDelimited[i].replaceAll("\\s+$", "");
            }
            if(trim) {
                strDelimited[i].trim();
            }
        }
        return strDelimited;
    }

    public NormalizeDoFn withProperties(NormalizeProperties properties) {
        this.properties = properties;
        return this;
    }

    private String[] getInputFields(IndexedRecord inputRecord, String columnToNormalize, String delim) {
        int indexColumnToNormalize = inputRecord.getSchema().getField(columnToNormalize).pos();
        Object inputValue = inputRecord.get(indexColumnToNormalize);
        return inputValue.toString().split(delim);
    }

    private List<Object> getInputFields(IndexedRecord inputRecord, String columnName) {
        // TODO current implementation will only extract one element, but
        // further implementation may
        ArrayList<Object> inputFields = new ArrayList<Object>();
        String[] path = columnName.split("\\.");
        Schema schema = inputRecord.getSchema();

        for (Integer i = 0; i < path.length; i++) {
            // The column was existing on the input record, we forward it to the
            // output record.
            if (schema.getField(path[i]) == null) {
                throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_ARGUMENT, new Throwable(String.format("The field %s is not present on the input record", columnName)));
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
            } else {
                // if we are on a object, then this is or the expected value of
                // an error.
                if (i == path.length - 1) {
                    inputFields.add(inputValue);
                } else {
                    // No need to go further, return an empty list
                    break;
                }
            }
        }

        return inputFields;
    }
}
