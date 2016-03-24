package org.talend.components.cassandra.runtime;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AuthenticationException;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.ConnectionPropertiesProvider;
import org.talend.components.cassandra.tcassandraconnection.TCassandraConnectionProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;

import java.io.IOException;
import java.util.List;

public class CassandraSourceOrSink implements SourceOrSink {

    protected ConnectionPropertiesProvider<TCassandraConnectionProperties> properties;

    protected static final String SESSION_GLOBALMAP_KEY = "Session"; //FIXME only for Studio?

    @Override
    public void initialize(RuntimeContainer container, ComponentProperties properties) {
        this.properties = (ConnectionPropertiesProvider<TCassandraConnectionProperties>) properties;
    }

    @Override
    public ValidationResult validate(RuntimeContainer container) {
        try {
            connect(container);
        } catch (NoHostAvailableException | IllegalStateException | AuthenticationException | IOException ex) {
            return new ValidationResult().setStatus(ValidationResult.Result.ERROR).setMessage(ex.getMessage());
        }
        return ValidationResult.OK;
    }

    protected Session connect(RuntimeContainer container) throws NoHostAvailableException, IllegalStateException, AuthenticationException, IOException {
        TCassandraConnectionProperties connProps = properties.getConnectionProperties();
        String referencedComponentId = connProps.getReferencedComponentId();
        if (referencedComponentId != null) {
            if (container != null) {
                Session conn = (Session) container.getComponentData(referencedComponentId, SESSION_GLOBALMAP_KEY);
                if (conn != null)
                    return conn;
                throw new IOException("Referenced component: " + referencedComponentId + " not connected");//FIXME can it be a common talend exception?
            }

        }
        Cluster.Builder clusterBuilder = Cluster.builder()
                .addContactPoints(connProps.host.getStringValue().split(","))
                .withPort(connProps.port.getIntValue());
        if (connProps.needAuth.getBooleanValue()) {
            clusterBuilder.withCredentials(connProps.userPassword.userId.getStringValue(), connProps.userPassword.password.getStringValue());
        }
        Cluster cluster = clusterBuilder.build();
        return cluster.connect();
    }

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        return null;
    }

    @Override
    public Schema getSchema(RuntimeContainer container, String schemaName) throws IOException {
        return null;
    }

    @Override
    public Schema getSchemaFromProperties(RuntimeContainer container) throws IOException {
        return null;
    }

    @Override
    public Schema getPossibleSchemaFromProperties(RuntimeContainer container) throws IOException {
        return null;
    }
}
