package org.talend.components.processing.runtime.window;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TimestampedValue;
import org.joda.time.Instant;
import org.junit.Test;

import org.apache.beam.runners.direct.DirectRunner;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.values.PCollection;
import org.talend.components.adapter.beam.LazyAvroCoder;
import org.talend.components.processing.definition.window.WindowProperties;
import org.talend.daikon.avro.GenericDataRecordHelper;

import java.util.Arrays;
import java.util.List;

public class WindowRuntimeTest {

    @Test
    public void testFixedWindow() {

        PipelineOptions options = PipelineOptionsFactory.create();
        options.setRunner(DirectRunner.class);
        final Pipeline p = Pipeline.create(options);

        Object[] oA = new Object[] {"a"};
        Schema a = GenericDataRecordHelper.createSchemaFromObject("a", oA);
        System.out.println(a);
        IndexedRecord irA = GenericDataRecordHelper.createRecord(oA);
        System.out.println("a: " + irA);

        Object[] oB = new Object[] {"b"};
        IndexedRecord irB = GenericDataRecordHelper.createRecord(oA);

        Object[] oC = new Object[] {"c"};
        IndexedRecord irC = GenericDataRecordHelper.createRecord(oA);

        /*
        // creation of PCollection with different timestamp
        PCollection<IndexedRecord> input = p.apply(Create.timestamped(
                TimestampedValue.of(irA, new Instant(4)))
                        .withCoder(AvroCoder.of(a))
        );*/


        List<TimestampedValue<IndexedRecord>> data = Arrays.asList(
                TimestampedValue.of(irA, new Instant(1L)),
                TimestampedValue.of(irB, new Instant(2L)),
                TimestampedValue.of(irC, new Instant(3L)));

        PCollection<IndexedRecord> input = p.apply(Create.timestamped(data)
        .withCoder());


                WindowProperties windowProperties = new WindowProperties("window");
        windowProperties.setValue("windowDurationLength", 2);
        windowProperties.setValue("windowSlideLength", -1);

        WindowRuntime windowRun = new WindowRuntime();
        windowRun.initialize(null, windowProperties);

        PCollection<IndexedRecord> test = windowRun.apply(input);

        PCollection<KV<IndexedRecord, Long>> windowed_counts = test.apply(Count.<IndexedRecord> perElement());

        /////////
        // Fixed duration: 2

        PAssert.that(windowed_counts).containsInAnyOrder(
        /*
         * KV.of(irA, 1L),
         * KV.of(irB, 1L),
         * KV.of(irC, 1L)
         */
        );

        p.run();
    }
    /*
     * @Test
     * public void testSlidingWindow() {
     * 
     * PipelineOptions options = PipelineOptionsFactory.create();
     * options.setRunner(DirectRunner.class);
     * final Pipeline p = Pipeline.create(options);
     * 
     * // creation of PCollection with different timestamp
     * PCollection<String> input = p.apply(Create.timestamped(TimestampedValue.of("a", new Instant(0)),
     * TimestampedValue.of("b", new Instant(0)), TimestampedValue.of("c", new Instant(1)),
     * TimestampedValue.of("a", new Instant(2)), TimestampedValue.of("a", new Instant(2)),
     * TimestampedValue.of("b", new Instant(2)), TimestampedValue.of("b", new Instant(3)),
     * TimestampedValue.of("c", new Instant(3)), TimestampedValue.of("a", new Instant(4))));
     * 
     * WindowProperties windowProperties = new WindowProperties("window");
     * windowProperties.setValue("windowDurationLength", 4);
     * windowProperties.setValue("windowSlideLength", 2);
     * 
     * WindowRuntime windowRun = new WindowRuntime();
     * windowRun.initialize(null, windowProperties);
     * 
     * PCollection<String> test = windowRun.apply(input);
     * 
     * PCollection<KV<String, Long>> windowed_counts = test.apply(Count.<String> perElement());
     * 
     * // window duration: 4 - sliding: 2
     * PAssert.that(windowed_counts).containsInAnyOrder(KV.of("a", 1L), KV.of("a", 1L), KV.of("a", 3L), KV.of("a", 3L),
     * KV.of("b", 1L), KV.of("b", 3L), KV.of("b", 2L), KV.of("c", 1L), KV.of("c", 1L), KV.of("c", 2L));
     * 
     * p.run();
     * }
     * 
     * @Test
     * public void testSessionWindow() {
     * 
     * PipelineOptions options = PipelineOptionsFactory.create();
     * options.setRunner(DirectRunner.class);
     * final Pipeline p = Pipeline.create(options);
     * 
     * // creation of PCollection with different timestamp
     * PCollection<String> input = p.apply(Create.timestamped(
     * TimestampedValue.of("a", new Instant(0)),
     * TimestampedValue.of("b", new Instant(0)),
     * TimestampedValue.of("c", new Instant(1)),
     * TimestampedValue.of("a", new Instant(2)),
     * TimestampedValue.of("a", new Instant(2)),
     * TimestampedValue.of("b", new Instant(2)),
     * TimestampedValue.of("b", new Instant(30)),
     * TimestampedValue.of("a", new Instant(30)),
     * TimestampedValue.of("a", new Instant(50)),
     * TimestampedValue.of("c", new Instant(55)),
     * TimestampedValue.of("a", new Instant(59))));
     * 
     * WindowProperties windowProperties = new WindowProperties("window");
     * windowProperties.setValue("windowDurationLength", 10);
     * windowProperties.setValue("windowSession", true);
     * windowProperties.setValue("windowSlideLength", -1);
     * 
     * WindowRuntime windowRun = new WindowRuntime();
     * windowRun.initialize(null, windowProperties);
     * 
     * PCollection<String> test = windowRun.apply(input);
     * 
     * PCollection<KV<String, Long>> windowed_counts = test.apply(Count.<String> perElement());
     * 
     * PAssert.that(windowed_counts).containsInAnyOrder(
     * // window 1
     * KV.of("a", 3L),
     * KV.of("b", 2L),
     * KV.of("c", 1L),
     * // window 2
     * KV.of("b", 1L),
     * KV.of("a", 1L),
     * // window 3
     * KV.of("a", 2L),
     * KV.of("c", 1L));
     * p.run();
     * }
     */
}
