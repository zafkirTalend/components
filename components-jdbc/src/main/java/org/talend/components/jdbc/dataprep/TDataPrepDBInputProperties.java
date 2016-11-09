package org.talend.components.jdbc.dataprep;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.components.common.UserPasswordProperties;
import org.talend.components.jdbc.CommonUtils;
import org.talend.components.jdbc.RuntimeSettingProvider;
import org.talend.components.jdbc.runtime.setting.AllSetting;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class TDataPrepDBInputProperties extends FixedConnectorsComponentProperties implements RuntimeSettingProvider {

    private transient Map<String, DBType> dyTypesInfo = new HashMap<String, DBType>();

    private final static String CONFIG_FILE_lOCATION_KEY = "org.talend.component.jdbc.config.file";

    private static Pattern dbInfoPattern = Pattern.compile("(.+?);(.+?);(.+?);(.+)");

    public TDataPrepDBInputProperties(String name) {
        super(name);

        String config_file = System.getProperty(CONFIG_FILE_lOCATION_KEY);
        try (InputStream is = config_file != null ? (new FileInputStream(config_file))
                : this.getClass().getClassLoader().getResourceAsStream("db_type_config");
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String row = null;
            boolean first = true;
            while ((row = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }

                Matcher matcher = dbInfoPattern.matcher(row);
                if (matcher.find()) {
                    DBType type = new DBType();

                    type.id = matcher.group(1);
                    type.jar = matcher.group(2);
                    type.driver = matcher.group(3);
                    type.jdbcUrlTemplate = matcher.group(4);

                    dyTypesInfo.put(type.id, type);
                }
            }
        } catch (IOException e) {
            throw new ComponentException(e);
        }
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

    public Property<String> dbTypes = PropertyFactory.newString("dbTypes").setRequired();

    public Property<String> jdbcUrl = PropertyFactory.newProperty("jdbcUrl").setRequired();

    public Property<String> driverClass = PropertyFactory.newProperty("driverClass").setRequired();

    public UserPasswordProperties userPassword = new UserPasswordProperties("userPassword");

    public Property<String> sql = PropertyFactory.newString("sql").setRequired(true);

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form mainForm = CommonUtils.addForm(this, Form.MAIN);

        mainForm.addRow(Widget.widget(dbTypes).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addRow(jdbcUrl);
        mainForm.addRow(driverClass);

        mainForm.addRow(userPassword.getForm(Form.MAIN));

        mainForm.addRow(main.getForm(Form.REFERENCE));

        mainForm.addRow(sql);
    }

    @Override
    public void refreshLayout(Form form) {
        if (Form.MAIN.equals(form.getName())) {
            DBType currentDBType = this.getCurrentDBType();
            jdbcUrl.setValue(currentDBType.jdbcUrlTemplate);
            driverClass.setValue(currentDBType.driver);
        }
    }

    public void afterDbTypes() {
        refreshLayout(getForm(Form.MAIN));
    }

    private DBType getCurrentDBType() {
        return dyTypesInfo.get(dbTypes.getValue());
    }

    @Override
    public void setupProperties() {
        super.setupProperties();

        List<String> dbTypesId = new ArrayList<String>();
        for (String id : dyTypesInfo.keySet()) {
            dbTypesId.add(id);
        }

        DBType defaultDBType = dyTypesInfo.get(dbTypesId.get(0));

        dbTypes.setPossibleValues(dbTypesId);
        dbTypes.setValue(defaultDBType.id);

        jdbcUrl.setValue(defaultDBType.jdbcUrlTemplate);
        driverClass.setValue(defaultDBType.driver);
    }

    @Override
    public AllSetting getRuntimeSetting() {
        AllSetting setting = new AllSetting();

        setting.setDriverPaths(getCurrentDriverPaths());
        setting.setDriverClass(driverClass.getValue());
        setting.setJdbcUrl(jdbcUrl.getValue());

        setting.setUsername(userPassword.userId.getValue());
        setting.setPassword(userPassword.password.getValue());

        setting.setSql(this.sql.getValue());

        return setting;
    }

    public List<String> getCurrentDriverPaths() {
        List<String> mavenPaths = new ArrayList<String>();

        DBType currentDBType = dyTypesInfo.get(dbTypes.getValue());
        mavenPaths.add(currentDBType.jar);

        return mavenPaths;
    }

}
