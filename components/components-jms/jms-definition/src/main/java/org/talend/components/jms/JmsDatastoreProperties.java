// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.components.jms;

import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.*;

import org.talend.components.common.SchemaProperties;
import org.talend.components.common.datastore.DatastoreProperties;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

import java.util.Hashtable;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JmsDatastoreProperties extends PropertiesImpl implements DatastoreProperties {

    public enum JmsVersion{
        V_1_1,
        V_2_0
    }

    public SchemaProperties main = new SchemaProperties("main");

    public JmsDatastoreProperties(String name) {
        super(name);
    }

    public Property<JmsVersion> version = newEnum("version", JmsVersion.class).setRequired();

    public Property<String> contextProvider = PropertyFactory.newString("contextProvider","com.tibco.tibjms.naming.TibjmsInitialContextFactory").setRequired();

    public Property<String> serverUrl = PropertyFactory.newString("serverUrl","tibjmsnaming://localhost:7222");

    public Property<String> connectionFactoryName = PropertyFactory.newString("connectionFactoryName","GenericConnectionFactory");

    public Property<Boolean> needUserIdentity = newBoolean("needUserIdentity", false);

    // TODO check if it is not better to do "UserPasswordProperties" class like for cassandra
    public Property<String> userName = PropertyFactory.newString("userName","");

    public Property<String> userPassword = PropertyFactory.newString("userPassword","");

    // Those advanced settings could be either in the datastore or in the dataset
    public Property<Boolean> useHttps = PropertyFactory.newBoolean("useHttps",false);

    public Property<String> httpsSettings = PropertyFactory.newString("httpsSettings");

    public Property<String> property = PropertyFactory.newString("property","");

    public Property<String> value = PropertyFactory.newString("value","");

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(main.getForm(Form.MAIN));
        mainForm.addRow(version);
        mainForm.addRow(contextProvider);
        mainForm.addRow(serverUrl);
        mainForm.addRow(connectionFactoryName);
        mainForm.addRow(userName);
        mainForm.addRow(userPassword);

        Form advancedForm = new Form(this, Form.ADVANCED);
        advancedForm.addRow(useHttps);
        advancedForm.addRow(widget(httpsSettings).setWidgetType(Widget.NAME_SELECTION_AREA_WIDGET_TYPE));
        advancedForm.addRow(property);
        advancedForm.addRow(value);
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        // Main properties
        if (form.getName().equals(Form.MAIN)) {
                form.getWidget(version.getName()).setHidden(false);
                form.getWidget(contextProvider.getName()).setHidden(false);
                form.getWidget(serverUrl.getName()).setHidden(false);
                if (needUserIdentity.getValue()) {
                    form.getWidget(userName.getName()).setHidden(false);
                    form.getWidget(userPassword.getName()).setHidden(false);
                } else {
                    form.getWidget(userName.getName()).setHidden(true);
                    form.getWidget(userPassword.getName()).setHidden(true);
                }
        }
        // Advanced Properties
        if (form.getName().equals(Form.ADVANCED)){
            form.getWidget(useHttps.getName()).setHidden(false);
            if (useHttps.getValue()){
                form.getWidget(httpsSettings.getName()).setHidden(false);
            } else {
                form.getWidget(httpsSettings.getName()).setHidden(true);
            }
            form.getWidget(property.getName()).setHidden(false);
            form.getWidget(value.getName()).setHidden(false);
        }
    }

    public ConnectionFactory getConnectionFactory() {
        InitialContext context;
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,contextProvider);
        env.put(Context.PROVIDER_URL, serverUrl);
        ConnectionFactory connection = null;
        try {
            context = new InitialContext(env);
            connection = (ConnectionFactory)context.lookup(connectionFactoryName.getValue());
            //TODO check if username required how it works
            /*
            if (datastore.needUserIdentity.getValue()) {
                connection = tcf.createConnection(datastore.userName.getValue(),datastore.userPassword.getValue());
            } else {
                connection = tcf.createTopicConnection();
            }*/
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return connection;
    }
}
