package org.talend.daikon.schema.avro;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.avro.Schema;
import org.talend.daikon.schema.type.AvroConverter;
import org.talend.daikon.schema.type.IndexedRecordFacadeFactory;

/**
 * 
 */
public class AvroRegistry {

    /** Registry of ways to discover a Schema from an object. */
    private Map<Class<?>, Function<?, ?>> mSchemaInferrer = new HashMap<>();

    /**
     * Registry of ways to discover a Schema from an object based on its class.
     */
    private static Map<Class<?>, AvroConverter<?, ?>> mRegistry = new HashMap<>();

    /**
     * Registry of ways to create an IndexedRecord from an object based on its class.
     */
    private static Map<Class<?>, Supplier<? extends IndexedRecordFacadeFactory<?, ?>>> mFacadeFactory = new HashMap<>();

    public <DatumT> void registerSchemaInferrer(Class<DatumT> datumClass, Function<? super DatumT, Schema> inferrer) {
        mSchemaInferrer.put(datumClass, inferrer);
    }

    public <T, FacadeFactoryT extends IndexedRecordFacadeFactory<? super T, ?>> void registerFacadeFactory(Class<T> datumClass,
            Class<FacadeFactoryT> facadeFactoryClass) {
        mFacadeFactory.put(datumClass, () -> createANew(facadeFactoryClass, datumClass));
    }

    protected <T> void registerFacadeFactory(Class<T> datumClass,
            Supplier<? extends IndexedRecordFacadeFactory<? super T, ?>> facadeFactoryFactory) {
        mFacadeFactory.put(datumClass, facadeFactoryFactory);
    }

    /**
     * Given an object, attempts to construct an Avro {@link Schema} that corresponds.
     * 
     * @param datum the object.
     * @return The schema for that object if one could be created.
     */
    public <DatumT> Schema inferSchema(DatumT datum) {
        if (datum == null)
            return null;
        // This is safe because of the contract of the registerSchemaInferrer
        // method.
        @SuppressWarnings("unchecked")
        Function<? super DatumT, Schema> inferrer = (Function<? super DatumT, Schema>) getFromClassRegistry(mSchemaInferrer,
                datum.getClass());
        if (inferrer == null)
            throw new RuntimeException("Cannot infer the schema from " + datum);
        return inferrer.apply(datum);
    }

    /**
     * Given a Map that takes a class and returns an value (likely a functor) that knows how to deal with that class,
     * this method performs a lookup.
     * 
     * If there is no exact match on the datum class, all of the super-classes are looked up then all of the interface
     * classes.
     * 
     * @param map A map keyed on Class.
     * @param datumClass The class to look up.
     * @return The best matching value from the map, or null if none is found.
     */
    public static <T> T getFromClassRegistry(Map<Class<?>, ? extends T> map, Class<?> datumClass) {
        if (datumClass == null)
            return null;

        T match = map.get(datumClass);
        if (match != null) {
            return match;
        }

        // Attempt to go up through the super classes.
        match = getFromClassRegistry(map, datumClass.getSuperclass());
        if (match != null) {
            return match;
        }

        // Attempt a match from all of the interface classes.
        for (Class<?> iClass : datumClass.getInterfaces()) {
            match = getFromClassRegistry(map, iClass);
            if (match != null)
                return match;
        }

        return null;
    }

    /**
     * Given a class and a parameter, attempts to create an instance of that object by looking for a constructor.
     * 
     * If a constructor with the parameter exists, it is preferred, otherwise the empty constructor is used.
     * 
     * @param cls The class to create.
     * @param optionalParam An optional parameter to use while finding the constructor.
     * @return An instance
     * @throws RuntimeException if any errors occurred while creating the instance.
     */
    private static <T> T createANew(Class<T> cls, Object optionalParam) {
        try {
            // Attempt to find a constructor that can be parameterized with the datum
            // class.
            return cls.getConstructor(optionalParam.getClass()).newInstance(optionalParam);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            // Ignore any errors if this attempt failed, since we'll try the empty
            // constructor next.
        }
        try {
            // Attempt to find a constructor that can be parameterized with the datum
            // class.
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
