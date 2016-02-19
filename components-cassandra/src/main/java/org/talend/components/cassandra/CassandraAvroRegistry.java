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
import org.talend.daikon.schema.avro.ContainerRegistry;
import org.talend.daikon.schema.avro.util.ConvertAvroList;
import org.talend.daikon.schema.avro.util.ConvertAvroMap;
import org.talend.daikon.schema.type.AvroConverter;
import org.talend.daikon.schema.type.ContainerReaderByIndex;
import org.talend.daikon.schema.type.ContainerWriterByIndex;

import com.datastax.driver.core.BoundStatement;
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

    private static final CassandraAvroRegistry sInstance = new CassandraAvroRegistry();

    private static final CassandraContainerRegistry sContainerReadWrite = new CassandraContainerRegistry();

    /**
     * Contains the DataType.Name from Cassandra where we can infer the Object type without any additional information.
     */
    private final Map<DataType.Name, Class<?>> mCassandraNativeTypes;

    /**
     * Hidden constructor: use the singleton.
     */
    private CassandraAvroRegistry() {
        // Ensure that other components know how to process Cassandra objects by sharing these facades.
        registerFacadeFactory(Row.class, RowFacadeFactory.class);
        registerFacadeFactory(UDTValue.class, UDTValueFacadeFactory.class);
        registerFacadeFactory(TupleValue.class, TupleValueFacadeFactory.class);

        // Ensure that we know how to get Schemas for these Cassandra objects.
        registerSchemaInferrer(BoundStatement.class,
                bs -> inferSchemaColumnDefinitions("BoundStatement", bs.preparedStatement().getVariables()));
        registerSchemaInferrer(Row.class, row -> inferSchemaColumnDefinitions("Row", row.getColumnDefinitions()));
        registerSchemaInferrer(ColumnDefinitions.class, cd -> inferSchemaColumnDefinitions("Record", cd));
        registerSchemaInferrer(UDTValue.class, udt -> inferSchemaDataType(udt.getType()));
        registerSchemaInferrer(TupleValue.class, tuple -> inferSchemaDataType(tuple.getType()));
        registerSchemaInferrer(TableMetadata.class, this::inferSchemaTableMetadata);
        registerSchemaInferrer(DataType.class, this::inferSchemaDataType);

        // The DataType.Name that we can convert to Avro-compatible without any additional information.
        mCassandraNativeTypes = new ImmutableMap.Builder<DataType.Name, Class<?>>() //
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

        // These are not yet implemented by our version of Cassandra.
        // DataType.Name.DATE // com.datastax.driver.core.LocalDate
        // DataType.Name.SMALLINT // short
        // DataType.Name.TINYINT // byte
    }

    /**
     * @return The family that uses the specific objects that this converter knows how to translate.
     */
    public String getFamilyName() {
        return FAMILY_NAME;
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
     * Infers an Avro schema for the given ColumnDefinitions. This can be an expensive operation so the schema should be
     * cached where possible. This is always an {@link Schema.Type#RECORD}.
     * 
     * @param in the row to analyze.
     * @return the schema for the given row.
     */
    private Schema inferSchemaColumnDefinitions(String recordNamePrefix, ColumnDefinitions in) {
        if (in.size() == 0)
            return Schema.create(Type.NULL);

        // Use the first column to get the namespace and record name from the keyspace and table name.
        Definition cd1 = in.iterator().next();
        FieldAssembler<Schema> fa = SchemaBuilder.record(cd1.getTable() + recordNamePrefix) //
                .namespace(cd1.getKeyspace() + '.' + cd1.getTable()).fields();
        for (Definition cd : in) {
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
    private Schema inferSchemaDataType(DataType in) {
        // Determine if a native type that can be easily mapped to a Schema exists.
        Class<?> nativeClass = mCassandraNativeTypes.get(in.getName());
        if (nativeClass != null) {
            AvroConverter<?, ?> ac = getConverter(nativeClass);
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
                // TODO(rskraba): validate that the key type is Schema.Type.STRING compatible.
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
                    .namespace(ut.getKeyspace()) //
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
    /**
     * Gets an AvroConverter for transforming between Cassandra objects and Avro-compatible objects using Cassandra type
     * information.
     * 
     * @param type
     * @param schema
     * @param datumClass
     * @return
     */
    public <T> AvroConverter<? super T, ?> getConverter(DataType type, Schema schema, Class<T> datumClass) {
        // If the DataType is present, then use it to get the correct converter.
        if (type != null) {
            // Primitive types are easiest :)
            if (mCassandraNativeTypes.containsKey(type.getName())) {
                AvroConverter<? super T, ?> converter = (AvroConverter<? super T, ?>) getConverter(
                        mCassandraNativeTypes.get(type.getName()));
                if (converter != null)
                    return converter;
            }

            // The types that can have nested values require more work to put together.
            switch (type.getName()) {
            case LIST:
            case SET:
                DataType elementType = type.getTypeArguments().get(0);
                Class<?> elementClass = elementType.asJavaClass();
                Schema elementSchema = unwrapIfNullable(unwrapIfNullable(schema).getElementType());
                return (AvroConverter<? super T, ?>) new ConvertAvroList(datumClass, schema,
                        getConverter(elementType, elementSchema, elementClass));
            case MAP:
                DataType valueType = type.getTypeArguments().get(1);
                Class<?> valueClass = valueType.asJavaClass();
                Schema valueSchema = unwrapIfNullable(unwrapIfNullable(schema).getValueType());
                return (AvroConverter<? super T, ?>) new ConvertAvroMap(datumClass, schema,
                        getConverter(valueType, valueSchema, valueClass));
            case TUPLE:
                return (AvroConverter<? super T, ?>) createFacadeFactory(TupleValue.class);
            case UDT:
                return (AvroConverter<? super T, ?>) createFacadeFactory(UDTValue.class);
            default:
                break;
            }
        }

        // If a converter wasn't found, try and get using the class of the datum.
        AvroConverter<? super T, ?> converter = getConverter(datumClass);
        if (converter != null)
            return converter;

        // This should never occur.
        throw new RuntimeException("Cannot convert " + type + ".");
    }

    public ContainerReaderByIndex<GettableByIndexData, ?> getReader(DataType type) {
        return sContainerReadWrite.getReader(type);
    }

    public ContainerWriterByIndex<SettableByIndexData<?>, ?> getWriter(DataType type) {
        return sContainerReadWrite.getWriter(type);
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
        return sInstance;
    }

    public void buildFacadesUsingDataType(AvroConverter<?, ?> ac, DataType t) {
        if (t != null) {
            switch (t.getName()) {
            // The container types need to be recursively built.
            case LIST:
            case SET:
                for (AvroConverter<?, ?> nestedAc : ((ConvertAvroList<?, ?>) ac).getNestedAvroConverters())
                    buildFacadesUsingDataType(nestedAc, t.getTypeArguments().get(0));
                return;
            case MAP:
                for (AvroConverter<?, ?> nestedAc : ((ConvertAvroMap<?, ?>) ac).getNestedAvroConverters())
                    buildFacadesUsingDataType(nestedAc, t.getTypeArguments().get(1));
                return;
            // User defined types need to have their type set, and then fall through to have their fields built.
            case TUPLE:
                TupleValueFacadeFactory tvff = (TupleValueFacadeFactory) ac;
                tvff.setContainerType((TupleType) t);
                break;
            case UDT:
                UDTValueFacadeFactory uvff = (UDTValueFacadeFactory) ac;
                uvff.setContainerType((UserType) t);
                break;
            default:
                // All other types do not need to be built.
                return;
            }
        }

        // Build all of the fields.
        if (ac instanceof CassandraBaseFacadeFactory) {
            CassandraBaseFacadeFactory<?, ?, ?> cbff = (CassandraBaseFacadeFactory<?, ?, ?>) ac;
            int i = 0;
            for (AvroConverter<?, ?> nestedAc : cbff.getNestedAvroConverters())
                buildFacadesUsingDataType(nestedAc, cbff.getFieldType(i++));
        }
    }

    /** A utility class for getting callbacks for reading and writing to Cassandra containers. */
    private static class CassandraContainerRegistry
            extends ContainerRegistry<DataType, GettableByIndexData, SettableByIndexData<?>> {

        public CassandraContainerRegistry() {
            // The following types can be read without requiring any additional information.
            registerReader(DataType.ascii(), (c, i) -> c.getString(i));
            registerReader(DataType.bigint(), (c, i) -> c.getLong(i));
            registerReader(DataType.blob(), (c, i) -> c.getBytes(i));
            registerReader(DataType.cboolean(), (c, i) -> c.getBool(i));
            registerReader(DataType.counter(), (c, i) -> c.getLong(i));
            registerReader(DataType.decimal(), (c, i) -> c.getDecimal(i));
            registerReader(DataType.cdouble(), (c, i) -> c.getDouble(i));
            registerReader(DataType.cfloat(), (c, i) -> c.getFloat(i));
            registerReader(DataType.inet(), (c, i) -> c.getInet(i));
            registerReader(DataType.cint(), (c, i) -> c.getInt(i));
            registerReader(DataType.text(), (c, i) -> c.getString(i));
            registerReader(DataType.timestamp(), (c, i) -> c.getDate(i));
            registerReader(DataType.timeuuid(), (c, i) -> c.getUUID(i));
            registerReader(DataType.uuid(), (c, i) -> c.getUUID(i));
            registerReader(DataType.varchar(), (c, i) -> c.getString(i));
            registerReader(DataType.varint(), (c, i) -> c.getVarint(i));

            // The following types can be written without any additional information.
            registerWriter(DataType.ascii(), (SettableByIndexData<?> c, int i, String v) -> c.setString(i, v));
            registerWriter(DataType.bigint(), (SettableByIndexData<?> c, int i, Long v) -> c.setLong(i, v));
            registerWriter(DataType.blob(), (SettableByIndexData<?> c, int i, ByteBuffer v) -> c.setBytes(i, v));
            registerWriter(DataType.cboolean(), (SettableByIndexData<?> c, int i, Boolean v) -> c.setBool(i, v));
            registerWriter(DataType.counter(), (SettableByIndexData<?> c, int i, Long v) -> c.setLong(i, v));
            registerWriter(DataType.decimal(), (SettableByIndexData<?> c, int i, BigDecimal v) -> c.setDecimal(i, v));
            registerWriter(DataType.cdouble(), (SettableByIndexData<?> c, int i, Double v) -> c.setDouble(i, v));
            registerWriter(DataType.cfloat(), (SettableByIndexData<?> c, int i, Float v) -> c.setFloat(i, v));
            registerWriter(DataType.inet(), (SettableByIndexData<?> c, int i, InetAddress v) -> c.setInet(i, v));
            registerWriter(DataType.cint(), (SettableByIndexData<?> c, int i, Integer v) -> c.setInt(i, v));
            registerWriter(DataType.text(), (SettableByIndexData<?> c, int i, String v) -> c.setString(i, v));
            registerWriter(DataType.timestamp(), (SettableByIndexData<?> c, int i, Date v) -> c.setDate(i, v));
            registerWriter(DataType.timeuuid(), (SettableByIndexData<?> c, int i, UUID v) -> c.setUUID(i, v));
            registerWriter(DataType.uuid(), (SettableByIndexData<?> c, int i, UUID v) -> c.setUUID(i, v));
            registerWriter(DataType.varchar(), (SettableByIndexData<?> c, int i, String v) -> c.setString(i, v));
            registerWriter(DataType.varint(), (SettableByIndexData<?> c, int i, BigInteger v) -> c.setVarint(i, v));
        }

        public ContainerReaderByIndex<GettableByIndexData, ?> getReader(DataType type) {
            ContainerReaderByIndex<GettableByIndexData, ?> reader = super.getReader(type);
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

            switch (type.getName()) {
            case TUPLE:
                return (c, i) -> c.getTupleValue(i);
            case UDT:
                return (c, i) -> c.getUDTValue(i);
            default:
                throw new RuntimeException("The DataType " + type + " is not handled.");
            }
        }

        public ContainerWriterByIndex<SettableByIndexData<?>, ?> getWriter(DataType type) {
            ContainerWriterByIndex<SettableByIndexData<?>, ?> writer = super.getWriter(type);
            if (writer != null)
                return writer;

            if (type.isCollection()) {
                if (type.getName().equals(DataType.Name.LIST)) {
                    return (SettableByIndexData<?> c, int i, List<?> v) -> c.setList(i, v);
                } else if (type.getName().equals(DataType.Name.SET)) {
                    // TODO: is there a better way to enforce a consistent order after read?
                    return (SettableByIndexData<?> c, int i, List<?> v) -> c.setSet(i, new HashSet<>(v));
                } else if (type.getName().equals(DataType.Name.MAP)) {
                    return (SettableByIndexData<?> c, int i, Map<?, ?> v) -> c.setMap(i, v);
                }
            }

            switch (type.getName()) {
            case TUPLE:
                return (SettableByIndexData<?> c, int i, TupleValue v) -> c.setTupleValue(i, v);
            case UDT:
                return (SettableByIndexData<?> c, int i, UDTValue v) -> c.setUDTValue(i, v);
            default:
                throw new RuntimeException("The DataType " + type + " is not handled.");
            }
        }

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
