// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.components.pubsub;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeLiteral;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class PubSubAttributesTableProperties extends PropertiesImpl {

    private static final TypeLiteral<List<String>> LIST_STRING_TYPE = new TypeLiteral<List<String>>() {// empty
    };

    public Property<List<String>> attributeName = PropertyFactory.newProperty(LIST_STRING_TYPE, "attributeName");

    public Property<List<String>> columnName = PropertyFactory.newProperty(LIST_STRING_TYPE, "columnName");

    public PubSubAttributesTableProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(attributeName);
        mainForm.addRow(columnName);
    }

    public boolean isEmpty() {
        return attributeName.getValue() == null || attributeName.getValue().isEmpty();
    }

    /**
     * Generate a mapping between attribute name and avro column name
     * @return
     */
    public Map<String, String> genAttributesMap() {
        //TODO how about one attr name mapping to two different column name?
        //of course forbidden to support two different column name mapping to one attr name.
        if (isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> attrsMap = new HashMap<>();
        // Attributes table is the mapping between attribute of Pub/Sub message and
        // IndexedRecord, if attribute set but columnName not, use the value of
        // attribute as columnName.
        List<String> attrsName = attributeName.getValue();
        List<String> columnsName = columnName.getValue();
        int i = 0;
        for (String attrName : attrsName) {
            String columnName = columnsName.get(i);
            if (StringUtils.isEmpty(columnName)) {
                columnName = attrName;
            }
            attrsMap.put(attrName, columnName);
            i++;
        }
        return attrsMap;
    }
}
