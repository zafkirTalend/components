package org.talend.components.s3;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.service.internal.ComponentRegistry;
import org.talend.components.api.service.internal.ComponentServiceImpl;
import org.talend.components.s3.tawss3connection.TAwsS3ConnectionDefinition;

@SuppressWarnings("nls")
public class tAWSS3ConnectionTest {

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    private ComponentServiceImpl componentService;

    @Before
    public void initializeComponentRegistryAndService() {
        // reset the component service
        componentService = null;
    }

    // default implementation for pure java test.
    public ComponentService getComponentService() {
        if (componentService == null) {
            ComponentRegistry testComponentRegistry = new ComponentRegistry();
            testComponentRegistry.registerComponentFamilyDefinition(new AwsS3FamilyDefinition());
            componentService = new ComponentServiceImpl(testComponentRegistry);
        }
        return componentService;
    }

    @Test
    public void testtAWSS3ConnectionRuntime() throws Exception {
        TAwsS3ConnectionDefinition def = (TAwsS3ConnectionDefinition) getComponentService()
                .getComponentDefinition("tAWSS3Connection");
        AwsS3ConnectionProperties props = (AwsS3ConnectionProperties) getComponentService()
                .getComponentProperties("tAWSS3Connection");

        // Set up the test schema - not really used for anything now
    }

}
