package org.talend.components.files.tfilepositional.tfilepositionalinput;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.component.runtime.Reader;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.exception.error.ComponentsErrorCode;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.service.internal.ComponentServiceImpl;
import org.talend.components.api.test.ComponentTestUtils;
import org.talend.components.api.test.SimpleComponentRegistry;
import org.talend.components.files.tfilepositional.tfilepositionalinput.TFilePositionalInputDefinition;
import org.talend.components.files.tfilepositional.tfilepositionalinput.TFilePositionalInputProperties;
import org.talend.components.files.tfilepositionalinput.runtime.TFilePositionalInputSource;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.exception.TalendRuntimeException;

@SuppressWarnings("nls")
public class TFilePositionalInputTest {

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    private ComponentServiceImpl componentService;

    @Before
    public void initializeComponentRegistryAndService() {
        // reset the component service
        componentService = null;
    }

    // default implementation for pure java test. 
    public ComponentService getComponentService() {
        if (componentService == null) {
            SimpleComponentRegistry testComponentRegistry = new SimpleComponentRegistry();
            testComponentRegistry.addComponent(TFilePositionalInputDefinition.COMPONENT_NAME, new TFilePositionalInputDefinition());
            componentService = new ComponentServiceImpl(testComponentRegistry);
        }
        return componentService;
    }

    @Test
    public void testTestInputRuntime() throws Exception {
    	TFilePositionalInputDefinition def = (TFilePositionalInputDefinition) getComponentService().getComponentDefinition("tFileInputPositional");
        TFilePositionalInputProperties props = (TFilePositionalInputProperties) getComponentService().getComponentProperties("tFileInputPositional");

        // Set up the test schema - not really used for anything now
        Schema schema = SchemaBuilder.builder().record("testRecord").fields()
        		.name("field1").type().stringType().noDefault()
        		.name("field2").type().intType().noDefault()
        		.name("field3").type().stringType().noDefault()
        		.endRecord();
        props.schema.schema.setValue(schema);
        props.pattern.setValue("5,5,4");
        
        File temp = File.createTempFile("TestInputtestFile", ".txt");
        try {
            PrintWriter writer = new PrintWriter(temp.getAbsolutePath(), "UTF-8");
            writer.println("The first line");
            writer.println("The secon line");
            writer.close();
            
            props.filename.setValue(temp.getAbsolutePath());
            Source source = def.getRuntime();
            source.initialize(null, props);
            assertThat(source, instanceOf(TFilePositionalInputSource.class));
            
            Reader<?> reader = ((BoundedSource) source).createReader(null);
            assertThat(reader.start(), is(true));
            //assertThat(reader.getCurrent(), is((Object) "The first line"));
            // No auto advance when calling getCurrent more than once.
            /*assertThat(reader.getCurrent(), is((Object) "The first line"));
            assertThat(reader.advance(), is(true));
            assertThat(reader.getCurrent(), is((Object) "The secon line"));
            assertThat(reader.advance(), is(false));*/
        } finally {// remote the temp file
            temp.delete();
        }
    }

}
