// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.salesforce;

import static org.talend.components.salesforce.SalesforceDefinition.SOURCE_OR_SINK_CLASS;
import static org.talend.components.salesforce.SalesforceDefinition.USE_CURRENT_JVM_PROPS;
import static org.talend.components.salesforce.SalesforceDefinition.getSandboxedInstance;
import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newInteger;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import org.talend.components.api.exception.ComponentException;
import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.api.properties.ComponentReferenceProperties;
import org.talend.components.common.ProxyProperties;
import org.talend.components.common.oauth.OauthProperties;
import org.talend.components.salesforce.common.SalesforceRuntimeSourceOrSink;
import org.talend.components.salesforce.tsalesforceconnection.TSalesforceConnectionDefinition;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.PresentationItem;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.sandbox.SandboxedInstance;
import org.talend.daikon.serialize.PostDeserializeSetup;
import org.talend.daikon.serialize.migration.SerializeSetVersion;

public class SalesforceConnectionProperties extends ComponentPropertiesImpl
        implements SalesforceProvideConnectionProperties, SerializeSetVersion {

    protected static final I18nMessages MESSAGES = GlobalI18N.getI18nMessageProvider()
            .getI18nMessages(SalesforceConnectionProperties.class);

    public static final String DEFAULT_API_VERSION = "39.0";

    public static final String URL = "https://www.salesforce.com/services/Soap/u/" + DEFAULT_API_VERSION;

    public static final String OAUTH_URL = "https://login.salesforce.com/services/oauth2";

    public Property<String> endpoint = newString("endpoint").setRequired();

    public static final String FORM_WIZARD = "Wizard";

    //
    // Properties
    //

    // Only for the wizard use
    public Property<String> name = newString("name").setRequired();

    public enum LoginType {
        Basic,
        OAuth
    }

    public Property<LoginType> loginType = newEnum("loginType", LoginType.class).setRequired();

    public Property<Boolean> bulkConnection = newBoolean("bulkConnection"); //$NON-NLS-1$

    public Property<Boolean> reuseSession = newBoolean("reuseSession"); //$NON-NLS-1$

    public Property<String> sessionDirectory = newString("sessionDirectory"); //$NON-NLS-1$

    public Property<Boolean> needCompression = newBoolean("needCompression"); //$NON-NLS-1$

    public Property<Integer> timeout = newInteger("timeout"); //$NON-NLS-1$

    public Property<Boolean> httpTraceMessage = newBoolean("httpTraceMessage"); //$NON-NLS-1$

    public Property<Boolean> httpChunked = newBoolean("httpChunked"); //$NON-NLS-1$

    public Property<String> clientId = newString("clientId"); //$NON-NLS-1$

    //
    // Presentation items
    //
    public PresentationItem testConnection = new PresentationItem("testConnection", "Test connection");

    public PresentationItem advanced = new PresentationItem("advanced", "Advanced...");

    //
    // Nested property collections
    //
    private static final String OAUTH = "oauth";

    public OauthProperties oauth = new OauthProperties(OAUTH);

    public Property<String> apiVersion = newString("apiVersion");

    private static final String USERPASSWORD = "userPassword";

    public SalesforceUserPasswordProperties userPassword = new SalesforceUserPasswordProperties(USERPASSWORD);

    public ProxyProperties proxy = new ProxyProperties("proxy");

    public ComponentReferenceProperties<SalesforceConnectionProperties> referencedComponent = new ComponentReferenceProperties<>(
            "referencedComponent", TSalesforceConnectionDefinition.COMPONENT_NAME);

    public SalesforceConnectionProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();

        loginType.setValue(LoginType.Basic);
        endpoint.setValue(URL);
        apiVersion.setValue(DEFAULT_API_VERSION);
        timeout.setValue(60000);
        httpChunked.setValue(true);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form wizardForm = Form.create(this, FORM_WIZARD);
        wizardForm.addRow(name);
        wizardForm.addRow(widget(loginType).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE).setDeemphasize(true));
        wizardForm.addRow(oauth.getForm(Form.MAIN));
        wizardForm.addRow(userPassword.getForm(Form.MAIN));
        wizardForm.addRow(widget(advanced).setWidgetType(Widget.BUTTON_WIDGET_TYPE));
        wizardForm.addColumn(widget(testConnection).setLongRunning(true).setWidgetType(Widget.BUTTON_WIDGET_TYPE));

        Form mainForm = Form.create(this, Form.MAIN);
        mainForm.addRow(widget(loginType).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addRow(oauth.getForm(Form.MAIN));
        mainForm.addRow(userPassword.getForm(Form.MAIN));

        Form advancedForm = Form.create(this, Form.ADVANCED);
        advancedForm.addRow(endpoint);
        advancedForm.addColumn(apiVersion);
        advancedForm.addRow(bulkConnection);
        advancedForm.addRow(reuseSession);
        advancedForm.addRow(widget(sessionDirectory).setWidgetType(Widget.DIRECTORY_WIDGET_TYPE));
        advancedForm.addRow(needCompression);
        advancedForm.addRow(httpTraceMessage);
        advancedForm.addRow(httpChunked);
        advancedForm.addRow(clientId);
        advancedForm.addRow(timeout);
        advancedForm.addRow(proxy.getForm(Form.MAIN));
        advanced.setFormtoShow(advancedForm);

        // A form for a reference to a connection, used in a tSalesforceInput for example
        Form refForm = Form.create(this, Form.REFERENCE);
        Widget compListWidget = widget(referencedComponent).setWidgetType(Widget.COMPONENT_REFERENCE_WIDGET_TYPE);
        refForm.addRow(compListWidget);
        refForm.addRow(mainForm);
    }

    public void afterLoginType() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(FORM_WIZARD));
        refreshLayout(getForm(Form.ADVANCED));
    }

    public void afterReferencedComponent() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.REFERENCE));
        refreshLayout(getForm(Form.ADVANCED));
    }

    public void afterReuseSession() {
        refreshLayout(getForm(Form.ADVANCED));
    }

    public void afterBulkConnection() {
        refreshLayout(getForm(Form.ADVANCED));
    }

    public ValidationResult validateTestConnection() throws Exception {
        try (SandboxedInstance sandboxedInstance = getSandboxedInstance(SOURCE_OR_SINK_CLASS, USE_CURRENT_JVM_PROPS)) {
            SalesforceRuntimeSourceOrSink ss = (SalesforceRuntimeSourceOrSink) sandboxedInstance.getInstance();
            ss.initialize(null, SalesforceConnectionProperties.this);
            ValidationResultMutable vr = new ValidationResultMutable(ss.validate(null));
            if (vr.getStatus() == ValidationResult.Result.OK) {
                vr.setMessage(MESSAGES.getMessage("connection.success"));
                getForm(FORM_WIZARD).setAllowForward(true);
            } else {
                getForm(FORM_WIZARD).setAllowForward(false);
            }
            return vr;
        }
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);

        String refComponentIdValue = getReferencedComponentId();
        boolean useOtherConnection = refComponentIdValue != null
                && refComponentIdValue.startsWith(TSalesforceConnectionDefinition.COMPONENT_NAME);
        if (form.getName().equals(Form.MAIN) || form.getName().equals(FORM_WIZARD)) {
            if (useOtherConnection) {
                form.getWidget(loginType.getName()).setHidden(true);
                form.getWidget(OAUTH).setHidden(true);
                form.getWidget(USERPASSWORD).setHidden(true);
            } else {
                form.getWidget(loginType.getName()).setHidden(false);
                String endpointValue = endpoint.getValue();
                switch (loginType.getValue()) {
                case Basic:
                    form.getWidget(OAUTH).setHidden(true);
                    form.getWidget(USERPASSWORD).setHidden(false);
                    if (endpointValue == null || endpointValue.contains(OAUTH_URL)) {
                        endpoint.setValue(URL);
                    }
                    break;
                case OAuth:
                    form.getWidget(OAUTH).setHidden(false);
                    form.getWidget(USERPASSWORD).setHidden(true);
                    if (endpointValue == null || endpointValue.contains(URL)) {
                        endpoint.setValue(OAUTH_URL);
                    }
                    break;

                default:
                    throw new ComponentException(new Throwable("Enum value should be handled :" + loginType.getValue()));
                }
            }
        }

        if (form.getName().equals(Form.ADVANCED)) {
            if (useOtherConnection) {
                form.setHidden(true);
            } else {
                form.setHidden(false);

                boolean bulkMode = bulkConnection.getValue();
                form.getWidget(httpChunked.getName()).setHidden(bulkMode);
                form.getWidget(httpTraceMessage.getName()).setHidden(!bulkMode);
                boolean isBasicLogin = LoginType.Basic.equals(loginType.getValue());
                form.getWidget(reuseSession.getName()).setVisible(isBasicLogin && !bulkMode);
                form.getWidget(sessionDirectory.getName()).setVisible(isBasicLogin && !bulkMode && reuseSession.getValue());
                form.getWidget(apiVersion.getName()).setHidden(isBasicLogin);

                Form proxyForm = form.getChildForm(proxy.getName());
                if (proxyForm != null) {
                    boolean isUseProxy = proxy.useProxy.getValue();
                    proxyForm.getWidget(proxy.host.getName()).setHidden(!isUseProxy);
                    proxyForm.getWidget(proxy.port.getName()).setHidden(!isUseProxy);
                    proxyForm.getWidget(proxy.userPassword.getName()).setHidden(!isUseProxy);
                }
            }
        }
    }

    @Override
    public SalesforceConnectionProperties getConnectionProperties() {
        return this;
    }

    public String getReferencedComponentId() {
        return referencedComponent.componentInstanceId.getStringValue();
    }

    public SalesforceConnectionProperties getReferencedConnectionProperties() {
        SalesforceConnectionProperties refProps = referencedComponent.getReference();
        if (refProps != null) {
            return refProps;
        }
        return null;
    }

    @Override
    public int getVersionNumber() {
        return 1;
    }

    @Override
    public boolean postDeserialize(int version, PostDeserializeSetup setup, boolean persistent) {
        boolean migrated = super.postDeserialize(version, setup, persistent);
        if (version < this.getVersionNumber()) {
            if (apiVersion.getValue() == null) {
                apiVersion.setValue("\"34.0\"");
                migrated = true;
            }
        }
        return migrated;
    }

}
