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

package org.talend.components.pubsub.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.beam.sdk.coders.AtomicCoder;
import org.apache.beam.sdk.coders.ByteArrayCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.coders.MapCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.PubsubIO;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * A {@link Coder} for {@link PubsubIO.PubsubMessage}
 */
public class PubSubMessageCoder extends AtomicCoder<PubsubIO.PubsubMessage> {

    private static final PubSubMessageCoder INSTANCE = new PubSubMessageCoder();

    private static final ByteArrayCoder BYTE_ARRAY_CODER = ByteArrayCoder.of();

    private static final MapCoder<String, String> MAP_CODER = MapCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of());

    @JsonCreator
    public static PubSubMessageCoder of() {
        return INSTANCE;
    }

    @Override
    public void encode(PubsubIO.PubsubMessage pubsubMessage, OutputStream outputStream, Context context)
            throws CoderException, IOException {
        Context nested = context.nested();
        BYTE_ARRAY_CODER.encode(pubsubMessage.getMessage(), outputStream, nested);
        MAP_CODER.encode(pubsubMessage.getAttributeMap(), outputStream, context);
    }

    @Override
    public PubsubIO.PubsubMessage decode(InputStream inputStream, Context context) throws CoderException, IOException {
        Context nested = context.nested();
        return new PubsubIO.PubsubMessage(BYTE_ARRAY_CODER.decode(inputStream, nested), MAP_CODER.decode(inputStream, context));
    }

}
