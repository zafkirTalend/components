package org.talend.components.salesforce.type;

import org.talend.daikon.schema.SchemaElement;
import org.talend.daikon.schema.type.ExternalBaseType;
import org.talend.daikon.schema.type.TypesRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bchen on 16-1-28.
 */
public class SalesforceTalendTypesRegistry implements TypesRegistry {
    @Override
    public String getFamilyName() {
        return SalesforceBaseType.FAMILY_NAME;
    }

    @Override
    public Map<Class<? extends ExternalBaseType>, SchemaElement.Type> getMapping() {
        Map<Class<? extends ExternalBaseType>, SchemaElement.Type> map = new HashMap<>();
        map.put(SF_Address.class, SchemaElement.Type.STRING);
        map.put(SF_AnyType.class, SchemaElement.Type.OBJECT);
        map.put(SF_Base64.class, SchemaElement.Type.BYTE_ARRAY);
        map.put(SF_Boolean.class, SchemaElement.Type.BOOLEAN);
        map.put(SF_Byte.class, SchemaElement.Type.BYTE);
//        map.put(SF_Calculated.class, SchemaElement.Type.STRING);
        map.put(SF_ComboBox.class, SchemaElement.Type.STRING);
        map.put(SF_Currency.class, SchemaElement.Type.DOUBLE);
//        map.put(SF_DataCategoryGroupReference.class, SchemaElement.Type.OBJECT);
        map.put(SF_Date.class, SchemaElement.Type.DATE);
        map.put(SF_Datetime.class, SchemaElement.Type.DATE);
        map.put(SF_Double.class, SchemaElement.Type.DOUBLE);
        map.put(SF_Email.class, SchemaElement.Type.STRING);
        map.put(SF_Encryptedstring.class, SchemaElement.Type.STRING);
        map.put(SF_ID.class, SchemaElement.Type.STRING);
        map.put(SF_Int.class, SchemaElement.Type.INT);
        map.put(SF_JunctionIdList.class, SchemaElement.Type.LIST);
//        map.put(SF_Location.class, SchemaElement.Type.STRING);
        map.put(SF_Masterrecord.class, SchemaElement.Type.STRING);
        map.put(SF_Multipicklist.class, SchemaElement.Type.LIST);
        map.put(SF_Percent.class, SchemaElement.Type.DOUBLE);
        map.put(SF_Phone.class, SchemaElement.Type.STRING);
        map.put(SF_Picklist.class, SchemaElement.Type.STRING);
        map.put(SF_Reference.class, SchemaElement.Type.STRING);
        map.put(SF_String.class, SchemaElement.Type.STRING);
        map.put(SF_Textarea.class, SchemaElement.Type.STRING);
        map.put(SF_Time.class, SchemaElement.Type.DATE);
        map.put(SF_Url.class, SchemaElement.Type.STRING);
        return map;
    }
}
