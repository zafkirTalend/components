package org.talend.components.benchmarks.filedelimited;

import java.util.HashMap;

import org.junit.Test;
import org.talend.components.jobs.filedelimited.FileOutputDelimitedProcess62;
import org.talend.components.stopwatch.StopWatch;

public class FileOutputDelimitedBenchmark62Test {

    FileOutputDelimitedProcess62 process = new FileOutputDelimitedProcess62();

    HashMap<String, Object> globalMap = new HashMap<>();

    @Test
    public void measureRowGenerator_2Process() throws Exception {
        StopWatch watch = StopWatch.getInstance(10);
        watch.startStageHere(0);
        process.tRowGenerator_2Process(globalMap);
        watch.finishStageHere(0);
        watch.showResults();
    }
    
}
