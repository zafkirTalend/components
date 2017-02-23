
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

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.service.common.ComponentServiceImpl;
import org.talend.components.api.service.common.DefinitionRegistry;
import org.talend.components.file.FileFamilyDefinition;

@SuppressWarnings("nls")
public class FileOutputTest {

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
    //
    // @Test
    // public void testFileInputRuntime() throws Exception {
    // FileOutputProperties props = (FileOutputProperties) getComponentService().getComponentProperties("FileInput");
    //
    // // Set up the test schema - not really used for anything now
    // Schema schema =
    // SchemaBuilder.builder().record("testRecord").fields().name("field1").type().stringType().noDefault().endRecord();
    // props.schema.schema.setValue(schema);
    //
    // File temp = File.createTempFile("FileInputtestFile", ".txt");
    // try {
    // PrintWriter writer = new PrintWriter(temp.getAbsolutePath(), "UTF-8");
    // writer.println("The first line");
    // writer.println("The second line");
    // writer.close();
    //
    // props.filename.setValue(temp.getAbsolutePath());
    // Source source = new FileInputSource();
    // source.initialize(null, props);
    // assertThat(source, instanceOf(FileInputSource.class));
    //
    // Reader<?> reader = ((BoundedSource) source).createReader(null);
    // assertThat(reader.start(), is(true));
    // assertThat(reader.getCurrent(), is((Object) "The first line"));
    // // No auto advance when calling getCurrent more than once.
    // assertThat(reader.getCurrent(), is((Object) "The first line"));
    // assertThat(reader.advance(), is(true));
    // assertThat(reader.getCurrent(), is((Object) "The second line"));
    // assertThat(reader.advance(), is(false));
    // } finally {// remote the temp file
    // temp.delete();
    // }
    // }

    // @Test
    // public void testFileWriter() throws Exception {
    // FileOutputProperties props = (FileOutputProperties) getComponentService()
    // .getComponentProperties("SimpleFileOutputWriter");
    // // Set up the test schema - not really used for anything now
    // Schema schema =
    // SchemaBuilder.builder().record("testRecord").fields().name("field1").type().stringType().noDefault()
    // .endRecord();
    // props.schema.schema.setValue(schema);
    //
    // props.filename.setValue("D:/projects/test/test.txt");
    // Sink fs = new FileSink();
    // fs.initialize(null, props);
    //
    // FileWriteOperation fwo = (FileWriteOperation) fs.createWriteOperation();
    // FileOutputWriter fow = (FileOutputWriter) fwo.createWriter(null);
    //
    //
    // fow.open("1");
    // fow.write(schema.);
    // fow.write("SecondTest");
    // Result result = fow.close();
    // System.out.println(props.filename.getStringValue());
    // System.out.println(result.successCount);
    //
    // }
}
