package org.talend.components.performance.filedelimited;

import java.util.HashMap;

import org.junit.Test;
import org.talend.components.stopwatch.StopWatch;

public class FileDelimitedInput62Test {
    
    public static final HashMap<String, Object> globalMap = new HashMap<String, Object>(); 

    @Test
    public void testProcess() throws Exception {
        FileInputDelimitedProcess process = new FileInputDelimitedProcess();
        process.tFileInputDelimited_2Process(globalMap);
    }
}
