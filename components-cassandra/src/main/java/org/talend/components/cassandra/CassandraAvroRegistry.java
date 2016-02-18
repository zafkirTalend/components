package org.talend.components.cassandra;

import static org.talend.daikon.schema.avro.util.AvroUtils.unwrapIfNullable;
import static org.talend.daikon.schema.avro.util.AvroUtils.wrapAsNullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.talend.daikon.schema.avro.AvroRegistry;
import org.talend.daikon.schema.avro.util.AvroListConverter;
import org.talend.daikon.schema.avro.util.AvroMapConverter;
import org.talend.daikon.schema.type.AvroConverter;
import org.talend.daikon.schema.type.ContainerReaderByIndex;
import org.talend.daikon.schema.type.ContainerWriterByIndex;
import org.talend.daikon.schema.type.DatumRegistry;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ColumnDefinitions.Definition;
import com.datastax.driver.core.ColumnMetadata;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.SettableByIndexData;
import com.datastax.driver.core.TableMetadata;
import com.datastax.driver.core.TupleType;
import com.datastax.driver.core.TupleValue;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import com.google.common.collect.ImmutableMap;

/**
 * https://docs.datastax.com/en/latest-java-driver/java-driver/reference/ javaClass2Cql3Datatypes.html
 */
public class CassandraAvroRegistry extends AvroRegistry {

    public static final String FAMILY_NAME = "Cassandra";

    public static final String PROP_CQL_CREATE = FAMILY_NAME.toLowerCase() + ".cql.create";

    public static final String PROP_CASSANDRA_DATATYPE_NAME = FAMILY_NAME.toLowerCase() + ".datatype.name";

    public static final String TUPLE_RECORD_NAME = "TupleRecord";

    public static final String TUPLE_FIELD_PREFIX = "field";

    public String getFamilyName() {
        return FAMILY_NAME;
    }

    private static final CassandraAvroRegistry INSTANCE = new CassandraAvroRegistry();

    /**
     * The sCassandraNativeTypes contains the DataType.Name from Cassandra where we can infer the Object type without
     * any additional information.
     */
    private final Map<DataType.Name, Class<?>> sCassandraNativeTypes;

    /** Helper adapters for reading data from Cassandra Row. */
    private final Map<DataType.Name, ContainerReaderByIndex<GettableByIndexData, ?>> sReaders;

    /** Helper adapters for writing data in Cassandra BoundStatements. */
    private final Map<DataType.Name, ContainerWriterByIndex<SettableByIndexData<?>, ?>> sWriters;

    private CassandraAvroRegistry() {
        // Ensure that other components know how to process Cassandra objects.
        DatumRegistry.registerFacadeFactory(Row.class, RowFacadeFactory.class);
        DatumRegistry.registerFacadeFactory(UDTValue.class, UDTValueFacadeFactory.class);
        DatumRegistry.registerFacadeFactory(TupleValue.class, TupleValueFacadeFactory.class);

        // These are not yet implemented by our version of Cassandra.
        // DataType.Name.DATE // com.datastax.driver.core.LocalDate
        // DataType.Name.SMALLINT // short
        // DataType.Name.TINYINT // byte

        // These require more information from the DataType and can't be found just
        // using the name.
        // DataType.Name.UDT // com.datastax.driver.core.UDTValue
        // DataType.Name.LIST // java.util.List<T>
        // DataType.Name.MAP // java.util.Map<K, V>
        // DataType.Name.SET // java.util.Set<T>
        sCassandraNativeTypes = new ImmutableMap.Builder<DataType.Name, Class<?>>() //
                .put(DataType.Name.ASCII, String.class) //
                .put(DataType.Name.BIGINT, Long.class) //
                .put(DataType.Name.BLOB, ByteBuffer.class) //
                .put(DataType.Name.BOOLEAN, Boolean.class) //
                .put(DataType.Name.COUNTER, Long.class) //
                .put(DataType.Name.DECIMAL, BigDecimal.class) //
                .put(DataType.Name.DOUBLE, Double.class) //
                .put(DataType.Name.FLOAT, Float.class) //
                .put(DataType.Name.INET, InetAddress.class) //
                .put(DataType.Name.INT, Integer.class) //
                .put(DataType.Name.TEXT, String.class) //
                .put(DataType.Name.TIMESTAMP, Date.class) //
                .put(DataType.Name.TIMEUUID, UUID.class) //
                .put(DataType.Name.UUID, UUID.class) //
                .put(DataType.Name.VARCHAR, String.class) //
                .put(DataType.Name.VARINT, BigInteger.class) //
                .build();

        // The following types need more information before they can be directly
        // read from the row.
        // DataType.Name.LIST // java.util.List<T>
        // DataType.Name.MAP // java.util.Map<K, V>
        // DataType.Name.SET // java.util.Set<T>
        sReaders = new ImmutableMap.Builder<DataType.Name, ContainerReaderByIndex<GettableByIndexData, ?>>() //
                .put(DataType.Name.ASCII, (c, i) -> c.getString(i)) //
                .put(DataType.Name.BIGINT, (c, i) -> c.getLong(i)) //
                .put(DataType.Name.BLOB, (c, i) -> c.getBytes(i)) //
                .put(DataType.Name.BOOLEAN, (c, i) -> c.getBool(i)) //
                .put(DataType.Name.COUNTER, (c, i) -> c.getLong(i)) //
                .put(DataType.Name.DECIMAL, (c, i) -> c.getDecimal(i)) //
                .put(DataType.Name.DOUBLE, (c, i) -> c.getDouble(i)) //
                .put(DataType.Name.FLOAT, (c, i) -> c.getFloat(i)) //
                .put(DataType.Name.INET, (c, i) -> c.getInet(i)) //
                .put(DataType.Name.INT, (c, i) -> c.getInt(i)) //
                .put(DataType.Name.TEXT, (c, i) -> c.getString(i)) //
                .put(DataType.Name.TIMESTAMP, (c, i) -> c.getDate(i)) //
                .put(DataType.Name.TIMEUUID, (c, i) -> c.getUUID(i)) //
                .put(DataType.Name.UUID, (c, i) -> c.getUUID(i)) //
                .put(DataType.Name.VARCHAR, (c, i) -> c.getString(i)) //
                .put(DataType.Name.VARINT, (c, i) -> c.getVarint(i)) //
                // .put(DataType.Name.LIST, (c, i) -> c.getList(i, Class or TypeToken))
                // //
                // .put(DataType.Name.MAP, (c, i) -> c.getMap(i, Class or TypeToken,
                // Class or TypeToken)) //
                // .put(DataType.Name.SET, (c, i) -> c.getSet(i, Class or TypeToken)) //
                .put(DataType.Name.TUPLE, (c, i) -> c.getTupleValue(i)) //
                .put(DataType.Name.UDT, (c, i) -> c.getUDTValue(i)) //
                .build();

        // All of the types can be written to a container.
        sWriters = new ImmutableMap.Builder<DataType.Name, ContainerWriterByIndex<SettableByIndexData<?>, ?>>() //
                .put(DataType.Name.ASCII, (SettableByIndexData<?> c, int i, String v) -> c.setString(i, v)) //
                .put(DataType.Name.BIGINT, (SettableByIndexData<?> c, int i, Long v) -> c.setLong(i, v)) //
                .put(DataType.Name.BLOB, (SettableByIndexData<?> c, int i, ByteBuffer v) -> c.setBytes(i, v)) //
                .put(DataType.Name.BOOLEAN, (SettableByIndexData<?> c, int i, Boolean v) -> c.setBool(i, v)) //
                .put(DataType.Name.COUNTER, (SettableByIndexData<?> c, int i, Long v) -> c.setLong(i, v)) //
                .put(DataType.Name.DECIMAL, (SettableByIndexData<?> c, int i, BigDecimal v) -> c.setDecimal(i, v)) //
                .put(DataType.Name.DOUBLE, (SettableByIndexData<?> c, int i, Double v) -> c.setDouble(i, v)) //
                .put(DataType.Name.FLOAT, (SettableByIndexData<?> c, int i, Float v) -> c.setFloat(i, v)) //
                .put(DataType.Name.INET, (SettableByIndexData<?> c, int i, InetAddress v) -> c.setInet(i, v)) //
                .put(DataType.Name.INT, (SettableByIndexData<?> c, int i, Integer v) -> c.setInt(i, v)) //
                .put(DataType.Name.TEXT, (SettableByIndexData<?> c, int i, String v) -> c.setString(i, v)) //
                .put(DataType.Name.TIMESTAMP, (SettableByIndexData<?> c, int i, Date v) -> c.setDate(i, v)) //
                .put(DataType.Name.TIMEUUID, (SettableByIndexData<?> c, int i, UUID v) -> c.setUUID(i, v)) //
                .put(DataType.Name.UUID, (SettableByIndexData<?> c, int i, UUID v) -> c.setUUID(i, v)) //
                .put(DataType.Name.VARCHAR, (SettableByIndexData<?> c, int i, String v) -> c.setString(i, v)) //
                .put(DataType.Name.VARINT, (SettableByIndexData<?> c, int i, BigInteger v) -> c.setVarint(i, v)) //
                .put(DataType.Name.LIST, (SettableByIndexData<?> c, int i, List<?> v) -> c.setList(i, v)) //
                .put(DataType.Name.MAP, (SettableByIndexData<?> c, int i, Map<?, ?> v) -> c.setMap(i, v)) //
                // .put(DataType.Name.SET, (SettableByIndexData<?> c, int i, Set<?> v)
                // -> c.setSet(i, v)) //
                .put(DataType.Name.TUPLE, (SettableByIndexData<?> c, int i, TupleValue v) -> c.setTupleValue(i, v)) //
                .put(DataType.Name.UDT, (SettableByIndexData<?> c, int i, UDTValue v) -> c.setUDTValue(i, v)) //
                .build();
    }

    {
        registerSchemaInferrer(Row.class, this::inferSchemaRow);
        registerSchemaInferrer(UDTValue.class, udt -> inferSchemaDataType(udt.getType()));
        registerSchemaInferrer(TupleValue.class, tuple -> inferSchemaDataType(tuple.getType()));
        registerSchemaInferrer(TableMetadata.class, this::inferSchemaTableMetadata);
        registerSchemaInferrer(DataType.class, this::inferSchemaDataType);
    }

    /**
     * Infers an Avro schema for the given table. This can be an expensive operation so the schema should be cached
     * where possible. This is always an {@link Schema.Type#RECORD}.
     * 
     * @param in the TableMetadata to analyse.
     * @return the schema for the given table.
     */
    private Schema inferSchemaTableMetadata(TableMetadata in) {
        // Tables without
        List<ColumnMetadata> columns = in.getColumns();
        if (columns.size() == 0)
            return Schema.create(Type.NULL);

        FieldAssembler<Schema> fa = SchemaBuilder.record(in.getName()) //
                .prop(PROP_CQL_CREATE, in.exportAsString()) //
                .namespace(in.getKeyspace().getName()).fields();
        for (ColumnMetadata colmd : columns) {
            fa = fa.name(colmd.getName()).type(inferSchema(colmd.getType())).noDefault();
        }
        return fa.endRecord();
    }

    /**
     * Infers an Avro schema for the given row. This can be an expensive operation so the schema should be cached where
     * possible. This is always an {@link Schema.Type#RECORD}.
     * 
     * @param in the row to analyze.
     * @return the schema for the given row.
     */
    private Schema inferSchemaRow(Row in) {
        ColumnDefinitions cds = in.getColumnDefinitions();
        // Rows without columns.
        if (cds.size() == 0)
            return Schema.create(Type.NULL);

        Definition cd1 = cds.iterator().next();

        // TODO(rskraba): Check to make sure that the input has one keyspace and
        // table, maybe disambiguate the
        // record
        // name with an ID.
        FieldAssembler<Schema> fa = SchemaBuilder.record(cd1.getTable()) //
                .namespace(cd1.getKeyspace()).fields();
        for (Definition cd : cds) {
            fa = fa.name(cd.getName()).type(inferSchema(cd.getType())).noDefault();
        }
        return fa.endRecord();
    }

    /**
     * Infers an Avro schema for the given Cassandra DataType. This can be an expensive operation so the schema should
     * be cached where possible.
     * 
     * @param in the DataType to analyze.
     * @return the schema for the given row.
     */
    public Schema inferSchemaDataType(DataType in) {
        // Determine if a native type that can be easily mapped to a Schema exists.
        Class<?> nativeClass = sCassandraNativeTypes.get(in.getName());
        if (nativeClass != null) {
            AvroConverter<?, ?> ac = DatumRegistry.getConverter(nativeClass);
            if (ac == null) {
                // This should never occur if all of the native DataTypes are handled.
                throw new RuntimeException("The DataType " + in + " is not handled.");
            }
            // TODO(rskraba): There's gotta be something better than this.
            Schema s = new Schema.Parser().parse(ac.getSchema().toString());
            // TODO(rskraba): What do we do about unions?
            if (s.getType() != Schema.Type.UNION) {
                s.addProp(PROP_CASSANDRA_DATATYPE_NAME, in.getName().name());
            }
            return wrapAsNullable(s);
        }

        // If a native type doesn't exist for the DataType, then additional
        // information is necessary to create the
        // schema.
        if (in.isCollection()) {
            List<DataType> containedTypes = in.getTypeArguments();
            // LIST and SET are mapped to Schema.Type.ARRAY
            if (in.getName().equals(DataType.Name.LIST) || in.getName().equals(DataType.Name.SET)) {
                if (containedTypes == null || containedTypes.size() < 1)
                    throw new RuntimeException("The DataType " + in + " is not handled.");
                Schema itemSchema = inferSchema(containedTypes.get(0));
                return wrapAsNullable(SchemaBuilder.array() //
                        .prop(PROP_CASSANDRA_DATATYPE_NAME, in.getName().name()) //
                        .items(itemSchema));
            } else if (in.getName().equals(DataType.Name.MAP)) {
                if (containedTypes == null || containedTypes.size() < 2)
                    throw new RuntimeException("The DataType " + in + " is not handled.");
                // TODO(rskraba): validate that the key type is Schema.Type.STRING
                // compatible.
                Schema valueSchema = inferSchema(containedTypes.get(1));
                return wrapAsNullable(SchemaBuilder.map() //
                        .prop(PROP_CASSANDRA_DATATYPE_NAME, in.getName().name()) //
                        .values(valueSchema));
            }
        } else if (in instanceof TupleType) {
            // TupleTypes are nested records with unnamed fields.
            TupleType tt = (TupleType) in;

            // TODO(rskraba): record naming strategy for tuples
            FieldAssembler<Schema> fa = SchemaBuilder.record(TUPLE_RECORD_NAME) //
                    .prop(PROP_CASSANDRA_DATATYPE_NAME, DataType.Name.TUPLE.name()) //
                    .fields();
            int i = 0;
            for (DataType fieldType : tt.getComponentTypes()) {
                fa = fa.name(TUPLE_FIELD_PREFIX + (i++)).type(inferSchema(fieldType)).noDefault();
            }
            return wrapAsNullable(fa.endRecord());
        } else if (in instanceof UserType) {
            // UserTypes are nested records.
            UserType ut = (UserType) in;

            FieldAssembler<Schema> fa = SchemaBuilder.record(ut.getTypeName()) //
                    .prop(PROP_CQL_CREATE, ut.asCQLQuery()) //
                    .prop(PROP_CASSANDRA_DATATYPE_NAME, DataType.Name.UDT.name()) //
                    .fields();
            for (String fieldName : ut.getFieldNames()) {
                DataType fieldType = ut.getFieldType(fieldName);
                fa = fa.name(fieldName).type(inferSchema(fieldType)).noDefault();
            }
            return wrapAsNullable(fa.endRecord());
        }

        // This should never occur.
        throw new RuntimeException("The DataType " + in + " is not handled.");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> AvroConverter<? super T, ?> getConverter(Class<T> datumClass, DataType type, Schema schema) {
        if (type != null && sCassandraNativeTypes.containsKey(type.getName())) {
            AvroConverter<? super T, ?> converter = (AvroConverter<? super T, ?>) DatumRegistry
                    .getConverter(sCassandraNativeTypes.get(type.getName()));
            if (converter != null)
                return converter;
        }

        AvroConverter<? super T, ?> converter = DatumRegistry.getConverter(datumClass);
        if (converter != null)
            return converter;

        if (type.getName() == DataType.Name.LIST || type.getName() == DataType.Name.SET) {
            DataType elementType = type.getTypeArguments().get(0);
            Class<?> elementClass = elementType.asJavaClass();
            Schema elementSchema = unwrapIfNullable(unwrapIfNullable(schema).getElementType());
            return (AvroConverter<? super T, ?>) new AvroListConverter(datumClass, schema, getConverter(elementClass,
                    elementType, elementSchema));
        } else if (type.getName() == DataType.Name.MAP) {
            DataType valueType = type.getTypeArguments().get(1);
            Class<?> valueClass = valueType.asJavaClass();
            Schema valueSchema = unwrapIfNullable(unwrapIfNullable(schema).getValueType());
            return (AvroConverter<? super T, ?>) new AvroMapConverter(datumClass, schema, getConverter(valueClass, valueType,
                    valueSchema));
        } else if (type.getName() == DataType.Name.UDT) {
            return (AvroConverter<? super T, ?>) DatumRegistry.getFacadeFactory(UDTValue.class);
        } else if (type.getName() == DataType.Name.TUPLE) {
            return (AvroConverter<? super T, ?>) DatumRegistry.getFacadeFactory(TupleValue.class);
        }

        // This should never occur.
        throw new RuntimeException("Cannot convert " + type + ".");
    }

    public ContainerReaderByIndex<GettableByIndexData, ?> getReader(DataType type) {
        ContainerReaderByIndex<GettableByIndexData, ?> reader = sReaders.get(type.getName());
        if (reader != null)
            return reader;

        if (type.isCollection()) {
            List<DataType> containedTypes = type.getTypeArguments();
            if (type.getName().equals(DataType.Name.LIST)) {
                return (c, i) -> c.getList(i, containedTypes.get(0).asJavaClass());
            } else if (type.getName().equals(DataType.Name.SET)) {
                // TODO: is there a better way to enforce a consistent order after read?
                return (c, i) -> new ArrayList<>(c.getSet(i, containedTypes.get(0).asJavaClass()));
            } else if (type.getName().equals(DataType.Name.MAP)) {
                return (c, i) -> c.getMap(i, containedTypes.get(0).asJavaClass(), containedTypes.get(1).asJavaClass());
            }
        }

        throw new RuntimeException("The DataType " + type + " is not handled.");
    }

    public ContainerWriterByIndex<SettableByIndexData<?>, ?> getWriter(DataType type) {
        ContainerWriterByIndex<SettableByIndexData<?>, ?> writer = sWriters.get(type.getName());
        if (writer != null)
            return writer;

        if (type.isCollection()) {
            if (type.getName().equals(DataType.Name.SET)) {
                // TODO: is there a better way to enforce a consistent order after read?
                return (SettableByIndexData<?> c, int i, List<?> v) -> c.setSet(i, new HashSet<>(v));
            }
        }

        throw new RuntimeException("The DataType " + type + " is not handled.");
    }

    public static DataType getDataType(Schema schema) {
        schema = unwrapIfNullable(schema);
        // TODO(rskraba): parse overrideDataType
        String overrideDataType = schema.getProp("talend.dbtype");
        if (null != overrideDataType) {
        }

        switch (schema.getType()) {
        case BOOLEAN:
            return DataType.cboolean();
        case BYTES:
            return DataType.blob();
        case DOUBLE:
            return DataType.cdouble();
        case FIXED:
            return DataType.blob();
        case FLOAT:
            return DataType.cfloat();
        case INT:
            return DataType.cint();
        case LONG:
            return DataType.bigint();
        case STRING:
            return DataType.text();
        case ENUM:
        case ARRAY:
        case MAP:
            // return DataType.map(keyType, valueType);
        case NULL:
        case RECORD:
        case UNION:
        default:
            // TODO(rskraba): add these types.
            break;
        }
        return null;
    }

    public static CassandraAvroRegistry get() {
        return INSTANCE;
    }

    // We don't need any of this if we're using Java 8 lambdas!
    // /**
    // * Common interface for the callbacks that know how to read to and write
    // from Cassandra specific containers.
    // *
    // * @param <T> The specific class that will be read/written for the callback.
    // */
    // public static interface ReadWrite<T> extends ContainerReaderByIndex<Row,
    // T>, ContainerReaderByName<Row, T>,
    // ContainerWriterByIndex<BoundStatement, T>,
    // ContainerWriterByName<BoundStatement, T> {
    // }
    //
    // public static class ReadWriteBigDecimal implements ReadWrite<BigDecimal> {
    //
    // public BigDecimal readValue(Row row, String key) {
    // return row.getDecimal(key);
    // }
    //
    // public BigDecimal readValue(Row row, int index) {
    // return row.getDecimal(index);
    // }
    //
    // public void writeValue(BoundStatement bs, String key, BigDecimal value) {
    // bs.setDecimal(key, value);
    // }
    //
    // public void writeValue(BoundStatement bs, int index, BigDecimal value) {
    // bs.setDecimal(index, value);
    // }
    // }
    //
    // public static class ReadWriteBigInteger implements ReadWrite<BigInteger> {
    //
    // public BigInteger readValue(Row row, String key) {
    // return row.getVarint(key);
    // }
    //
    // public BigInteger readValue(Row row, int index) {
    // return row.getVarint(index);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, String key, BigInteger value) {
    // bs.setVarint(key, value);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, int index, BigInteger value) {
    // bs.setVarint(index, value);
    // }
    // }
    //
    // public static class ReadWriteBoolean implements ReadWrite<Boolean> {
    //
    // public Boolean readValue(Row row, String key) {
    // return row.getBool(key);
    // }
    //
    // public Boolean readValue(Row row, int index) {
    // return row.getBool(index);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, String key, Boolean value) {
    // bs.setBool(key, value);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, int index, Boolean value) {
    // bs.setBool(index, value);
    // }
    // }
    //
    // public static class ReadWriteByteBuffer implements ReadWrite<ByteBuffer> {
    //
    // public ByteBuffer readValue(Row row, String key) {
    // return row.getBytes(key);
    // }
    //
    // public ByteBuffer readValue(Row row, int index) {
    // return row.getBytes(index);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, String key, ByteBuffer value) {
    // bs.setBytes(key, value);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, int index, ByteBuffer value) {
    // bs.setBytes(index, value);
    // }
    // }
    //
    // public static class ReadWriteDate implements ReadWrite<Date> {
    //
    // public Date readValue(Row row, String key) {
    // return row.getDate(key);
    // }
    //
    // public Date readValue(Row row, int index) {
    // return row.getDate(index);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, String key, Date value) {
    // bs.setDate(key, value);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, int index, Date value) {
    // bs.setDate(index, value);
    // }
    // }
    //
    // public static class ReadWriteDouble implements ReadWrite<Double> {
    //
    // public Double readValue(Row row, String key) {
    // return row.getDouble(key);
    // }
    //
    // public Double readValue(Row row, int index) {
    // return row.getDouble(index);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, String key, Double value) {
    // bs.setDouble(key, value);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, int index, Double value) {
    // bs.setDouble(index, value);
    // }
    // }
    //
    // public static class ReadWriteFloat implements ReadWrite<Float> {
    //
    // public Float readValue(Row row, String key) {
    // return row.getFloat(key);
    // }
    //
    // public Float readValue(Row row, int index) {
    // return row.getFloat(index);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, String key, Float value) {
    // bs.setFloat(key, value);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, int index, Float value) {
    // bs.setFloat(index, value);
    // }
    // }
    //
    // public static class ReadWriteInetAddress implements ReadWrite<InetAddress>
    // {
    //
    // public InetAddress readValue(Row row, String key) {
    // return row.getInet(key);
    // }
    //
    // public InetAddress readValue(Row row, int index) {
    // return row.getInet(index);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, String key, InetAddress value) {
    // bs.setInet(key, value);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, int index, InetAddress value) {
    // bs.setInet(index, value);
    // }
    // }
    //
    // public static class ReadWriteInt implements ReadWrite<Integer> {
    //
    // public Integer readValue(Row row, String key) {
    // return row.getInt(key);
    // }
    //
    // public Integer readValue(Row row, int index) {
    // return row.getInt(index);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, String key, Integer value) {
    // bs.setInt(key, value);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, int index, Integer value) {
    // bs.setInt(index, value);
    // }
    // }
    //
    // public static class ReadWriteLong implements ReadWrite<Long> {
    //
    // public Long readValue(Row row, String key) {
    // return row.getLong(key);
    // }
    //
    // public Long readValue(Row row, int index) {
    // return row.getLong(index);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, String key, Long value) {
    // bs.setLong(key, value);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, int index, Long value) {
    // bs.setLong(index, value);
    // }
    // }
    //
    // public static class ReadWriteString implements ReadWrite<String> {
    //
    // public String readValue(Row row, String key) {
    // return row.getString(key);
    // }
    //
    // public String readValue(Row row, int index) {
    // return row.getString(index);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, String key, String value) {
    // bs.setString(key, value);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, int index, String value) {
    // bs.setString(index, value);
    // }
    // }
    //
    // public static class ReadWriteUUID implements ReadWrite<UUID> {
    //
    // public UUID readValue(Row row, String key) {
    // return row.getUUID(key);
    // }
    //
    // public UUID readValue(Row row, int index) {
    // return row.getUUID(index);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, String key, UUID value) {
    // bs.setUUID(key, value);
    // }
    //
    // @Override
    // public void writeValue(BoundStatement bs, int index, UUID value) {
    // bs.setUUID(index, value);
    // }
    // }
}
