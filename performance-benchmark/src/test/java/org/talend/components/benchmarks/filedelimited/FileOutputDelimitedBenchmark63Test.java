package org.talend.components.benchmarks.filedelimited;

import java.util.HashMap;

import org.junit.Test;
import org.talend.components.jobs.filedelimited.FileOutputDelimitedProcess63;
import org.talend.components.stopwatch.StopWatch;

public class FileOutputDelimitedBenchmark63Test {

    FileOutputDelimitedProcess63 process = new FileOutputDelimitedProcess63();

    HashMap<String, Object> globalMap = new HashMap<>();

    @Test
    public void measureRowGenerator_2Process() throws Exception {
        StopWatch watch = StopWatch.getInstance(15);
        watch.startStageHere(0);
        process.tRowGenerator_2Process(globalMap);
        watch.finishStageHere(0);
        watch.showResults();
    }

}
