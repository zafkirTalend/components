package org.talend.components.performance.filedelimited;

import java.util.HashMap;

import org.junit.Test;

public class FileDelimitedInput63Test {

    public static final HashMap<String, Object> globalMap = new HashMap<String, Object>(); 

    @Test
    public void testProcess() throws Exception {
        FileInputDelimited63Process process = new FileInputDelimited63Process();
        process.tFileInputDelimited_2Process(globalMap);
    }
}
