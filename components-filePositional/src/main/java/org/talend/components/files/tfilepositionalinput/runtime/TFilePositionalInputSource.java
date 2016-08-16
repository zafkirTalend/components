package org.talend.components.files.tfilepositionalinput.runtime;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.avro.Schema;

import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;

/**
 * The TestInputSource provides the mechanism to supply data to other
 * components at run-time.
 *
 * Based on the Apache Beam project, the Source mechanism is appropriate to
 * describe distributed and non-distributed data sources and can be adapted
 * to scalable big data execution engines on a cluster, or run locally.
 *
 * This example component describes an input source that is guaranteed to be
 * run in a single JVM (whether on a cluster or locally), so:
 *
 * <ul>
 * <li>the simplified logic for reading is found in the {@link TestInputReader},
 *     and</li>
 * </ul>
 */
public class TFilePositionalInputSource extends TFilePositionalInputSourceOrSink implements BoundedSource {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Configuration extracted from the input properties. */
    //private TFileInputPositionalProperties properties;

	private transient Schema schema;

    public BoundedReader createReader(RuntimeContainer container) {
        return new TFilePositionalInputReader(container, this);
    }

     public List<? extends BoundedSource> splitIntoBundles(long desiredBundleSizeBytes, RuntimeContainer adaptor) throws Exception {
        // There can be only one.
        return Arrays.asList(this);
     }

     public long getEstimatedSizeBytes(RuntimeContainer adaptor) {
        // This will be ignored since the source will never be split.
        return 0;
     }

     public boolean producesSortedKeys(RuntimeContainer adaptor) {
        return false;
     }

}
