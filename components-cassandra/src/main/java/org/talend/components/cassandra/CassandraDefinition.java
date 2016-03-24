package org.talend.components.cassandra;

import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.ComponentImageType;

public abstract class CassandraDefinition extends AbstractComponentDefinition {

    protected String componentName;

    public CassandraDefinition(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public String[] getFamilies() {
        return new String[]{"Databases/Cassandra", "Big Data/Cassandra"};
    }

    @Override
    public String getName() {
        return componentName;
    }

    @Override
    public String getPngImagePath(ComponentImageType imageType) {
        //FIXME can it be generic?
        switch (imageType) {
            case PALLETE_ICON_32X32:
                return componentName.replace("New", "") + "_icon32.png"; //$NON-NLS-1$
        }
        return null;
    }

    @Override
    public String getMavenGroupId() {
        return "org.talend.components";
    }

    @Override
    public String getMavenArtifactId() {
        return "components-cassandra";
    }

}
