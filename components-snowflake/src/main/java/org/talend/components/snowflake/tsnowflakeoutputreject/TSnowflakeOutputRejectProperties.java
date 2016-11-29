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
package org.talend.components.snowflake.tsnowflakeoutputreject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.avro.Schema;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.SchemaProperties;
import org.talend.components.snowflake.SnowflakeConnectionTableProperties;
import org.talend.daikon.avro.SchemaConstants;

public class TSnowflakeOutputRejectProperties extends SnowflakeConnectionTableProperties {

    public static final String FIELD_COLUMN_NAME = "columnName";

    public static final String FIELD_ROW_NUMBER = "rowNumber";

    public static final String FIELD_CATEGORY = "category";

    public static final String FIELD_CHARACTER = "character";

    public static final String FIELD_ERROR_MESSAGE = "errorMessage";

    public static final String FIELD_BYTE_OFFSET = "byteOffset";

    public static final String FIELD_LINE = "line";

    public static final String FIELD_SQL_STATE = "sqlState";

    public static final String FIELD_CODE = "code";

    public transient PropertyPathConnector REJECT_CONNECTOR = new PropertyPathConnector(Connector.REJECT_NAME, "schemaReject");

    public SchemaProperties schemaReject = new SchemaProperties("schemaReject"); //$NON-NLS-1$

    public TSnowflakeOutputRejectProperties(String name) {
        super(name);
    }

    @Override public void setupProperties() {
        super.setupProperties();
        updateOutputSchemas();
    }

    private void addSchemaField(String name, List<Schema.Field> fields) {
        Schema.Field field = new Schema.Field(name, Schema.create(Schema.Type.STRING), null, (Object) null);
        field.addProp(SchemaConstants.TALEND_IS_LOCKED, "false");
        field.addProp(SchemaConstants.TALEND_FIELD_GENERATED, "true");
        field.addProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH, "255");
        fields.add(field);
    }

    private void updateOutputSchemas() {
        //Schema inputSchema = table.main.schema.getValue();

        final List<Schema.Field> additionalRejectFields = new ArrayList<Schema.Field>();
        addSchemaField(FIELD_COLUMN_NAME, additionalRejectFields);
        addSchemaField(FIELD_ROW_NUMBER, additionalRejectFields);
        addSchemaField(FIELD_CATEGORY, additionalRejectFields);
        addSchemaField(FIELD_CHARACTER, additionalRejectFields);
        addSchemaField(FIELD_ERROR_MESSAGE, additionalRejectFields);
        addSchemaField(FIELD_BYTE_OFFSET, additionalRejectFields);
        addSchemaField(FIELD_LINE, additionalRejectFields);
        addSchemaField(FIELD_SQL_STATE, additionalRejectFields);
        addSchemaField(FIELD_CODE, additionalRejectFields);

        Schema rejectSchema = Schema.createRecord("rejectOutput", "reject", "rejectNs", false);

        List<Schema.Field> copyFieldList = new ArrayList<>();
        copyFieldList.addAll(additionalRejectFields);

        rejectSchema.setFields(copyFieldList);

        schemaReject.schema.setValue(rejectSchema);
    }

    @Override protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        HashSet<PropertyPathConnector> connectors = new HashSet<>();
        if (isOutputConnection) {
            connectors.add(REJECT_CONNECTOR);
        }
        return connectors;
    }
}
