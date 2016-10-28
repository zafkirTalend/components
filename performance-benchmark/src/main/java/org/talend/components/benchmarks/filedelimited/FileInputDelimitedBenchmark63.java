package org.talend.components.benchmarks.filedelimited;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.talend.components.jobs.filedelimited.FileInputDelimitedProcess63;

@State(Scope.Thread)
public class FileInputDelimitedBenchmark63 {

    FileInputDelimitedProcess63 process = new FileInputDelimitedProcess63();

    HashMap<String, Object> globalMap = new HashMap<>();

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureRowGenerator_2Process() throws Exception {
        process.tFileInputDelimited_2Process(globalMap);
    }

    /**
     * run benchmark
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(FileInputDelimitedBenchmark63.class.getSimpleName()).warmupIterations(5)
                .measurementTime(TimeValue.seconds(20)).measurementIterations(5).forks(1).build();

        new Runner(opt).run();
    }
}
