package org.talend.components.cassandra;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.reflect.ReflectData;
import org.junit.Rule;
import org.junit.Test;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

/**
 * Unit tests for the {@link RowFacadeFactory}.
 */
public class RowFacadeFactoryTest {

    @Rule
    public EmbeddedCassandraExampleDataResource mCass = new EmbeddedCassandraExampleDataResource(getClass().getSimpleName());

    /**
     * Basic test case adapting a simple {@link Row} from the embedded database.
     */
    @Test
    public void testBasic() {
        RowFacadeFactory rff = new RowFacadeFactory();

        assertThat(rff.getSpecificClass(), equalTo(Row.class));
        // If it has never been used, there is no schema yet.
        assertThat(rff.getSchema(), nullValue());

        Row exampleRow = mCass.execute("SELECT st_text FROM " + mCass.getTableSrc() + " WHERE key1 = 'example'").one();
        IndexedRecord ir = rff.convertToAvro(exampleRow);
        assertThat(rff.getSchema(), not(nullValue()));

        assertThat(ir.get(0), is((Object) "1234567"));
        assertThat(ir.getSchema().toString().replace('"', '\''),
                is("{'type':'record','name':'example_src','namespace':'rowfacadefactorytest'," //
                        + "'fields':[" //
                        + "{'name':'st_text'," //
                        + "'type':[{'type':'string','cassandra.datatype.name':'VARCHAR'},'null']}" //
                        + "]}"));
    }

    /**
     * Ensures that all of the records generated in the example set are valid with the inferred schema.
     */
    @Test
    public void testValidateExampleData() {
        RowFacadeFactory rff = new RowFacadeFactory();

        assertThat(rff.getSpecificClass(), equalTo(Row.class));
        // If it has never been used, there is no schema yet.
        assertThat(rff.getSchema(), nullValue());

        // Collect all of the results from the example table.
        ResultSet rs = mCass.execute("SELECT * FROM " + mCass.getTableSrc());
        for (Row exampleRow = rs.one(); exampleRow != null; exampleRow = rs.one()) {
            IndexedRecord ir = rff.convertToAvro(exampleRow);
            ReflectData.get().validate(ir.getSchema(), ir);
        }
    }

    // /**
    // * Ensures that all of the records generated in the example set are valid
    // with the inferred schema.
    // */
    // @Test
    // public void testValidExampleData() {
    // RowFacadeFactory rff = new RowFacadeFactory();
    //
    // assertThat(rff.getSpecificClass(), equalTo(Row.class));
    // // If it has never been used, there is no schema yet.
    // assertThat(rff.getSchema(), nullValue());
    //
    // // Collect all of the results from the example table.
    // Integer keyPos = null;
    // HashMap<String, IndexedRecord> exampleRows = new HashMap<>();
    // ResultSet rs = mCass.execute("SELECT * FROM " + mCass.getTableSrc());
    // for (Row exampleRow = rs.one(); exampleRow != null; exampleRow = rs.one())
    // {
    // IndexedRecord ir = rff.createFacade(exampleRow);
    // ReflectData.get().validate(ir.getSchema(), ir);
    // if (keyPos == null) {
    // keyPos = ir.getSchema().getField("key1").pos();
    // }
    // exampleRows.put((String) ir.get(keyPos), ir);
    // }
    // }

}
