package org.talend.components.jms.runtime_1_1;

import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import javax.jms.*;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;


import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;

import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JmsSourceOrSink implements SourceOrSink {
/*
    protected transient JmsDatastoreProperties properties;

    // TODO create external class "JmsVersion" instead of enum in JmsDatastoreProperties ?
    private JmsDatastoreProperties.JmsVersion version;

    private String contextProvider;

    private String serverUrl;

    private String connectionFactoryName;

    private String userName;

    private String userPassword;

    //private JmsDatastoreProperties.JmsMsgType msgType;
*/
    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        /*JmsDatastoreProperties jmsProperties = (JmsDatastoreProperties) properties;
        // TODO usefull ?
        contextProvider = jmsProperties.contextProvider.getValue();
        serverUrl = jmsProperties.serverUrl.getValue();
        connectionFactoryName = jmsProperties.connectionFactoryName.getValue();
        userName = jmsProperties.userName.getValue();
        userPassword = jmsProperties.userPassword.getValue();
        //msgType = jmsProperties.msgType.getValue();
        */
        return ValidationResult.OK;
    }

    @Override
    public ValidationResult validate(RuntimeContainer container) {
        /*try {
            connect(container);
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        }
        */
        return ValidationResult.OK;
    }
/*
    public JmsDatastoreProperties getProperties() {
        return properties;
    }*/

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        return null;
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
        return null;
    }

    boolean doValidate (String name, ComponentProperties properties){
        try {
            //JmsSourceOrSink mdbsos = new JmsSourceOrSink();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /*
        Connection part
     */

    /*public void connect (RuntimeContainer container) throws NamingException,JMSException {
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

    //TODO method getSchema
    /*
     public Schema getSchema(RuntimeContainer container, String keyspaceName, String tableName) throws IOException {
        return CassandraAvroRegistry.get().inferSchema(getCassandraMetadata(container,
                keyspaceName, tableName));
    }
     */
}
