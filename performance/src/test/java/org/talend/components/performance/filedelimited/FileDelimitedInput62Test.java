package org.talend.components.performance.filedelimited;

import java.util.HashMap;

import org.junit.Test;
import org.talend.components.stopwatch.StopWatch;

public class FileDelimitedInput62Test {
    
    public static final HashMap<String, Object> globalMap = new HashMap<String, Object>(); 

    @Test
    public void testProcess() throws Exception {
        FileInputDelimitedProcess process = new FileInputDelimitedProcess();
        StopWatch watch = StopWatch.getInstance(10);
        watch.startStageHere(0);
        process.tFileInputDelimited_2Process(globalMap);
        watch.finishStageHere(0);
        watch.showResults();
    }
}
