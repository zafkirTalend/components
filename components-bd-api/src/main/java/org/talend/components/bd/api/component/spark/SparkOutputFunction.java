package org.talend.components.bd.api.component.spark;

import org.apache.hadoop.mapred.JobConf;
import org.apache.spark.api.java.function.VoidFunction;
import org.talend.components.api.component.output.Sink;
import org.talend.components.api.component.output.Writer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.row.BaseRowStruct;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Created by bchen on 16-1-18.
 */
public class SparkOutputFunction implements VoidFunction<Iterator<BaseRowStruct>>, Serializable {
    String className;
    String props;


    public SparkOutputFunction(JobConf conf) {
        className = conf.get("output.sink");
        props = conf.get("output.props");
    }

    @Override
    public void call(Iterator<BaseRowStruct> baseRowStructIterator) throws Exception {
        Class<? extends Sink> aClass = null;
        Sink sink = null;
        try {
            aClass = (Class<? extends Sink>) Class.forName(className);
            sink = aClass.newInstance();
            ComponentProperties.Deserialized deserialized = ComponentProperties.fromSerialized(props);
            ComponentProperties properties = deserialized.properties;
            sink.init(properties);
            Writer writer = sink.getRecordWriter();
            while (baseRowStructIterator.hasNext()) {
                writer.write(baseRowStructIterator.next());
            }
            writer.close();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
