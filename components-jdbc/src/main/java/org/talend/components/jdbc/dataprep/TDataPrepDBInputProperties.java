package org.talend.components.jdbc.dataprep;

import java.util.Collections;
import java.util.Set;

import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.components.common.UserPasswordProperties;
import org.talend.components.jdbc.CommonUtils;
import org.talend.components.jdbc.RuntimeSettingProvider;
import org.talend.components.jdbc.runtime.setting.AllSetting;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class TDataPrepDBInputProperties extends FixedConnectorsComponentProperties implements RuntimeSettingProvider {

    public TDataPrepDBInputProperties(String name) {
        super(name);
    }

    // declare the connector and schema
    public final PropertyPathConnector mainConnector = new PropertyPathConnector(Connector.MAIN_NAME, "main");

    public SchemaProperties main = new SchemaProperties("main");

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        if (isOutputConnection) {
            return Collections.singleton(mainConnector);
        }
        return Collections.emptySet();
    }

    public DBTypeProperties dbTypes = new DBTypeProperties("dbTypes");

    public UserPasswordProperties userPassword = new UserPasswordProperties("userPassword");

    public Property<String> sql = PropertyFactory.newString("sql").setRequired(true);

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form mainForm = CommonUtils.addForm(this, Form.MAIN);

        mainForm.addRow(dbTypes.getForm(Form.MAIN));

        mainForm.addRow(userPassword.getForm(Form.MAIN));

        mainForm.addRow(main.getForm(Form.REFERENCE));

        mainForm.addRow(sql);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();

        dbTypes.initProperties();
    }

    @Override
    public AllSetting getRuntimeSetting() {
        AllSetting setting = new AllSetting();

        setting.setDriverPaths(dbTypes.getCurrentDriverPaths());
        setting.setDriverClass(dbTypes.driverClass.getValue());
        setting.setJdbcUrl(dbTypes.jdbcUrl.getValue());

        setting.setUsername(userPassword.userId.getValue());
        setting.setPassword(userPassword.password.getValue());

        setting.setSql(this.sql.getValue());

        return setting;
    }

}
