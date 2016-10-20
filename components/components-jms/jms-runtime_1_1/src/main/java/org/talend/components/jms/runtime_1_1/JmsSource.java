package org.talend.components.jms.runtime_1_1;

import org.talend.components.api.component.runtime.Reader;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.container.RuntimeContainer;


public class JmsSource extends JmsSourceOrSink implements Source {
/*
    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        ValidationResult validate = super.initialize(container, properties);
        if (validate.getStatus() == ValidationResult.Result.ERROR) {
            return validate;
        }
        JmsInputProperties inputProperties = (JmsInputProperties) properties;
        return ValidationResult.OK;
    }
*/
    @Override
    public Reader createReader(RuntimeContainer container) {
        return null;
    }
}
