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
package org.talend.components.snowflake.runtime;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.component.runtime.AbstractBoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.snowflake.tsnowflakeoutputreject.TSnowflakeOutputRejectProperties;

public class SnowflakeRejectReader<T> extends AbstractBoundedReader<IndexedRecord> {

    private transient Connection connection;

    protected TSnowflakeOutputRejectProperties properties;

    private RuntimeContainer container;

    private transient int dataCount;

    public SnowflakeRejectReader(RuntimeContainer container, BoundedSource source, TSnowflakeOutputRejectProperties props)
            throws IOException {
        super(source);
        this.container = container;
        this.properties = props;
    }

    protected Connection getConnection() throws IOException {
        if (null == connection) {
            connection = ((SnowflakeSource) getCurrentSource()).connect(container);
        }
        return connection;
    }

    protected Schema getSchema() throws IOException {
        // FIXME
        return null;
    }

    @Override public boolean start() throws IOException {
        // FIXME
        return false;
    }

    private boolean haveNext() throws SQLException {
        return false;
    }

    @Override public boolean advance() throws IOException {
        try {
            return haveNext();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    @Override public IndexedRecord getCurrent() throws NoSuchElementException {
        try {
            return null;
        } catch (Exception e) {
            throw new ComponentException(e);
        }
    }

    @Override public void close() throws IOException {
    }

    @Override public Map<String, Object> getReturnValues() {
        Result result = new Result();
        result.totalCount = dataCount;
        return result.toMap();
    }

}
