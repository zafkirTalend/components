package org.talend.components.processing.runtime.window;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarLongCoder;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.OutputTimeFns;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.transforms.windowing.WindowFn;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TimestampedValue;
import org.joda.time.Instant;
import org.junit.Test;

import org.apache.beam.runners.direct.DirectRunner;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.values.PCollection;
import org.talend.components.processing.definition.window.WindowProperties;

public class WindowRuntimeTestIT {

    private String output(String value, int count, int timestamp, int windowStart, int windowEnd) {
        return value + ":" + count + ":" + timestamp + ":[" + new Instant(windowStart) + ".." + new Instant(windowEnd) + ")";
    }

    @Test
    public void test() {

        PipelineOptions options = PipelineOptionsFactory.create();
        options.setRunner(DirectRunner.class);
        final Pipeline p = Pipeline.create(options);

        // creation of PCollection with different timestamp
        PCollection<String> input = p.apply(Create.timestamped(TimestampedValue.of("a", new Instant(0)),
                TimestampedValue.of("b", new Instant(0)), TimestampedValue.of("c", new Instant(1)),
                TimestampedValue.of("a", new Instant(2)), TimestampedValue.of("a", new Instant(2)),
                TimestampedValue.of("b", new Instant(2)), TimestampedValue.of("b", new Instant(3)),
                TimestampedValue.of("c", new Instant(3)), TimestampedValue.of("a", new Instant(4))));

        WindowProperties windowProperties = new WindowProperties("window");
        windowProperties.setValue("windowDuration", 4);
        windowProperties.setValue("slideWindow", 2);

        WindowRuntime windowRun = new WindowRuntime();
        windowRun.initialize(null, windowProperties);

        PCollection<String> test = windowRun.apply(input);

        PCollection<KV<String, Long>> windowed_counts = test.apply(Count.<String> perElement());
        /*
         * windowed_counts.setCoder(KvCoder.of(StringUtf8Coder.of(), VarLongCoder.of()));
         * PCollection<KV<String, Long>> windowed_count = windowed_counts.apply(
         * ParDo.of(new DoFn<KV<String, Long>, KV<String, Long>>() {
         * 
         * @ProcessElement
         * public void processElement(ProcessContext c) {
         * System.out.println(c.element());
         * }}));
         * windowed_count.setCoder(KvCoder.of(StringUtf8Coder.of(), VarLongCoder.of()));
         */
        // Fixed duration: 2
        /*
         * PAssert.that(windowed_counts).containsInAnyOrder(
         * KV.of("a",1L),
         * KV.of("a",2L),
         * KV.of("a",1L),
         * KV.of("b",1L),
         * KV.of("b",2L),
         * KV.of("c",1L),
         * KV.of("c",1L)
         * 
         * );
         */
        // Sliding duration: 4 - sliding: 2
        PAssert.that(windowed_counts).containsInAnyOrder(KV.of("a", 1L), KV.of("a", 1L), KV.of("a", 3L), KV.of("a", 3L),
                KV.of("b", 1L), KV.of("b", 3L), KV.of("b", 2L), KV.of("c", 1L), KV.of("c", 1L), KV.of("c", 2L));

        p.run();
    }
}
