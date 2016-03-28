package org.talend.components.cassandra.runtime;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AuthenticationException;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.ConnectionPropertiesProvider;
import org.talend.components.cassandra.CassandraConnectionProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;

import java.io.IOException;
import java.util.List;

public class CassandraSourceOrSink implements SourceOrSink {

    protected ConnectionPropertiesProvider<CassandraConnectionProperties> properties;

    protected static final String SESSION_GLOBALMAP_KEY = "Session"; //FIXME only for Studio?
    protected static final String CLUSTER_GLOBALMAP_KEY = "Cluster"; //FIXME only for Studio?

    @Override
    public void initialize(RuntimeContainer container, ComponentProperties properties) {
        this.properties = (ConnectionPropertiesProvider<CassandraConnectionProperties>) properties;
    }

    @Override
    public ValidationResult validate(RuntimeContainer container) {
        try {
            connect(container);
        } catch (IOException ex) {
            return new ValidationResult().setStatus(ValidationResult.Result.ERROR).setMessage(ex.getMessage());
        }
        return ValidationResult.OK;
    }

    protected Session connect(RuntimeContainer container) throws IOException {
        String referencedComponentId = properties.getConnectionProperties().getReferencedComponentId();
        if (referencedComponentId != null) {
            if (container != null) {
                Session conn = (Session) container.getComponentData(referencedComponentId, SESSION_GLOBALMAP_KEY);
                if (conn != null)
                    return conn;
                throw new IOException("Referenced component: " + referencedComponentId + " not connected");
            }
        }
        try {
            return createCluster(container).connect();
        }catch (NoHostAvailableException| IllegalStateException| AuthenticationException ex){
            throw new IOException(ex.getMessage());
        }
    }

    protected Cluster createCluster(RuntimeContainer container) {
        CassandraConnectionProperties connProps = properties.getConnectionProperties();
        Cluster.Builder clusterBuilder = Cluster.builder()
                .addContactPoints(connProps.host.getStringValue().split(","))
                .withPort(connProps.port.getIntValue());
        if (connProps.needAuth.getBooleanValue()) {
            clusterBuilder.withCredentials(connProps.userPassword.userId.getStringValue(), connProps.userPassword.password.getStringValue());
        }
        return clusterBuilder.build();
    }

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        Metadata metadata = createCluster(container).getMetadata();
        List<KeyspaceMetadata> keyspaces = metadata.getKeyspaces();

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
