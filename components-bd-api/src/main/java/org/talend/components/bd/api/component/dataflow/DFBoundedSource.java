package org.talend.components.bd.api.component.dataflow;

import com.google.cloud.dataflow.sdk.coders.Coder;
import com.google.cloud.dataflow.sdk.coders.MapCoder;
import com.google.cloud.dataflow.sdk.coders.StringUtf8Coder;
import com.google.cloud.dataflow.sdk.io.BoundedSource;
import com.google.cloud.dataflow.sdk.options.DataflowPipelineWorkerPoolOptions;
import com.google.cloud.dataflow.sdk.options.PipelineOptions;
import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.runtime.input.SingleSplit;
import org.talend.components.api.runtime.input.Source;
import org.talend.components.api.runtime.input.Split;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.schema.SchemaElement;
import org.talend.daikon.schema.internal.DataSchemaElement;
import org.talend.daikon.schema.type.TypeMapping;

import java.io.IOException;
import java.util.*;

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
    public List<? extends BoundedSource<Map<String, String>>> splitIntoBundles(long desiredBundleSizeBytes, PipelineOptions options) throws Exception {
        List<DFBoundedSource> sourceList = new ArrayList<>();
        DataflowPipelineWorkerPoolOptions poolOptions =
                options.as(DataflowPipelineWorkerPoolOptions.class);
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
        //TODO source.getCount?
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
        org.talend.components.api.runtime.input.Reader reader;

        public DFBoundedReader(DFBoundedSource dfsource, org.talend.components.api.runtime.input.Reader reader) {
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
            Map<String, String> result = new HashMap<>();
            List<SchemaElement> fields = reader.getSchema();
            for (SchemaElement column : fields) {
                DataSchemaElement dataFiled = (DataSchemaElement) column;
                try {
                    result.put(dataFiled.getName(), TypeMapping.convert(source.getFamilyName(), dataFiled, dataFiled.getAppColType().newInstance().retrieveTValue(reader.getCurrent(), dataFiled.getAppColName())).toString());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return result;
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
