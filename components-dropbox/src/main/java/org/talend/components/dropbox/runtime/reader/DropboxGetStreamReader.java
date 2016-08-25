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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.component.runtime.Reader;
import org.talend.components.dropbox.avro.DropboxIndexedRecord;
import org.talend.components.dropbox.runtime.DropboxGetSource;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;

/**
 * Dropbox Get component {@link Reader}, which produces records with {@link InputStream} as file content.
 * This {@link Reader} produces only 1 record.
 * This feature is compatible with old implementation of Dropbox components
 */
public class DropboxGetStreamReader extends DropboxGetReader {

    /**
     * Constructor sets {@link DropboxGetSource}
     * 
     * @param source {@link DropboxGetSource} instance
     */
    public DropboxGetStreamReader(DropboxGetSource source) {
        super(source);
    }

    /**
     * Obtains {@link InputStream} from which file will be read.
     * Optionally, saves file on filesystem depending on Save As File property value
     * 
     * @return true, if download was successful
     */
    @Override
    public boolean start() throws IOException {
        DbxDownloader<FileMetadata> downloader = getDownloader();
        try {
            if (source.isSaveAsFile()) {
                String saveTo = source.getSaveTo();
                File fileToSave = new File(saveTo);
                if (!fileToSave.getParentFile().exists()) {
                    fileToSave.getParentFile().mkdirs();
                }
                try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                    downloader.download(fos);
                }
                inputStream = new FileInputStream(fileToSave);
            } else {
                inputStream = downloader.getInputStream();
            }
        } catch (DbxException e) {
            throw new IOException(e);
        }
        createRecord();
        started = true;
        return started;
    }

    /**
     * Creates the only {@link IndexedRecord} for InputStream read mode
     */
    private void createRecord() {
        currentRecord = new DropboxIndexedRecord(source.getSchema());
        currentRecord.put(0, fileName);
        currentRecord.put(1, inputStream);
    }

    /**
     * Returns <code>false</code>, because in InputStream read mode component can produce only 1 
     * {@link IndexedRecord}
     * 
     * @return false
     */
    @Override
    public boolean advance() {
        return false;
    }

    /**
     * Does nothing, because Dropbox client releases resources by itself
     */
    @Override
    public void close() {
        // does nothing
    }

}
