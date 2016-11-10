package org.talend.components.benchmarks.filedelimited;

import java.util.HashMap;

import org.junit.Test;
import org.talend.components.jobs.filedelimited.FileInputDelimitedProcess63;
import org.talend.components.stopwatch.StopWatch;

public class FileInputDelimitedBenchmark63Test {

    FileInputDelimitedProcess63 process = new FileInputDelimitedProcess63();

    HashMap<String, Object> globalMap = new HashMap<>();

    @Test
    public void measureRowGenerator_2Process() throws Exception {
        StopWatch watch = StopWatch.getInstance(15);
        watch.startStageHere(0);
        process.tFileInputDelimited_2Process(globalMap);
        watch.finishStageHere(0);
        watch.showResults();
    }

}
