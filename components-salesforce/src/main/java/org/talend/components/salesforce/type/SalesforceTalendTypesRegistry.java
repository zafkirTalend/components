package org.talend.components.salesforce.type;

import org.talend.daikon.schema.MakoElement;
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
    public Map<Class<? extends ExternalBaseType>, MakoElement.Type> getMapping() {
        Map<Class<? extends ExternalBaseType>, MakoElement.Type> map = new HashMap<>();
        map.put(SF_Address.class, MakoElement.Type.STRING);
        map.put(SF_AnyType.class, MakoElement.Type.OBJECT);
        map.put(SF_Base64.class, MakoElement.Type.BYTE_ARRAY);
        map.put(SF_Boolean.class, MakoElement.Type.BOOLEAN);
        map.put(SF_Byte.class, MakoElement.Type.BYTE);
        // map.put(SF_Calculated.class, SchemaElement.Type.STRING);
        map.put(SF_ComboBox.class, MakoElement.Type.STRING);
        map.put(SF_Currency.class, MakoElement.Type.DOUBLE);
        // map.put(SF_DataCategoryGroupReference.class, SchemaElement.Type.OBJECT);
        map.put(SF_Date.class, MakoElement.Type.DATE);
        map.put(SF_Datetime.class, MakoElement.Type.DATE);
        map.put(SF_Double.class, MakoElement.Type.DOUBLE);
        map.put(SF_Email.class, MakoElement.Type.STRING);
        map.put(SF_Encryptedstring.class, MakoElement.Type.STRING);
        map.put(SF_ID.class, MakoElement.Type.STRING);
        map.put(SF_Int.class, MakoElement.Type.INT);
        map.put(SF_JunctionIdList.class, MakoElement.Type.LIST);
        // map.put(SF_Location.class, SchemaElement.Type.STRING);
        map.put(SF_Masterrecord.class, MakoElement.Type.STRING);
        map.put(SF_Multipicklist.class, MakoElement.Type.LIST);
        map.put(SF_Percent.class, MakoElement.Type.DOUBLE);
        map.put(SF_Phone.class, MakoElement.Type.STRING);
        map.put(SF_Picklist.class, MakoElement.Type.STRING);
        map.put(SF_Reference.class, MakoElement.Type.STRING);
        map.put(SF_String.class, MakoElement.Type.STRING);
        map.put(SF_Textarea.class, MakoElement.Type.STRING);
        map.put(SF_Time.class, MakoElement.Type.DATE);
        map.put(SF_Url.class, MakoElement.Type.STRING);
        return map;
    }
}
