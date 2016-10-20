package org.talend.components.jms.runtime_1_1;

import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.jms.JmsDatastoreProperties;
import org.talend.components.jms.JmsMessageType;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class JmsDatastoreRuntime {

    private JmsDatastoreProperties.JmsVersion version;

    private String contextProvider;

    private String serverUrl;

    private String connectionFactoryName;

    private String userName;

    private String userPassword;

    private JmsMessageType msgType;

    List<NamedThing> getPossibleDatasetNames(RuntimeContainer container) throws IOException {
        List<NamedThing> datasetList = new ArrayList();
        try {
            Context context = new InitialContext();
            NamingEnumeration list = context.listBindings("");
            while (list.hasMore()) {
                Binding nc = (Binding) list.next();
                Object jmsObject = context.lookup(nc.getName());
                /*if (messageType.equals("TOPIC") && jmsObject instanceof Topic) {
                    datasetList.add(new SimpleNamedThing(nc.getName(),nc.getName()));
                } else if (messageType.equals("QUEUE") && jmsObject instanceof Queue) {
                    datasetList.add(new SimpleNamedThing(nc.getName(),nc.getName()));
                }*/
            }
        }catch (NamingException e) {
            e.printStackTrace();
        }
        return datasetList;
    }
/*
    public void connect (RuntimeContainer container) throws NamingException,JMSException {
        JmsDatastoreProperties connProps = properties.getConnectionProperties();
        InitialContext context;
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,connProps.contextProvider);
        env.put(Context.PROVIDER_URL, connProps.serverUrl);
            context = new InitialContext(env);
            if (connProps.msgType.getValue().equals("topic")) {
                TopicConnectionFactory tcf = (javax.jms.TopicConnectionFactory)context.lookup(connProps.connectionFactoryName.getValue());
                TopicConnection connection;
                if (connProps.needUserIdentity.getValue()) {
                    connection = tcf.createTopicConnection(connProps.userName.getValue(),connProps.userPassword.getValue());
                } else {
                    connection = tcf.createTopicConnection();
                }
                connection.start();
            } else {
                // TODO check if "cn=" is good
                QueueConnectionFactory qcf = (javax.jms.QueueConnectionFactory)context.lookup("cn=" + connProps.connectionFactoryName.getValue());
                QueueConnection connection;
                if (connProps.needUserIdentity.getValue()) {
                    connection = qcf.createQueueConnection(connProps.userName.getValue(),connProps.userPassword.getValue());
                } else {
                    connection = qcf.createQueueConnection();
                }
                qcf.createQueueConnection();
                connection.start();
            }
    }*/
}
