package org.talend.components.jdbc.datastore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.talend.components.api.exception.ComponentException;
import org.talend.components.common.UserPasswordProperties;
import org.talend.components.common.datastore.DatastoreProperties;
import org.talend.components.common.datastore.runtime.DatastoreRuntime;
import org.talend.components.jdbc.CommonUtils;
import org.talend.components.jdbc.RuntimeSettingProvider;
import org.talend.components.jdbc.dataprep.DBType;
import org.talend.components.jdbc.runtime.setting.AllSetting;
import org.talend.daikon.properties.PresentationItem;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.runtime.RuntimeInfo;
import org.talend.daikon.runtime.RuntimeUtil;
import org.talend.daikon.sandbox.SandboxedInstance;

public class JDBCDatastoreProperties extends PropertiesImpl implements DatastoreProperties, RuntimeSettingProvider {

    private transient Map<String, DBType> dyTypesInfo = new HashMap<String, DBType>();

    private final static String CONFIG_FILE_lOCATION_KEY = "org.talend.component.jdbc.config.file";

    private static Pattern dbInfoPattern = Pattern.compile("(.+?);(.+?);(.+?);(.+)");

    public JDBCDatastoreProperties(String name) {
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

    public Property<String> dbTypes = PropertyFactory.newString("dbTypes").setRequired();

    public Property<String> jdbcUrl = PropertyFactory.newProperty("jdbcUrl").setRequired();

    public Property<String> driverClass = PropertyFactory.newProperty("driverClass").setRequired();

    public UserPasswordProperties userPassword = new UserPasswordProperties("userPassword");

    public PresentationItem testConnection = new PresentationItem("testConnection", "Test connection");

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

        jdbcUrl.setValue("\"" + defaultDBType.jdbcUrlTemplate + "\"");
        driverClass.setValue("\"" + defaultDBType.driver + "\"");
    }

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form mainForm = CommonUtils.addForm(this, Form.MAIN);

        mainForm.addRow(Widget.widget(dbTypes).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addRow(jdbcUrl);
        mainForm.addRow(driverClass);

        mainForm.addRow(userPassword.getForm(Form.MAIN));
    }

    public void afterDbTypes() {
        DBType currentDBType = this.getCurrentDBType();
        jdbcUrl.setValue("\"" + currentDBType.jdbcUrlTemplate + "\"");
        driverClass.setValue("\"" + currentDBType.driver + "\"");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ValidationResult validateTestConnection() {
        JDBCDatastoreDefinition definition = new JDBCDatastoreDefinition();
        RuntimeInfo runtimeInfo = definition.getRuntimeInfo(this, null);
        try (SandboxedInstance sandboxedInstance = RuntimeUtil.createRuntimeClass(runtimeInfo, getClass().getClassLoader())) {
            DatastoreRuntime runtime = (DatastoreRuntime) sandboxedInstance.getInstance();
            runtime.initialize(null, this);
            Iterable<ValidationResult> iterables = runtime.doHealthChecks(null);
            for (ValidationResult validationResult : iterables) {
                if (validationResult.getStatus() == ValidationResult.Result.ERROR) {
                    return validationResult;
                }
            }
        }

        return ValidationResult.OK;
    }

    @Override
    public AllSetting getRuntimeSetting() {
        AllSetting setting = new AllSetting();

        setting.setDriverPaths(getCurrentDriverPaths());
        setting.setDriverClass(driverClass.getValue());
        setting.setJdbcUrl(jdbcUrl.getValue());

        setting.setUsername(userPassword.userId.getValue());
        setting.setPassword(userPassword.password.getValue());

        return setting;
    }

    private DBType getCurrentDBType() {
        return dyTypesInfo.get(dbTypes.getValue());
    }

    public List<String> getCurrentDriverPaths() {
        List<String> mavenPaths = new ArrayList<String>();

        DBType currentDBType = dyTypesInfo.get(dbTypes.getValue());
        mavenPaths.add(currentDBType.jar);

        return mavenPaths;
    }

}
