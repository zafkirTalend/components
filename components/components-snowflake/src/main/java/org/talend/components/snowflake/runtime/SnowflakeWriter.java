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
package org.talend.components.snowflake.runtime;

import static org.talend.components.snowflake.tsnowflakeoutput.TSnowflakeOutputProperties.OutputAction.UPSERT;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.WriterWithFeedback;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.snowflake.SnowflakeConnectionProperties;
import org.talend.components.snowflake.tsnowflakeoutput.TSnowflakeOutputProperties;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.avro.converter.IndexedRecordConverter;

import net.snowflake.client.loader.LoaderFactory;
import net.snowflake.client.loader.LoaderProperty;
import net.snowflake.client.loader.Operation;
import net.snowflake.client.loader.StreamLoader;

public final class SnowflakeWriter implements WriterWithFeedback<Result, IndexedRecord, IndexedRecord> {

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    private static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss.SSS");

    private static SimpleDateFormat timestampFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSXXX");

    private StreamLoader loader;

    private final SnowflakeWriteOperation snowflakeWriteOperation;

    private Connection uploadConnection;

    private Connection processingConnection;

    private Object[] row;

    private SnowflakeResultListener listener;

    protected final List<IndexedRecord> successfulWrites = new ArrayList<>();

    protected final List<IndexedRecord> rejectedWrites = new ArrayList<>();

    private String uId;

    private final SnowflakeSink sink;

    private final RuntimeContainer container;

    private final TSnowflakeOutputProperties sprops;

    private transient IndexedRecordConverter<Object, ? extends IndexedRecord> factory;

    private transient Schema mainSchema;

    private transient boolean isFirst = true;

    private transient List<Schema.Field> collectedFields;

    static {
        // Time in milliseconds would mean time from midnight. It shouldn't be influenced by timezone differences.
        // That's why we have to use GMT.
        timeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @Override
    public Iterable<IndexedRecord> getSuccessfulWrites() {
        return new ArrayList<IndexedRecord>();
    }

    @Override
    public Iterable<IndexedRecord> getRejectedWrites() {
        return listener.getErrors();
    }

    public SnowflakeWriter(SnowflakeWriteOperation sfWriteOperation, RuntimeContainer container) {
        this.snowflakeWriteOperation = sfWriteOperation;
        this.container = container;
        sink = snowflakeWriteOperation.getSink();
        sprops = sink.getSnowflakeOutputProperties();
        listener = new SnowflakeResultListener(sprops);
    }

    @Override
    public void open(String uId) throws IOException {
        this.uId = uId;
        processingConnection = sink.connect(container);
        uploadConnection = sink.connect(container);
        if (null == mainSchema) {
            mainSchema = sprops.table.main.schema.getValue();
            if (AvroUtils.isIncludeAllFields(mainSchema)) {
                mainSchema = sink.getSchema(container, processingConnection, sprops.table.tableName.getStringValue());
            } // else schema is fully specified
        }

        SnowflakeConnectionProperties connectionProperties = sprops.getConnectionProperties();

        Map<LoaderProperty, Object> prop = new HashMap<>();
        prop.put(LoaderProperty.tableName, sprops.table.tableName.getStringValue());
        prop.put(LoaderProperty.schemaName, connectionProperties.schemaName.getStringValue());
        prop.put(LoaderProperty.databaseName, connectionProperties.db.getStringValue());
        switch (sprops.outputAction.getValue()) {
        case INSERT:
            prop.put(LoaderProperty.operation, Operation.INSERT);
            break;
        case UPDATE:
            prop.put(LoaderProperty.operation, Operation.MODIFY);
            break;
        case UPSERT:
            prop.put(LoaderProperty.operation, Operation.UPSERT);
            break;
        case DELETE:
            prop.put(LoaderProperty.operation, Operation.DELETE);
            break;
        }

        List<Field> columns = mainSchema.getFields();
        List<String> keyStr = new ArrayList<>();
        List<String> columnsStr = new ArrayList<>();
        for (Field f : columns) {
            columnsStr.add(f.name());
            if (null != f.getProp(SchemaConstants.TALEND_COLUMN_IS_KEY))
                keyStr.add(f.name());
        }

        row = new Object[columnsStr.size()];

        prop.put(LoaderProperty.columns, columnsStr);
        if (sprops.outputAction.getValue() == UPSERT) {
            keyStr.clear();
            keyStr.add(sprops.upsertKeyColumn.getValue());
        }
        if (keyStr.size() > 0)
            prop.put(LoaderProperty.keys, keyStr);

        prop.put(LoaderProperty.remoteStage, "~");

        loader = (StreamLoader) LoaderFactory.createLoader(prop, uploadConnection, processingConnection);
        loader.setListener(listener);

        loader.start();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(Object datum) throws IOException {
        if (null == datum) {
            return;
        }
        if (null == factory) {
            factory = (IndexedRecordConverter<Object, ? extends IndexedRecord>) SnowflakeAvroRegistry.get()
                    .createIndexedRecordConverter(datum.getClass());
        }
        IndexedRecord input = factory.convertToAvro(datum);
        List<Schema.Field> fields = input.getSchema().getFields();

        // input and mainSchema synchronization. Such situation is useful in case of Dynamic
        if (isFirst) {
             collectedFields = new ArrayList<>();
            for (Schema.Field item : fields) {
                Schema.Field fieldFromMainSchema = mainSchema.getField(item.name());
                if (fieldFromMainSchema != null) {
                    collectedFields.add(fieldFromMainSchema);
                }
            }
            isFirst = false;
        }

        for (int i = 0; i < row.length; i++) {
            Field f = collectedFields.get(i);
            Schema s = AvroUtils.unwrapIfNullable(f.schema());
            Object inputValue = input.get(i);
            if (null == inputValue || inputValue instanceof String) {
                row[i] = inputValue;
            } else if (AvroUtils.isSameType(s, AvroUtils._date())) {
                Date date = (Date) inputValue;
                row[i] = date.getTime();
            } else if (LogicalTypes.fromSchemaIgnoreInvalid(s) == LogicalTypes.timeMillis()) {
                Date date = new Date((int) inputValue);
                row[i] = timeFormatter.format(date);
            } else if (LogicalTypes.fromSchemaIgnoreInvalid(s) == LogicalTypes.date()) {
                Date date = null;
                if (inputValue instanceof Date) {
                    // Sometimes it can be sent as a Date object. We need to process it like a common date then.
                    date = (Date) inputValue;
                } else if (inputValue instanceof Integer) {
                    // If the date is int, it represents amount of days from 1970(no timezone). So if the date is
                    // 14.01.2017 it shouldn't be influenced by timezones time differences. It should be the same date
                    // in any timezone.
                    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                    c.setTimeInMillis(0);
                    c.add(Calendar.DATE, (Integer) inputValue);
                    c.setTimeZone(TimeZone.getDefault());
                    long timeInMillis = c.getTime().getTime();
                    date = new Date(timeInMillis - c.getTimeZone().getOffset(timeInMillis));
                } else {
                    // long is just a common timestamp value.
                    date = new Date((Long) inputValue);
                }
                row[i] = dateFormatter.format(date);
            } else if (LogicalTypes.fromSchemaIgnoreInvalid(s) == LogicalTypes.timestampMillis()) {
                if (inputValue instanceof Date) {
                    row[i] = timestampFormatter.format(inputValue);
                } else if (inputValue instanceof Long) {
                    row[i] = timestampFormatter.format(new Date((Long) inputValue));
                } else {
                    row[i] = inputValue;
                }
            } else {
                row[i] = inputValue;
            }
        }

        loader.submitRow(row);
    }

    @Override
    public Result close() throws IOException {
        try {
            loader.finish();
        } catch (Exception ex) {
            throw new IOException(ex);
        }

        try {
            sink.closeConnection(container, processingConnection);
            sink.closeConnection(container, uploadConnection);
        } catch (SQLException e) {
            throw new IOException(e);
        }
        return new Result(uId, listener.getSubmittedRowCount(), listener.counter.get(), listener.getErrorRecordCount());
    }

    @Override
    public WriteOperation<Result> getWriteOperation() {
        return snowflakeWriteOperation;
    }

}
