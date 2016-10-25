package org.talend.components.performance.filedelimited;

import java.util.HashMap;

import org.junit.Test;

public class FileDelimitedOutput63Test {

    public static final HashMap<String, Object> globalMap = new HashMap<String, Object>(); 

    @Test
    public void testProcess() throws Exception {
        FileOutputDelimimed63Process process = new FileOutputDelimimed63Process();
        process.tRowGenerator_2Process(globalMap);
    }
}
