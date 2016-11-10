package org.talend.components.jdbc.dataset;

import org.talend.components.common.dataset.DatasetDefinition;
import org.talend.components.jdbc.runtime.JDBCTemplate;
import org.talend.components.jdbc.runtime.dataprep.JDBCDatasetRuntime;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.runtime.RuntimeInfo;

public class JDBCDatasetDefinition extends SimpleNamedThing implements DatasetDefinition<JDBCDatasetProperties> {

    public static final String NAME = "JDBCDataset";

    public JDBCDatasetDefinition() {
        super(NAME);
    }

    @Override
    public JDBCDatasetProperties createProperties() {
        JDBCDatasetProperties properties = new JDBCDatasetProperties(NAME);
        properties.init();
        return properties;
    }

    @Override
    public RuntimeInfo getRuntimeInfo(JDBCDatasetProperties properties, Object ctx) {
        return JDBCTemplate.createCommonRuntime(this.getClass().getClassLoader(), properties,
                JDBCDatasetRuntime.class.getCanonicalName());
    }

    @Override
    public String getImagePath() {
        // TODO Auto-generated method stub
        return null;
    }
}
