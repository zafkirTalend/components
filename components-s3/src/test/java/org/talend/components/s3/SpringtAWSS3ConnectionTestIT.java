package org.talend.components.s3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.test.AbstractComponentTest;
import org.talend.components.api.test.ComponentTestUtils;
import org.talend.components.api.test.SpringTestApp;
import org.talend.components.s3.tawss3connection.TAwsS3ConnectionDefinition;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringTestApp.class)
public class SpringtAWSS3ConnectionTestIT extends AbstractComponentTest {

    @Inject
    private ComponentService componentService;

    public ComponentService getComponentService() {
        return componentService;
    }

    @Test
    public void testAfterExtendedOutput() throws Throwable {
        ComponentProperties props;

        props = new TAwsS3ConnectionDefinition().createProperties();
        ComponentTestUtils.checkSerialize(props, errorCollector);
        Property<Boolean> inheritFromAwsRole = (Property<Boolean>) props.getProperty("inheritFromAwsRole");
        assertEquals(false, inheritFromAwsRole.getValue());
        Form mainForm = props.getForm(Form.MAIN);
        assertFalse(mainForm.getWidget("accessSecretKeyProperties").isHidden());

        inheritFromAwsRole.setValue(true);
        props = checkAndAfter(mainForm, "inheritFromAwsRole", props);
        mainForm = props.getForm(Form.MAIN);
        assertTrue(mainForm.isRefreshUI());

        assertTrue(mainForm.getWidget("accessSecretKeyProperties").isHidden());
    }

    @Test
    // this is an integration test to check that the dependencies file is properly generated.
    public void testDependencies() {
        ComponentTestUtils.testAllDesignDependenciesPresent(componentService, errorCollector);
    }

    protected ComponentProperties checkAndAfter(Form form, String propName, ComponentProperties props) throws Throwable {
        assertTrue(form.getWidget(propName).isCallAfter());
        return (ComponentProperties) getComponentService().afterProperty(propName, props);
    }

}
