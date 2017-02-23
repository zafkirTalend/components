
// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.fileoutput;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData.Record;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.service.common.ComponentServiceImpl;
import org.talend.components.api.service.common.DefinitionRegistry;
import org.talend.components.file.FileFamilyDefinition;
import org.talend.components.file.fileoutputdefinition.FileOutputProperties;
import org.talend.components.file.runtime.FileOutputWriter;
import org.talend.components.file.runtime.FileSink;
import org.talend.components.file.runtime.FileWriteOperation;

@SuppressWarnings("nls")
public class FileOutputWriterTest {

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    private ComponentService componentService;

    @Before
    public void initializeComponentRegistryAndService() {
        // reset the component service
        componentService = null;
    }

    // default implementation for pure java test.
    public ComponentService getComponentService() {
        if (componentService == null) {
            DefinitionRegistry testComponentRegistry = new DefinitionRegistry();
            testComponentRegistry.registerComponentFamilyDefinition(new FileFamilyDefinition());
            componentService = new ComponentServiceImpl(testComponentRegistry);
        }
        return componentService;
    }

    @Test
    public void testOpen() throws IOException {
        FileOutputProperties props = (FileOutputProperties) getComponentService()
                .getComponentProperties("SimpleFileOutputWriter");

        File f = new File("test.txt");
        props.filename.setValue("test.txt");

        Sink fs = new FileSink();
        fs.initialize(null, props);

        FileWriteOperation fwo = (FileWriteOperation) fs.createWriteOperation();
        FileOutputWriter fow = (FileOutputWriter) fwo.createWriter(null);

        fow.open("1");

        Assert.assertTrue(f.exists());
        Assert.assertTrue(f.canWrite());

        f.delete();

    }

    @Test
    public void testWrite() throws Exception {
        FileOutputProperties props = (FileOutputProperties) getComponentService()
                .getComponentProperties("SimpleFileOutputWriter");

        Schema schema = SchemaBuilder.builder().record("testRecord").fields().name("field1").type().stringType().noDefault()
                .name("field2").type().intType().noDefault().endRecord();
        props.schema.schema.setValue(schema);

        props.filename.setValue("test.txt");
        props.fieldSeparator.setValue(" ; ");
        Sink fs = new FileSink();
        fs.initialize(null, props);

        FileWriteOperation fwo = (FileWriteOperation) fs.createWriteOperation();
        FileOutputWriter fow = (FileOutputWriter) fwo.createWriter(null);

        fow.open("1");
        Record record = new Record(schema);
        record.put("field1", "Test");
        record.put("field2", 2);
        fow.write(record);
        Result result = fow.close();
        System.out.println(props.filename.getStringValue());
        Assert.assertTrue(result.successCount == 1);

        Files.deleteIfExists(Paths.get("test.txt"));

    }
}
