package org.talend.components.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.components.api.exception.TalendConnectionException;
import org.talend.components.api.runtime.input.Reader;
import org.talend.components.api.runtime.input.Source;
import org.talend.components.api.runtime.input.Split;
import org.talend.components.api.runtime.metadata.Metadata;
import org.talend.components.api.runtime.row.BaseRowStruct;
import org.talend.components.mysql.metadata.MysqlMetadata;
import org.talend.components.mysql.tMysqlInput.MysqlSource;
import org.talend.components.mysql.tMysqlInput.tMysqlInputProperties;
import org.talend.components.mysql.type.MysqlTalendTypesRegistry;
import org.talend.daikon.schema.SchemaElement;
import org.talend.daikon.schema.internal.DataSchemaElement;
import org.talend.daikon.schema.type.ExternalBaseType;
import org.talend.daikon.schema.type.TypeMapping;

/**
 * Created by bchen on 16-1-18.
 */
public class MysqlDITest {

    public static final String HOST = "localhost";

    public static final String PORT = "3306";

    public static final String USER = "root";

    public static final String PASS = "mysql";

    public static final String DBNAME = "tuj";

    public static final String TABLE = "test";

    public static final String PROPERTIES = "noDatetimeStringSync=true";

    Connection conn;

    @Before
    public void prepare() {
        TypeMapping.registryTypes(new MysqlTalendTypesRegistry());

        try {
            Class.forName("org.gjt.mm.mysql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DBNAME + "?" + PROPERTIES;

        try {
            conn = DriverManager.getConnection(url, USER, PASS);
            Statement statement = conn.createStatement();
            statement.execute("drop table tuj.test");
            statement.execute("create table tuj.test(id int PRIMARY KEY, name VARCHAR(50))");
            statement.execute("insert into tuj.test(id, name) values(1,'hello')");
            statement.execute("insert into tuj.test(id, name) values(2,'world')");
            statement.execute("insert into tuj.test(id, name) values(3,'mysql')");
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Ignore
    public void testSplit() throws TalendConnectionException {
        tMysqlInputProperties props = new tMysqlInputProperties("tMysqlInput_1");
        props.initForRuntime();
        props.HOST.setValue(HOST);
        props.PORT.setValue(PORT);
        props.USER.setValue(USER);
        props.PASS.setValue(PASS);
        props.DBNAME.setValue(DBNAME);
        props.PROPERTIES.setValue(PROPERTIES);
        props.TABLE.setValue(TABLE);
        props.QUERY.setValue("select id, name from tuj.test");

        Metadata metadata = new MysqlMetadata();
        metadata.initSchema(props);

        Source source = new MysqlSource();
        source.init(props);

        Map<String, SchemaElement.Type> row_metadata = new HashMap<>();

        List<BaseRowStruct> rows = new ArrayList<>();

        Split[] splits = source.getSplit(2);

        Reader reader = source.getRecordReader(splits[0]);
        List<SchemaElement> fields = reader.getSchema();
        for (SchemaElement field : fields) {
            row_metadata.put(field.getName(), field.getType());
        }
        while (reader.advance()) {
            BaseRowStruct baseRowStruct = new BaseRowStruct(row_metadata);
            for (SchemaElement column : fields) {
                DataSchemaElement dataFiled = (DataSchemaElement) column;
                try {
                    ExternalBaseType converter = dataFiled.getAppColType().newInstance();
                    Object dataValue = converter
                            .convertToKnown(converter.readValue(reader.getCurrent(), dataFiled.getAppColName()));
                    baseRowStruct.put(dataFiled.getName(), TypeMapping.convert(source.getFamilyName(), dataFiled, dataValue));
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            rows.add(baseRowStruct);
        }

        Assert.assertEquals(1, rows.size());

        rows.clear();
        reader = source.getRecordReader(splits[1]);
        while (reader.advance()) {
            BaseRowStruct baseRowStruct = new BaseRowStruct(row_metadata);
            for (SchemaElement column : fields) {
                DataSchemaElement dataFiled = (DataSchemaElement) column;
                try {
                    ExternalBaseType converter = dataFiled.getAppColType().newInstance();
                    Object dataValue = converter
                            .convertToKnown(converter.readValue(reader.getCurrent(), dataFiled.getAppColName()));
                    baseRowStruct.put(dataFiled.getName(), TypeMapping.convert(source.getFamilyName(), dataFiled, dataValue));
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            rows.add(baseRowStruct);
        }
        Assert.assertEquals(2, rows.size());
    }
}
