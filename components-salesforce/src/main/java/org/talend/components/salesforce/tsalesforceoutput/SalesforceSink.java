package org.talend.components.salesforce.tsalesforceoutput;

import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.output.Sink;
import org.talend.components.api.runtime.output.Writer;
import org.talend.components.api.runtime.row.BaseRowStruct;
import org.talend.components.salesforce.metadata.SalesforceMetadata;
import org.talend.components.salesforce.tsalesforceconnection.SalesforceConnectionObject;
import org.talend.components.salesforce.tsalesforceconnection.SalesforceConnectionManager;
import org.talend.daikon.schema.Schema;
import org.talend.daikon.schema.SchemaElement;

import java.util.List;

/**
 * Created by bchen on 16-1-28.
 */
public class SalesforceSink implements Sink {
    private TSalesforceOutputProperties props;
    private SalesforceConnectionObject conn;

    @Override
    public void init(ComponentProperties properties) throws TalendConnectionException {
        props = (TSalesforceOutputProperties) properties;
        String refedComponentId = props.connection.referencedComponentId.getStringValue();
        SalesforceConnectionManager connManager = new SalesforceConnectionManager();
        if (refedComponentId == null) {
            conn = connManager.newConnection(props.connection);
        } else {
            conn = connManager.getConnectionByKey(refedComponentId, props.connection);
        }
        SalesforceMetadata metadata = new SalesforceMetadata();
        metadata.initSchemaForDynamic(props.module);
    }

    @Override
    public void close() {

    }

    @Override
    public Writer getRecordWriter() {
        return null;
    }

    @Override
    public void initDest() {

    }

    @Override
    public List<SchemaElement> getSchema() {
        return ((Schema) props.module.schema.schema.getValue()).getRoot().getChildren();
    }

    public class SalesforceWriter implements Writer {

        @Override
        public void write(BaseRowStruct rowStruct) {

        }

        @Override
        public void close() {

        }
    }
}
