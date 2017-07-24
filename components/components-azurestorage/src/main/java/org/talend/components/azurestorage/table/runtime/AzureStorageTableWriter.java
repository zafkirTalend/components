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
package org.talend.components.azurestorage.table.runtime;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.WriterWithFeedback;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.azurestorage.table.AzureStorageTableProperties;
import org.talend.components.azurestorage.table.AzureStorageTableService;
import org.talend.components.azurestorage.table.tazurestorageoutputtable.TAzureStorageOutputTableProperties;
import org.talend.components.azurestorage.table.tazurestorageoutputtable.TAzureStorageOutputTableProperties.ActionOnTable;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.TableBatchOperation;
import com.microsoft.azure.storage.table.TableOperation;

public class AzureStorageTableWriter implements WriterWithFeedback<Result, IndexedRecord, IndexedRecord> {

    protected transient WriteOperation<Result> writeOperation;

    private transient Schema writeSchema;

    private transient Schema rejectSchema;

    private Result result;

    private String tableName;

    private TAzureStorageOutputTableProperties.ActionOnData actionData;

    private Boolean processOperationInBatch;

    private int batchOperationsCount;

    private List<TableOperation> batchOperations = new ArrayList<>();

    private List<IndexedRecord> batchRecords = new ArrayList<>();

    private String latestPartitionKey;

    private List<IndexedRecord> successfulWrites = new ArrayList<>();

    private List<IndexedRecord> rejectedWrites = new ArrayList<>();

    private String partitionKey;

    private String rowKey;

    private Map<String, String> nameMappings;

    private Boolean useNameMappings = Boolean.FALSE;

    private static final int MAX_RECORDS_TO_ENQUEUE = 250;

    private List<IndexedRecord> recordToEnqueue = new ArrayList<>();

    private ActionOnTable actionOnTable;

    private boolean dieOnError;

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureStorageTableWriter.class);

    private static final I18nMessages i18nMessages = GlobalI18N.getI18nMessageProvider()
            .getI18nMessages(AzureStorageTableWriter.class);

    public AzureStorageTableService tableservice;

    public AzureStorageTableWriter(WriteOperation<Result> writeOperation, RuntimeContainer adaptor) {

        this.writeOperation = writeOperation;
        AzureStorageTableSink sink = (AzureStorageTableSink) this.writeOperation.getSink();
        tableservice = new AzureStorageTableService(sink.getAzureConnection(adaptor));

        // if design schema include dynamic,need to get schema from record
        if (!AvroUtils.isIncludeAllFields(sink.getProperties().schema.schema.getValue())) {
            writeSchema = sink.getProperties().schema.schema.getValue();
        }

        rejectSchema = sink.getProperties().schemaReject.schema.getValue();
        dieOnError = sink.getProperties().dieOnError.getValue();
        tableName = sink.getProperties().tableName.getValue();
        actionOnTable = sink.getProperties().actionOnTable.getValue();
        actionData = sink.getProperties().actionOnData.getValue();
        processOperationInBatch = sink.getProperties().processOperationInBatch.getValue();
        partitionKey = sink.getProperties().partitionKey.getStringValue();
        rowKey = sink.getProperties().rowKey.getStringValue();
        nameMappings = sink.getProperties().nameMapping.getNameMappings();
        if (nameMappings != null) {
            useNameMappings = true;
        }
    }

    @Override
    public void open(String uId) throws IOException {
        try {

            this.result = new Result(uId);
            tableservice.handleActionOnTable(tableName, actionOnTable);

        } catch (InvalidKeyException | URISyntaxException | StorageException e) {
            LOGGER.error(e.getLocalizedMessage());
            throw new ComponentException(e);
        }
    }

    @Override
    public void write(Object object) throws IOException {
        if (object == null) {
            return;
        }
        // initialize feedback collections for the write operation
        successfulWrites = new ArrayList<>();
        rejectedWrites = new ArrayList<>();

        result.totalCount++;
        IndexedRecord inputRecord = (IndexedRecord) object;
        // This for dynamic which would get schema from the first record
        if (writeSchema == null) {
            writeSchema = ((IndexedRecord) object).getSchema();
        }

        if (processOperationInBatch) {
            DynamicTableEntity entity = createDynamicEntityFromInputRecord(inputRecord, writeSchema);
            addOperationToBatch(entity, inputRecord);
        } else {
            recordToEnqueue.add(inputRecord);
            if (recordToEnqueue.size() >= MAX_RECORDS_TO_ENQUEUE) {
                processParallelRecords();
            }
        }
    }

    private void processParallelRecords() {
        recordToEnqueue.parallelStream().forEach(new Consumer<IndexedRecord>() {

            @Override
            public void accept(IndexedRecord record) {
                try {
                    DynamicTableEntity entity = createDynamicEntityFromInputRecord(record, writeSchema);
                    tableservice.executeOperation(tableName, getTableOperation(entity));
                    handleSuccess(record, 1);
                } catch (StorageException e) {
                    LOGGER.error(i18nMessages.getMessage("error.ProcessSingleOperation", actionData, e.getLocalizedMessage()), e);
                    if (dieOnError) {
                        throw new ComponentException(e);
                    }
                    handleReject(record, e, 1);

                } catch (URISyntaxException | InvalidKeyException e) {
                    throw new ComponentException(e); // connection problem so next operation will also fail, we stop the process
                }
            }
        });
        recordToEnqueue.clear();
    }

    private DynamicTableEntity createDynamicEntityFromInputRecord(IndexedRecord indexedRecord, Schema schema) {
        DynamicTableEntity entity = new DynamicTableEntity();
        HashMap<String, EntityProperty> entityProps = new HashMap<>();
        for (Field f : schema.getFields()) {

            if (indexedRecord.get(f.pos()) == null) {
                continue; // record value may be null, No need to set the property in azure in this case
            }

            String sName = f.name(); // schema name
            String mName = getMappedNameIfNecessary(sName); // mapped name

            Schema fSchema = f.schema();
            if (fSchema.getType() == Type.UNION) {
                for (Schema s : f.schema().getTypes()) {
                    if (s.getType() != Type.NULL) {
                        fSchema = s;
                        break;
                    }
                }
            }

            if (sName.equals(partitionKey)) {
                entity.setPartitionKey((String) indexedRecord.get(f.pos()));
            } else if (sName.equals(rowKey)) {
                entity.setRowKey((String) indexedRecord.get(f.pos()));
            } else if (mName.equals(AzureStorageTableProperties.TABLE_TIMESTAMP)) {
                // nop : managed by server
            } else { // that's some properties !
                if (fSchema.getType().equals(Type.BOOLEAN)) {
                    entityProps.put(mName, new EntityProperty((Boolean) indexedRecord.get(f.pos())));
                } else if (fSchema.getType().equals(Type.DOUBLE)) {
                    entityProps.put(mName, new EntityProperty((Double) indexedRecord.get(f.pos())));
                } else if (fSchema.getType().equals(Type.INT)) {
                    entityProps.put(mName, new EntityProperty((Integer) indexedRecord.get(f.pos())));
                } else if (fSchema.getType().equals(Type.BYTES)) {
                    entityProps.put(mName, new EntityProperty((byte[]) indexedRecord.get(f.pos())));
                }
                //
                else if (fSchema.getType().equals(Type.LONG)) {
                    String clazz = fSchema.getProp(SchemaConstants.JAVA_CLASS_FLAG);
                    if (clazz != null && clazz.equals(Date.class.getCanonicalName())) {
                        Date dt = null;
                        String pattern = fSchema.getProp(SchemaConstants.TALEND_COLUMN_PATTERN);
                        if (pattern != null && !pattern.isEmpty()) {
                            try {
                                dt = new SimpleDateFormat(pattern).parse(indexedRecord.get(f.pos()).toString());
                            } catch (ParseException e) {
                                LOGGER.error(i18nMessages.getMessage("error.ParseError", e));
                                if (dieOnError) {
                                    throw new ComponentException(e);
                                }
                            }
                        } else {
                            dt = (Date) indexedRecord.get(f.pos());
                        }

                        entityProps.put(mName, new EntityProperty(dt));
                    } else {
                        entityProps.put(mName, new EntityProperty((Long) indexedRecord.get(f.pos())));
                    }
                }
                //
                else if (fSchema.getType().equals(Type.STRING)) {
                    entityProps.put(mName, new EntityProperty((String) indexedRecord.get(f.pos())));
                } else { // use string as default type...
                    entityProps.put(mName, new EntityProperty((String) indexedRecord.get(f.pos())));

                }
            }
        }
        // Etag is needed for some operations (delete, merge, replace) but we rely only on PK and RK for those ones.
        entity.setEtag("*");
        entity.setProperties(entityProps);
        return entity;
    }

    /**
     * this method return the mapped name is useNameMappings is true else it return the original name
     */
    private String getMappedNameIfNecessary(String sName) {
        if (useNameMappings) {
            if (nameMappings.containsKey(sName)) {
                return nameMappings.get(sName);
            }
        }

        return sName;
    }

    @Override
    public Result close() throws IOException {
        if (batchOperationsCount > 0) {
            LOGGER.debug(i18nMessages.getMessage("debug.ExecutingBrtch", batchOperationsCount));
            processBatch();
        }

        if (recordToEnqueue.size() > 0) {
            processParallelRecords();
        }

        return result;
    }

    @Override
    public WriteOperation<Result> getWriteOperation() {
        return this.writeOperation;
    }

    @Override
    public Iterable<IndexedRecord> getSuccessfulWrites() {
        return Collections.unmodifiableList(successfulWrites);
    }

    @Override
    public Iterable<IndexedRecord> getRejectedWrites() {
        return Collections.unmodifiableList(rejectedWrites);
    }

    private TableOperation getTableOperation(DynamicTableEntity entity) {
        TableOperation tableOpe = null;
        switch (actionData) {
        case Insert:
            tableOpe = TableOperation.insert(entity);
            break;
        case Insert_Or_Merge:
            tableOpe = TableOperation.insertOrMerge(entity);
            break;
        case Insert_Or_Replace:
            tableOpe = TableOperation.insertOrReplace(entity);
            break;
        case Merge:
            tableOpe = TableOperation.merge(entity);
            break;
        case Replace:
            tableOpe = TableOperation.replace(entity);
            break;
        case Delete:
            tableOpe = TableOperation.delete(entity);
            break;
        default:
            LOGGER.error("No specified operation for table");
        }

        return tableOpe;
    }

    private void addOperationToBatch(DynamicTableEntity entity, IndexedRecord record) throws IOException {
        if (latestPartitionKey == null || latestPartitionKey.isEmpty()) {
            latestPartitionKey = entity.getPartitionKey();
        }
        // we reached the threshold for batch OR changed PartitionKey
        if (batchOperationsCount == 100 || !entity.getPartitionKey().equals(latestPartitionKey)) {
            processBatch();
            latestPartitionKey = entity.getPartitionKey();
        }
        TableOperation to = getTableOperation(entity);
        batchOperations.add(to);
        batchRecords.add(record);
        batchOperationsCount++;
        latestPartitionKey = entity.getPartitionKey();
    }

    private void processBatch() throws IOException {
        TableBatchOperation batch = new TableBatchOperation();
        batch.addAll(batchOperations);
        //
        try {
            tableservice.executeOperation(tableName, batch);

            handleSuccess(null, batchOperationsCount);

        } catch (StorageException e) {
            LOGGER.error(i18nMessages.getMessage("error.ProcessBatch", actionData, e.getLocalizedMessage()));

            handleReject(null, e, batchOperationsCount);

            if (dieOnError) {
                throw new ComponentException(e);
            }
        } catch (URISyntaxException | InvalidKeyException e) {
            throw new ComponentException(e); // connection problem so next operation will also fail, we stop the process
        }
        // reset operations, count and marker
        batchOperations.clear();
        batchRecords.clear();
        batchOperationsCount = 0;
        latestPartitionKey = "";
    }

    private void handleSuccess(IndexedRecord record, int counted) {
        result.successCount = result.successCount + counted;
        if (writeSchema == null || writeSchema.getFields().isEmpty())
            return;
        if (record != null) {
            successfulWrites.add(record);
        } else {
            successfulWrites.addAll(batchRecords);
        }
    }

    private void handleReject(IndexedRecord record, StorageException e, int counted) {
        result.rejectCount = result.rejectCount + counted;

        if (rejectSchema == null || rejectSchema.getFields().isEmpty()) {
            LOGGER.warn(i18nMessages.getMessage("warn.NoRejectSchema"));
            return;
        }

        if (record != null && record.getSchema().equals(rejectSchema)) {
            rejectedWrites.add(record);
        } else {
            if (processOperationInBatch) {
                for (IndexedRecord r : batchRecords) {
                    IndexedRecord reject = new GenericData.Record(rejectSchema);
                    reject.put(rejectSchema.getField("errorCode").pos(), e.getErrorCode());
                    reject.put(rejectSchema.getField("errorMessage").pos(), e.getLocalizedMessage());
                    for (Schema.Field outField : reject.getSchema().getFields()) {
                        Object outValue;
                        Schema.Field inField = r.getSchema().getField(outField.name());
                        if (inField != null) {
                            outValue = r.get(inField.pos());
                            reject.put(outField.pos(), outValue);
                        }
                    }
                    rejectedWrites.add(reject);
                }

            } else {
                IndexedRecord reject = new GenericData.Record(rejectSchema);
                reject.put(rejectSchema.getField("errorCode").pos(), e.getErrorCode());
                reject.put(rejectSchema.getField("errorMessage").pos(), e.getLocalizedMessage());
                for (Schema.Field outField : reject.getSchema().getFields()) {
                    Object outValue;
                    Schema.Field inField = record.getSchema().getField(outField.name());
                    if (inField != null) {
                        outValue = record.get(inField.pos());
                        reject.put(outField.pos(), outValue);
                    }
                }
                rejectedWrites.add(reject);
            }
        }
    }
}
