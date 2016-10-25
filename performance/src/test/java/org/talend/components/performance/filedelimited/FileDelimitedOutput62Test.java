package org.talend.components.performance.filedelimited;

import java.util.HashMap;

import org.junit.Test;

public class FileDelimitedOutput62Test {
    
    public static final HashMap<String, Object> globalMap = new HashMap<String, Object>(); 

    @Test
    public void testProcess() throws Exception {
        FileOutputDelimitedProcess process = new FileOutputDelimitedProcess();
        process.tRowGenerator_2Process(globalMap);
    }
}
