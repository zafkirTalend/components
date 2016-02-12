package org.talend.components.cassandra;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.avro.Schema;
import org.talend.daikon.schema.type.AvroConverter;
import org.talend.daikon.schema.type.ContainerReader;
import org.talend.daikon.schema.type.ContainerWriter;
import org.talend.daikon.schema.type.DatumRegistry;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Row;

/**
 * https://docs.datastax.com/en/latest-java-driver/java-driver/reference/javaClass2Cql3Datatypes.html
 */
public class CassandraAvroRegistry {

    public static final String FAMILY_NAME = "Cassandra";

    public String getFamilyName() {
        return FAMILY_NAME;
    }

    private static final DatumRegistry mConverterRegistry = new DatumRegistry();

    private static final Map<DataType.Name, Class<?>> sCassandraTypes;

    private static final Map<DataType.Name, ReadWrite<?>> sReadWrite;

    static {
        // Ensure that other components know how to process Row objects.
        DatumRegistry.registerFacadeFactory(Row.class, RowReadOnlyWrapper.class);

        // Maps from the Cassandra DataType.Name to the Avro Schema that should represent it.
        {
            Map<DataType.Name, Class<?>> m = new HashMap<>();

            // These specific types are easily mappable to Avro types. The actual CQL type is in the comment.
            m.put(DataType.Name.ASCII, String.class);
            m.put(DataType.Name.BIGINT, Long.class);
            m.put(DataType.Name.BLOB, ByteBuffer.class);
            m.put(DataType.Name.BOOLEAN, Boolean.class);
            m.put(DataType.Name.COUNTER, Long.class);
            m.put(DataType.Name.DECIMAL, BigDecimal.class);
            m.put(DataType.Name.DOUBLE, Double.class);
            m.put(DataType.Name.FLOAT, Float.class);
            m.put(DataType.Name.INET, InetAddress.class);
            m.put(DataType.Name.INT, Integer.class);
            m.put(DataType.Name.TEXT, String.class);
            m.put(DataType.Name.TIMESTAMP, Date.class);
            m.put(DataType.Name.TIMEUUID, UUID.class);
            m.put(DataType.Name.UUID, UUID.class);
            m.put(DataType.Name.VARCHAR, String.class);
            m.put(DataType.Name.VARINT, BigInteger.class);

            // These are not yet implemented by our version of Cassandra.
            // m.put(DataType.Name.DATE, ...) // com.datastax.driver.core.LocalDate
            // m.put(DataType.Name.SMALLINT, Schema.create(...)); // short
            // m.put(DataType.Name.TINYINT, Schema.create(...)); // byte

            // These require more information from the DataType and can't be found just using the name.
            // m.put(DataType.Name.LIST, Schema.create(...)); // java.util.List<T>
            // m.put(DataType.Name.MAP, Schema.create(...)); // java.util.Map<K, V>
            // m.put(DataType.Name.SET, Schema.create(...)); // java.util.Set<T>
            sCassandraTypes = Collections.unmodifiableMap(m);
        }

        {
            Map<DataType.Name, ReadWrite<?>> m = new HashMap<>();

            // These specific types are easily mappable to Avro types. The actual CQL type is in the comment.
            m.put(DataType.Name.ASCII, new ReadWriteString());
            m.put(DataType.Name.BIGINT, new ReadWriteLong());
            m.put(DataType.Name.BLOB, new ReadWriteByteBuffer());
            m.put(DataType.Name.BOOLEAN, new ReadWriteBoolean());
            m.put(DataType.Name.COUNTER, new ReadWriteLong());
            m.put(DataType.Name.DECIMAL, new ReadWriteBigDecimal());
            m.put(DataType.Name.DOUBLE, new ReadWriteDouble());
            m.put(DataType.Name.FLOAT, new ReadWriteFloat());
            m.put(DataType.Name.INET, new ReadWriteInetAddress());
            m.put(DataType.Name.INT, new ReadWriteInt());
            m.put(DataType.Name.TEXT, new ReadWriteString());
            m.put(DataType.Name.TIMESTAMP, new ReadWriteDate());
            m.put(DataType.Name.TIMEUUID, new ReadWriteUUID());
            m.put(DataType.Name.UUID, new ReadWriteUUID());
            m.put(DataType.Name.VARCHAR, new ReadWriteString());
            m.put(DataType.Name.VARINT, new ReadWriteBigInteger());

            // These are not yet implemented by our version of Cassandra.
            // m.put(DataType.Name.DATE, ...) // com.datastax.driver.core.LocalDate
            // m.put(DataType.Name.SMALLINT, Schema.create(...)); // short
            // m.put(DataType.Name.TINYINT, Schema.create(...)); // byte

            // These require more information from the DataType and can't be found just using the name.
            // m.put(DataType.Name.LIST, Schema.create(...)); // java.util.List<T>
            // m.put(DataType.Name.MAP, Schema.create(...)); // java.util.Map<K, V>
            // m.put(DataType.Name.SET, Schema.create(...)); // java.util.Set<T>
            sReadWrite = Collections.unmodifiableMap(m);
        }
    }

    public static <T> AvroConverter<T, ?> getConverter(Class<T> specificClass) {
        return mConverterRegistry.getConverter(specificClass);
    }

    public static Schema getSchema(DataType type) {
        Class<?> specificClass = sCassandraTypes.get(type.getName());
        if (specificClass == null) {
            // TODO(rskraba): add these types
            // m3.put(LIST, ...
            // m3.put(MAP, ...
            // m3.put(SET, ...
            // m3.put(UDT, ...
            throw new RuntimeException("The DataType " + type + " is not handled.");
        }

        AvroConverter<?, ?> ac = mConverterRegistry.getConverter(specificClass);
        if (ac == null) {
            // TODO(rskraba): Error handling?
            throw new RuntimeException("The DataType " + type + " is not handled.");
        }

        return ac.getAvroSchema();
    }

    public static ContainerReader<Row, ?> getReader(DataType type) {
        ContainerReader<Row, ?> reader = sReadWrite.get(type.getName());
        if (reader == null) {
            // TODO(rskraba): add these types
            // m3.put(LIST, ...
            // m3.put(MAP, ...
            // m3.put(SET, ...
            // m3.put(UDT, ...
            throw new RuntimeException("The DataType " + type + " is not handled.");
        }
        return reader;
    }

    public static ContainerWriter<BoundStatement, ?> getWriter(DataType type) {
        ContainerWriter<BoundStatement, ?> writer = sReadWrite.get(type.getName());
        if (writer == null) {
            // TODO(rskraba): add these types
            // m3.put(LIST, ...
            // m3.put(MAP, ...
            // m3.put(SET, ...
            // m3.put(UDT, ...
            throw new RuntimeException("The DataType " + type + " is not handled.");
        }
        return writer;
    }

    public static DataType getDataType(Schema schema) {
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

    /**
     * Common interface for the callbacks that know how to read to and write from Cassandra specific containers.
     * 
     * @param <T> The specific class that will be read/written for the callback.
     */
    public static interface ReadWrite<T> extends ContainerReader<Row, T>, ContainerWriter<BoundStatement, T> {
    }

    public static class ReadWriteBigDecimal implements ReadWrite<BigDecimal> {

        public BigDecimal readValue(Row row, String key) {
            return row.getDecimal(key);
        }

        public BigDecimal readValue(Row row, int index) {
            return row.getDecimal(index);
        }

        public void writeValue(BoundStatement bs, String key, BigDecimal value) {
            bs.setDecimal(key, value);
        }

        public void writeValue(BoundStatement bs, int index, BigDecimal value) {
            bs.setDecimal(index, value);
        }
    }

    public static class ReadWriteBigInteger implements ReadWrite<BigInteger> {

        public BigInteger readValue(Row row, String key) {
            return row.getVarint(key);
        }

        public BigInteger readValue(Row row, int index) {
            return row.getVarint(index);
        }

        @Override
        public void writeValue(BoundStatement bs, String key, BigInteger value) {
            bs.setVarint(key, value);
        }

        @Override
        public void writeValue(BoundStatement bs, int index, BigInteger value) {
            bs.setVarint(index, value);
        }
    }

    public static class ReadWriteBoolean implements ReadWrite<Boolean> {

        public Boolean readValue(Row row, String key) {
            return row.getBool(key);
        }

        public Boolean readValue(Row row, int index) {
            return row.getBool(index);
        }

        @Override
        public void writeValue(BoundStatement bs, String key, Boolean value) {
            bs.setBool(key, value);
        }

        @Override
        public void writeValue(BoundStatement bs, int index, Boolean value) {
            bs.setBool(index, value);
        }
    }

    public static class ReadWriteByteBuffer implements ReadWrite<ByteBuffer> {

        public ByteBuffer readValue(Row row, String key) {
            return row.getBytes(key);
        }

        public ByteBuffer readValue(Row row, int index) {
            return row.getBytes(index);
        }

        @Override
        public void writeValue(BoundStatement bs, String key, ByteBuffer value) {
            bs.setBytes(key, value);
        }

        @Override
        public void writeValue(BoundStatement bs, int index, ByteBuffer value) {
            bs.setBytes(index, value);
        }
    }

    public static class ReadWriteDate implements ReadWrite<Date> {

        public Date readValue(Row row, String key) {
            return row.getDate(key);
        }

        public Date readValue(Row row, int index) {
            return row.getDate(index);
        }

        @Override
        public void writeValue(BoundStatement bs, String key, Date value) {
            bs.setDate(key, value);
        }

        @Override
        public void writeValue(BoundStatement bs, int index, Date value) {
            bs.setDate(index, value);
        }
    }

    public static class ReadWriteDouble implements ReadWrite<Double> {

        public Double readValue(Row row, String key) {
            return row.getDouble(key);
        }

        public Double readValue(Row row, int index) {
            return row.getDouble(index);
        }

        @Override
        public void writeValue(BoundStatement bs, String key, Double value) {
            bs.setDouble(key, value);
        }

        @Override
        public void writeValue(BoundStatement bs, int index, Double value) {
            bs.setDouble(index, value);
        }
    }

    public static class ReadWriteFloat implements ReadWrite<Float> {

        public Float readValue(Row row, String key) {
            return row.getFloat(key);
        }

        public Float readValue(Row row, int index) {
            return row.getFloat(index);
        }

        @Override
        public void writeValue(BoundStatement bs, String key, Float value) {
            bs.setFloat(key, value);
        }

        @Override
        public void writeValue(BoundStatement bs, int index, Float value) {
            bs.setFloat(index, value);
        }
    }

    public static class ReadWriteInetAddress implements ReadWrite<InetAddress> {

        public InetAddress readValue(Row row, String key) {
            return row.getInet(key);
        }

        public InetAddress readValue(Row row, int index) {
            return row.getInet(index);
        }

        @Override
        public void writeValue(BoundStatement bs, String key, InetAddress value) {
            bs.setInet(key, value);
        }

        @Override
        public void writeValue(BoundStatement bs, int index, InetAddress value) {
            bs.setInet(index, value);
        }
    }

    public static class ReadWriteInt implements ReadWrite<Integer> {

        public Integer readValue(Row row, String key) {
            return row.getInt(key);
        }

        public Integer readValue(Row row, int index) {
            return row.getInt(index);
        }

        @Override
        public void writeValue(BoundStatement bs, String key, Integer value) {
            bs.setInt(key, value);
        }

        @Override
        public void writeValue(BoundStatement bs, int index, Integer value) {
            bs.setInt(index, value);
        }
    }

    public static class ReadWriteLong implements ReadWrite<Long> {

        public Long readValue(Row row, String key) {
            return row.getLong(key);
        }

        public Long readValue(Row row, int index) {
            return row.getLong(index);
        }

        @Override
        public void writeValue(BoundStatement bs, String key, Long value) {
            bs.setLong(key, value);
        }

        @Override
        public void writeValue(BoundStatement bs, int index, Long value) {
            bs.setLong(index, value);
        }
    }

    public static class ReadWriteString implements ReadWrite<String> {

        public String readValue(Row row, String key) {
            return row.getString(key);
        }

        public String readValue(Row row, int index) {
            return row.getString(index);
        }

        @Override
        public void writeValue(BoundStatement bs, String key, String value) {
            bs.setString(key, value);
        }

        @Override
        public void writeValue(BoundStatement bs, int index, String value) {
            bs.setString(index, value);
        }
    }

    public static class ReadWriteUUID implements ReadWrite<UUID> {

        public UUID readValue(Row row, String key) {
            return row.getUUID(key);
        }

        public UUID readValue(Row row, int index) {
            return row.getUUID(index);
        }

        @Override
        public void writeValue(BoundStatement bs, String key, UUID value) {
            bs.setUUID(key, value);
        }

        @Override
        public void writeValue(BoundStatement bs, int index, UUID value) {
            bs.setUUID(index, value);
        }
    }
}
