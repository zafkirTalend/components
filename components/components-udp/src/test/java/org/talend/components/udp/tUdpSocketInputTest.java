package org.talend.components.udp;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Collections;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.component.runtime.Reader;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.exception.error.ComponentsErrorCode;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.service.internal.ComponentServiceImpl;
import org.talend.components.api.test.ComponentTestUtils;
import org.talend.components.api.test.SimpleComponentRegistry;
import org.talend.components.udp.tofix.SingleColumnIndexRecordConverter;
import org.talend.daikon.avro.converter.IndexedRecordConverter;
import org.talend.daikon.exception.TalendRuntimeException;

@SuppressWarnings("nls")
public class tUdpSocketInputTest {

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    private ComponentServiceImpl componentService;

    private Reader<?> reader;

    private int port =9876;

    @Before
    public void initializeComponentRegistryAndService() throws IOException {
        // reset the component service
        componentService = null;

    }

    @After
    public void finalizeTest() throws IOException {
    }


    // default implementation for pure java test. 
    public ComponentService getComponentService() {
        if (componentService == null) {
            SimpleComponentRegistry testComponentRegistry = new SimpleComponentRegistry();
            testComponentRegistry.addComponent(tUdpSocketInputDefinition.COMPONENT_NAME, new tUdpSocketInputDefinition());
            componentService = new ComponentServiceImpl(testComponentRegistry);
        }
        return componentService;
    }

    @Test
    public void testtUdpSocketInputRuntime() throws Exception {

        try {

            tUdpSocketInputDefinition def = (tUdpSocketInputDefinition) getComponentService().getComponentDefinition("tUdpSocketInput");
            tUdpSocketInputProperties props = (tUdpSocketInputProperties) getComponentService().getComponentProperties("tUdpSocketInput");


            // Set up the test schema - not really used for anything now
            Schema schema = SchemaBuilder.builder().record("testRecord").fields().name("data").type().stringType().noDefault().endRecord();
            props.schema.schema.setValue(schema);
            props.setValue("port",port);

            Source source = def.getRuntime();
            source.initialize(null, props);
            assertThat(source, instanceOf(tUdpSocketInputSource.class));
            reader = ((BoundedSource) source).createReader(null);
            Thread one=new Thread(){
                public void run() {
                    try {
                        reader.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            one.start();

            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName("localhost");
            byte[] sendData ;
            String sentence = "tesdt";

            sendData = sentence.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

            IndexedRecordConverter converter = new SingleColumnIndexRecordConverter(byte[].class, Schema.create(Schema.Type.BYTES),"data");

            clientSocket.send(sendPacket);
            Thread.sleep(10);

            assertThat(converter.convertToDatum(reader.getCurrent()), is((Object) sendData));
            clientSocket.send(sendPacket);
            clientSocket.close();
            assertThat(reader.advance(), is(true));
            assertThat(converter.convertToDatum(reader.getCurrent()), is((Object) sendData));
        } finally {// remote the temp file
        }
    }


    @Test
    public void testAlli18n() {
        ComponentTestUtils.testAlli18n(getComponentService(), errorCollector);
    }

    @Test
    public void testAllImagePath() {
        ComponentTestUtils.testAllImages(getComponentService());
    }
}
