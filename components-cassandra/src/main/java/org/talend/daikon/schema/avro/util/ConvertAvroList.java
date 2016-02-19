package org.talend.daikon.schema.avro.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.avro.Schema;
import org.talend.daikon.schema.type.AvroConverter;

/**
 * Provides a wrapper around {@link List} of input data to view them as Avro data.
 * 
 * @param <DatumT> The specific elements of the input list.
 * @param <AvroT> The Avro-compatible type that the elements should be viewed as.
 */
public class ConvertAvroList<DatumT, AvroT> implements HasNestedAvroConverter<List<DatumT>, List<AvroT>> {

    /**
     * The Avro Schema corresponding to this list. This should be an Schema.Type.ARRAY.
     */
    private Schema mSchema;

    /** The specific list class that this convert will wrap. */
    private Class<List<DatumT>> mDatumClass;

    /** An AvroConverter to and from the element values. */
    private final AvroConverter<DatumT, AvroT> mElementConverter;

    public ConvertAvroList(Class<List<DatumT>> datumClass, Schema schema, AvroConverter<DatumT, AvroT> elementConverter) {
        mDatumClass = datumClass;
        mSchema = schema;
        mElementConverter = elementConverter;
    }

    @Override
    public Schema getSchema() {
        return mSchema;
    }

    @Override
    public Class<List<DatumT>> getDatumClass() {
        return mDatumClass;
    }

    @Override
    public List<DatumT> convertToDatum(List<AvroT> value) {
        return Collections
                .unmodifiableList(new MappedList<>(value, mElementConverter::convertToDatum, mElementConverter::convertToAvro));
    }

    @Override
    public List<AvroT> convertToAvro(List<DatumT> value) {
        return Collections
                .unmodifiableList(new MappedList<>(value, mElementConverter::convertToAvro, mElementConverter::convertToDatum));
    }

    @Override
    public Iterable<AvroConverter<?, ?>> getNestedAvroConverters() {
        return Arrays.asList(mElementConverter);
    }
}
