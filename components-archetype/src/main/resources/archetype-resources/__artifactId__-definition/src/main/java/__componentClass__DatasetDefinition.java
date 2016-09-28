package ${package};

import java.net.URL;
import java.util.List;

import ${packageTalend}.api.component.runtime.RuntimeInfo;
import ${packageTalend}.common.dataset.DatasetDefinition;
import org.talend.daikon.SimpleNamedThing;

public class ${componentClass}DatasetDefinition extends SimpleNamedThing implements DatasetDefinition<${componentClass}DatasetProperties> {

    public ${componentClass}DatasetProperties createProperties() {
        return new ${componentClass}DatasetProperties(${componentClass}ComponentFamilyDefinition.NAME);
    }

    public RuntimeInfo getRuntimeInfo(${componentClass}DatasetProperties properties, Object ctx) {
        return new RuntimeInfo() {

            public List<URL> getMavenUrlDependencies() {
                return null;
            }

            public String getRuntimeClassName() {
                return null;
            }
        };
    }
}
