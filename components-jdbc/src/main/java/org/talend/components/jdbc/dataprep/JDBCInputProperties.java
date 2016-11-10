package org.talend.components.jdbc.dataprep;

import java.util.Collections;
import java.util.Set;

import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.io.IOProperties;
import org.talend.components.jdbc.RuntimeSettingProvider;
import org.talend.components.jdbc.dataset.JDBCDatasetProperties;
import org.talend.components.jdbc.datastore.JDBCDatastoreProperties;
import org.talend.components.jdbc.runtime.setting.AllSetting;

public class JDBCInputProperties extends FixedConnectorsComponentProperties
        implements IOProperties<JDBCDatasetProperties>, RuntimeSettingProvider {

    public JDBCDatasetProperties dataset = new JDBCDatasetProperties("dataset");

    protected transient PropertyPathConnector MAIN_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "dataset.main");

    public JDBCInputProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        if (isOutputConnection) {
            return Collections.singleton(MAIN_CONNECTOR);
        } else {
            return Collections.EMPTY_SET;
        }
    }

    @Override
    public JDBCDatasetProperties getDatasetProperties() {
        return dataset;
    }

    @Override
    public void setDatasetProperties(JDBCDatasetProperties datasetProperties) {
        this.dataset = datasetProperties;
    }

    @Override
    public AllSetting getRuntimeSetting() {
        AllSetting setting = new AllSetting();

        JDBCDatastoreProperties datastore = dataset.getDatastoreProperties();
        setting.setDriverPaths(datastore.getCurrentDriverPaths());
        setting.setDriverClass(datastore.driverClass.getValue());
        setting.setJdbcUrl(datastore.jdbcUrl.getValue());

        setting.setUsername(datastore.userPassword.userId.getValue());
        setting.setPassword(datastore.userPassword.password.getValue());

        setting.setSql(dataset.sql.getValue());

        return setting;
    }
}
