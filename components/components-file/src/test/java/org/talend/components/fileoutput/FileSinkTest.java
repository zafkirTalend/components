package org.talend.components.fileoutput;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.service.common.ComponentServiceImpl;
import org.talend.components.api.service.common.DefinitionRegistry;
import org.talend.components.file.FileFamilyDefinition;
import org.talend.components.file.fileoutputdefinition.FileOutputProperties;
import org.talend.components.file.runtime.FileSink;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;

public class FileSinkTest {

    private ComponentService componentService;

    @Before
    public void initializeComponentRegistryAndService() {
        // reset the component service
        componentService = null;
    }

    public ComponentService getComponentService() {
        if (componentService == null) {
            DefinitionRegistry testComponentRegistry = new DefinitionRegistry();
            testComponentRegistry.registerComponentFamilyDefinition(new FileFamilyDefinition());
            componentService = new ComponentServiceImpl(testComponentRegistry);
        }
        return componentService;
    }

    @Test
    public void initializeTest() {
        FileOutputProperties props = (FileOutputProperties) getComponentService()
                .getComponentProperties("SimpleFileOutputWriter");
        File testFile = new File("test.txt");
        props.setValue("filename", testFile.getAbsolutePath());

        FileSink fs = new FileSink();

        ValidationResult result = fs.initialize(null, props);

        Assert.assertTrue(result.equals(ValidationResult.OK));

        testFile.delete();

    }

    @Test
    public void validateTest() {
        FileOutputProperties props = (FileOutputProperties) getComponentService()
                .getComponentProperties("SimpleFileOutputWriter");
        File testFile = new File("test.txt");
        props.setValue("filename", testFile.getAbsolutePath());

        FileSink fs = new FileSink();
        fs.initialize(null, props);
        ValidationResult result = fs.validate(null);
        Assert.assertTrue(result.equals(ValidationResult.OK));
        Assert.assertTrue(testFile.exists());
        Assert.assertTrue(testFile.canWrite());
        testFile.delete();

        // uncreatable file
        testFile = new File("ZZZZ:/test.txt");
        props.setValue("filename", testFile.getAbsolutePath());
        fs.initialize(null, props);
        result = fs.validate(null);
        Assert.assertTrue(result.getStatus().equals(Result.ERROR));
        Assert.assertTrue(!testFile.exists());
        if (testFile.exists())
            testFile.delete();

        // directory
        testFile = new File("testFile/");
        props.setValue("filename", testFile.getAbsolutePath());
        fs.initialize(null, props);
        result = fs.validate(null);
        Assert.assertTrue(result.getStatus().equals(Result.ERROR));
        if (testFile.exists())
            testFile.delete();
    }
}
