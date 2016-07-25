// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.s3;

/**
 * created by dmytro.chmyga on Jul 20, 2016
 */
public enum Region {
    DEFAULT("DEFAULT"),
    AP_SOUTHEAST_1("ap-southeast-1"),
    AP_SOUTHEAST_2("ap-southeast-2"),
    AP_NORTHEAST_1("ap-northeast-1"),
    CN_NORTH_1("cn-north-1"),
    EU_WEST_1("eu-west-1"),
    EU_CENTRAL_1("eu-central-1"),
    GOV_CLOUD("us-gov-west-1"),
    SA_EAST_1("sa-east-1"),
    US_EAST_1("us-east-1"),
    US_WEST_1("us-west-1"),
    US_WEST_2("us-west-2");

    private final String awsRegionCode;

    private Region(String awsRegionCode) {
        this.awsRegionCode = awsRegionCode;
    }

    public String getAwsRegionCode() {
        return awsRegionCode;
    }
}
