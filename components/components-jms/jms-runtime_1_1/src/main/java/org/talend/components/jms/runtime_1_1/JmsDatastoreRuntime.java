package org.talend.components.jms.runtime_1_1;

import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.common.datastore.DatastoreProperties;
import org.talend.components.common.datastore.runtime.DatastoreRuntime;
import org.talend.components.jms.JmsDatastoreProperties;
import org.talend.components.jms.JmsMessageType;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.properties.ValidationResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class JmsDatastoreRuntime implements DatastoreRuntime {

    protected transient JmsDatastoreProperties properties;

    private JmsDatastoreProperties.JmsVersion version;

    private String contextProvider;

    private String serverUrl;

    private String connectionFactoryName;

    private String userName;

    private String userPassword;

    private JmsMessageType msgType;

    private List<NamedThing> getPossibleDatasetNames(RuntimeContainer container, String datasetPath) throws IOException {
        List<NamedThing> datasetList = new ArrayList();
        try {
            Context context = new InitialContext();
            NamingEnumeration list = context.listBindings("");
            while (list.hasMore()) {
                Binding nc = (Binding) list.next();
                Object jmsObject = context.lookup(nc.getName());
                if (datasetPath.equals("TOPIC") && jmsObject instanceof Topic) {
                    datasetList.add(new SimpleNamedThing(nc.getName(),nc.getName()));
                } else if (datasetPath.equals("QUEUE") && jmsObject instanceof Queue) {
                    datasetList.add(new SimpleNamedThing(nc.getName(),nc.getName()));
                }
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return datasetList;
    }

    @Override public Iterable<ValidationResult> doHealthChecks(RuntimeContainer container) {

        Context context = null;
        Hashtable<String, String> env  = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,"org.exolab.jms.jndi.InitialContextFactory");
        env.put(Context.PROVIDER_URL, "tcp://localhost:3035");
        env.put(Context.SECURITY_PRINCIPAL, "admin");
        env.put(Context.SECURITY_CREDENTIALS, "openjms");

        ConnectionFactory connection = null;
        try {
            context = new InitialContext(env);
            System.out.println("context");
            connection = (ConnectionFactory)context.lookup("ConnectionFactory");

        } catch (NamingException e) {
            e.printStackTrace();
        }




        ConnectionFactory connectionFactory = null;//properties.getConnectionFactory();
        connection = null;
        if (properties.needUserIdentity.getValue()) {
            //connection = connectionFactory.createConnection(properties.userName.getValue(), properties.userPassword.getValue());
        } else {
            //connection = connectionFactory.createConnection();
        }
        //connection.start();
        //connection.close();

        if (connection != null) {
            return Arrays.asList(ValidationResult.OK);
        }
        return null;
    }

    @Override public ValidationResult initialize(RuntimeContainer container, DatastoreProperties properties) {
        this.properties = (JmsDatastoreProperties) properties;
        return ValidationResult.OK;
    }

    public ValidationResult initialize(RuntimeContainer container, JmsDatastoreProperties properties) {
        this.properties = properties;
        return ValidationResult.OK;
    }
}
