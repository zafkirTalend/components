package org.talend.components.oracle.runtime;

import org.talend.daikon.avro.AvroRegistry;


public class OracleSource extends DBSource {

    OracleTemplate template = new OracleTemplate();//no state object
    
    @Override
    protected DBTemplate getDBTemplate() {
        return template;
    }

    @Override
    protected AvroRegistry getAvroRegistry() {
        return OracleAvroRegistry.get();
    }

}
