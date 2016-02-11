package org.talend.components.salesforce.metadata;

import com.sforce.soap.partner.*;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.bind.XmlObject;
import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.runtime.metadata.Metadata;
import org.talend.components.salesforce.SalesforceConnectionProperties;
import org.talend.components.salesforce.SalesforceModuleProperties;
import org.talend.components.salesforce.tsalesforceconnection.SalesforceConnectionObject;
import org.talend.components.salesforce.tsalesforceconnection.SalesforceConnectionManager;
import org.talend.components.salesforce.type.*;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.schema.DataSchema;
import org.talend.daikon.schema.MakoElement;
import org.talend.daikon.schema.DataSchemaFactory;
import org.talend.daikon.schema.internal.DataSchemaElement;
import org.talend.daikon.schema.type.TypeMapping;

import java.util.*;

/**
 * Created by bchen on 16-1-28.
 */
public class SalesforceMetadata implements Metadata {

    public SalesforceMetadata() {
        TypeMapping.registryTypes(new SalesforceTalendTypesRegistry());
    }

    private static Map<FieldType, Class<? extends SalesforceBaseType>> mapping = new HashMap<>();

    static {
        // TODO no field type for SF_Byte/SF_Calculated/SF_JunctionIdList/SF_Masterrecord
        mapping.put(FieldType.address, SF_Address.class);
        mapping.put(FieldType.anyType, SF_AnyType.class);
        mapping.put(FieldType.base64, SF_Base64.class);
        mapping.put(FieldType._boolean, SF_Boolean.class);
        mapping.put(FieldType.combobox, SF_ComboBox.class);
        mapping.put(FieldType.currency, SF_Currency.class);
        // mapping.put(FieldType.datacategorygroupreference, SF_DataCategoryGroupReference.class);
        mapping.put(FieldType.date, SF_Date.class);
        mapping.put(FieldType.datetime, SF_Datetime.class);
        mapping.put(FieldType._double, SF_Double.class);
        mapping.put(FieldType.email, SF_Email.class);
        mapping.put(FieldType.encryptedstring, SF_Encryptedstring.class);
        mapping.put(FieldType.id, SF_ID.class);
        mapping.put(FieldType._int, SF_Int.class);
        // mapping.put(FieldType.location, SF_Location.class);
        mapping.put(FieldType.multipicklist, SF_Multipicklist.class);
        mapping.put(FieldType.percent, SF_Percent.class);
        mapping.put(FieldType.phone, SF_Phone.class);
        mapping.put(FieldType.picklist, SF_Picklist.class);
        mapping.put(FieldType.reference, SF_Reference.class);
        mapping.put(FieldType.string, SF_String.class);
        mapping.put(FieldType.textarea, SF_Textarea.class);
        mapping.put(FieldType.time, SF_Time.class);
        mapping.put(FieldType.url, SF_Url.class);
    }

    @Override
    public void initSchema(ComponentProperties properties) throws TalendConnectionException {
        SalesforceModuleProperties props = (SalesforceModuleProperties) properties;
        props.schema.schema.setValue(getSchema(props));
    }

    private DataSchema getSchema(SalesforceModuleProperties props) throws TalendConnectionException {
        DataSchema schema = DataSchemaFactory.newSchema();
        MakoElement root = DataSchemaFactory.newSchemaElement("Root");
        schema.setRoot(root);

        SalesforceConnectionManager connManager = new SalesforceConnectionManager();
        SalesforceConnectionObject connection = null;
        try {
            connection = connManager.newConnection(props.getConnection());// TODO: replace to connection pool after
                                                                          // ConnectionManager implement
                                                                          // getConnectionPoolByKey
            DescribeSObjectResult describeSObjectResult = connection.getPartnerConnection().describeSObject(
                    props.moduleName.getStringValue());
            Field[] fields = describeSObjectResult.getFields();
            for (Field field : fields) {
                root.addChild(DataSchemaFactory.newDataSchemaElement(SalesforceBaseType.FAMILY_NAME, field.getName(),
                        mapping.get(field.getType())));
            }
        } catch (ConnectionException e) {
            throw new TalendConnectionException(e.getMessage());
        } finally {
            connManager.destoryConnection(connection);
        }

        return schema;
    }

    @Override
    public void initSchemaForDynamic(ComponentProperties properties) throws TalendConnectionException {
        SalesforceModuleProperties props = (SalesforceModuleProperties) properties;
        DataSchema schema = (DataSchema) (props.schema.schema.getValue());
        List<MakoElement> children = schema.getRoot().getChildren();
        MakoElement dynamicElement = null;
        List<String> unDynamicElementsName = new ArrayList<>();
        for (MakoElement child : children) {
            if (child.getType() == MakoElement.Type.DYNAMIC) {
                dynamicElement = child;
                continue;
            }
            unDynamicElementsName.add(((DataSchemaElement) child).getAppColName());
        }
        if (dynamicElement != null) {
            DataSchema realSchema = getSchema(props);
            List<MakoElement> realElements = realSchema.getRoot().getChildren();
            for (MakoElement realElement : realElements) {
                if (!unDynamicElementsName.contains(((DataSchemaElement) realElement).getAppColName())) {
                    dynamicElement.addChild(realElement);
                }
            }
        }
    }

    @Override
    public void initSchemaForDynamicWithFirstRow(ComponentProperties properties, Object firstRow)
            throws TalendConnectionException {
        SObject row = (SObject) firstRow;
        SalesforceModuleProperties props = (SalesforceModuleProperties) properties;
        DataSchema schema = (DataSchema) (props.schema.schema.getValue());
        List<MakoElement> children = schema.getRoot().getChildren();
        MakoElement dynamicElement = null;
        List<String> unDynamicElementsName = new ArrayList<>();
        for (MakoElement child : children) {
            if (child.getType() == MakoElement.Type.DYNAMIC) {
                dynamicElement = child;
                continue;
            }
            unDynamicElementsName.add(((DataSchemaElement) child).getAppColName());
        }
        if (dynamicElement != null) {
            List<String> colsName = new ArrayList<>();
            Iterator<XmlObject> cols = row.getChildren();
            while (cols.hasNext()) {
                XmlObject col = cols.next();
                colsName.add(col.getName().getLocalPart());
            }
            colsName.removeAll(unDynamicElementsName);
            DataSchema realSchema = getSchema(props);
            List<MakoElement> realElements = realSchema.getRoot().getChildren();
            for (MakoElement realElement : realElements) {
                if (colsName.contains(((DataSchemaElement) realElement).getAppColName())) {
                    dynamicElement.addChild(realElement);
                }
            }
        }
    }

    @Override
    public List<NamedThing> getSchemasName(ComponentProperties properties) throws TalendConnectionException {
        List<NamedThing> returnList = new ArrayList<>();
        SalesforceConnectionProperties props = (SalesforceConnectionProperties) properties;
        SalesforceConnectionManager connManager = new SalesforceConnectionManager();
        SalesforceConnectionObject connection = null;
        try {
            connection = connManager.newConnection(props);
            DescribeGlobalResult describeGlobalResult = null;
            try {
                describeGlobalResult = connection.getPartnerConnection().describeGlobal();
            } catch (ConnectionException e) {
                // TODO Do better job here
                throw new TalendConnectionException(e.getMessage());
            }
            DescribeGlobalSObjectResult[] sobjects = describeGlobalResult.getSobjects();
            for (DescribeGlobalSObjectResult sobject : sobjects) {
                returnList.add(new SimpleNamedThing(sobject.getName(), sobject.getLabel()));
            }
        } finally {
            connManager.destoryConnection(connection);
        }
        return returnList;
    }
}
