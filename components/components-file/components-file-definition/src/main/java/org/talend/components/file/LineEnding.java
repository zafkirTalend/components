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
package org.talend.components.file;


public enum LineEnding {
    LF("\n"),
    CRLF("\r\n");
    
    private final String lineSeparator;
    private LineEnding(String separator) {
        this.lineSeparator = separator;
    }
    
    public String getLineSeparator() {
        return this.lineSeparator;
    }
}
