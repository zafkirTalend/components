package org.talend.components.bd.api.component.x_dataflow;

import com.google.cloud.dataflow.sdk.transforms.PTransform;
import com.google.cloud.dataflow.sdk.util.WindowingStrategy;
import com.google.cloud.dataflow.sdk.values.PCollection;
import com.google.cloud.dataflow.sdk.values.PDone;
import com.google.cloud.dataflow.sdk.values.PInput;
import org.talend.components.api.properties.ComponentProperties;

/**
 * Created by bchen on 16-1-25.
 */
public class DataflowIO {

    private DataflowIO() {
    }

    public static final class Read {

        private Read() {
        }

        public static Component<String> named(String name) {
            return new Component<String>().named(name);
        }

        public static class Component<T> extends PTransform<PInput, PCollection<T>> {

            private boolean streaming = false;

            private final ComponentProperties properties;

            Component() {
                this(null, null, false);
            }

            private Component(String name, ComponentProperties properties, boolean streaming) {
                super(name);
                this.properties = properties;
                this.streaming = streaming;
            }

            public Component<T> named(String name) {
                return new Component<>(name, properties, streaming);
            }

            public Component<T> fromProperties(ComponentProperties properties) {
                return new Component<>(name, properties, streaming);
            }

            // TODO set if unbounded in there or ?
            public Component<T> unbounded() {
                return new Component<>(name, properties, true);
            }

            public ComponentProperties getProperties() {
                return properties;
            }

            @Override
            public PCollection<T> apply(PInput input) {
                if (properties == null) {
                    throw new IllegalStateException("need to set the properties of a DataflowIO.Read transform");
                }
                return PCollection.<T> createPrimitiveOutputInternal(input.getPipeline(), WindowingStrategy.globalDefault(),
                        streaming ? PCollection.IsBounded.UNBOUNDED : PCollection.IsBounded.BOUNDED);
            }
        }
    }

    public static final class Write {

        private Write() {

        }

        public static Component<String> named(String name) {
            return new Component<String>().named(name);
        }

        public static class Component<T> extends PTransform<PCollection<T>, PDone> {

            private final ComponentProperties properties;

            // TODO how use streaming for output?
            private boolean streaming = false;

            Component() {
                this(null, null, false);
            }

            private Component(String name, ComponentProperties properties, boolean streaming) {
                super(name);
                this.properties = properties;
                this.streaming = streaming;
            }

            public Component<T> named(String name) {
                return new Component<>(name, properties, streaming);
            }

            public Component<T> fromProperties(ComponentProperties properties) {
                return new Component<>(name, properties, streaming);
            }

            // TODO set if unbounded in there or ?
            public Component<T> unbounded() {
                return new Component<>(name, properties, true);
            }

            public ComponentProperties getProperties() {
                return properties;
            }

            @Override
            public PDone apply(PCollection<T> input) {
                if (properties == null) {
                    throw new IllegalStateException("need to set the properties of a Dataflow.Write transform");
                }
                return PDone.in(input.getPipeline());
            }
        }
    }
}
