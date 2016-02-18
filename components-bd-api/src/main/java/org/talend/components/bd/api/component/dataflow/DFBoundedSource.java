package org.talend.components.bd.api.component.dataflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.input.SingleSplit;
import org.talend.components.api.runtime.input.Source;
import org.talend.components.api.runtime.input.Split;
import org.talend.daikon.schema.type.DatumRegistry;
import org.talend.daikon.schema.type.IndexedRecordFacadeFactory;

import com.google.cloud.dataflow.sdk.coders.Coder;
import com.google.cloud.dataflow.sdk.coders.MapCoder;
import com.google.cloud.dataflow.sdk.coders.StringUtf8Coder;
import com.google.cloud.dataflow.sdk.io.BoundedSource;
import com.google.cloud.dataflow.sdk.options.DataflowPipelineWorkerPoolOptions;
import com.google.cloud.dataflow.sdk.options.PipelineOptions;

/**
 * Created by bchen on 16-1-17.
 */
public class DFBoundedSource extends BoundedSource<Map<String, String>> {

    Source source;

    Split split;

    public DFBoundedSource(Class<? extends Source> sourceClazz, ComponentProperties props) {
        try {
            this.source = sourceClazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            this.source.init(props);
        } catch (TalendConnectionException e) {
            e.printStackTrace();
        }
    }

    public DFBoundedSource(Split split) {
        this.split = split;
    }

    @Override
    public List<? extends BoundedSource<Map<String, String>>> splitIntoBundles(long desiredBundleSizeBytes,
            PipelineOptions options) throws Exception {
        List<DFBoundedSource> sourceList = new ArrayList<>();
        DataflowPipelineWorkerPoolOptions poolOptions = options.as(DataflowPipelineWorkerPoolOptions.class);
        if (source.supportSplit() && poolOptions.getNumWorkers() > 1) {
            Split[] split = source.getSplit(poolOptions.getNumWorkers());
            for (Split s : split) {
                sourceList.add(new DFBoundedSource(s));
            }
        } else {
            sourceList.add(new DFBoundedSource(new SingleSplit()));
        }
        return sourceList;
    }

    @Override
    public long getEstimatedSizeBytes(PipelineOptions options) throws Exception {
        // TODO source.getCount?
        return 0;
    }

    @Override
    public boolean producesSortedKeys(PipelineOptions options) throws Exception {
        return false;
    }

    @Override
    public BoundedReader createReader(PipelineOptions options) throws IOException {
        try {
            return new DFBoundedReader(this, source.getRecordReader(split));
        } catch (TalendConnectionException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void validate() {

    }

    @Override
    public Coder getDefaultOutputCoder() {
        return MapCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of());
    }

    public class DFBoundedReader extends BoundedReader<Map<String, String>> {

        DFBoundedSource dfsource;

        org.talend.components.api.runtime.input.Reader<? extends IndexedRecord> reader;

        public DFBoundedReader(DFBoundedSource dfsource,
                org.talend.components.api.runtime.input.Reader<? extends IndexedRecord> reader) {
            this.dfsource = dfsource;
            this.reader = reader;
        }

        @Override
        public boolean start() throws IOException {
            return reader.start();
        }

        @Override
        public boolean advance() throws IOException {
            return reader.advance();
        }

        @Override
        public Map<String, String> getCurrent() throws NoSuchElementException {
            Object datum = reader.getCurrent();
            @SuppressWarnings("unchecked")
            // TODO(rskraba): This should only be done once.
            IndexedRecordFacadeFactory<Object, ? extends IndexedRecord> irff = (IndexedRecordFacadeFactory<Object, ? extends IndexedRecord>) DatumRegistry
                    .getFacadeFactory(datum.getClass());
            IndexedRecord in = irff.convertToAvro(datum);

            Map<String, String> out = new HashMap<>();
            Schema schema = in.getSchema();
            for (Field f : schema.getFields()) {
                out.put(f.name(), String.valueOf(in.get(f.pos())));
            }
            return out;
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }

        @Override
        public BoundedSource getCurrentSource() {
            return dfsource;
        }
    }
}
