package org.talend.components.cassandra.runtime;

import com.datastax.driver.core.*;
import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.component.runtime.WriterResult;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.cassandra.output.TCassandraOutputProperties;
import org.talend.daikon.avro.IndexedRecordAdapterFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

public class CassandraWriter implements Writer<WriterResult> {
    private CassandraWriteOperation cassandraWriteOperation;
    private RuntimeContainer container;

    private CassandraSink cassandraSink;
    private IndexedRecordAdapterFactory<Object, ? extends IndexedRecord> adapterFactory;
    private BoundStatementAdapterFactory boundStatementAdapterFactory = new BoundStatementAdapterFactory();

    private TCassandraOutputProperties properties;
    private Session session;
    private PreparedStatement preparedStatement;
    private BoundStatement boundStatement;

    //batch only properties
    private boolean useBatch = false;
    private BatchStatement batchStatement;
    private CassandraBatchUtil cassandraBatchUtil;
    private ByteBuffer currentKey;
    private ByteBuffer lastKey;
    private boolean newOne;

    public CassandraWriter(CassandraWriteOperation cassandraWriteOperation, RuntimeContainer container) {
        this.cassandraWriteOperation = cassandraWriteOperation;
        this.container = container;
        this.cassandraSink = (CassandraSink) cassandraWriteOperation.getSink();
    }

    @Override
    public void open(String uId) throws IOException {
        session = ((CassandraSink) getWriteOperation().getSink()).connect(container);
        properties = (TCassandraOutputProperties) cassandraSink.properties;
        CQLManager cqlManager = new CQLManager(properties);
        preparedStatement = session.prepare(cqlManager.generatePreActionCQL());
        useBatch = properties.useUnloggedBatch.getBooleanValue();
        if (useBatch) {
            cassandraBatchUtil = new CassandraBatchUtil(session.getCluster(), cqlManager.getKeyspace(), cqlManager.getTableName(), cqlManager.getValueColumns());
            newOne = false;
            batchStatement = new BatchStatement(BatchStatement.Type.UNLOGGED);
        } else {
            boundStatement = new BoundStatement(preparedStatement);
        }
    }

    @Override
    public void write(Object object) throws IOException {
        if(object == null){
            return;
        }

        if(object instanceof Row){
            //TODO optimizedWrite
        }

        if (useBatch) {
            boundStatement = new BoundStatement(preparedStatement);
        }

        if (adapterFactory == null) {
            adapterFactory = (IndexedRecordAdapterFactory<Object, ? extends IndexedRecord>) CassandraAvroRegistry.get().createAdapterFactory(object.getClass());
        }
        boundStatementAdapterFactory.setContainerTypeFromInstance(boundStatement);
        boundStatement = boundStatementAdapterFactory.convertToDatum((IndexedRecord) object);

        if (useBatch) {
            currentKey = cassandraBatchUtil.getKey(boundStatement);
            newOne = lastKey != null && lastKey.compareTo(currentKey) != 0;
            if (newOne && batchStatement.size() > 0) {
                executeBatch();
            }
            batchStatement.add(boundStatement);
            lastKey = currentKey;
            if (batchStatement.size() >= properties.batchSize.getIntValue()) {
                executeBatch();
            }
        } else {
            try {
                session.execute(boundStatement);
            } catch (Exception e) {
                if (properties.dieOnError.getBooleanValue()) {
                    throw e;
                } else {
                    //FIXME(bchen) what can we do here? still print err
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    private void executeBatch() {
        session.execute(batchStatement);
        batchStatement.clear();
    }

    @Override
    public WriterResult close() throws IOException {
        session.close();
        session.getCluster().close();
        return null;
    }

    @Override
    public WriteOperation<WriterResult> getWriteOperation() {
        return cassandraWriteOperation;
    }
}
