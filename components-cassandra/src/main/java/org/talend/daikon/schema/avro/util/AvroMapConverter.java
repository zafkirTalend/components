package org.talend.daikon.schema.avro.util;

import java.util.Collections;
import java.util.Map;

import org.apache.avro.Schema;
import org.talend.daikon.schema.type.AvroConverter;

/**
 * Provides a wrapper around {@link Map} of specific input data to view them as Avro data.
 * 
 * @param <DatumT> The specific elements of the input list.
 * @param <AvroT> The Avro-compatible type that the elements should be seen as.
 */
public class AvroMapConverter<DatumT, AvroT> implements AvroConverter<Map<?, DatumT>, Map<?, AvroT>> {

    private Schema mSchema;

    private Class<Map<?, DatumT>> mDatumClass;

    private final AvroConverter<DatumT, AvroT> mElementConverter;

    public AvroMapConverter(Class<Map<?, DatumT>> datumClass, Schema schema, AvroConverter<DatumT, AvroT> elementConverter) {
        mDatumClass = datumClass;
        mSchema = schema;
        mElementConverter = elementConverter;
    }

    @Override
    public Schema getSchema() {
        return mSchema;
    }

    @Override
    public Class<Map<?, DatumT>> getSpecificClass() {
        return mDatumClass;
    }

    @Override
    public Map<?, DatumT> convertFromAvro(Map<?, AvroT> value) {
        return Collections.unmodifiableMap(new MappedValueMap<>(value, mElementConverter::convertFromAvro,
                mElementConverter::convertToAvro));
    }

    @Override
    public Map<?, AvroT> convertToAvro(Map<?, DatumT> value) {
        return Collections.unmodifiableMap(new MappedValueMap<>(value, mElementConverter::convertToAvro,
                mElementConverter::convertFromAvro));
    }

}
