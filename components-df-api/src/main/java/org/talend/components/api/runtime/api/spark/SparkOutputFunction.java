package org.talend.components.api.runtime.api.spark;

import org.apache.hadoop.mapred.JobConf;
import org.apache.spark.api.java.function.VoidFunction;
import org.talend.components.api.component.runtime.output.Sink;
import org.talend.components.api.component.runtime.output.Writer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.row.BaseRowStruct;

import java.util.Iterator;

/**
 * Created by bchen on 16-1-18.
 */
public class SparkOutputFunction implements VoidFunction<Iterator<BaseRowStruct>> {

    Sink sink;

    public SparkOutputFunction(JobConf conf) {
        Class<? extends Sink> aClass = null;
        try {
            aClass = (Class<? extends Sink>) Class.forName(conf.get("output.sink"));
            sink = aClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        String componentPropertiesString = conf.get("output.props");
        ComponentProperties.Deserialized deserialized = ComponentProperties.fromSerialized(componentPropertiesString);
        ComponentProperties properties = deserialized.properties;
        sink.init(properties);
    }

    @Override
    public void call(Iterator<BaseRowStruct> baseRowStructIterator) throws Exception {
        Writer writer = sink.getRecordWriter();
        while (baseRowStructIterator.hasNext()) {
            writer.write(baseRowStructIterator.next());
        }
        writer.close();
    }
}
