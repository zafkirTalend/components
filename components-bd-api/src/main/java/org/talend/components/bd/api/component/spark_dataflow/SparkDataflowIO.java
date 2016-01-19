package org.talend.components.bd.api.component.spark_dataflow;

import com.google.cloud.dataflow.sdk.transforms.PTransform;
import com.google.cloud.dataflow.sdk.util.WindowingStrategy;
import com.google.cloud.dataflow.sdk.values.PCollection;
import com.google.cloud.dataflow.sdk.values.PDone;
import com.google.cloud.dataflow.sdk.values.PInput;
import org.talend.components.api.component.input.Source;
import org.talend.components.api.component.output.Sink;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.bd.api.component.spark.SparkInputConf;
import org.talend.components.bd.api.component.spark.SparkOutputConf;

/**
 * Created by bchen on 16-1-18.
 */
public class SparkDataflowIO {
    private SparkDataflowIO() {

    }

    public static final class Read {

        private Read() {

        }

        public static Bound<String> named(String name) {
            return new Bound<String>().named(name);
        }

        public static class Bound<T> extends PTransform<PInput, PCollection<T>> {

            private final ComponentProperties properties;

            private final Class<? extends Source> sourceClazz;
            private final Class<? extends SparkInputConf> sparkInputConfClazz;

            Bound() {
                this(null, null, null, null);
            }

            private Bound(String name, ComponentProperties properties, Class<? extends Source> sourceClazz, Class<? extends SparkInputConf> sparkInputConfClazz) {
                super(name);
                this.properties = properties;
                this.sourceClazz = sourceClazz;
                this.sparkInputConfClazz = sparkInputConfClazz;
            }

            public Bound<T> named(String name) {
                return new Bound<>(name, properties, sourceClazz, sparkInputConfClazz);
            }

            public Bound<T> fromProperties(ComponentProperties properties) {
                return new Bound<>(name, properties, sourceClazz, sparkInputConfClazz);
            }

            public Bound<T> withSource(Class<? extends Source> sourceClazz) {
                return new Bound<>(name, properties, sourceClazz, sparkInputConfClazz);
            }

            public Bound<T> withSparkConf(Class<? extends SparkInputConf> sparkInputConfClazz) {
                return new Bound<>(name, properties, sourceClazz, sparkInputConfClazz);
            }

            public ComponentProperties getProperties() {
                return properties;
            }

            public Class<? extends Source> getSourceClazz() {
                return sourceClazz;
            }

            public Class<? extends SparkInputConf> getSparkInputConfClazz() {
                return sparkInputConfClazz;
            }

            @Override
            public PCollection<T> apply(PInput input) {
                if (properties == null) {
                    throw new IllegalStateException("need to set the properties of a CassandraIO.Read transform");
                }
                // Force the output's Coder to be what the read is using, and
                // unchangeable later, to ensure that we read the input in the
                // format specified by the Read transform.
                return PCollection.<T>createPrimitiveOutputInternal(
                        input.getPipeline(),
                        WindowingStrategy.globalDefault(),
                        PCollection.IsBounded.BOUNDED);
            }
        }
    }

    public static final class Write {
        private Write() {

        }

        public static Bound<String> named(String name) {
            return new Bound<String>().named(name);
        }

        public static class Bound<T> extends PTransform<PCollection<T>, PDone> {

            private final ComponentProperties properties;

            private final Class<? extends Sink> sinkClazz;
            private final Class<? extends SparkOutputConf> sparkOutputConfClazz;

            Bound() {
                this(null, null, null, null);
            }

            private Bound(String name, ComponentProperties properties, Class<? extends Sink> sinkClazz, Class<? extends SparkOutputConf> sparkOutputConfClazz) {
                super(name);
                this.properties = properties;
                this.sinkClazz = sinkClazz;
                this.sparkOutputConfClazz = sparkOutputConfClazz;
            }

            public Bound<T> named(String name) {
                return new Bound<>(name, properties, sinkClazz, sparkOutputConfClazz);
            }

            public Bound<T> fromProperties(ComponentProperties properties) {
                return new Bound<>(name, properties, sinkClazz, sparkOutputConfClazz);
            }

            public Bound<T> withSource(Class<? extends Sink> sinkClazz) {
                return new Bound<>(name, properties, sinkClazz, sparkOutputConfClazz);
            }

            public Bound<T> withSparkConf(Class<? extends SparkOutputConf> sparkOutputConfClazz) {
                return new Bound<>(name, properties, sinkClazz, sparkOutputConfClazz);
            }

            public ComponentProperties getProperties() {
                return properties;
            }

            public Class<? extends Sink> getSinkClazz() {
                return sinkClazz;
            }

            public Class<? extends SparkOutputConf> getSparkOutputConfClazz() {
                return sparkOutputConfClazz;
            }

            @Override
            public PDone apply(PCollection<T> input) {
                if (properties == null) {
                    throw new IllegalStateException("need to set the properties of a CassandraIO.Read transform");
                }
                // Force the output's Coder to be what the read is using, and
                // unchangeable later, to ensure that we read the input in the
                // format specified by the Read transform.
                return PDone.in(input.getPipeline());
            }
        }
    }
}
