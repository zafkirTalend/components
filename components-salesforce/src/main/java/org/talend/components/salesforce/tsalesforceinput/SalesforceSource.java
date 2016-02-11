package org.talend.components.salesforce.tsalesforceinput;

import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.input.Reader;
import org.talend.components.api.runtime.input.SingleSplit;
import org.talend.components.api.runtime.input.Source;
import org.talend.components.api.runtime.input.Split;
import org.talend.components.salesforce.metadata.SalesforceMetadata;
import org.talend.components.salesforce.tsalesforceconnection.SalesforceConnectionManager;
import org.talend.components.salesforce.tsalesforceconnection.SalesforceConnectionObject;
import org.talend.components.salesforce.type.SalesforceBaseType;
import org.talend.daikon.schema.DataSchema;
import org.talend.daikon.schema.MakoElement;
import org.talend.daikon.schema.internal.DataSchemaElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bchen on 16-1-28.
 */
public class SalesforceSource implements Source<SObject> {

    TSalesforceInputProperties props;

    SalesforceConnectionObject conn;

    @Override
    public void init(ComponentProperties properties) throws TalendConnectionException {
        props = (TSalesforceInputProperties) properties;
        // even use exist connection, but connection information has been store in input properties before call source
        String refedComponentId = props.connection.referencedComponentId.getStringValue();
        SalesforceConnectionManager connManager = new SalesforceConnectionManager();
        if (refedComponentId == null) {
            conn = connManager.newConnection(props.connection);
        } else {
            conn = connManager.getConnectionByKey(refedComponentId, props.connection);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public Reader<SObject> getRecordReader(Split split) throws TalendConnectionException {
        return new SalesforceReader(props, conn, split);
    }

    @Override
    public boolean supportSplit() {
        return false;
    }

    @Override
    public Split[] getSplit(int num) {
        return new Split[0];
    }

    @Override
    public String getFamilyName() {
        return SalesforceBaseType.FAMILY_NAME;
    }

    public class SalesforceReader implements Reader<SObject> {

        SalesforceConnectionObject conn;

        QueryResult query;

        SObject[] records;

        SObject current;

        int currentIndex = 0;

        SalesforceReader(TSalesforceInputProperties props, SalesforceConnectionObject conn, Split split)
                throws TalendConnectionException {
            if (split instanceof SingleSplit) {
                String queryText;
                SalesforceMetadata metadata = new SalesforceMetadata();
                if (props.manualQuery.getBooleanValue()) {
                    queryText = props.query.getStringValue();
                } else {
                    metadata.initSchemaForDynamic(props.module);
                    List<String> columnsName = new ArrayList<>();
                    for (MakoElement se : ((DataSchema) props.module.schema.schema.getValue()).getRoot().getChildren()) {
                        if (se.getType() == MakoElement.Type.DYNAMIC) {
                            for (MakoElement seInDyn : se.getChildren()) {
                                columnsName.add(((DataSchemaElement) seInDyn).getAppColName());
                            }
                        } else {
                            columnsName.add(((DataSchemaElement) se).getAppColName());
                        }
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append("select ");
                    int count = 0;
                    for (String colName : columnsName) {
                        if (count++ > 0) {
                            sb.append(", ");
                        }
                        sb.append(colName);
                    }
                    sb.append(" from ");
                    sb.append(props.module.moduleName.getStringValue());
                    queryText = sb.toString();
                }
                try {
                    this.conn = conn;
                    query = conn.getPartnerConnection().query(queryText);
                    records = query.getRecords();
                    if (props.manualQuery.getBooleanValue()) {
                        if (records.length > 0 && records[0] != null) {
                            metadata.initSchemaForDynamicWithFirstRow(props.module, records[0]);
                        }
                    }
                } catch (ConnectionException e) {
                    throw new TalendConnectionException(e.getMessage());
                }
            }
        }

        @Override
        public boolean start() {
            return advance();
        }

        @Override
        public boolean advance() {
            if (currentIndex >= records.length) {
                if (query.isDone()) {
                    return false;
                }
                try {
                    query = conn.getPartnerConnection().queryMore(query.getQueryLocator());
                    records = query.getRecords();
                    currentIndex = 0;
                } catch (ConnectionException e) {
                    e.printStackTrace();
                }
            }
            current = records[currentIndex];
            currentIndex++;
            return true;
        }

        @Override
        public SObject getCurrent() {
            return current;
        }

        @Override
        public void close() {

        }

        @Override
        public List<MakoElement> getSchema() {
            return ((DataSchema) props.module.schema.schema.getValue()).getRoot().getChildren();
        }
    }
}
