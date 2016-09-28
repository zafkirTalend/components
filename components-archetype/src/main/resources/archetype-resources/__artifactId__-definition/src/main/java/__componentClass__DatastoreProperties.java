package ${package};

import ${packageTalend}.api.component.runtime.RuntimeInfo;
import ${packageTalend}.common.datastore.DatastoreDefinition;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.Properties;

public class ${componentClass}DatastoreDefinition extends SimpleNamedThing implements DatastoreDefinition<${componentClass}DatastoreProperties> {

    @Override
    public ${componentClass}DatastoreProperties createProperties() {
        return new ${componentClass}DatastoreProperties(getName());
    }

    @Override
    public RuntimeInfo getRuntimeInfo(${componentClass}DatastoreProperties properties, Object ctx) {
        return null;
    }
}
