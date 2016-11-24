
// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.udp;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
import org.talend.components.api.service.common.DefinitionRegistry;
import org.talend.components.api.service.common.ComponentServiceImpl;
import org.talend.components.api.test.ComponentTestUtils;
import org.talend.daikon.avro.converter.IndexedRecordConverter;
import org.talend.daikon.avro.converter.SingleColumnIndexedRecordConverter;
import org.talend.daikon.exception.TalendRuntimeException;

@SuppressWarnings("nls")
public class tUdpSocketInputTest {

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    private ComponentService componentService;

    private int port =9876;

    @Before
    public void initializeComponentRegistryAndService() {
        // reset the component service
        componentService = null;
    }

    // default implementation for pure java test. 
    public ComponentService getComponentService() {
        if (componentService == null) {
            DefinitionRegistry testComponentRegistry = new DefinitionRegistry();
            testComponentRegistry.registerComponentFamilyDefinition(new tUdpSocketInputFamilyDefinition());
            componentService = new ComponentServiceImpl(testComponentRegistry);
        }
        return componentService;
    }

    @Test
    public void testtUdpSocketInputRuntime() throws Exception {
        tUdpSocketInputProperties props = (tUdpSocketInputProperties) getComponentService().getComponentProperties("tUdpSocketInput");

        // Set up the test schema - not really used for anything now
        Schema schema = SchemaBuilder.builder().record("testRecord").fields().name("data").type().stringType().noDefault().endRecord();
        props.schema.schema.setValue(schema);
        props.schema.schema.setValue(schema);
        props.setValue("port",port);
        Source source = new tUdpSocketInputSource();
        source.initialize(null, props);
        assertThat(source, instanceOf(tUdpSocketInputSource.class));
        final Reader<?> reader = ((BoundedSource) source).createReader(null);
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

        IndexedRecordConverter converter = new SingleColumnIndexedRecordConverter(byte[].class, Schema.create(Schema.Type.BYTES),"RecordName","data");

        clientSocket.send(sendPacket);
        Thread.sleep(30);

        assertThat(converter.convertToDatum(reader.getCurrent()), is((Object) sendData));
        clientSocket.send(sendPacket);
        clientSocket.close();
        assertThat(reader.advance(), is(true));
        assertThat(converter.convertToDatum(reader.getCurrent()), is((Object) sendData));
    }

}
