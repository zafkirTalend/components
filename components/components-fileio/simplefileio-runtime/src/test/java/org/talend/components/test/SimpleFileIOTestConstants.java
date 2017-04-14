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

package org.talend.components.test;

public class SimpleFileIOTestConstants {

    public static final String S3AccessKey;

    public static final String S3SecretKey;

    public static final String S3Region;

    static {
        S3AccessKey = System.getProperty("s3.accesskey");
        S3SecretKey = System.getProperty("s3.secretkey");
        S3Region = System.getProperty("s3.region");
    }
}
