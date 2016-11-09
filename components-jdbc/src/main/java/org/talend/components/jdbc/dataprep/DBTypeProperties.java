package org.talend.components.jdbc.dataprep;

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
import org.talend.components.jdbc.CommonUtils;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class DBTypeProperties extends PropertiesImpl {

    private transient Map<String, DBType> dyTypesInfo = new HashMap<String, DBType>();

    private final static String CONFIG_FILE_lOCATION_KEY = "org.talend.component.jdbc.config.file";

    public Property<String> dbTypes = PropertyFactory.newString("dbTypes").setRequired();

    public Property<String> jdbcUrl = PropertyFactory.newProperty("jdbcUrl").setRequired();

    public Property<String> driverClass = PropertyFactory.newProperty("driverClass").setRequired();

    private static Pattern dbInfoPattern = Pattern.compile("(.+?);(.+?);(.+?);(.+)");

    public DBTypeProperties(String name) {
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

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form mainForm = CommonUtils.addForm(this, Form.MAIN);
        mainForm.addRow(Widget.widget(dbTypes).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addRow(jdbcUrl);
        mainForm.addRow(driverClass);
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

    public void initProperties() {
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

    public List<String> getCurrentDriverPaths() {
        List<String> mavenPaths = new ArrayList<String>();

        DBType currentDBType = dyTypesInfo.get(dbTypes.getValue());
        mavenPaths.add(currentDBType.jar);

        return mavenPaths;
    }

    private DBType getCurrentDBType() {
        return dyTypesInfo.get(dbTypes.getValue());
    }

}
