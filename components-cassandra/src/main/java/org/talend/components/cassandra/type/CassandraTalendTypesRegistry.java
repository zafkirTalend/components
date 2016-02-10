package org.talend.components.cassandra.type;

import static com.datastax.driver.core.DataType.Name.ASCII;
import static com.datastax.driver.core.DataType.Name.BIGINT;
import static com.datastax.driver.core.DataType.Name.BLOB;
import static com.datastax.driver.core.DataType.Name.BOOLEAN;
import static com.datastax.driver.core.DataType.Name.COUNTER;
import static com.datastax.driver.core.DataType.Name.DECIMAL;
import static com.datastax.driver.core.DataType.Name.DOUBLE;
import static com.datastax.driver.core.DataType.Name.FLOAT;
import static com.datastax.driver.core.DataType.Name.INET;
import static com.datastax.driver.core.DataType.Name.INT;
import static com.datastax.driver.core.DataType.Name.LIST;
import static com.datastax.driver.core.DataType.Name.MAP;
import static com.datastax.driver.core.DataType.Name.SET;
import static com.datastax.driver.core.DataType.Name.TEXT;
import static com.datastax.driver.core.DataType.Name.TIMESTAMP;
import static com.datastax.driver.core.DataType.Name.TIMEUUID;
import static com.datastax.driver.core.DataType.Name.UUID;
import static com.datastax.driver.core.DataType.Name.VARCHAR;
import static com.datastax.driver.core.DataType.Name.VARINT;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.talend.components.cassandra.CassandraProperties;
import org.talend.daikon.schema.SchemaElement;
import org.talend.daikon.schema.SchemaElement.Type;
import org.talend.daikon.schema.type.ExternalBaseType;
import org.talend.daikon.schema.type.TypesRegistry;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Row;

/**
 * Created by bchen on 16-1-15.
 */
public class CassandraTalendTypesRegistry implements TypesRegistry<CassandraTalendTypesRegistry.CassandraBaseType<?, ?>> {

    @Override
    public String getFamilyName() {
        return CassandraProperties.FAMILY_NAME;
    }

    private static final Map<Class<? extends CassandraBaseType<?, ?>>, SchemaElement.Type> converterToKnown;

    private static final Map<DataType.Name, Class<? extends CassandraBaseType<?, ?>>> specificToConverter;

    static {
        // Set up the maps for type converter discovery.
        Map<Class<? extends CassandraBaseType<?, ?>>, SchemaElement.Type> m1 = new HashMap<>();
        m1.put(Cassandra_ASCII.class, SchemaElement.Type.STRING);
        m1.put(Cassandra_BIGINT.class, SchemaElement.Type.LONG);
        m1.put(Cassandra_BLOB.class, SchemaElement.Type.BYTE_ARRAY);
        m1.put(Cassandra_BOOLEAN.class, SchemaElement.Type.BOOLEAN);
        m1.put(Cassandra_COUNTER.class, SchemaElement.Type.LONG);
        m1.put(Cassandra_DECIMAL.class, SchemaElement.Type.DECIMAL);
        m1.put(Cassandra_DOUBLE.class, SchemaElement.Type.DOUBLE);
        m1.put(Cassandra_FLOAT.class, SchemaElement.Type.FLOAT);
        m1.put(Cassandra_INET.class, SchemaElement.Type.OBJECT);
        m1.put(Cassandra_INT.class, SchemaElement.Type.INT);
        m1.put(Cassandra_LIST.class, SchemaElement.Type.LIST);
        m1.put(Cassandra_MAP.class, SchemaElement.Type.OBJECT);
        m1.put(Cassandra_SET.class, SchemaElement.Type.OBJECT);
        m1.put(Cassandra_TEXT.class, SchemaElement.Type.STRING);
        m1.put(Cassandra_TIMESTAMP.class, SchemaElement.Type.DATE);
        m1.put(Cassandra_TIMEUUID.class, SchemaElement.Type.STRING);
        m1.put(Cassandra_UUID.class, SchemaElement.Type.STRING);
        m1.put(Cassandra_VARCHAR.class, SchemaElement.Type.STRING);
        m1.put(Cassandra_VARINT.class, SchemaElement.Type.OBJECT);
        converterToKnown = Collections.unmodifiableMap(m1);

        Map<DataType.Name, Class<? extends CassandraBaseType<?, ?>>> m2 = new HashMap<>();
        m2.put(ASCII, Cassandra_ASCII.class);
        m2.put(BIGINT, Cassandra_BIGINT.class);
        m2.put(BLOB, Cassandra_BLOB.class);
        m2.put(BOOLEAN, Cassandra_BOOLEAN.class);
        m2.put(COUNTER, Cassandra_COUNTER.class);
        m2.put(DECIMAL, Cassandra_DECIMAL.class);
        m2.put(DOUBLE, Cassandra_DOUBLE.class);
        m2.put(FLOAT, Cassandra_FLOAT.class);
        m2.put(INET, Cassandra_INET.class);
        m2.put(INT, Cassandra_INT.class);
        m2.put(LIST, Cassandra_LIST.class);
        m2.put(MAP, Cassandra_MAP.class);
        m2.put(SET, Cassandra_SET.class);
        m2.put(TEXT, Cassandra_TEXT.class);
        m2.put(TIMESTAMP, Cassandra_TIMESTAMP.class);
        m2.put(TIMEUUID, Cassandra_TIMEUUID.class);
        m2.put(UUID, Cassandra_UUID.class);
        m2.put(VARCHAR, Cassandra_VARCHAR.class);
        m2.put(VARINT, Cassandra_VARINT.class);
        specificToConverter = Collections.unmodifiableMap(m2);
    }

    // TODO(rskraba): return an instance instead of a class. rename for clarity
    @Override
    public Map<Class<? extends CassandraBaseType<?, ?>>, Type> getMapping() {
        return converterToKnown;
    }

    // TODO(rskraba): where is this used?
    public Map<DataType.Name, Class<? extends CassandraBaseType<?, ?>>> getConverter() {
        return specificToConverter;
    }

    /*
     * The base classes for Cassandra type mapping --------------------------
     */

    /**
     * All of the Cassandra data can be read from {@link Row} and written to a {@link BoundStatement}.
     */
    public static interface CassandraBaseType<SpecificT, KnownT>
            extends ExternalBaseType<SpecificT, KnownT, Row, BoundStatement> {
    }

    /**
     * A special converter that does not need to convert to and from a specific and known type.
     */
    public abstract static class CassandraUnconvertedBaseType<T> implements CassandraBaseType<T, T> {

        @Override
        public final T convertFromKnown(T value) {
            return value;
        }

        @Override
        public final T convertToKnown(T value) {
            return value;
        }
    }

    /*
     * The specific converters for Cassandra type mapping -------------------
     */

    public static class Cassandra_ASCII extends CassandraUnconvertedBaseType<String> {

        @Override
        public String readValue(Row app, String key) {
            return app.getString(key);
        }

        @Override
        public void writeValue(BoundStatement app, String key, String value) {
            app.setString(key, value);
        }
    }

    public static class Cassandra_BIGINT extends CassandraUnconvertedBaseType<Long> {

        @Override
        public Long readValue(Row app, String key) {
            return app.getLong(key);
        }

        @Override
        public void writeValue(BoundStatement app, String key, Long value) {
            app.setLong(key, value);
        }

    }

    public static class Cassandra_BLOB implements CassandraBaseType<ByteBuffer, byte[]> {

        @Override
        public ByteBuffer convertFromKnown(byte[] value) {
            return ByteBuffer.wrap(value);
        }

        @Override
        public byte[] convertToKnown(ByteBuffer value) {
            byte[] bytes = new byte[value.remaining()];
            value.get(bytes, 0, bytes.length);
            return bytes;
        }

        @Override
        public ByteBuffer readValue(Row app, String key) {
            return app.getBytes(key);
        }

        @Override
        public void writeValue(BoundStatement app, String key, ByteBuffer value) {
            app.setBytes(key, value);
        }

    }

    public static class Cassandra_BOOLEAN extends CassandraUnconvertedBaseType<Boolean> {

        @Override
        public Boolean readValue(Row app, String key) {
            return app.getBool(key);
        }

        @Override
        public void writeValue(BoundStatement app, String key, Boolean value) {
            app.setBool(key, value);
        }

    }

    public static class Cassandra_COUNTER extends CassandraUnconvertedBaseType<Long> {

        @Override
        public Long readValue(Row app, String key) {
            return app.getLong(key);
        }

        @Override
        public void writeValue(BoundStatement app, String key, Long value) {
            app.setLong(key, value);
        }

    }

    public static class Cassandra_DECIMAL extends CassandraUnconvertedBaseType<BigDecimal> {

        @Override
        public BigDecimal readValue(Row app, String key) {
            return app.getDecimal(key);
        }

        @Override
        public void writeValue(BoundStatement app, String key, BigDecimal value) {
            app.setDecimal(key, value);
        }

    }

    public static class Cassandra_DOUBLE extends CassandraUnconvertedBaseType<Double> {

        @Override
        public Double readValue(Row app, String key) {
            return app.getDouble(key);
        }

        @Override
        public void writeValue(BoundStatement app, String key, Double value) {
            app.setDouble(key, value);
        }

    }

    public static class Cassandra_FLOAT extends CassandraUnconvertedBaseType<Float> {

        @Override
        public Float readValue(Row app, String key) {
            return app.getFloat(key);
        }

        @Override
        public void writeValue(BoundStatement app, String key, Float value) {
            app.setFloat(key, value);
        }

    }

    public static class Cassandra_INET extends CassandraUnconvertedBaseType<InetAddress> {

        @Override
        public InetAddress readValue(Row app, String key) {
            return app.getInet(key);
        }

        @Override
        public void writeValue(BoundStatement app, String key, InetAddress value) {
            app.setInet(key, value);
        }

    }

    public static class Cassandra_INT extends CassandraUnconvertedBaseType<Integer> {

        @Override
        public Integer readValue(Row app, String key) {
            return app.getInt(key);
        }

        @Override
        public void writeValue(BoundStatement app, String key, Integer value) {
            app.setInt(key, value);
        }

    }

    public static class Cassandra_LIST extends CassandraUnconvertedBaseType<List> {

        @Override
        public List readValue(Row app, String key) {
            return app.getList(key, Object.class);
        }

        @Override
        public void writeValue(BoundStatement app, String key, List value) {
            app.setList(key, value);
        }

    }

    public static class Cassandra_MAP extends CassandraUnconvertedBaseType<Map> {

        @Override
        public Map readValue(Row app, String key) {
            return app.getMap(key, Object.class, Object.class);
        }

        @Override
        public void writeValue(BoundStatement app, String key, Map value) {
            app.setMap(key, value);
        }

    }

    public static class Cassandra_SET extends CassandraUnconvertedBaseType<Set> {

        @Override
        public Set readValue(Row app, String key) {
            return app.getSet(key, Object.class);
        }

        @Override
        public void writeValue(BoundStatement app, String key, Set value) {
            app.setSet(key, value);
        }

    }

    public static class Cassandra_TEXT extends Cassandra_ASCII {

    }

    public static class Cassandra_TIMESTAMP extends CassandraUnconvertedBaseType<Date> {

        @Override
        public Date readValue(Row app, String key) {
            return app.getDate(key);
        }

        @Override
        public void writeValue(BoundStatement app, String key, Date value) {
            app.setDate(key, value);
        }

    }

    public static class Cassandra_TIMEUUID extends Cassandra_UUID {

    }

    public static class Cassandra_UUID implements CassandraBaseType<java.util.UUID, String> {

        @Override
        public java.util.UUID convertFromKnown(String value) {
            return java.util.UUID.fromString(value);
        }

        @Override
        public String convertToKnown(java.util.UUID value) {
            return value.toString();
        }

        @Override
        public java.util.UUID readValue(Row app, String key) {
            return app.getUUID(key);
        }

        @Override
        public void writeValue(BoundStatement app, String key, java.util.UUID value) {
            app.setUUID(key, value);
        }
    }

    public static class Cassandra_VARCHAR extends Cassandra_ASCII {

    }

    public static class Cassandra_VARINT extends CassandraUnconvertedBaseType<BigInteger> {

        @Override
        public BigInteger readValue(Row app, String key) {
            return app.getVarint(key);
        }

        @Override
        public void writeValue(BoundStatement app, String key, BigInteger value) {
            app.setVarint(key, value);
        }

    }

}
