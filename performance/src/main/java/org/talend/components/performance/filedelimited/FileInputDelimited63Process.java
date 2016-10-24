package org.talend.components.performance.filedelimited;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import routines.system.TDieException;

public class FileInputDelimited63Process {

    public void tFileInputDelimited_2Process(final java.util.Map<String, Object> globalMap) throws TalendException {
        globalMap.put("tFileInputDelimited_2_SUBPROCESS_STATE", 0);

        String iterateId = "";

        String currentComponent = "";
        java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

        try {

            String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
            if (true) {

                row1Struct row1 = new row1Struct();

                /**
                 * [tJavaRow_1 begin ] start
                 */

                currentComponent = "tJavaRow_1";

                int tos_count_tJavaRow_1 = 0;

                class BytesLimit65535_tJavaRow_1 {

                    public void limitLog4jByte() throws Exception {

                    }
                }

                new BytesLimit65535_tJavaRow_1().limitLog4jByte();

                int nb_line_tJavaRow_1 = 0;

                /**
                 * [tJavaRow_1 begin ] stop
                 */

                /**
                 * [tFileInputDelimited_2 begin ] start
                 */

                currentComponent = "tFileInputDelimited_2";

                int tos_count_tFileInputDelimited_2 = 0;

                class BytesLimit65535_tFileInputDelimited_2 {

                    public void limitLog4jByte() throws Exception {

                    }
                }

                new BytesLimit65535_tFileInputDelimited_2().limitLog4jByte();

                org.talend.components.api.component.ComponentDefinition def_tFileInputDelimited_2 = new org.talend.components.filedelimited.tfileinputdelimited.TFileInputDelimitedDefinition();

                org.talend.components.filedelimited.tfileinputdelimited.TFileInputDelimitedProperties props_tFileInputDelimited_2 = (org.talend.components.filedelimited.tfileinputdelimited.TFileInputDelimitedProperties) def_tFileInputDelimited_2
                        .createRuntimeProperties();
                props_tFileInputDelimited_2.setValue("uncompress", false);
                props_tFileInputDelimited_2.setValue("dieOnError", false);
                props_tFileInputDelimited_2.setValue("random", false);
                props_tFileInputDelimited_2.setValue("checkFieldsNum", false);
                props_tFileInputDelimited_2.setValue("checkDate", false);
                props_tFileInputDelimited_2.setValue("splitRecord", false);
                props_tFileInputDelimited_2.setValue("enableDecode", false);
                props_tFileInputDelimited_2.setValue("fileName", "D:/tmp/bench/out.csv");
                props_tFileInputDelimited_2.setValue("csvOptions", false);
                props_tFileInputDelimited_2.setValue("rowSeparator", "\n");
                props_tFileInputDelimited_2.setValue("fieldSeparator", ";");
                props_tFileInputDelimited_2.setValue("advancedSeparator", false);
                props_tFileInputDelimited_2.setValue("header", 0);
                props_tFileInputDelimited_2.setValue("footer", 0);
                props_tFileInputDelimited_2.setValue("removeEmptyRow", false);
                props_tFileInputDelimited_2.trimColumns.setValue("trimAll", false);
                java.util.List<Object> tFileInputDelimited_2_trimColumns_trimTable_trim = new java.util.ArrayList<Object>();
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                tFileInputDelimited_2_trimColumns_trimTable_trim.add(false);
                ((org.talend.daikon.properties.Properties) props_tFileInputDelimited_2.trimColumns.trimTable).setValue("trim",
                        tFileInputDelimited_2_trimColumns_trimTable_trim);
                java.util.List<Object> tFileInputDelimited_2_trimColumns_trimTable_columnName = new java.util.ArrayList<Object>();
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column1");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column2");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column3");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column4");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column5");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column6");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column7");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column8");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column9");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column10");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column11");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column12");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column13");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column14");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column15");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column16");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column17");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column18");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column19");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column20");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column21");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column22");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column23");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column24");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column25");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column26");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column27");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column28");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column29");
                tFileInputDelimited_2_trimColumns_trimTable_columnName.add("Column30");
                ((org.talend.daikon.properties.Properties) props_tFileInputDelimited_2.trimColumns.trimTable)
                        .setValue("columnName", tFileInputDelimited_2_trimColumns_trimTable_columnName);
                java.util.List<Object> tFileInputDelimited_2_decodeTable_decode = new java.util.ArrayList<Object>();
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                tFileInputDelimited_2_decodeTable_decode.add(false);
                ((org.talend.daikon.properties.Properties) props_tFileInputDelimited_2.decodeTable).setValue("decode",
                        tFileInputDelimited_2_decodeTable_decode);
                java.util.List<Object> tFileInputDelimited_2_decodeTable_columnName = new java.util.ArrayList<Object>();
                tFileInputDelimited_2_decodeTable_columnName.add("Column1");
                tFileInputDelimited_2_decodeTable_columnName.add("Column2");
                tFileInputDelimited_2_decodeTable_columnName.add("Column3");
                tFileInputDelimited_2_decodeTable_columnName.add("Column4");
                tFileInputDelimited_2_decodeTable_columnName.add("Column5");
                tFileInputDelimited_2_decodeTable_columnName.add("Column6");
                tFileInputDelimited_2_decodeTable_columnName.add("Column7");
                tFileInputDelimited_2_decodeTable_columnName.add("Column8");
                tFileInputDelimited_2_decodeTable_columnName.add("Column9");
                tFileInputDelimited_2_decodeTable_columnName.add("Column10");
                tFileInputDelimited_2_decodeTable_columnName.add("Column11");
                tFileInputDelimited_2_decodeTable_columnName.add("Column12");
                tFileInputDelimited_2_decodeTable_columnName.add("Column13");
                tFileInputDelimited_2_decodeTable_columnName.add("Column14");
                tFileInputDelimited_2_decodeTable_columnName.add("Column15");
                tFileInputDelimited_2_decodeTable_columnName.add("Column16");
                tFileInputDelimited_2_decodeTable_columnName.add("Column17");
                tFileInputDelimited_2_decodeTable_columnName.add("Column18");
                tFileInputDelimited_2_decodeTable_columnName.add("Column19");
                tFileInputDelimited_2_decodeTable_columnName.add("Column20");
                tFileInputDelimited_2_decodeTable_columnName.add("Column21");
                tFileInputDelimited_2_decodeTable_columnName.add("Column22");
                tFileInputDelimited_2_decodeTable_columnName.add("Column23");
                tFileInputDelimited_2_decodeTable_columnName.add("Column24");
                tFileInputDelimited_2_decodeTable_columnName.add("Column25");
                tFileInputDelimited_2_decodeTable_columnName.add("Column26");
                tFileInputDelimited_2_decodeTable_columnName.add("Column27");
                tFileInputDelimited_2_decodeTable_columnName.add("Column28");
                tFileInputDelimited_2_decodeTable_columnName.add("Column29");
                tFileInputDelimited_2_decodeTable_columnName.add("Column30");
                ((org.talend.daikon.properties.Properties) props_tFileInputDelimited_2.decodeTable).setValue("columnName",
                        tFileInputDelimited_2_decodeTable_columnName);
                props_tFileInputDelimited_2.schemaFlow.setValue("schema", new org.apache.avro.Schema.Parser().parse(
                        "{\"type\":\"record\",\"name\":\"metadata\",\"fields\":[{\"name\":\"Column1\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column1\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column1\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column2\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column2\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column2\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column3\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column3\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column3\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column4\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column4\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column4\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column5\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column5\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column5\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column6\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column6\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column6\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column7\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column7\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column7\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column8\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column8\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column8\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column9\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column9\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column9\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column10\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column10\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column10\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column11\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column11\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column11\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column12\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column12\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column12\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column13\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column13\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column13\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column14\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column14\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column14\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column15\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column15\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column15\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column16\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column16\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column16\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column17\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column17\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column17\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column18\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column18\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column18\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column19\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column19\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column19\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column20\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column20\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column20\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column21\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column21\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column21\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column22\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column22\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column22\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column23\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column23\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column23\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column24\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column24\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column24\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column25\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column25\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column25\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column26\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column26\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column26\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column27\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column27\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column27\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column28\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column28\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column28\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column29\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column29\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column29\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column30\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column30\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column30\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"}],\"di.table.name\":\"tFileInputDelimited_2\",\"di.table.label\":\"metadata\"}"));
                props_tFileInputDelimited_2.schemaReject.setValue("schema", new org.apache.avro.Schema.Parser().parse(
                        "{\"type\":\"record\",\"name\":\"rejectOutput\",\"fields\":[{\"name\":\"Column1\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column1\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column1\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column2\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column2\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column2\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column3\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column3\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column3\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column4\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column4\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column4\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column5\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column5\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column5\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column6\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column6\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column6\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column7\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column7\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column7\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column8\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column8\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column8\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column9\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column9\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column9\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column10\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column10\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column10\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column11\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column11\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column11\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column12\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column12\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column12\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column13\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column13\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column13\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column14\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column14\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column14\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column15\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column15\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column15\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column16\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column16\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column16\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column17\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column17\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column17\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column18\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column18\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column18\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column19\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column19\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column19\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column20\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column20\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column20\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column21\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column21\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column21\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column22\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column22\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column22\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column23\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column23\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column23\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column24\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column24\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column24\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column25\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column25\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column25\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column26\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column26\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column26\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column27\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column27\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column27\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column28\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column28\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column28\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column29\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column29\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column29\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column30\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column30\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column30\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"errorCode\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"},{\"name\":\"errorMessage\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"}],\"di.table.name\":\"tFileInputDelimited_2\",\"di.table.label\":\"metadata\"}"));
                props_tFileInputDelimited_2.main.setValue("schema", new org.apache.avro.Schema.Parser().parse(
                        "{\"type\":\"record\",\"name\":\"metadata\",\"fields\":[{\"name\":\"Column1\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column1\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column1\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column2\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column2\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column2\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column3\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column3\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column3\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column4\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column4\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column4\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column5\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column5\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column5\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column6\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column6\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column6\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column7\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column7\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column7\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column8\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column8\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column8\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column9\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column9\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column9\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column10\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column10\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column10\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column11\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column11\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column11\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column12\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column12\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column12\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column13\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column13\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column13\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column14\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column14\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column14\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column15\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column15\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column15\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column16\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column16\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column16\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column17\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column17\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column17\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column18\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column18\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column18\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column19\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column19\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column19\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column20\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column20\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column20\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column21\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column21\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column21\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column22\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column22\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column22\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column23\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column23\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column23\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column24\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column24\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column24\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column25\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column25\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column25\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column26\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column26\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column26\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column27\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column27\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column27\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column28\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column28\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column28\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column29\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column29\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column29\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column30\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column30\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column30\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"}],\"di.table.name\":\"tFileInputDelimited_2\",\"di.table.label\":\"metadata\"}"));
                props_tFileInputDelimited_2.encoding.setValue("encodingType", "CUSTOM");
                props_tFileInputDelimited_2.encoding.setValue("customEncoding", "US-ASCII");
                org.talend.components.api.container.RuntimeContainer container_tFileInputDelimited_2 = new org.talend.components.api.container.RuntimeContainer() {

                    public Object getComponentData(String componentId, String key) {
                        return globalMap.get(componentId + "_" + key);
                    }

                    public void setComponentData(String componentId, String key, Object data) {
                        globalMap.put(componentId + "_" + key, data);
                    }

                    public String getCurrentComponentId() {
                        return "tFileInputDelimited_2";
                    }

                    public Object getGlobalData(String key) {
                        return globalMap.get(key);
                    }
                };

                int nb_line_tFileInputDelimited_2 = 0;

                org.talend.components.api.component.ConnectorTopology topology_tFileInputDelimited_2 = null;
                topology_tFileInputDelimited_2 = org.talend.components.api.component.ConnectorTopology.OUTGOING;

                org.talend.daikon.runtime.RuntimeInfo runtime_info_tFileInputDelimited_2 = def_tFileInputDelimited_2
                        .getRuntimeInfo(props_tFileInputDelimited_2, topology_tFileInputDelimited_2);
                java.util.Set<org.talend.components.api.component.ConnectorTopology> supported_connector_topologies_tFileInputDelimited_2 = def_tFileInputDelimited_2
                        .getSupportedConnectorTopologies();

                org.talend.components.api.component.runtime.SourceOrSink sourceOrSink_tFileInputDelimited_2 = (org.talend.components.api.component.runtime.SourceOrSink) (Class
                        .forName(runtime_info_tFileInputDelimited_2.getRuntimeClassName()).newInstance());
                sourceOrSink_tFileInputDelimited_2.initialize(container_tFileInputDelimited_2, props_tFileInputDelimited_2);
                org.talend.daikon.properties.ValidationResult vr_tFileInputDelimited_2 = sourceOrSink_tFileInputDelimited_2
                        .validate(container_tFileInputDelimited_2);
                if (vr_tFileInputDelimited_2.getStatus() == org.talend.daikon.properties.ValidationResult.Result.ERROR) {
                    throw new RuntimeException(vr_tFileInputDelimited_2.getMessage());
                }

                org.talend.components.api.component.runtime.Source source_tFileInputDelimited_2 = (org.talend.components.api.component.runtime.Source) sourceOrSink_tFileInputDelimited_2;
                org.talend.components.api.component.runtime.Reader reader_tFileInputDelimited_2 = source_tFileInputDelimited_2
                        .createReader(container_tFileInputDelimited_2);

                boolean multi_output_is_allowed_tFileInputDelimited_2 = false;
                org.talend.components.api.component.Connector c_tFileInputDelimited_2 = null;
                for (org.talend.components.api.component.Connector currentConnector : props_tFileInputDelimited_2
                        .getAvailableConnectors(null, true)) {
                    if (currentConnector.getName().equals("MAIN")) {
                        c_tFileInputDelimited_2 = currentConnector;
                    }

                    if (currentConnector.getName().equals("REJECT")) {// it's
                                                                      // better
                                                                      // to
                                                                      // move
                                                                      // the
                                                                      // code
                                                                      // to
                                                                      // javajet
                        multi_output_is_allowed_tFileInputDelimited_2 = true;
                    }
                }
                org.apache.avro.Schema schema_tFileInputDelimited_2 = props_tFileInputDelimited_2
                        .getSchema(c_tFileInputDelimited_2, true);

                org.talend.daikon.di.DiOutgoingSchemaEnforcer current_tFileInputDelimited_2 = new org.talend.daikon.di.DiOutgoingSchemaEnforcer(
                        schema_tFileInputDelimited_2, false);

                // Create a reusable factory that converts the output of the
                // reader to an IndexedRecord.
                org.talend.daikon.avro.converter.IndexedRecordConverter<Object, ? extends org.apache.avro.generic.IndexedRecord> factory_tFileInputDelimited_2 = null;

                // Iterate through the incoming data.
                boolean available_tFileInputDelimited_2 = reader_tFileInputDelimited_2.start();

                resourceMap.put("reader_tFileInputDelimited_2", reader_tFileInputDelimited_2);

                for (; available_tFileInputDelimited_2; available_tFileInputDelimited_2 = reader_tFileInputDelimited_2
                        .advance()) {
                    nb_line_tFileInputDelimited_2++;

                    if (multi_output_is_allowed_tFileInputDelimited_2) {
                        row1 = null;

                    }

                    try {
                        Object data_tFileInputDelimited_2 = reader_tFileInputDelimited_2.getCurrent();

                        if (multi_output_is_allowed_tFileInputDelimited_2) {
                            row1 = new row1Struct();
                        }

                        // Construct the factory once when the first data
                        // arrives.
                        if (factory_tFileInputDelimited_2 == null) {
                            factory_tFileInputDelimited_2 = (org.talend.daikon.avro.converter.IndexedRecordConverter<Object, ? extends org.apache.avro.generic.IndexedRecord>) new org.talend.daikon.avro.AvroRegistry()
                                    .createIndexedRecordConverter(data_tFileInputDelimited_2.getClass());
                        }

                        // Enforce the outgoing schema on the input.
                        current_tFileInputDelimited_2
                                .setWrapped(factory_tFileInputDelimited_2.convertToAvro(data_tFileInputDelimited_2));
                        if (current_tFileInputDelimited_2.get(0) == null) {
                            row1.Column1 = null;
                        } else {
                            row1.Column1 = String.valueOf(current_tFileInputDelimited_2.get(0));
                        }
                        if (current_tFileInputDelimited_2.get(1) == null) {
                            row1.Column2 = null;
                        } else {
                            row1.Column2 = String.valueOf(current_tFileInputDelimited_2.get(1));
                        }
                        if (current_tFileInputDelimited_2.get(2) == null) {
                            row1.Column3 = null;
                        } else {
                            row1.Column3 = String.valueOf(current_tFileInputDelimited_2.get(2));
                        }
                        if (current_tFileInputDelimited_2.get(3) == null) {
                            row1.Column4 = null;
                        } else {
                            row1.Column4 = String.valueOf(current_tFileInputDelimited_2.get(3));
                        }
                        if (current_tFileInputDelimited_2.get(4) == null) {
                            row1.Column5 = null;
                        } else {
                            row1.Column5 = String.valueOf(current_tFileInputDelimited_2.get(4));
                        }
                        if (current_tFileInputDelimited_2.get(5) == null) {
                            row1.Column6 = null;
                        } else {
                            row1.Column6 = String.valueOf(current_tFileInputDelimited_2.get(5));
                        }
                        if (current_tFileInputDelimited_2.get(6) == null) {
                            row1.Column7 = null;
                        } else {
                            row1.Column7 = String.valueOf(current_tFileInputDelimited_2.get(6));
                        }
                        if (current_tFileInputDelimited_2.get(7) == null) {
                            row1.Column8 = null;
                        } else {
                            row1.Column8 = String.valueOf(current_tFileInputDelimited_2.get(7));
                        }
                        if (current_tFileInputDelimited_2.get(8) == null) {
                            row1.Column9 = null;
                        } else {
                            row1.Column9 = String.valueOf(current_tFileInputDelimited_2.get(8));
                        }
                        if (current_tFileInputDelimited_2.get(9) == null) {
                            row1.Column10 = null;
                        } else {
                            row1.Column10 = String.valueOf(current_tFileInputDelimited_2.get(9));
                        }
                        if (current_tFileInputDelimited_2.get(10) == null) {
                            row1.Column11 = null;
                        } else {
                            row1.Column11 = String.valueOf(current_tFileInputDelimited_2.get(10));
                        }
                        if (current_tFileInputDelimited_2.get(11) == null) {
                            row1.Column12 = null;
                        } else {
                            row1.Column12 = String.valueOf(current_tFileInputDelimited_2.get(11));
                        }
                        if (current_tFileInputDelimited_2.get(12) == null) {
                            row1.Column13 = null;
                        } else {
                            row1.Column13 = String.valueOf(current_tFileInputDelimited_2.get(12));
                        }
                        if (current_tFileInputDelimited_2.get(13) == null) {
                            row1.Column14 = null;
                        } else {
                            row1.Column14 = String.valueOf(current_tFileInputDelimited_2.get(13));
                        }
                        if (current_tFileInputDelimited_2.get(14) == null) {
                            row1.Column15 = null;
                        } else {
                            row1.Column15 = String.valueOf(current_tFileInputDelimited_2.get(14));
                        }
                        if (current_tFileInputDelimited_2.get(15) == null) {
                            row1.Column16 = null;
                        } else {
                            row1.Column16 = String.valueOf(current_tFileInputDelimited_2.get(15));
                        }
                        if (current_tFileInputDelimited_2.get(16) == null) {
                            row1.Column17 = null;
                        } else {
                            row1.Column17 = String.valueOf(current_tFileInputDelimited_2.get(16));
                        }
                        if (current_tFileInputDelimited_2.get(17) == null) {
                            row1.Column18 = null;
                        } else {
                            row1.Column18 = String.valueOf(current_tFileInputDelimited_2.get(17));
                        }
                        if (current_tFileInputDelimited_2.get(18) == null) {
                            row1.Column19 = null;
                        } else {
                            row1.Column19 = String.valueOf(current_tFileInputDelimited_2.get(18));
                        }
                        if (current_tFileInputDelimited_2.get(19) == null) {
                            row1.Column20 = null;
                        } else {
                            row1.Column20 = String.valueOf(current_tFileInputDelimited_2.get(19));
                        }
                        if (current_tFileInputDelimited_2.get(20) == null) {
                            row1.Column21 = null;
                        } else {
                            row1.Column21 = String.valueOf(current_tFileInputDelimited_2.get(20));
                        }
                        if (current_tFileInputDelimited_2.get(21) == null) {
                            row1.Column22 = null;
                        } else {
                            row1.Column22 = String.valueOf(current_tFileInputDelimited_2.get(21));
                        }
                        if (current_tFileInputDelimited_2.get(22) == null) {
                            row1.Column23 = null;
                        } else {
                            row1.Column23 = String.valueOf(current_tFileInputDelimited_2.get(22));
                        }
                        if (current_tFileInputDelimited_2.get(23) == null) {
                            row1.Column24 = null;
                        } else {
                            row1.Column24 = String.valueOf(current_tFileInputDelimited_2.get(23));
                        }
                        if (current_tFileInputDelimited_2.get(24) == null) {
                            row1.Column25 = null;
                        } else {
                            row1.Column25 = String.valueOf(current_tFileInputDelimited_2.get(24));
                        }
                        if (current_tFileInputDelimited_2.get(25) == null) {
                            row1.Column26 = null;
                        } else {
                            row1.Column26 = String.valueOf(current_tFileInputDelimited_2.get(25));
                        }
                        if (current_tFileInputDelimited_2.get(26) == null) {
                            row1.Column27 = null;
                        } else {
                            row1.Column27 = String.valueOf(current_tFileInputDelimited_2.get(26));
                        }
                        if (current_tFileInputDelimited_2.get(27) == null) {
                            row1.Column28 = null;
                        } else {
                            row1.Column28 = String.valueOf(current_tFileInputDelimited_2.get(27));
                        }
                        if (current_tFileInputDelimited_2.get(28) == null) {
                            row1.Column29 = null;
                        } else {
                            row1.Column29 = String.valueOf(current_tFileInputDelimited_2.get(28));
                        }
                        if (current_tFileInputDelimited_2.get(29) == null) {
                            row1.Column30 = null;
                        } else {
                            row1.Column30 = String.valueOf(current_tFileInputDelimited_2.get(29));
                        }
                    } catch (org.talend.components.api.exception.DataRejectException e_tFileInputDelimited_2) {
                        java.util.Map<String, Object> info_tFileInputDelimited_2 = e_tFileInputDelimited_2.getRejectInfo();
                        // TODO use a method instead of getting method by the
                        // special key "error"
                        String errorMessage_tFileInputDelimited_2 = "Row " + nb_line_tFileInputDelimited_2 + ":"
                                + info_tFileInputDelimited_2.get("error");
                        System.err.println(errorMessage_tFileInputDelimited_2);
                    }

                    /**
                     * [tFileInputDelimited_2 begin ] stop
                     */

                    /**
                     * [tFileInputDelimited_2 main ] start
                     */

                    currentComponent = "tFileInputDelimited_2";

                    tos_count_tFileInputDelimited_2++;

                    /**
                     * [tFileInputDelimited_2 main ] stop
                     */
                    // Start of branch "row1"
                    if (row1 != null) {

                        /**
                         * [tJavaRow_1 main ] start
                         */

                        currentComponent = "tJavaRow_1";

                        // code sample:
                        //
                        // multiply by 2 the row identifier
                        // output_row.id = row1.id * 2;
                        //
                        // lowercase the name
                        // output_row.name = row1.name.toLowerCase();

                        nb_line_tJavaRow_1++;

                        tos_count_tJavaRow_1++;

                        /**
                         * [tJavaRow_1 main ] stop
                         */

                    } // End of branch "row1"

                    /**
                     * [tFileInputDelimited_2 end ] start
                     */

                    currentComponent = "tFileInputDelimited_2";

                    // end of generic

                    resourceMap.put("finish_tFileInputDelimited_2", Boolean.TRUE);

                } // while
                reader_tFileInputDelimited_2.close();
                final java.util.Map<String, Object> resultMap_tFileInputDelimited_2 = reader_tFileInputDelimited_2
                        .getReturnValues();
                if (resultMap_tFileInputDelimited_2 != null) {
                    for (java.util.Map.Entry<String, Object> entry_tFileInputDelimited_2 : resultMap_tFileInputDelimited_2
                            .entrySet()) {
                        switch (entry_tFileInputDelimited_2.getKey()) {
                        case org.talend.components.api.component.ComponentDefinition.RETURN_ERROR_MESSAGE:
                            container_tFileInputDelimited_2.setComponentData("tFileInputDelimited_2", "ERROR_MESSAGE",
                                    entry_tFileInputDelimited_2.getValue());
                            break;
                        case org.talend.components.api.component.ComponentDefinition.RETURN_TOTAL_RECORD_COUNT:
                            container_tFileInputDelimited_2.setComponentData("tFileInputDelimited_2", "NB_LINE",
                                    entry_tFileInputDelimited_2.getValue());
                            break;
                        case org.talend.components.api.component.ComponentDefinition.RETURN_SUCCESS_RECORD_COUNT:
                            container_tFileInputDelimited_2.setComponentData("tFileInputDelimited_2", "NB_SUCCESS",
                                    entry_tFileInputDelimited_2.getValue());
                            break;
                        case org.talend.components.api.component.ComponentDefinition.RETURN_REJECT_RECORD_COUNT:
                            container_tFileInputDelimited_2.setComponentData("tFileInputDelimited_2", "NB_REJECT",
                                    entry_tFileInputDelimited_2.getValue());
                            break;
                        default:
                            StringBuilder studio_key_tFileInputDelimited_2 = new StringBuilder();
                            for (int i_tFileInputDelimited_2 = 0; i_tFileInputDelimited_2 < entry_tFileInputDelimited_2.getKey()
                                    .length(); i_tFileInputDelimited_2++) {
                                char ch_tFileInputDelimited_2 = entry_tFileInputDelimited_2.getKey()
                                        .charAt(i_tFileInputDelimited_2);
                                if (Character.isUpperCase(ch_tFileInputDelimited_2) && i_tFileInputDelimited_2 > 0) {
                                    studio_key_tFileInputDelimited_2.append('_');
                                }
                                studio_key_tFileInputDelimited_2.append(ch_tFileInputDelimited_2);
                            }
                            container_tFileInputDelimited_2.setComponentData("tFileInputDelimited_2",
                                    studio_key_tFileInputDelimited_2.toString().toUpperCase(java.util.Locale.ENGLISH),
                                    entry_tFileInputDelimited_2.getValue());
                            break;
                        }
                    }
                }

                /**
                 * [tFileInputDelimited_2 end ] stop
                 */

                /**
                 * [tJavaRow_1 end ] start
                 */

                currentComponent = "tJavaRow_1";

                globalMap.put("tJavaRow_1_NB_LINE", nb_line_tJavaRow_1);

                /**
                 * [tJavaRow_1 end ] stop
                 */

            } // end the resume

        } catch (java.lang.Exception e) {

            TalendException te = new TalendException(e, currentComponent, globalMap);

            throw te;
        } catch (java.lang.Error error) {

            throw error;
        } finally {

            try {

                /**
                 * [tFileInputDelimited_2 finally ] start
                 */

                currentComponent = "tFileInputDelimited_2";

                // finally of generic

                if (resourceMap.get("finish_tFileInputDelimited_2") == null) {
                    if (resourceMap.get("reader_tFileInputDelimited_2") != null) {
                        try {
                            ((org.talend.components.api.component.runtime.Reader) resourceMap.get("reader_tFileInputDelimited_2"))
                                    .close();
                        } catch (java.io.IOException e_tFileInputDelimited_2) {
                            String errorMessage_tFileInputDelimited_2 = "failed to release the resource in tFileInputDelimited_2 :"
                                    + e_tFileInputDelimited_2.getMessage();
                            System.err.println(errorMessage_tFileInputDelimited_2);
                        }
                    }
                }

                /**
                 * [tFileInputDelimited_2 finally ] stop
                 */

                /**
                 * [tJavaRow_1 finally ] start
                 */

                currentComponent = "tJavaRow_1";

                /**
                 * [tJavaRow_1 finally ] stop
                 */

            } catch (java.lang.Exception e) {
                // ignore
            } catch (java.lang.Error error) {
                // ignore
            }
            resourceMap = null;
        }

        globalMap.put("tFileInputDelimited_2_SUBPROCESS_STATE", 1);
    }

    private class TalendException extends Exception {

        private static final long serialVersionUID = 1L;

        private java.util.Map<String, Object> globalMap = null;

        private Exception e = null;

        private String currentComponent = null;

        private String virtualComponentName = null;

        public void setVirtualComponentName(String virtualComponentName) {
            this.virtualComponentName = virtualComponentName;
        }

        private TalendException(Exception e, String errorComponent, final java.util.Map<String, Object> globalMap) {
            this.currentComponent = errorComponent;
            this.globalMap = globalMap;
            this.e = e;
        }

        public Exception getException() {
            return this.e;
        }

        public String getCurrentComponent() {
            return this.currentComponent;
        }

        public String getExceptionCauseMessage(Exception e) {
            Throwable cause = e;
            String message = null;
            int i = 10;
            while (null != cause && 0 < i--) {
                message = cause.getMessage();
                if (null == message) {
                    cause = cause.getCause();
                } else {
                    break;
                }
            }
            if (null == message) {
                message = e.getClass().getName();
            }
            return message;
        }

        @Override
        public void printStackTrace() {
            if (!(e instanceof TalendException || e instanceof TDieException)) {
                if (virtualComponentName != null && currentComponent.indexOf(virtualComponentName + "_") == 0) {
                    globalMap.put(virtualComponentName + "_ERROR_MESSAGE", getExceptionCauseMessage(e));
                }
                globalMap.put(currentComponent + "_ERROR_MESSAGE", getExceptionCauseMessage(e));
                System.err.println("Exception in component " + currentComponent);
            }
            if (!(e instanceof TDieException)) {
                if (e instanceof TalendException) {
                    e.printStackTrace();
                } else {
                    e.printStackTrace();
                }
            }
            if (!(e instanceof TalendException)) {
                try {
                    for (java.lang.reflect.Method m : this.getClass().getEnclosingClass().getMethods()) {
                        if (m.getName().compareTo(currentComponent + "_error") == 0) {
                            break;
                        }
                    }

                    if (!(e instanceof TDieException)) {
                    }
                } catch (Exception e) {
                    this.e.printStackTrace();
                }
            }
        }
    }

    public static class row1Struct implements routines.system.IPersistableRow<row1Struct> {

        final static byte[] commonByteArrayLock_LOCAL_PROJECT_fileInputDelimited = new byte[0];

        static byte[] commonByteArray_LOCAL_PROJECT_fileInputDelimited = new byte[0];

        public String Column1;

        public String getColumn1() {
            return this.Column1;
        }

        public String Column2;

        public String getColumn2() {
            return this.Column2;
        }

        public String Column3;

        public String getColumn3() {
            return this.Column3;
        }

        public String Column4;

        public String getColumn4() {
            return this.Column4;
        }

        public String Column5;

        public String getColumn5() {
            return this.Column5;
        }

        public String Column6;

        public String getColumn6() {
            return this.Column6;
        }

        public String Column7;

        public String getColumn7() {
            return this.Column7;
        }

        public String Column8;

        public String getColumn8() {
            return this.Column8;
        }

        public String Column9;

        public String getColumn9() {
            return this.Column9;
        }

        public String Column10;

        public String getColumn10() {
            return this.Column10;
        }

        public String Column11;

        public String getColumn11() {
            return this.Column11;
        }

        public String Column12;

        public String getColumn12() {
            return this.Column12;
        }

        public String Column13;

        public String getColumn13() {
            return this.Column13;
        }

        public String Column14;

        public String getColumn14() {
            return this.Column14;
        }

        public String Column15;

        public String getColumn15() {
            return this.Column15;
        }

        public String Column16;

        public String getColumn16() {
            return this.Column16;
        }

        public String Column17;

        public String getColumn17() {
            return this.Column17;
        }

        public String Column18;

        public String getColumn18() {
            return this.Column18;
        }

        public String Column19;

        public String getColumn19() {
            return this.Column19;
        }

        public String Column20;

        public String getColumn20() {
            return this.Column20;
        }

        public String Column21;

        public String getColumn21() {
            return this.Column21;
        }

        public String Column22;

        public String getColumn22() {
            return this.Column22;
        }

        public String Column23;

        public String getColumn23() {
            return this.Column23;
        }

        public String Column24;

        public String getColumn24() {
            return this.Column24;
        }

        public String Column25;

        public String getColumn25() {
            return this.Column25;
        }

        public String Column26;

        public String getColumn26() {
            return this.Column26;
        }

        public String Column27;

        public String getColumn27() {
            return this.Column27;
        }

        public String Column28;

        public String getColumn28() {
            return this.Column28;
        }

        public String Column29;

        public String getColumn29() {
            return this.Column29;
        }

        public String Column30;

        public String getColumn30() {
            return this.Column30;
        }

        private String readString(ObjectInputStream dis) throws IOException {
            String strReturn = null;
            int length = 0;
            length = dis.readInt();
            if (length == -1) {
                strReturn = null;
            } else {
                if (length > commonByteArray_LOCAL_PROJECT_fileInputDelimited.length) {
                    if (length < 1024 && commonByteArray_LOCAL_PROJECT_fileInputDelimited.length == 0) {
                        commonByteArray_LOCAL_PROJECT_fileInputDelimited = new byte[1024];
                    } else {
                        commonByteArray_LOCAL_PROJECT_fileInputDelimited = new byte[2 * length];
                    }
                }
                dis.readFully(commonByteArray_LOCAL_PROJECT_fileInputDelimited, 0, length);
                strReturn = new String(commonByteArray_LOCAL_PROJECT_fileInputDelimited, 0, length, "UTF8");
            }
            return strReturn;
        }

        private void writeString(String str, ObjectOutputStream dos) throws IOException {
            if (str == null) {
                dos.writeInt(-1);
            } else {
                byte[] byteArray = str.getBytes("UTF8");
                dos.writeInt(byteArray.length);
                dos.write(byteArray);
            }
        }

        public void readData(ObjectInputStream dis) {

            synchronized (commonByteArrayLock_LOCAL_PROJECT_fileInputDelimited) {

                try {

                    int length = 0;

                    this.Column1 = readString(dis);

                    this.Column2 = readString(dis);

                    this.Column3 = readString(dis);

                    this.Column4 = readString(dis);

                    this.Column5 = readString(dis);

                    this.Column6 = readString(dis);

                    this.Column7 = readString(dis);

                    this.Column8 = readString(dis);

                    this.Column9 = readString(dis);

                    this.Column10 = readString(dis);

                    this.Column11 = readString(dis);

                    this.Column12 = readString(dis);

                    this.Column13 = readString(dis);

                    this.Column14 = readString(dis);

                    this.Column15 = readString(dis);

                    this.Column16 = readString(dis);

                    this.Column17 = readString(dis);

                    this.Column18 = readString(dis);

                    this.Column19 = readString(dis);

                    this.Column20 = readString(dis);

                    this.Column21 = readString(dis);

                    this.Column22 = readString(dis);

                    this.Column23 = readString(dis);

                    this.Column24 = readString(dis);

                    this.Column25 = readString(dis);

                    this.Column26 = readString(dis);

                    this.Column27 = readString(dis);

                    this.Column28 = readString(dis);

                    this.Column29 = readString(dis);

                    this.Column30 = readString(dis);

                } catch (IOException e) {
                    throw new RuntimeException(e);

                }

            }

        }

        public void writeData(ObjectOutputStream dos) {
            try {

                // String

                writeString(this.Column1, dos);

                // String

                writeString(this.Column2, dos);

                // String

                writeString(this.Column3, dos);

                // String

                writeString(this.Column4, dos);

                // String

                writeString(this.Column5, dos);

                // String

                writeString(this.Column6, dos);

                // String

                writeString(this.Column7, dos);

                // String

                writeString(this.Column8, dos);

                // String

                writeString(this.Column9, dos);

                // String

                writeString(this.Column10, dos);

                // String

                writeString(this.Column11, dos);

                // String

                writeString(this.Column12, dos);

                // String

                writeString(this.Column13, dos);

                // String

                writeString(this.Column14, dos);

                // String

                writeString(this.Column15, dos);

                // String

                writeString(this.Column16, dos);

                // String

                writeString(this.Column17, dos);

                // String

                writeString(this.Column18, dos);

                // String

                writeString(this.Column19, dos);

                // String

                writeString(this.Column20, dos);

                // String

                writeString(this.Column21, dos);

                // String

                writeString(this.Column22, dos);

                // String

                writeString(this.Column23, dos);

                // String

                writeString(this.Column24, dos);

                // String

                writeString(this.Column25, dos);

                // String

                writeString(this.Column26, dos);

                // String

                writeString(this.Column27, dos);

                // String

                writeString(this.Column28, dos);

                // String

                writeString(this.Column29, dos);

                // String

                writeString(this.Column30, dos);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        public String toString() {

            StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            sb.append("[");
            sb.append("Column1=" + Column1);
            sb.append(",Column2=" + Column2);
            sb.append(",Column3=" + Column3);
            sb.append(",Column4=" + Column4);
            sb.append(",Column5=" + Column5);
            sb.append(",Column6=" + Column6);
            sb.append(",Column7=" + Column7);
            sb.append(",Column8=" + Column8);
            sb.append(",Column9=" + Column9);
            sb.append(",Column10=" + Column10);
            sb.append(",Column11=" + Column11);
            sb.append(",Column12=" + Column12);
            sb.append(",Column13=" + Column13);
            sb.append(",Column14=" + Column14);
            sb.append(",Column15=" + Column15);
            sb.append(",Column16=" + Column16);
            sb.append(",Column17=" + Column17);
            sb.append(",Column18=" + Column18);
            sb.append(",Column19=" + Column19);
            sb.append(",Column20=" + Column20);
            sb.append(",Column21=" + Column21);
            sb.append(",Column22=" + Column22);
            sb.append(",Column23=" + Column23);
            sb.append(",Column24=" + Column24);
            sb.append(",Column25=" + Column25);
            sb.append(",Column26=" + Column26);
            sb.append(",Column27=" + Column27);
            sb.append(",Column28=" + Column28);
            sb.append(",Column29=" + Column29);
            sb.append(",Column30=" + Column30);
            sb.append("]");

            return sb.toString();
        }

        /**
         * Compare keys
         */
        public int compareTo(row1Struct other) {

            int returnValue = -1;

            return returnValue;
        }

        private int checkNullsAndCompare(Object object1, Object object2) {
            int returnValue = 0;
            if (object1 instanceof Comparable && object2 instanceof Comparable) {
                returnValue = ((Comparable) object1).compareTo(object2);
            } else if (object1 != null && object2 != null) {
                returnValue = compareStrings(object1.toString(), object2.toString());
            } else if (object1 == null && object2 != null) {
                returnValue = 1;
            } else if (object1 != null && object2 == null) {
                returnValue = -1;
            } else {
                returnValue = 0;
            }

            return returnValue;
        }

        private int compareStrings(String string1, String string2) {
            return string1.compareTo(string2);
        }

    }
}
