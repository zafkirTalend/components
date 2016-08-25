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
package org.talend.components.dropbox.runtime.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.avro.generic.IndexedRecord;
import org.joda.time.Instant;
import org.talend.components.api.component.runtime.Reader;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.dropbox.avro.DropboxIndexedRecord;
import org.talend.components.dropbox.runtime.DropboxGetSource;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DbxUserFilesRequests;
import com.dropbox.core.v2.files.FileMetadata;

/**
 * Common {@link Reader} for Dropbox Get component.
 * It downloads one file from Dropbox server
 * Provides {@link IndexedRecord} with file name and its content
 */
public abstract class DropboxGetReader implements Reader<IndexedRecord> {

    /**
     * {@link Source} from which this {@link Reader} reads
     */
    protected DropboxGetSource source;

    /**
     * Current {@link IndexedRecord}
     */
    protected DropboxIndexedRecord currentRecord;

    /**
     * Specifies whether {@link DropboxGetReader#start()} was called
     */
    protected boolean started;

    /**
     * {@link InputStream} from which file is read
     */
    protected InputStream inputStream;

    /**
     * Name of file
     */
    protected String fileName;

    /**
     * Constructor sets {@link DropboxGetSource} 
     * 
     * @param source {@link DropboxGetSource} instance
     */
    public DropboxGetReader(DropboxGetSource source) {
        this.source = source;
    }

    /**
     * Creates and returns Dropbox downloader, retrieves file name
     * 
     * @return Dropbox downloader
     * @throws IOException in case of wrong file path, connection problems. Exception contains {@link DbxException} inside
     */
    protected DbxDownloader<FileMetadata> getDownloader() throws IOException {
        DbxClientV2 client = source.getClient();
        DbxUserFilesRequests filesClient = client.files();
        String path = source.getPath();
        try {
            DbxDownloader<FileMetadata> downloader = filesClient.download(path);
            fileName = downloader.getResult().getName();
            return downloader;
        } catch (DbxException e) {
            throw new IOException(e);
        }
    }

    /**
     * Returns current {@link IndexedRecord}
     * 
     * @return current {@link IndexedRecord}
     */
    @Override
    public IndexedRecord getCurrent() throws NoSuchElementException {
        if (!started) {
            throw new NoSuchElementException("Reader wasn't started");
        }
        return currentRecord;
    }

    /**
     * Returns null
     * Currently this method is not supported
     * 
     * @return null
     */
    @Override
    public Instant getCurrentTimestamp() {
        return null;
    }

    /**
     * Returns {@link DropboxGetSource}, from which this component reads
     * 
     * @return {@link DropboxGetSource}
     */
    @Override
    public DropboxGetSource getCurrentSource() {
        return source;
    }

    /**
     * Returns empty map, because this Dropbox Get component doesn't provide any return properties
     * except ERROR_MESSAGE, which is computed outside component in generated code
     */
    @Override
    public Map<String, Object> getReturnValues() {
        return Collections.EMPTY_MAP;
    }

}
