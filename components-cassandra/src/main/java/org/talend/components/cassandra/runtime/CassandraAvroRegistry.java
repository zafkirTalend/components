package org.talend.components.cassandra.runtime;

import com.datastax.driver.core.*;
import com.google.common.collect.ImmutableMap;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.talend.components.cassandra.avro.ConvertLocalDate;
import org.talend.daikon.avro.AvroConverter;
import org.talend.daikon.avro.AvroRegistry;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.avro.container.ContainerReaderByIndex;
import org.talend.daikon.avro.container.ContainerRegistry;
import org.talend.daikon.avro.container.ContainerWriterByIndex;
import org.talend.daikon.avro.util.AvroUtils;
import org.talend.daikon.avro.util.ConvertAvroList;
import org.talend.daikon.avro.util.ConvertAvroMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * http://docs.datastax.com/en/developer/java-driver/3.0/java-driver/reference/javaClass2Cql3Datatypes.html
 */
public class CassandraAvroRegistry extends AvroRegistry {

    public static final String TUPLE_RECORD_NAME = "TupleRecord"; // TODO(bchen) is there any common way?

    public static final String TUPLE_FIELD_PREFIX = "field"; // TODO(bchen) is there any common way?

    private static final CassandraAvroRegistry sInstance = new CassandraAvroRegistry();

    /**
     * Contains the DataType.Name from Cassandra where we can infer the Object type without any additional information.
     */
    private static final Map<DataType.Name, Class<?>> mCassandraNativeTypes;

    private static final Map<DataType.Name, Class<?>> mCassandraAllTypes;

    private static final CassandraContainerRegistry sContainerReadWrite = new CassandraContainerRegistry();

    static {
        // The DataType.Name that we can convert to Avro-compatible without any additional information.
        mCassandraNativeTypes = new ImmutableMap.Builder<DataType.Name, Class<?>>() //
                .put(DataType.Name.ASCII, String.class) //
                .put(DataType.Name.BIGINT, Long.class) //
                .put(DataType.Name.BLOB, ByteBuffer.class) // TODO(bchen) not byte[]?
                .put(DataType.Name.BOOLEAN, Boolean.class) //
                .put(DataType.Name.COUNTER, Long.class) //
                .put(DataType.Name.DATE, LocalDate.class) // new from 2.2
                .put(DataType.Name.DECIMAL, BigDecimal.class) //
                .put(DataType.Name.DOUBLE, Double.class) //
                .put(DataType.Name.FLOAT, Float.class) //
                .put(DataType.Name.INET, InetAddress.class) //
                .put(DataType.Name.INT, Integer.class) //
                .put(DataType.Name.SMALLINT, Short.class) // new from 2.2
                .put(DataType.Name.TEXT, String.class) //
                .put(DataType.Name.TIME, Long.class) // new from 2.2
                .put(DataType.Name.TIMESTAMP, Date.class) //
                .put(DataType.Name.TIMEUUID, UUID.class) //
                .put(DataType.Name.TINYINT, Byte.class) // new from 2.2
                .put(DataType.Name.UUID, UUID.class) //
                .put(DataType.Name.VARCHAR, String.class) //
                .put(DataType.Name.VARINT, BigInteger.class) //
                .build();
        mCassandraAllTypes = new ImmutableMap.Builder<DataType.Name, Class<?>>() //
                .putAll(mCassandraNativeTypes) //
                .put(DataType.Name.SET, Set.class) //
                .put(DataType.Name.LIST, List.class) //
                .put(DataType.Name.MAP, Map.class) //
                .put(DataType.Name.UDT, UDTValue.class) //
                .put(DataType.Name.TUPLE, TupleValue.class) //
                .build();
    }

    private CassandraAvroRegistry() {

        registerConverter(LocalDate.class, new ConvertLocalDate());

        // Ensure that other components know how to process Cassandra objects by sharing these facades.
        registerAdapterFactory(Row.class, RowAdapterFactory.class);
        registerAdapterFactory(UDTValue.class, UDTValueAdapterFactory.class);
        registerAdapterFactory(TupleValue.class, TupleValueAdapterFactory.class);

        // Ensure that we know how to get Schemas for these Cassandra objects.
        registerSchemaInferrer(BoundStatement.class,
                bs -> inferSchemaColumnDefinitions("BoundStatement", bs.preparedStatement().getVariables()));
        registerSchemaInferrer(Row.class, row -> inferSchemaColumnDefinitions("Row", row.getColumnDefinitions()));
        registerSchemaInferrer(ColumnDefinitions.class, cd -> inferSchemaColumnDefinitions("Record", cd)); //TODO(bchen) why need "Record" here?
        registerSchemaInferrer(UDTValue.class, udt -> inferSchemaDataType(udt.getType()));
        registerSchemaInferrer(TupleValue.class, tuple -> inferSchemaDataType(tuple.getType()));
        registerSchemaInferrer(TableMetadata.class, this::inferSchemaTableMetadata);
        registerSchemaInferrer(DataType.class, this::inferSchemaDataType);
    }

    public static CassandraAvroRegistry get() {
        return sInstance;
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
            return Schema.create(Schema.Type.NULL);

        SchemaBuilder.FieldAssembler<Schema> fa = SchemaBuilder.record(in.getName()) //
                //.prop(PROP_CQL_CREATE, in.exportAsString()) // TODO(bchen) why need it?
                .namespace(in.getKeyspace().getName())
                .fields();
        for (ColumnMetadata columnMetadata : in.getColumns()) {
            fa = fa.name(columnMetadata.getName()).type(inferSchema(columnMetadata.getType())).noDefault();
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
            return Schema.create(Schema.Type.NULL);

        // Use the first column to get the namespace and record name from the keyspace and table name.
        ColumnDefinitions.Definition cd1 = in.iterator().next();
        SchemaBuilder.FieldAssembler<Schema> fa = SchemaBuilder.record(cd1.getTable() + recordNamePrefix) //
                .namespace(cd1.getKeyspace() + '.' + cd1.getTable()).fields();
        for (ColumnDefinitions.Definition cd : in) {
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
            // TODO(rskraba): What do we do about unions? -- bchen: is it ok to add the db type in each element of the union?
            if (s.getType() != Schema.Type.UNION) {
                s.addProp(SchemaConstants.TALEND_COLUMN_DB_TYPE, in.getName().name());
            } else {
                List<Schema> types = s.getTypes();
                for (Schema type : types) {
                    type.addProp(SchemaConstants.TALEND_COLUMN_DB_TYPE, in.getName().name());
                }
                Schema.createUnion(types);
            }
            return AvroUtils.wrapAsNullable(s);
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
                return AvroUtils.wrapAsNullable(SchemaBuilder.array() //
                        .prop(SchemaConstants.TALEND_COLUMN_DB_TYPE, in.getName().name()) //
                        .items(itemSchema));
            } else if (in.getName().equals(DataType.Name.MAP)) {
                if (containedTypes == null || containedTypes.size() < 2)
                    throw new RuntimeException("The DataType " + in + " is not handled.");
                // TODO(rskraba): validate that the key type is Schema.Type.STRING compatible.
                Schema valueSchema = inferSchema(containedTypes.get(1));
                return AvroUtils.wrapAsNullable(SchemaBuilder.map() //
                        .prop(SchemaConstants.TALEND_COLUMN_DB_TYPE, in.getName().name()) //
                        .values(valueSchema));
            }
        } else if (in instanceof TupleType) {
            // TupleTypes are nested records with unnamed fields.
            TupleType tt = (TupleType) in;
            // TODO(rskraba): record naming strategy for tuples
            SchemaBuilder.FieldAssembler<Schema> fa = SchemaBuilder.record(TUPLE_RECORD_NAME) //
                    .prop(SchemaConstants.TALEND_COLUMN_DB_TYPE, DataType.Name.TUPLE.name()) //
                    .fields();
            int i = 0;
            for (DataType fieldType : tt.getComponentTypes()) {
                fa = fa.name(TUPLE_FIELD_PREFIX + (i++)).type(inferSchema(fieldType)).noDefault();
            }
            return AvroUtils.wrapAsNullable(fa.endRecord());
        } else if (in instanceof UserType) {
            // UserTypes are nested records.
            UserType ut = (UserType) in;
            SchemaBuilder.FieldAssembler<Schema> fa = SchemaBuilder.record(ut.getTypeName()) //
                    .namespace(ut.getKeyspace()) //
                    //.prop(PROP_CQL_CREATE, ut.asCQLQuery()) // TODO(bchen) why need it?
                    .prop(SchemaConstants.TALEND_COLUMN_DB_TYPE, DataType.Name.UDT.name()) //
                    .fields();
            for (String fieldName : ut.getFieldNames()) {
                DataType fieldType = ut.getFieldType(fieldName);
                fa = fa.name(fieldName).type(inferSchema(fieldType)).noDefault();
            }
            return AvroUtils.wrapAsNullable(fa.endRecord());
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
                    Class<?> elementClass = mCassandraAllTypes.get(elementType.getName());
                    Schema elementSchema = AvroUtils.unwrapIfNullable(AvroUtils.unwrapIfNullable(schema).getElementType());
                    return (AvroConverter<? super T, ?>) new ConvertAvroList(datumClass, schema,
                            getConverter(elementType, elementSchema, elementClass));
                case MAP:
                    DataType valueType = type.getTypeArguments().get(1);
                    Class<?> valueClass = mCassandraAllTypes.get(valueType.getName());
                    Schema valueSchema = AvroUtils.unwrapIfNullable(AvroUtils.unwrapIfNullable(schema).getValueType());
                    return (AvroConverter<? super T, ?>) new ConvertAvroMap(datumClass, schema,
                            getConverter(valueType, valueSchema, valueClass));
                case TUPLE:
                    return (AvroConverter<? super T, ?>) createAdapterFactory(TupleValue.class);
                case UDT:
                    return (AvroConverter<? super T, ?>) createAdapterFactory(UDTValue.class);
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

    public static String getDataType(Schema schema) {
        schema = AvroUtils.unwrapIfNullable(schema);
        String overrideDataType = schema.getProp(SchemaConstants.TALEND_COLUMN_DB_TYPE);
        if(overrideDataType == null || "".equals(overrideDataType)){
            //FIXME(bchen) give the best choice based on the current avro type
        }
        return overrideDataType.toLowerCase();
    }

    public void buildAdaptersUsingDataType(AvroConverter<?, ?> ac, DataType t) {
        if (t != null) {
            switch (t.getName()) {
                // The container types need to be recursively built.
                case LIST:
                case SET:
                    for (AvroConverter<?, ?> nestedAc : ((ConvertAvroList<?, ?>) ac).getNestedAvroConverters())
                        buildAdaptersUsingDataType(nestedAc, t.getTypeArguments().get(0));
                    return;
                case MAP:
                    for (AvroConverter<?, ?> nestedAc : ((ConvertAvroMap<?, ?>) ac).getNestedAvroConverters())
                        buildAdaptersUsingDataType(nestedAc, t.getTypeArguments().get(1));
                    return;
                // User defined types need to have their type set, and then fall through to have their fields built.
                case TUPLE:
                    TupleValueAdapterFactory tvff = (TupleValueAdapterFactory) ac;
                    tvff.setContainerType((TupleType) t);
                    break;
                case UDT:
                    UDTValueAdapterFactory uvff = (UDTValueAdapterFactory) ac;
                    uvff.setContainerType((UserType) t);
                    break;
                default:
                    // All other types do not need to be built.
                    return;
            }
        }

        // Build all of the fields.
        if (ac instanceof CassandraBaseAdapterFactory) {
            CassandraBaseAdapterFactory<?, ?, ?> cbff = (CassandraBaseAdapterFactory<?, ?, ?>) ac;
            int i = 0;
            for (AvroConverter<?, ?> nestedAc : cbff.getNestedAvroConverters())
                buildAdaptersUsingDataType(nestedAc, cbff.getFieldType(i++));
        }
    }

    /** A utility class for getting callbacks for reading and writing to Cassandra containers. */
    //TODO(bchen) can it be DataType.Name, to accordance with mCassandraAllTypes
    private static class CassandraContainerRegistry
            extends ContainerRegistry<DataType, GettableByIndexData, SettableByIndexData<?>> {

        public CassandraContainerRegistry() {
            // The following types can be read without requiring any additional information.
            registerReader(DataType.ascii(), (c, i) -> c.getString(i));
            registerReader(DataType.bigint(), (c, i) -> c.getLong(i));
            registerReader(DataType.blob(), (c, i) -> c.getBytes(i));
            registerReader(DataType.cboolean(), (c, i) -> c.getBool(i));
            registerReader(DataType.counter(), (c, i) -> c.getLong(i));
            registerReader(DataType.date(), (c, i) -> c.getDate(i)); // from 2.2
            registerReader(DataType.decimal(), (c, i) -> c.getDecimal(i));
            registerReader(DataType.cdouble(), (c, i) -> c.getDouble(i));
            registerReader(DataType.cfloat(), (c, i) -> c.getFloat(i));
            registerReader(DataType.inet(), (c, i) -> c.getInet(i));
            registerReader(DataType.cint(), (c, i) -> c.getInt(i));
            registerReader(DataType.smallint(), (c, i) -> c.getShort(i)); // from 2.2
            registerReader(DataType.text(), (c, i) -> c.getString(i));
            registerReader(DataType.time(), (c, i) -> c.getTime(i)); // from 2.2
            registerReader(DataType.timestamp(), (c, i) -> c.getTimestamp(i)); // FIXME(bchen) different method between 2.1 and 3.0 API
            registerReader(DataType.timeuuid(), (c, i) -> c.getUUID(i));
            registerReader(DataType.tinyint(), (c, i) -> c.getByte(i)); // from 2.2
            registerReader(DataType.uuid(), (c, i) -> c.getUUID(i));
            registerReader(DataType.varchar(), (c, i) -> c.getString(i));
            registerReader(DataType.varint(), (c, i) -> c.getVarint(i));

            // The following types can be written without any additional information.
            registerWriter(DataType.ascii(), (SettableByIndexData<?> c, int i, String v) -> c.setString(i, v));
            registerWriter(DataType.bigint(), (SettableByIndexData<?> c, int i, Long v) -> c.setLong(i, v));
            registerWriter(DataType.blob(), (SettableByIndexData<?> c, int i, ByteBuffer v) -> c.setBytes(i, v));
            registerWriter(DataType.cboolean(), (SettableByIndexData<?> c, int i, Boolean v) -> c.setBool(i, v));
            registerWriter(DataType.counter(), (SettableByIndexData<?> c, int i, Long v) -> c.setLong(i, v));
            registerWriter(DataType.date(), (SettableByIndexData<?> c, int i, LocalDate v) -> c.setDate(i, v)); // from 2.2
            registerWriter(DataType.decimal(), (SettableByIndexData<?> c, int i, BigDecimal v) -> c.setDecimal(i, v));
            registerWriter(DataType.cdouble(), (SettableByIndexData<?> c, int i, Double v) -> c.setDouble(i, v));
            registerWriter(DataType.cfloat(), (SettableByIndexData<?> c, int i, Float v) -> c.setFloat(i, v));
            registerWriter(DataType.inet(), (SettableByIndexData<?> c, int i, InetAddress v) -> c.setInet(i, v));
            registerWriter(DataType.cint(), (SettableByIndexData<?> c, int i, Integer v) -> c.setInt(i, v));
            registerWriter(DataType.smallint(), (SettableByIndexData<?> c, int i, Short v) -> c.setShort(i, v)); // from 2.2
            registerWriter(DataType.text(), (SettableByIndexData<?> c, int i, String v) -> c.setString(i, v));
            registerWriter(DataType.time(), (SettableByIndexData<?> c, int i, Long v) -> c.setTime(i, v)); // from 2.2
            registerWriter(DataType.timestamp(), (SettableByIndexData<?> c, int i, Date v) -> c.setTimestamp(i, v)); // FIXME(bchen) different method between 2.1 and 3.0 API
            registerWriter(DataType.timeuuid(), (SettableByIndexData<?> c, int i, UUID v) -> c.setUUID(i, v));
            registerWriter(DataType.tinyint(), (SettableByIndexData<?> c, int i, Byte v) -> c.setByte(i, v)); // from 2.2
            registerWriter(DataType.uuid(), (SettableByIndexData<?> c, int i, UUID v) -> c.setUUID(i, v));
            registerWriter(DataType.varchar(), (SettableByIndexData<?> c, int i, String v) -> c.setString(i, v));
            registerWriter(DataType.varint(), (SettableByIndexData<?> c, int i, BigInteger v) -> c.setVarint(i, v));
        }

        @Override
        public ContainerReaderByIndex<GettableByIndexData, ?> getReader(DataType type) {
            ContainerReaderByIndex<GettableByIndexData, ?> reader = super.getReader(type);
            if (reader != null)
                return reader;

            if (type.isCollection()) {
                List<DataType> containedTypes = type.getTypeArguments();
                if (type.getName().equals(DataType.Name.LIST)) {
                    return (c, i) -> c.getList(i, mCassandraAllTypes.get(containedTypes.get(0).getName()));
                } else if (type.getName().equals(DataType.Name.SET)) {
                    // TODO: is there a better way to enforce a consistent order after read?
                    return (c, i) -> new ArrayList<>(c.getSet(i, mCassandraAllTypes.get(containedTypes.get(0).getName())));
                } else if (type.getName().equals(DataType.Name.MAP)) {
                    return (c, i) -> c.getMap(i, mCassandraAllTypes.get(containedTypes.get(0).getName()), mCassandraAllTypes.get(containedTypes.get(1).getName()));
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

        @Override
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

}
