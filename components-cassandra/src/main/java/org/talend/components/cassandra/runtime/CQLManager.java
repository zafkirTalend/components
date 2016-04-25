package org.talend.components.cassandra.runtime;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.talend.components.cassandra.output.TCassandraOutputProperties;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.talend6.Talend6SchemaConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Column {

    private final Field f;

    private String mark = "?";

    private String assignmentOperation = "=";

    private Column assignmentKey;

    private boolean asColumnKey = false;

    public Column(Field f) {
        this.f = f;
    }

    public Schema getSchema() {
        return f.schema();
    }

    public String getName() {
        return f.name();
    }

    public String getDBName() {
        //FIXME(bchen) getSchema.getProp or field.getProp?
        String dbName = getSchema().getProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME);
        return dbName == null ? getName() : dbName;
    }

    public boolean isKey() {
        //FIXME(bchen) getSchema.getProp or field.getProp?
        String isKey = getSchema().getProp(Talend6SchemaConstants.TALEND6_COLUMN_IS_KEY);//FIXME(bchen) move to use SchemaConstants
        return isKey != null && "true".equalsIgnoreCase(isKey);
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public void setAssignmentOperation(String op) {
        this.assignmentOperation = op;
    }

    public String getAssignmentOperation() {
        return assignmentOperation;
    }

    public void setAssignmentKey(Column keyColumn) {
        this.assignmentKey = keyColumn;
    }

    public Column getAssignmentKey() {
        return assignmentKey;
    }

    public void setAsColumnKey(boolean asColumnKey) {
        this.asColumnKey = asColumnKey;
    }

    public boolean getAsColumnKey() {
        return asColumnKey;
    }
}

class CQLManager {

    private String[] KeyWords = {"ADD", "ALL", "ALLOW", "ALTER", "AND", "ANY", "APPLY", "AS", "ASC", "ASCII", "AUTHORIZE",
            "BATCH", "BEGIN", "BIGINT", "BLOB", "BOOLEAN", "BY", "CLUSTERING", "COLUMNFAMILY", "COMPACT", "CONSISTENCY", "COUNT",
            "COUNTER", "CREATE", "CUSTOM", "DECIMAL", "DELETE", "DESC", "DISTINCT", "DOUBLE", "DROP", "EACH_QUORUM", "EXISTS",
            "FILTERING", "FLOAT", "FROM", "frozen", "GRANT", "IF", "IN", "INDEX", "INET", "INFINITY", "INSERT", "INT", "INTO",
            "KEY", "KEYSPACE", "KEYSPACES", "LEVEL", "LIMIT", "LIST", "LOCAL_ONE", "LOCAL_QUORUM", "MAP", "MODIFY", "NAN",
            "NORECURSIVE", "NOSUPERUSER", "NOT", "OF", "ON", "ONE", "ORDER", "PASSWORD", "PERMISSION", "PERMISSIONS", "PRIMARY",
            "QUORUM", "RENAME", "REVOKE", "SCHEMA", "SELECT", "SET", "STATIC", "STORAGE", "SUPERUSER", "TABLE", "TEXT",
            "TIMESTAMP", "TIMEUUID", "THREE", "TO", "TOKEN", "TRUNCATE", "TTL", "TWO", "TYPE", "UNLOGGED", "UPDATE", "USE",
            "USER", "USERS", "USING", "UUID", "VALUES", "VARCHAR", "VARINT", "WHERE", "WITH", "WRITETIME"};

    private TCassandraOutputProperties props;

    private String action;

    private String keyspace;

    private String tableName;

    private Boolean useSpark = false;

    private List<Column> valueColumns;

    public CQLManager(TCassandraOutputProperties props) {
        this.props = props;
        this.action = this.props.dataAction.getStringValue();
        this.keyspace = this.props.getSchemaProperties().keyspace.getStringValue();
        this.tableName = this.props.getSchemaProperties().columnFamily.getStringValue();
        this.tableName = this.keyspace + "." + this.tableName;//FIXME(bchen) double quote around
        createColumnList(new Schema.Parser().parse(this.props.getSchemaProperties().main.schema.getStringValue()));
        this.valueColumns = collectValueColumns();
    }

    public CQLManager(TCassandraOutputProperties props, boolean useSpark) {
        this(props);
        this.useSpark = useSpark;
    }

    public String getKeyspace(){
        return keyspace;
    }
    public String getTableName(){
        return tableName;
    }

    private List<Column> all;

    private List<Column> keys;

    private List<Column> normals;

    private List<Column> conditions;

    private Column ttl;

    private Column timestamp;

    private void createColumnList(Schema schema) {
        all = new ArrayList<Column>();
        for (Field f : schema.getFields()) {
            all.add(new Column(f));
        }
        keys = new ArrayList<Column>();
        normals = new ArrayList<Column>();
        conditions = new ArrayList<Column>();
        boolean usingTimestamp = props.usingTimestamp.getBooleanValue();
        String timestampColName = props.timestamp.getStringValue();
        for (Column column : all) {
            if (TCassandraOutputProperties.ACTION_INSERT.equals(action) || TCassandraOutputProperties.ACTION_UPDATE.equals(action)) {
                boolean usingTTL = props.usingTTL.getBooleanValue();
                String ttlColName = props.ttl.getStringValue();
                if (usingTTL && ttlColName.equals(column.getName())) {
                    ttl = column;
                    ttl.setMark("TTL ?");
                    continue;
                }
            }
            if (usingTimestamp && timestampColName.equals(column.getName())) {
                timestamp = column;
                timestamp.setMark("TIMESTAMP ?");
                continue;
            }
            if (column.isKey()) {
                keys.add(column);
                continue;
            }
            if (TCassandraOutputProperties.ACTION_UPDATE.equals(action) || (TCassandraOutputProperties.ACTION_DELETE.equals(action) && !props.deleteIfExists.getBooleanValue())) {
                List<Map<String, String>> ifCoditions = (List<Map<String, String>>) props.ifCondition.getValue();
                boolean matched = false;
                for (Map<String, String> ifCodition : ifCoditions) {
                    if (ifCodition.get(props.ifConditionColumnName.getName()).equals(column.getName())) {
                        conditions.add(column);
                        matched = true;
                        continue;
                    }
                }
                if (matched) {
                    continue;
                }
            }
            normals.add(column);
        }
        if (TCassandraOutputProperties.ACTION_UPDATE.equals(action)) {
            List<Map<String, String>> assignOperations = (List<Map<String, String>>) props.assignmentOperation.getValue();
            List<Column> keyColumns = new ArrayList<Column>();
            for (Column column : normals) {
                for (Map<String, String> operation : assignOperations) {
                    String updateColumnKeyName = operation.get(props.keyColumn.getName());
                    String updateColumnOperation = operation.get(props.operation.getName());
                    if (props.POSITION_OR_KEY.equals(updateColumnOperation) && column.getName().equals(updateColumnKeyName)) {
                        keyColumns.add(column);
                    }
                }
            }
            normals.removeAll(keyColumns);
            for (Column column : normals) {
                for (Map<String, String> operation : assignOperations) {
                    String updateColumnName = operation.get(props.assignmentOperationColumnName.getName());
                    String updateColumnKeyName = operation.get(props.keyColumn.getName());
                    String updateColumnOperation = operation.get(props.operation.getName());
                    if (updateColumnName.equals(column.getName())) {
                        column.setAssignmentOperation(updateColumnOperation);
                        if (props.POSITION_OR_KEY.equals(updateColumnOperation)) {
                            for (Column keyColumn : keyColumns) {
                                if (keyColumn.getName().equals(updateColumnKeyName)) {
                                    column.setAssignmentKey(keyColumn);
                                }
                            }
                        }
                        continue;
                    }
                }
            }
        }
        if (TCassandraOutputProperties.ACTION_DELETE.equals(action)) {
            List<Map<String, String>> columnsKey = (List<Map<String, String>>) props.deleteColumnByPositionKey.getValue();
            for (Column column : normals) {
                for (Map<String, String> columnKey : columnsKey) {
                    if (column.getName().equals(columnKey.get(props.deleteColumnByPositionKeyColumnName.getName()))) {
                        column.setAsColumnKey(true);
                    }
                }
            }
        }
    }

    private List<Column> collectValueColumns() {
        List<Column> columns = new ArrayList<>();
        if (TCassandraOutputProperties.ACTION_INSERT.equals(action)) {
            columns.addAll(keys);
            columns.addAll(normals);
            if (ttl != null)
                columns.add(ttl);
            if (timestamp != null)
                columns.add(timestamp);
        } else if (TCassandraOutputProperties.ACTION_UPDATE.equals(action)) {
            if (ttl != null)
                columns.add(ttl);
            if (timestamp != null)
                columns.add(timestamp);
            for (Column normal : normals) {
                if (normal.getAssignmentKey() != null) {
                    columns.add(normal.getAssignmentKey());
                }
                columns.add(normal);
            }
            columns.addAll(keys);
            columns.addAll(conditions);
        } else if (TCassandraOutputProperties.ACTION_DELETE.equals(action)) {
            for (Column column : normals) {
                if (column.getAsColumnKey()) {
                    columns.add(column);
                }
            }
            if (timestamp != null)
                columns.add(timestamp);
            columns.addAll(keys);
            boolean ifExist = props.deleteIfExists.getBooleanValue();
            if (!ifExist) {
                columns.addAll(conditions);
            }
        }
        return columns;
    }

    private String getLProtectedChar(String keyword) {
        return "\"";
    }

    private String getRProtectedChar(String keyword) {
        return "\"";
    }

    private String wrapProtectedChar(String keyword) {
        if (keyword.matches("^[a-z0-9_]+$")) {
            return keyword;
        } else {
            return getLProtectedChar(keyword) + keyword + getRProtectedChar(keyword);
        }
    }

    public List<String> getValueColumns() {
        List<String> valueColumnsName = new ArrayList<String>();
        for (Column col : valueColumns) {
            valueColumnsName.add(col.getName());
        }
        return valueColumnsName;
    }

    private String getDropKSCQL(boolean ifExists) {
        StringBuilder dropKSCQL = new StringBuilder();
        dropKSCQL.append("DROP KEYSPACE ");
        if (ifExists) {
            dropKSCQL.append("IF EXISTS ");
        }
        dropKSCQL.append(this.keyspace);
        return dropKSCQL.toString();
    }

    private String getCreateKSCQL(boolean ifNotExists) {
        StringBuilder createKSCQL = new StringBuilder();
        createKSCQL.append("CREATE KEYSPACE ");
        if (ifNotExists) {
            createKSCQL.append("IF NOT EXISTS ");
        }
        createKSCQL.append(this.keyspace);
        createKSCQL.append("WITH REPLICATION = {'class' : '" + props.replicaStrategy.getStringValue() + "',");
        if (TCassandraOutputProperties.KS_REPLICA_SIMPLE.equals(props.replicaStrategy.getStringValue())) {
            createKSCQL.append("'replication_factor' : " + props.simpleReplicaNumber.getIntValue() + "}");
        } else {
            List<Map<String, String>> replicas = (List<Map<String, String>>) props.networkReplicaTable.getValue();
            int count = 1;
            for (Map<String, String> replica : replicas) {
                createKSCQL.append("'" + replica.get(props.datacenterName.getName()) + "' : " + replica.get(props.replicaNumber.getIntValue()));
                if (count < replicas.size()) {
                    createKSCQL.append(",");
                }
                count++;
            }
            createKSCQL.append("}");
        }

        return createKSCQL.toString();
    }

    public List<String> getKSCQLs() {
        List<String> cqls = new ArrayList<>();
        String actionOnKeyspace = props.actionOnKeyspace.getStringValue();
        //No action needed for delete operation
        if (TCassandraOutputProperties.ACTION_DELETE.equals(props.dataAction.getStringValue()) || TCassandraOutputProperties.ACTION_NONE.equals(actionOnKeyspace)) {
            return cqls;
        }

        if (TCassandraOutputProperties.ACTION_DROP_CREATE.equals(actionOnKeyspace)) {
            cqls.add(getDropKSCQL(false));
            cqls.add(getCreateKSCQL(false));
        } else if (TCassandraOutputProperties.ACTION_CREATE.equals(actionOnKeyspace)) {
            cqls.add(getCreateKSCQL(false));
        } else if (TCassandraOutputProperties.ACTION_CREATE_IF_NOT_EXISTS.equals(actionOnKeyspace)) {
            cqls.add(getCreateKSCQL(true));
        } else if (TCassandraOutputProperties.ACTION_DROP_IF_EXISTS_AND_CREATE.equals(actionOnKeyspace)) {
            cqls.add(getDropKSCQL(true));
            cqls.add(getCreateKSCQL(false));
        }
        return cqls;
    }

    public List<String> getTableCQLs() throws IOException {
        List<String> cqls = new ArrayList<>();
        String actionOnColumnFamily = props.actionOnColumnFamily.getStringValue();
        //No action needed for delete operation
        if (TCassandraOutputProperties.ACTION_DELETE.equals(props.dataAction.getStringValue()) || TCassandraOutputProperties.ACTION_NONE.equals(actionOnColumnFamily)) {
            return cqls;
        }

        if (!TCassandraOutputProperties.ACTION_TRUNCATE.equals(actionOnColumnFamily) && containsUnsupportTypes()) {
            throw new IOException("Don't support create table with set/list/map");
        }

        switch (actionOnColumnFamily) {
            case TCassandraOutputProperties.ACTION_DROP_CREATE:
                cqls.add(getDropTableCQL(false));
                cqls.add(getCreateTableCQL(false));
                break;
            case TCassandraOutputProperties.ACTION_CREATE:
                cqls.add(getCreateTableCQL(false));
                break;
            case TCassandraOutputProperties.ACTION_DROP_IF_EXISTS_AND_CREATE:
                cqls.add(getDropTableCQL(true));
                cqls.add(getCreateTableCQL(false));
                break;
            case TCassandraOutputProperties.ACTION_CREATE_IF_NOT_EXISTS:
                cqls.add(getCreateTableCQL(true));
                break;
            case TCassandraOutputProperties.ACTION_TRUNCATE:
                cqls.add(getTruncateTableCQL());
            default:
                break;
        }

        return cqls;
    }

    private String getDropTableCQL(boolean ifExists) {
        StringBuilder dropTableCQL = new StringBuilder();
        dropTableCQL.append("DROP TABLE ");
        if (ifExists) {
            dropTableCQL.append("IF EXISTS ");
        }
        dropTableCQL.append(tableName);
        return dropTableCQL.toString();
    }

    private String getCreateTableCQL(boolean ifNotExists) {
        StringBuilder createCQL = new StringBuilder();
        createCQL.append("CREATE TABLE ");
        if (ifNotExists) {
            createCQL.append("IF NOT EXISTS ");
        }
        createCQL.append(tableName + "(");
        List<Column> columns = new ArrayList<Column>();
        columns.addAll(keys);
        columns.addAll(normals);
        if (TCassandraOutputProperties.ACTION_UPDATE.equals(action)) {
            columns.addAll(conditions);
        }
        int count = 1;
        for (Column column : columns) {
            createCQL.append(wrapProtectedChar(column.getDBName()));
            createCQL.append(" ");
            createCQL.append(CassandraAvroRegistry.getDataType(column.getSchema()));
            if (count < columns.size()) {
                createCQL.append(",");
            }
            count++;
        }
        if (keys.size() > 0) {
            createCQL.append(",PRIMARY KEY(");
            int i = 1;
            for (Column column : keys) {
                createCQL.append(wrapProtectedChar(column.getDBName()));
                if (i < keys.size()) {
                    createCQL.append(",");
                }
                i++;
            }
            createCQL.append(")");
        }
        createCQL.append(")");
        return createCQL.toString();
    }

    private boolean containsUnsupportTypes() {
        boolean unsupport = false;
        List<String> unsupportTypes = java.util.Arrays.asList(new String[]{"set", "list", "map"});
        List<Column> columns = new ArrayList<Column>();
        columns.addAll(keys);
        columns.addAll(normals);
        if (TCassandraOutputProperties.ACTION_UPDATE.equals(action)) {
            columns.addAll(conditions);
        }
        for (Column column : columns) {
            if (unsupportTypes.contains(CassandraAvroRegistry.getDataType(column.getSchema()))) {
                return true;
            }
        }
        return false;
    }

    private String getDeleteTableCQL() {
        StringBuilder deleteTableCQL = new StringBuilder();
        deleteTableCQL.append("DELETE FROM " + tableName);
        return deleteTableCQL.toString();
    }

    private String getTruncateTableCQL() {
        StringBuilder truncateTableCQL = new StringBuilder();
        truncateTableCQL.append("TRUNCATE " + tableName);
        return truncateTableCQL.toString();
    }

    public String generatePreActionCQL() {
        if ("INSERT".equals(action)) {
            return generatePreInsertCQL();
        } else if ("UPDATE".equals(action)) {
            return generatePreUpdateCQL();
        } else if ("DELETE".equals(action)) {
            return generatePreDeleteCQL();
        }
        return "";
    }

    /*
     * INSERT INTO table_name( identifier, column_name...)VALUES ( value, value ... )USING option AND option
     */
    private String generatePreInsertCQL() {
        List<Column> columns = new ArrayList<Column>();
        columns.addAll(keys);
        columns.addAll(normals);

        int count = 1;
        StringBuilder preInsertCQL = new StringBuilder();
        preInsertCQL.append("INSERT INTO " + tableName + " (");
        for (Column column : columns) {
            preInsertCQL.append(wrapProtectedChar(column.getDBName()));
            if (count < columns.size()) {
                preInsertCQL.append(",");
            }
            count++;
        }
        preInsertCQL.append(") VALUES (");
        count = 1;
        for (Column column : columns) {
            preInsertCQL.append(column.getMark());
            if (count < columns.size()) {
                preInsertCQL.append(",");
            }
            count++;
        }
        preInsertCQL.append(")");
        boolean ifNotExist = props.insertIfNotExists.getBooleanValue();
        if (ifNotExist) {
            preInsertCQL.append(" IF NOT EXISTS");
        }
        if (ttl != null || timestamp != null) {
            preInsertCQL.append(" USING ");
            if (ttl != null) {
                preInsertCQL.append(ttl.getMark());
                if (timestamp != null) {
                    preInsertCQL.append(" AND ");
                }
            }
            if (timestamp != null) {
                preInsertCQL.append(timestamp.getMark());
            }
        }
        return preInsertCQL.toString();
    }

    private String generatePreUpdateCQL() {
        StringBuilder preUpdateCQL = new StringBuilder();
        preUpdateCQL.append("UPDATE " + tableName);
        if (ttl != null || timestamp != null) {
            preUpdateCQL.append(" USING ");
            if (ttl != null) {
                preUpdateCQL.append(ttl.getMark());
                if (timestamp != null) {
                    preUpdateCQL.append(" AND ");
                }
            }
            if (timestamp != null) {
                preUpdateCQL.append(timestamp.getMark());
            }
        }
        preUpdateCQL.append(" SET ");
        int count = 1;
        for (Column column : normals) {

            String assignment = wrapProtectedChar(column.getDBName()) + "=" +
                    column.getMark();

            if (TCassandraOutputProperties.APPEND.equals(column.getAssignmentOperation())) {
                assignment = wrapProtectedChar(column.getDBName()) + "=" +
                        wrapProtectedChar(column.getDBName()) + "+" +
                        column.getMark();
            } else if (TCassandraOutputProperties.PREPEND.equals(column.getAssignmentOperation())) {
                assignment = wrapProtectedChar(column.getDBName()) + "=" + column.getMark()
                        + "+" +
                        wrapProtectedChar(column.getDBName());
            } else if (TCassandraOutputProperties.MINUS.equals(column.getAssignmentOperation())) {
                assignment = wrapProtectedChar(column.getDBName()) + "=" +
                        wrapProtectedChar(column.getDBName()) + "-" +
                        column.getMark();
            } else if (TCassandraOutputProperties.POSITION_OR_KEY.equals(column.getAssignmentOperation())) {
                assignment = wrapProtectedChar(column.getDBName()) + "[?]=" +
                        column.getMark();
            }

            preUpdateCQL.append(assignment);

            if (count < normals.size()) {
                preUpdateCQL.append(",");
            }
            count++;
        }
        preUpdateCQL.append(" WHERE ");
        count = 1;
        for (Column column : keys) {
            preUpdateCQL.append(wrapProtectedChar(column.getDBName()));
            preUpdateCQL.append(rowKeyInList(column) ? " IN " : "=");
            preUpdateCQL.append(column.getMark());
            if (count < keys.size()) {
                preUpdateCQL.append(" AND ");
            }
            count++;
        }
        if (conditions.size() > 0) {
            preUpdateCQL.append(" IF ");
            count = 1;
            for (Column column : conditions) {
                preUpdateCQL.append(wrapProtectedChar(column.getDBName()));
                preUpdateCQL.append("=");
                preUpdateCQL.append(column.getMark());
                if (count < conditions.size()) {
                    preUpdateCQL.append(" AND ");
                }
                count++;
            }
        }
        // can't work actually, even it supported on office document
        // boolean ifExist = "true".equals(ElementParameterParser.getValue(node,
        // "__UPDATE_IF_EXISTS__"));
        // if(ifExist){
        // preUpdateSQL.append(" IF EXISTS");
        // }

        return preUpdateCQL.toString();

    }

    private boolean rowKeyInList(Column column) {
        List<Map<String, String>> rowKeyInList = (List<Map<String, String>>) props.rowKeyInList.getValue();
        for (Map<String, String> rowKey : rowKeyInList) {
            if (column.getName().equals(rowKey.get(props.rowKeyInListColumnName.getName()))) {
                return true;
            }
        }
        return false;
    }

    private String generatePreDeleteCQL() {
        StringBuilder preDeleteCQL = new StringBuilder();
        preDeleteCQL.append("DELETE ");
        int count = 1;
        for (Column column : normals) {
            preDeleteCQL.append(wrapProtectedChar(column.getDBName()));
            if (column.getAsColumnKey()) {
                preDeleteCQL.append("[?]");
            }
            if (count < normals.size()) {
                preDeleteCQL.append(",");
            }
            count++;
        }
        preDeleteCQL.append(" FROM " + tableName);
        if (timestamp != null) {
            preDeleteCQL.append(" USING ");
            preDeleteCQL.append(timestamp.getMark());
        }
        if (keys.size() > 0) {
            preDeleteCQL.append(" WHERE ");
            count = 1;
            for (Column column : keys) {
                preDeleteCQL.append(wrapProtectedChar(column.getDBName()));
                preDeleteCQL.append(rowKeyInList(column) ? " IN " : "=");
                preDeleteCQL.append(column.getMark());
                if (count < keys.size()) {
                    preDeleteCQL.append(" AND ");
                }
                count++;
            }
        }
        boolean ifExist = props.deleteIfExists.getBooleanValue();
        if (ifExist) {
            preDeleteCQL.append(" IF EXISTS");
        } else {
            if (conditions.size() > 0) {
                preDeleteCQL.append(" IF ");
                count = 1;
                for (Column column : conditions) {
                    preDeleteCQL.append(wrapProtectedChar(column.getDBName()));
                    preDeleteCQL.append("=");
                    preDeleteCQL.append(column.getMark());
                    if (count < conditions.size()) {
                        preDeleteCQL.append(" AND ");
                    }
                    count++;
                }
            }
        }
        return preDeleteCQL.toString();
    }

}