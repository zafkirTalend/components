// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.oracle.runtime;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.oracle.DBInputProperties;
import org.talend.daikon.schema.Schema;
import org.talend.daikon.schema.SchemaElement;

public class DBReader implements BoundedReader {

    private static final Logger          LOG = LoggerFactory.getLogger(DBReader.class);

    protected DBInputProperties          properties;

    protected Map<String, SchemaElement> fieldMap;

    protected List<SchemaElement>        fieldList;

    protected DBSource                   source;

    protected RuntimeContainer           adaptor;

    protected Connection                 conn;

    protected String                     dbschema;

    protected ResultSet                  resultSet;

    protected DBTemplate                 dbTemplate;

    public DBReader(RuntimeContainer adaptor, DBSource source, DBInputProperties props) {
        this.source = source;
        this.adaptor = adaptor;
        this.properties = props;
    }

    public void setDBTemplate(DBTemplate template) {
        this.dbTemplate = template;
    }

    @Override
    public boolean start() throws IOException {
        Schema schema = source.getSchema(adaptor, properties.tablename.getStringValue());
        fieldMap = schema.getRoot().getChildMap();
        fieldList = schema.getRoot().getChildren();
        
        try {
            conn = dbTemplate.connect(properties.getConnectionProperties());
            Statement statement = conn.createStatement();
            resultSet = statement.executeQuery(properties.sql.getStringValue());
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean advance() throws IOException {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Object getCurrent() throws NoSuchElementException {
        Map<String, Object> row = new HashMap<>();

        for (int i = 0; i < fieldList.size(); i++) {
            SchemaElement element = fieldList.get(i);
            // TODO fix the type mapping
            try {
                row.put(element.getName(), resultSet.getString(i + 1));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return row;
    }

    @Override
    public Instant getCurrentTimestamp() throws NoSuchElementException {
        return null;
    }

    @Override
    public void close() throws IOException {
        try {
            resultSet.close();
            conn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public Double getFractionConsumed() {
        return null;
    }

    @Override
    public BoundedSource getCurrentSource() {
        return source;
    }

    @Override
    public BoundedSource splitAtFraction(double fraction) {
        return null;
    }

}
