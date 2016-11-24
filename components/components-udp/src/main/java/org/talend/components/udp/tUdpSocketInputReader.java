
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.component.runtime.AbstractBoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.component.runtime.Result;
import org.talend.daikon.avro.converter.SingleColumnIndexedRecordConverter;

/**
 * Simple implementation of a reader.
 */
public class tUdpSocketInputReader extends AbstractBoundedReader<IndexedRecord> {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(tUdpSocketInputDefinition.class);

    private RuntimeContainer container;

    private final Integer port;

    private final Integer sizeArray;

    private boolean started = false;

    private BufferedReader reader = null;

    private transient byte[] current;

    private DatagramSocket serverSocket;

    private transient Schema schema;

    private SingleColumnIndexedRecordConverter converter ;

    public tUdpSocketInputReader(RuntimeContainer container, BoundedSource source, Integer port,Integer sizeArray, Schema schema) {
        super(source);
        this.container = container;
        this.port = port;
        this.schema = schema;
        this.sizeArray = sizeArray;
        converter = new SingleColumnIndexedRecordConverter(byte[].class, Schema.create(Schema.Type.BYTES),"recordName",schema.getFields().get(0).name());
    }

    @Override
    public boolean start() throws IOException {
        serverSocket = new DatagramSocket(port);
        started = true;
        LOGGER.debug("Open UDP Socket at " + serverSocket.getLocalPort()); //$NON-NLS-1$
        return advance() ;
    }

    @Override
    public boolean advance() throws IOException {
        byte[] receiveData = new byte[sizeArray];

        DatagramPacket receivePacket = new DatagramPacket(receiveData,0,receiveData.length);
        LOGGER.debug("Start waiting for Data"); //$NON-NLS-1$Ò
        serverSocket.receive(receivePacket);
        if(receivePacket.getData()!=null){
            current = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
        }

        LOGGER.debug("New Line : "+new String(current)); //$NON-NLS-1$

        return true;
    }

    @Override
    public IndexedRecord getCurrent() throws NoSuchElementException {
        if (!started) {
            throw new NoSuchElementException();
        }
        return converter.convertToAvro(current);
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
        LOGGER.debug("Close UDP socket on port " + port); //$NON-NLS-1$
    }

    @Override
    public Map<String, Object> getReturnValues() {
        return new Result().toMap();
    }

}
