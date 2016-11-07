package org.talend.components.jdbc.dataprep;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.talend.components.api.exception.ComponentException;
import org.talend.components.jdbc.CommonUtils;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;

public class DBTypeProperties extends PropertiesImpl {

    @SuppressWarnings("rawtypes")
    private transient Map dyTypesInfo;

    private final static String CONFIG_FILE_lOCATION_KEY = "org.talend.component.jdbc.config.file";

    public Property<String> dbTypes = PropertyFactory.newString("dbTypes").setRequired();

    public DBTypeProperties(String name) {
        super(name);

        StringBuilder json = new StringBuilder();
        String config_file = System.getProperty(CONFIG_FILE_lOCATION_KEY);
        try (InputStream is = config_file != null ? (new FileInputStream(config_file))
                : this.getClass().getClassLoader().getResourceAsStream("db_type_config.json");
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

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form mainForm = CommonUtils.addForm(this, Form.MAIN);
        mainForm.addRow(Widget.widget(dbTypes).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
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

    public String getDriverClass() {
        String driverClass = null;

        for (Object o : (Object[]) dyTypesInfo.get("@items")) {
            @SuppressWarnings("rawtypes")
            JsonObject eachDBInfo = (JsonObject) o;
            String id = (String) eachDBInfo.get("id");
            if (dbTypes.getValue().equals(id)) {
                driverClass = (String) eachDBInfo.get("class");
                break;
            }
        }

        return driverClass;
    }

    @SuppressWarnings("rawtypes")
    public List<String> getDriverPaths() {
        List<String> mavenPaths = new ArrayList<String>();

        for (Object o : (Object[]) dyTypesInfo.get("@items")) {
            JsonObject eachDBInfo = (JsonObject) o;
            String id = (String) eachDBInfo.get("id");
            if (dbTypes.getValue().equals(id)) {
                Object[] paths = (Object[]) eachDBInfo.get("paths");
                for (Object path : paths) {
                    JsonObject jo_path = (JsonObject) path;
                    mavenPaths.add((String) jo_path.get("path"));
                }
                break;
            }
        }

        return mavenPaths;
    }

}
