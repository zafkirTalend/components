package org.talend.components.jdbc.dataprep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;

public class TDataPrepDBInputProperties extends FixedConnectorsComponentProperties implements RuntimeSettingProvider {

    @SuppressWarnings("rawtypes")
    private transient Map dyTypesInfo;

    public TDataPrepDBInputProperties(String name) {
        super(name);

        StringBuilder json = new StringBuilder();
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("db_type_config.json");
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String row = null;
            while ((row = br.readLine()) != null) {
                json.append(row);
            }
        } catch (IOException e) {
            throw new ComponentException(e);
        }

        dyTypesInfo = JsonReader.jsonToMaps(json.toString());
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

    public UserPasswordProperties userPassword = new UserPasswordProperties("userPassword");

    public Property<String> sql = PropertyFactory.newString("sql").setRequired(true);

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form mainForm = CommonUtils.addForm(this, Form.MAIN);

        mainForm.addRow(Widget.widget(dbTypes).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addRow(jdbcUrl);
        mainForm.addRow(userPassword.getForm(Form.MAIN));

        mainForm.addRow(main.getForm(Form.REFERENCE));

        mainForm.addRow(sql);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();

        List<String> dbTypesId = new ArrayList<String>();

        for (Object o : (Object[]) dyTypesInfo.get("@items")) {
            @SuppressWarnings("rawtypes")
            JsonObject eachDBInfo = (JsonObject) o;
            String id = (String) eachDBInfo.get("id");
            dbTypesId.add(id);
        }

        dbTypes.setPossibleValues(dbTypesId);
        dbTypes.setValue(dbTypesId.get(0));
    }

    @Override
    public AllSetting getRuntimeSetting() {
        AllSetting setting = new AllSetting();

        setDriverAndClass(setting);

        setting.setJdbcUrl(this.jdbcUrl.getValue());
        setting.setUsername(this.userPassword.userId.getValue());
        setting.setPassword(this.userPassword.password.getValue());

        setting.setSql(this.sql.getValue());

        return setting;
    }

    @SuppressWarnings("rawtypes")
    private void setDriverAndClass(AllSetting setting) {
        List<String> mavenPaths = new ArrayList<String>();
        String driverClass = null;

        for (Object o : (Object[]) dyTypesInfo.get("@items")) {
            JsonObject eachDBInfo = (JsonObject) o;
            String id = (String) eachDBInfo.get("id");
            if (dbTypes.getValue().equals(id)) {
                driverClass = (String) eachDBInfo.get("class");
                Object[] paths = (Object[]) eachDBInfo.get("paths");
                for (Object path : paths) {
                    JsonObject jo_path = (JsonObject) path;
                    mavenPaths.add((String) jo_path.get("path"));
                }
                break;
            }
        }

        setting.setDriverPaths(mavenPaths);
        setting.setDriverClass(driverClass);
    }

}
