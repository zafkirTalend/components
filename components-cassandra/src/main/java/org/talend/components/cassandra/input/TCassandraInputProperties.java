package org.talend.components.cassandra.input;

import org.talend.components.cassandra.CassandraIOBasedProperties;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.presentation.Form;

import static org.talend.daikon.properties.PropertyFactory.newProperty;

public class TCassandraInputProperties extends CassandraIOBasedProperties {
    /**
     * named constructor to be used is these properties are nested in other properties. Do not subclass this method for
     * initialization, use {@link #init()} instead.
     *
     * @param name
     */
    public TCassandraInputProperties(String name) {
        super(name);
    }

    public Property query = newProperty("query");

    @Override
    public void setupLayout(){
        super.setupLayout();
        Form form = getForm(Form.MAIN);
        form.addRow(query);
    }

}
