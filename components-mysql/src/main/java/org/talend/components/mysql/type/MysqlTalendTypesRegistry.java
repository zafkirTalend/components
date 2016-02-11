package org.talend.components.mysql.type;

import org.talend.daikon.schema.MakoElement;
import org.talend.daikon.schema.type.ExternalBaseType;
import org.talend.daikon.schema.type.TypesRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bchen on 16-1-18.
 */
public class MysqlTalendTypesRegistry implements TypesRegistry {

    @Override
    public String getFamilyName() {
        return MysqlBaseType.FAMILY_NAME;
    }

    @Override
    public Map<Class<? extends ExternalBaseType>, MakoElement.Type> getMapping() {
        Map<Class<? extends ExternalBaseType>, MakoElement.Type> map = new HashMap<>();
        map.put(Mysql_VARCHAR.class, MakoElement.Type.STRING);
        map.put(Mysql_INT.class, MakoElement.Type.INT);
        return map;
    }
}
