package org.talend.components.jms.runtime_1_1;

import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;

public class JmsSink extends JmsSourceOrSink implements Sink {
    /*
    /**
     * Initializes this {@link Sink} with user specified properties
     *
     * @param container {@link RuntimeContainer} instance
     * @param properties user specified properties
     *
    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        ValidationResult validate = super.initialize(container, properties);
        if (validate.getStatus() == ValidationResult.Result.ERROR) {
            return validate;
        }
        JmsOutputProperties outputProperties = (JmsOutputProperties) properties;
        return ValidationResult.OK;
    }
*/
    @Override
    public WriteOperation<?> createWriteOperation() {
        return null;
    }

}
