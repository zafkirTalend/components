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

    private Boolean hasOutputSchema = false;

    private Boolean hasRejectSchema = false;

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

        if (hasOutputSchema) {
            context.output(inputRecord);
        } else {
            if (hasRejectSchema) {
                context.sideOutput(NormalizeRuntime.rejectOutput, inputRecord);
            }
        }
    }

    public NormalizeDoFn withOutputSchema(boolean hasSchema) {
        hasOutputSchema = hasSchema;
        return this;
    }

    public NormalizeDoFn withRejectSchema(boolean hasSchema) {
        hasRejectSchema = hasSchema;
        return this;
    }

    public NormalizeDoFn withProperties(NormalizeProperties properties) {
        this.properties = properties;
        return this;
    }
}
