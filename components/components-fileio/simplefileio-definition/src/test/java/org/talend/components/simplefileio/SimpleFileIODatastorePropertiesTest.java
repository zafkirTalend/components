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

package org.talend.components.simplefileio;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

/**
 * Unit tests for {@link SimpleFileIODatastoreProperties}.
 */
public class SimpleFileIODatastorePropertiesTest {

    /**
     * Useful constant listing all of the fields in the properties.
     */
    public static final Iterable<String> ALL = Arrays.asList("fileSystemType", "useKerberos", "kerberosPrincipal",
            "kerberosKeytab", "userName", "accessKey", "secretKey", "region");

    public static final Iterable<String> HDFS = Arrays.asList("useKerberos", "kerberosPrincipal",
            "kerberosKeytab", "userName");

    public static final Iterable<String> S3 = Arrays.asList("accessKey", "secretKey", "region");

    /**
     * Instance to test. A new instance is created for each test.
     */
    SimpleFileIODatastoreProperties properties = null;

    @Before
    public void setup() {
        properties = new SimpleFileIODatastoreProperties("test");
        properties.init();
    }

    /**
     * Check the correct default values in the properties.
     */
    @Test
    public void testDefaultProperties() {
        assertThat(properties.userName.getValue(), nullValue());
    }

    /**
     * Check the setup of the form layout.
     */
    @Test
    public void testSetupLayout() {
        properties.setupLayout();

        Form main = properties.getForm(Form.MAIN);
        assertThat(main, notNullValue());
        assertThat(main.getWidgets(), hasSize(8));

        for (String field : ALL) {
            Widget w = main.getWidget(field);
            assertThat(w, notNullValue());
        }
    }

    /**
     * Checks {@link Properties#refreshLayout(Form)}
     */
    @Test
    public void testRefreshLayout() {
        Form main = properties.getForm(Form.MAIN);
        properties.useKerberos.setValue(false);
        properties.refreshLayout(main);

        for (String field : HDFS) {
            // true for everything but kerberosPrincipal or kerberosKeytab
            assertThat(main.getWidget(field).isVisible(), is(field != "kerberosPrincipal" && field != "kerberosKeytab"));
        }
        for (String field : S3) {
            assertThat(main.getWidget(field).isVisible(), is(false));
        }

        properties.useKerberos.setValue(true);
        properties.refreshLayout(main);

        // All of the fields are visible.
        for (String field : HDFS) {
            // true for everything but username
            assertThat(main.getWidget(field).isVisible(), is(field != "userName"));
        }
        for (String field : S3) {
            assertThat(main.getWidget(field).isVisible(), is(false));
        }
        properties.fileSystemType.setValue(FileSystemType.S3);
        properties.refreshLayout(main);

        // All of the fields are visible.
        for (String field : S3) {
            assertThat(main.getWidget(field).isVisible(), is(true));
        }
        for (String field : HDFS) {
            assertThat(main.getWidget(field).isVisible(), is(false));
        }
    }
}
