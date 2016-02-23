package org.talend.components.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.DataType;

/**
 * Creates an {@link IndexedRecordAdapterFactory} that knows how to interpret Cassandra {@link BoundStatement} objects.
 */
public class BoundStatementAdapterFactory extends CassandraBaseAdapterFactory<BoundStatement, BoundStatement, BoundStatement> {

    @Override
    public Class<BoundStatement> getDatumClass() {
        return BoundStatement.class;
    }

    @Override
    protected void setContainerDataSpecFromInstance(BoundStatement statement) {
        setContainerDataSpec(statement);
    }

    @Override
    public DataType getFieldDataSpec(int i) {
        return getContainerDataSpec().preparedStatement().getVariables().getType(i);
    }

    /**
     * This always returns the instance passed in {@link #setContainerDataSpec(BoundStatement)}.
     */
    @Override
    protected BoundStatement createOrGetInstance() {
        return getContainerDataSpec();
    }
}
