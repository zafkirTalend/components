package org.talend.components.cassandra.output;

import org.apache.avro.Schema;
import org.talend.components.cassandra.CassandraIOBasedProperties;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

import java.util.ArrayList;
import java.util.List;

import static org.talend.daikon.properties.PropertyFactory.*;
import static org.talend.daikon.properties.presentation.Widget.widget;

public class TCassandraOutputProperties extends CassandraIOBasedProperties {

    /**
     * named constructor to be used is these properties are nested in other properties. Do not subclass this method for
     * initialization, use {@link #init()} instead.
     *
     * @param name
     */
    public TCassandraOutputProperties(String name) {
        super(name);
    }

    //TODO(bchen) be common?
    public static final String ACTION_NONE = "NONE";
    public static final String ACTION_DROP_CREATE = "DROP_CREATE";
    public static final String ACTION_CREATE = "CREATE";
    public static final String ACTION_CREATE_IF_NOT_EXISTS = "CREATE_IF_NOT_EXISTS";
    public static final String ACTION_DROP_IF_EXISTS_AND_CREATE = "DROP_IF_EXISTS_AND_CREATE";
    public static final String ACTION_TRUNCATE = "TRUNCATE";

    //TODO(bchen) any improvement for enum? how to set, how to get, how to check
    public Property actionOnKeyspace = newEnum("actionOnKeyspace", ACTION_NONE, ACTION_DROP_CREATE, ACTION_CREATE, ACTION_CREATE_IF_NOT_EXISTS, ACTION_DROP_IF_EXISTS_AND_CREATE);

    public static final String KS_REPLICA_NETWORK = "NetworkTopologyStrategy";
    public static final String KS_REPLICA_SIMPLE = "SimpleStrategy";

    public Property replicaStrategy = newEnum("replicaStrategy", KS_REPLICA_NETWORK, KS_REPLICA_SIMPLE);//SHOW_IF actionOnKeyspace != NONE

    public Property simpleReplicaNumber = newInteger("simpleReplicaNumber", 3); //SHOW_IF replica is simple

    public Property networkReplicaTable = new Property("networkReplicaTable");
    public Property datacenterName = newString("datacenterName");
    public Property replicaNumber = newInteger("replicaNumber", 3);

    public Property actionOnColumnFamily = newEnum("actionOnColumnFamily", ACTION_NONE, ACTION_DROP_CREATE, ACTION_CREATE, ACTION_CREATE_IF_NOT_EXISTS, ACTION_DROP_IF_EXISTS_AND_CREATE, ACTION_TRUNCATE);

    public static final String ACTION_INSERT = "INSERT";
    public static final String ACTION_UPDATE = "UPDATE";
    public static final String ACTION_DELETE = "DELETE";
    public Property dataAction = newEnum("dataAction", ACTION_INSERT, ACTION_UPDATE, ACTION_DELETE);

    public Property dieOnError = newBoolean("dieOnError", false);

    //Advanced setting
    public Property useUnloggedBatch = newBoolean("useUnloggedBatch", false);
    public Property batchSize = newInteger("batchSize", 10000);

    public Property insertIfNotExists = newBoolean("insertIfNotExists", false);

    public Property deleteIfExists = newBoolean("deleteIfExists", false);

    public Property usingTTL = newBoolean("usingTTL", false);
    public Property ttl = newEnum("ttl");

    public Property usingTimestamp = newBoolean("usingTimestamp", false);
    public Property timestamp = newEnum("timestamp");

    public Property ifCondition = new Property("ifCondition");
    public Property ifConditionColumnName = newEnum("ifConditionColumnName");

    public Property assignmentOperation = new Property("assignmentOperation");
    public Property assignmentOperationColumnName = newEnum("assignmentOperationColumnName");
    public static final String APPEND = "+v";
    public static final String PREPEND = "v+";
    public static final String MINUS = "-";
    public static final String POSITION_OR_KEY = "p/k";
    public Property operation = newEnum("operation", APPEND, PREPEND, MINUS, POSITION_OR_KEY);
    public Property keyColumn = newEnum("keyColumn");

    public Property deleteColumnByPositionKey = new Property("deleteColumnByPositionKey");
    public Property deleteColumnByPositionKeyColumnName = newEnum("deleteColumnByPositionKeyColumnName");

    public Property rowKeyInList = new Property("rowKeyInList");
    public Property rowKeyInListColumnName = newEnum("rowKeyInListColumnName");

    @Override
    public void setupProperties() {
        super.setupProperties();
        actionOnKeyspace.setValue(ACTION_NONE);

        networkReplicaTable.setOccurMaxTimes(Property.INFINITE);
        networkReplicaTable.addChild(datacenterName);
        networkReplicaTable.addChild(replicaNumber);

        actionOnColumnFamily.setValue(ACTION_NONE);
        dataAction.setValue(ACTION_INSERT);

        ifCondition.setOccurMaxTimes(Property.INFINITE);
        ifCondition.addChild(ifConditionColumnName);

        assignmentOperation.setOccurMaxTimes(Property.INFINITE);
        assignmentOperation.addChild(assignmentOperationColumnName);
        assignmentOperation.addChild(operation);
        assignmentOperation.addChild(keyColumn);

        deleteColumnByPositionKey.setOccurMaxTimes(Property.INFINITE);
        deleteColumnByPositionKey.addChild(deleteColumnByPositionKeyColumnName);

        rowKeyInList.setOccurMaxTimes(Property.INFINITE);
        rowKeyInList.addChild(rowKeyInListColumnName);
    }

    private List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<>();
        List<Schema> schemas = getSchemas();
        if (schemas.size() == 1) {
            Schema schema = schemas.get(0);
            List<Schema.Field> fields = schema.getFields();
            for (Schema.Field field : fields) {
                columnNames.add(field.name());
            }
        }
        return columnNames;
    }

    //TODO(bchen) can be common? how to avoid trigger for common usage
    public void beforeTtl() {
        ttl.setPossibleValues(getColumnNames());
    }

    public void beforeTimestamp() {
        timestamp.setPossibleValues(getColumnNames());
    }

    public void beforeIfConditionColumnName() {
        ifConditionColumnName.setPossibleValues(getColumnNames());
    }

    public void beforeAssignmentOperationColumnName() {
        assignmentOperationColumnName.setPossibleValues(getColumnNames());
    }

    public void beforeAssignmentOperationKeyColumn() {
        keyColumn.setPossibleValues(getColumnNames());
    }

    public void beforeDeleteColumnByPositionKeyColumnName() {
        deleteColumnByPositionKeyColumnName.setPossibleValues(getColumnNames());
    }

    public void beforeRowKeyInListColumnName() {
        rowKeyInListColumnName.setPossibleValues(getColumnNames());
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = getForm(Form.MAIN);
        mainForm.addRow(actionOnKeyspace).addColumn(replicaStrategy);
        mainForm.addRow(simpleReplicaNumber);
        mainForm.addRow(widget(networkReplicaTable).setWidgetType(Widget.WidgetType.TABLE));
        mainForm.addRow(actionOnColumnFamily);
        mainForm.addRow(dataAction);
        mainForm.addRow(dieOnError);

        Form advancedForm = new Form(this, Form.ADVANCED);
        advancedForm.addRow(useUnloggedBatch).addColumn(batchSize);
        advancedForm.addRow(insertIfNotExists);
        advancedForm.addRow(deleteIfExists);
        advancedForm.addRow(usingTTL).addColumn(ttl);
        advancedForm.addRow(usingTimestamp).addColumn(timestamp);
        advancedForm.addRow(widget(ifCondition).setWidgetType(Widget.WidgetType.TABLE));
        advancedForm.addRow(widget(assignmentOperation).setWidgetType(Widget.WidgetType.TABLE));
        advancedForm.addRow(widget(deleteColumnByPositionKey).setWidgetType(Widget.WidgetType.TABLE));
        advancedForm.addRow(widget(rowKeyInList).setWidgetType(Widget.WidgetType.TABLE));
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (form.getName().equals(Form.MAIN)) {
            form.getWidget(actionOnKeyspace.getName()).setVisible(!ACTION_DELETE.equals(dataAction.getValue()));
            form.getWidget(replicaStrategy.getName()).setVisible(form.getWidget(actionOnKeyspace.getName()).isVisible() && !ACTION_NONE.equals(actionOnKeyspace.getValue()));
            form.getWidget(simpleReplicaNumber.getName()).setVisible(form.getWidget(replicaStrategy.getName()).isVisible() && KS_REPLICA_SIMPLE.equals(replicaStrategy.getValue()));
            form.getWidget(networkReplicaTable.getName()).setVisible(form.getWidget(replicaStrategy.getName()).isVisible() && KS_REPLICA_NETWORK.equals(replicaStrategy.getValue()));
            form.getWidget(actionOnColumnFamily.getName()).setVisible(!ACTION_DELETE.equals(dataAction.getValue()));
        } else if (form.getName().equals(Form.ADVANCED)) {
            form.getWidget(batchSize.getName()).setVisible(useUnloggedBatch.getBooleanValue());
            form.getWidget(insertIfNotExists.getName()).setVisible(ACTION_INSERT.equals(dataAction.getValue()) && !usingTimestamp.getBooleanValue());
            form.getWidget(deleteIfExists.getName()).setVisible(ACTION_DELETE.equals(dataAction.getValue()));
            form.getWidget(usingTTL.getName()).setVisible(ACTION_INSERT.equals(dataAction.getValue()) || ACTION_UPDATE.equals(dataAction.getValue()));
            form.getWidget(ttl.getName()).setVisible(form.getWidget(usingTTL.getName()).isVisible() && usingTTL.getBooleanValue());
            form.getWidget(usingTimestamp.getName()).setVisible((ACTION_INSERT.equals(dataAction.getValue()) && !insertIfNotExists.getBooleanValue()) || ACTION_UPDATE.equals(dataAction.getValue()) || ACTION_DELETE.equals(dataAction.getValue()));
            form.getWidget(timestamp.getName()).setVisible(form.getWidget(usingTimestamp.getName()).isVisible() && usingTimestamp.getBooleanValue());
            form.getWidget(ifCondition.getName()).setVisible(ACTION_UPDATE.equals(dataAction.getValue()) || (ACTION_DELETE.equals(dataAction.getValue()) && !deleteIfExists.getBooleanValue()));
            form.getWidget(assignmentOperation.getName()).setVisible(ACTION_UPDATE.equals(dataAction.getValue()));
            //FIXME how to hidden keyColumn in assignmentOperation when operation is p/k,waiting for TableComponentProperties
            form.getWidget(deleteColumnByPositionKey.getName()).setVisible(ACTION_DELETE.equals(dataAction.getValue()));
            form.getWidget(rowKeyInList.getName()).setVisible(ACTION_UPDATE.equals(dataAction.getValue()) || ACTION_DELETE.equals(dataAction.getValue()));
        }
    }

    //FIXME(bchen) how to avoid trigger for each property in condition
    public void afterDataAction() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.ADVANCED));
    }

    public void afterActionOnKeyspace() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.ADVANCED));
    }

    public void afterReplicaStrategy() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.ADVANCED));
    }

    public void afterUseUnloggedBatch() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.ADVANCED));
    }

    public void afterUsingTimestamp() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.ADVANCED));
    }

    public void afterUsingTTL() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.ADVANCED));
    }

    public void afterInsertIfNotExists() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.ADVANCED));
    }

    public void afterDeleteIfExists() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.ADVANCED));
    }

}
