package org.talend.components.jobs.filedelimited;

import static org.talend.components.jobs.filedelimited.FileOutputDelimitedSettings.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import routines.TalendString;
import routines.system.TDieException;

public class FileOutputDelimitedProcess63 {

    public void tRowGenerator_2Process(final java.util.Map<String, Object> globalMap) throws TalendException {
        globalMap.put("tRowGenerator_2_SUBPROCESS_STATE", 0);

        String iterateId = "";

        String currentComponent = "";
        java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

        try {

            String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
          
            if (true) {

                row2Struct row2 = new row2Struct();

                /**
                 * [tFileOutputDelimited_2 begin ] start
                 */

                currentComponent = "tFileOutputDelimited_2";

                int tos_count_tFileOutputDelimited_2 = 0;

                class BytesLimit65535_tFileOutputDelimited_2 {

                    public void limitLog4jByte() throws Exception {

                    }
                }

                new BytesLimit65535_tFileOutputDelimited_2().limitLog4jByte();

                org.talend.components.api.component.ComponentDefinition def_tFileOutputDelimited_2 = new org.talend.components.filedelimited.tfileoutputdelimited.TFileOutputDelimitedDefinition();

                org.talend.components.filedelimited.tfileoutputdelimited.TFileOutputDelimitedProperties props_tFileOutputDelimited_2 = (org.talend.components.filedelimited.tfileoutputdelimited.TFileOutputDelimitedProperties) def_tFileOutputDelimited_2
                        .createRuntimeProperties();
                props_tFileOutputDelimited_2.setValue("targetIsStream", false);
                props_tFileOutputDelimited_2.setValue("append", false);
                props_tFileOutputDelimited_2.setValue("includeHeader", false);
                props_tFileOutputDelimited_2.setValue("compress", false);
                props_tFileOutputDelimited_2.setValue("creatDirIfNotExist", true);
                props_tFileOutputDelimited_2.setValue("split", false);
                props_tFileOutputDelimited_2.setValue("flushOnRow", false);
                props_tFileOutputDelimited_2.setValue("rowMode", false);
                props_tFileOutputDelimited_2.setValue("deleteEmptyFile", false);
                props_tFileOutputDelimited_2.setValue("fileName", FILE_NAME);
                props_tFileOutputDelimited_2.setValue("csvOptions", false);
                props_tFileOutputDelimited_2.setValue("rowSeparator", "\n");
                props_tFileOutputDelimited_2.setValue("fieldSeparator", ";");
                props_tFileOutputDelimited_2.setValue("advancedSeparator", false);
                props_tFileOutputDelimited_2.setValue("header", 0);
                props_tFileOutputDelimited_2.setValue("footer", 0);
                props_tFileOutputDelimited_2.setValue("removeEmptyRow", true);
                props_tFileOutputDelimited_2.main.setValue("schema", new org.apache.avro.Schema.Parser().parse(
                        "{\"type\":\"record\",\"name\":\"metadata\",\"fields\":[{\"name\":\"Column1\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column1\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column1\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column2\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column2\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column2\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column3\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column3\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column3\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column4\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column4\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column4\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column5\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column5\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column5\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column6\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column6\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column6\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column7\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column7\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column7\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column8\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column8\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column8\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column9\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column9\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column9\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column10\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column10\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column10\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column11\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column11\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column11\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column12\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column12\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column12\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column13\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column13\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column13\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column14\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column14\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column14\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column15\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column15\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column15\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column16\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column16\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column16\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column17\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column17\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column17\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column18\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column18\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column18\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column19\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column19\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column19\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column20\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column20\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column20\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column21\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column21\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column21\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column22\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column22\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column22\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column23\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column23\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column23\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column24\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column24\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column24\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column25\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column25\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column25\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column26\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column26\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column26\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column27\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column27\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column27\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column28\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column28\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column28\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column29\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column29\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column29\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{\"name\":\"Column30\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"Column30\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"6\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"Column30\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"}],\"di.table.name\":\"tFileOutputDelimited_2\",\"di.table.label\":\"metadata\"}"));
                props_tFileOutputDelimited_2.encoding.setValue("encodingType", "CUSTOM");
                props_tFileOutputDelimited_2.encoding.setValue("customEncoding", "US-ASCII");
                org.talend.components.api.container.RuntimeContainer container_tFileOutputDelimited_2 = new org.talend.components.api.container.RuntimeContainer() {

                    public Object getComponentData(String componentId, String key) {
                        return globalMap.get(componentId + "_" + key);
                    }

                    public void setComponentData(String componentId, String key, Object data) {
                        globalMap.put(componentId + "_" + key, data);
                    }

                    public String getCurrentComponentId() {
                        return "tFileOutputDelimited_2";
                    }

                    public Object getGlobalData(String key) {
                        return globalMap.get(key);
                    }
                };

                int nb_line_tFileOutputDelimited_2 = 0;

                org.talend.components.api.component.ConnectorTopology topology_tFileOutputDelimited_2 = null;
                topology_tFileOutputDelimited_2 = org.talend.components.api.component.ConnectorTopology.INCOMING;

                org.talend.daikon.runtime.RuntimeInfo runtime_info_tFileOutputDelimited_2 = def_tFileOutputDelimited_2
                        .getRuntimeInfo(props_tFileOutputDelimited_2, topology_tFileOutputDelimited_2);
                java.util.Set<org.talend.components.api.component.ConnectorTopology> supported_connector_topologies_tFileOutputDelimited_2 = def_tFileOutputDelimited_2
                        .getSupportedConnectorTopologies();

                org.talend.components.api.component.runtime.SourceOrSink sourceOrSink_tFileOutputDelimited_2 = (org.talend.components.api.component.runtime.SourceOrSink) (Class
                        .forName(runtime_info_tFileOutputDelimited_2.getRuntimeClassName()).newInstance());
                sourceOrSink_tFileOutputDelimited_2.initialize(container_tFileOutputDelimited_2, props_tFileOutputDelimited_2);
                org.talend.daikon.properties.ValidationResult vr_tFileOutputDelimited_2 = sourceOrSink_tFileOutputDelimited_2
                        .validate(container_tFileOutputDelimited_2);
                if (vr_tFileOutputDelimited_2.getStatus() == org.talend.daikon.properties.ValidationResult.Result.ERROR) {
                    throw new RuntimeException(vr_tFileOutputDelimited_2.getMessage());
                }

                org.talend.components.api.component.runtime.Sink sink_tFileOutputDelimited_2 = (org.talend.components.api.component.runtime.Sink) sourceOrSink_tFileOutputDelimited_2;
                org.talend.components.api.component.runtime.WriteOperation writeOperation_tFileOutputDelimited_2 = sink_tFileOutputDelimited_2
                        .createWriteOperation();
                writeOperation_tFileOutputDelimited_2.initialize(container_tFileOutputDelimited_2);
                org.talend.components.api.component.runtime.Writer writer_tFileOutputDelimited_2 = writeOperation_tFileOutputDelimited_2
                        .createWriter(container_tFileOutputDelimited_2);
                writer_tFileOutputDelimited_2.open("tFileOutputDelimited_2");

                resourceMap.put("writer_tFileOutputDelimited_2", writer_tFileOutputDelimited_2);

                org.talend.components.api.component.Connector c_tFileOutputDelimited_2 = null;
                for (org.talend.components.api.component.Connector currentConnector : props_tFileOutputDelimited_2
                        .getAvailableConnectors(null, false)) {
                    if (currentConnector.getName().equals("MAIN")) {
                        c_tFileOutputDelimited_2 = currentConnector;
                        break;
                    }
                }
                org.apache.avro.Schema designSchema_tFileOutputDelimited_2 = props_tFileOutputDelimited_2
                        .getSchema(c_tFileOutputDelimited_2, false);
                org.talend.daikon.di.DiIncomingSchemaEnforcer current_tFileOutputDelimited_2 = new org.talend.daikon.di.DiIncomingSchemaEnforcer(
                        designSchema_tFileOutputDelimited_2);

                /**
                 * [tFileOutputDelimited_2 begin ] stop
                 */

                /**
                 * [tRowGenerator_2 begin ] start
                 */

                currentComponent = "tRowGenerator_2";

                int tos_count_tRowGenerator_2 = 0;

                class BytesLimit65535_tRowGenerator_2 {

                    public void limitLog4jByte() throws Exception {

                    }
                }

                new BytesLimit65535_tRowGenerator_2().limitLog4jByte();

                int nb_line_tRowGenerator_2 = 0;
                int nb_max_row_tRowGenerator_2 = ROW_GENERATOR_ROWS;

                class tRowGenerator_2Randomizer {

                    public String getRandomColumn1() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn2() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn3() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn4() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn5() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn6() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn7() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn8() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn9() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn10() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn11() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn12() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn13() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn14() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn15() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn16() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn17() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn18() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn19() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn20() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn21() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn22() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn23() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn24() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn25() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn26() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn27() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn28() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn29() {

                        return TalendString.getAsciiRandomString(6);

                    }

                    public String getRandomColumn30() {

                        return TalendString.getAsciiRandomString(6);

                    }
                }
                tRowGenerator_2Randomizer randtRowGenerator_2 = new tRowGenerator_2Randomizer();

                for (int itRowGenerator_2 = 0; itRowGenerator_2 < nb_max_row_tRowGenerator_2; itRowGenerator_2++) {
                    row2.Column1 = randtRowGenerator_2.getRandomColumn1();
                    row2.Column2 = randtRowGenerator_2.getRandomColumn2();
                    row2.Column3 = randtRowGenerator_2.getRandomColumn3();
                    row2.Column4 = randtRowGenerator_2.getRandomColumn4();
                    row2.Column5 = randtRowGenerator_2.getRandomColumn5();
                    row2.Column6 = randtRowGenerator_2.getRandomColumn6();
                    row2.Column7 = randtRowGenerator_2.getRandomColumn7();
                    row2.Column8 = randtRowGenerator_2.getRandomColumn8();
                    row2.Column9 = randtRowGenerator_2.getRandomColumn9();
                    row2.Column10 = randtRowGenerator_2.getRandomColumn10();
                    row2.Column11 = randtRowGenerator_2.getRandomColumn11();
                    row2.Column12 = randtRowGenerator_2.getRandomColumn12();
                    row2.Column13 = randtRowGenerator_2.getRandomColumn13();
                    row2.Column14 = randtRowGenerator_2.getRandomColumn14();
                    row2.Column15 = randtRowGenerator_2.getRandomColumn15();
                    row2.Column16 = randtRowGenerator_2.getRandomColumn16();
                    row2.Column17 = randtRowGenerator_2.getRandomColumn17();
                    row2.Column18 = randtRowGenerator_2.getRandomColumn18();
                    row2.Column19 = randtRowGenerator_2.getRandomColumn19();
                    row2.Column20 = randtRowGenerator_2.getRandomColumn20();
                    row2.Column21 = randtRowGenerator_2.getRandomColumn21();
                    row2.Column22 = randtRowGenerator_2.getRandomColumn22();
                    row2.Column23 = randtRowGenerator_2.getRandomColumn23();
                    row2.Column24 = randtRowGenerator_2.getRandomColumn24();
                    row2.Column25 = randtRowGenerator_2.getRandomColumn25();
                    row2.Column26 = randtRowGenerator_2.getRandomColumn26();
                    row2.Column27 = randtRowGenerator_2.getRandomColumn27();
                    row2.Column28 = randtRowGenerator_2.getRandomColumn28();
                    row2.Column29 = randtRowGenerator_2.getRandomColumn29();
                    row2.Column30 = randtRowGenerator_2.getRandomColumn30();
                    nb_line_tRowGenerator_2++;

                    /**
                     * [tRowGenerator_2 begin ] stop
                     */

                    /**
                     * [tRowGenerator_2 main ] start
                     */

                    currentComponent = "tRowGenerator_2";

                    tos_count_tRowGenerator_2++;

                    /**
                     * [tRowGenerator_2 main ] stop
                     */

                    /**
                     * [tFileOutputDelimited_2 main ] start
                     */

                    currentComponent = "tFileOutputDelimited_2";

                    current_tFileOutputDelimited_2.put("Column1", row2.Column1);
                    current_tFileOutputDelimited_2.put("Column2", row2.Column2);
                    current_tFileOutputDelimited_2.put("Column3", row2.Column3);
                    current_tFileOutputDelimited_2.put("Column4", row2.Column4);
                    current_tFileOutputDelimited_2.put("Column5", row2.Column5);
                    current_tFileOutputDelimited_2.put("Column6", row2.Column6);
                    current_tFileOutputDelimited_2.put("Column7", row2.Column7);
                    current_tFileOutputDelimited_2.put("Column8", row2.Column8);
                    current_tFileOutputDelimited_2.put("Column9", row2.Column9);
                    current_tFileOutputDelimited_2.put("Column10", row2.Column10);
                    current_tFileOutputDelimited_2.put("Column11", row2.Column11);
                    current_tFileOutputDelimited_2.put("Column12", row2.Column12);
                    current_tFileOutputDelimited_2.put("Column13", row2.Column13);
                    current_tFileOutputDelimited_2.put("Column14", row2.Column14);
                    current_tFileOutputDelimited_2.put("Column15", row2.Column15);
                    current_tFileOutputDelimited_2.put("Column16", row2.Column16);
                    current_tFileOutputDelimited_2.put("Column17", row2.Column17);
                    current_tFileOutputDelimited_2.put("Column18", row2.Column18);
                    current_tFileOutputDelimited_2.put("Column19", row2.Column19);
                    current_tFileOutputDelimited_2.put("Column20", row2.Column20);
                    current_tFileOutputDelimited_2.put("Column21", row2.Column21);
                    current_tFileOutputDelimited_2.put("Column22", row2.Column22);
                    current_tFileOutputDelimited_2.put("Column23", row2.Column23);
                    current_tFileOutputDelimited_2.put("Column24", row2.Column24);
                    current_tFileOutputDelimited_2.put("Column25", row2.Column25);
                    current_tFileOutputDelimited_2.put("Column26", row2.Column26);
                    current_tFileOutputDelimited_2.put("Column27", row2.Column27);
                    current_tFileOutputDelimited_2.put("Column28", row2.Column28);
                    current_tFileOutputDelimited_2.put("Column29", row2.Column29);
                    current_tFileOutputDelimited_2.put("Column30", row2.Column30);
                    Object data_tFileOutputDelimited_2 = current_tFileOutputDelimited_2.createIndexedRecord();

                    writer_tFileOutputDelimited_2.write(data_tFileOutputDelimited_2);

                    nb_line_tFileOutputDelimited_2++;

                    tos_count_tFileOutputDelimited_2++;

                    /**
                     * [tFileOutputDelimited_2 main ] stop
                     */

                    /**
                     * [tRowGenerator_2 end ] start
                     */

                    currentComponent = "tRowGenerator_2";

                }
                globalMap.put("tRowGenerator_2_NB_LINE", nb_line_tRowGenerator_2);

                /**
                 * [tRowGenerator_2 end ] stop
                 */

                /**
                 * [tFileOutputDelimited_2 end ] start
                 */

                currentComponent = "tFileOutputDelimited_2";

                // end of generic

                resourceMap.put("finish_tFileOutputDelimited_2", Boolean.TRUE);

                org.talend.components.api.component.runtime.Result resultObject_tFileOutputDelimited_2 = (org.talend.components.api.component.runtime.Result) writer_tFileOutputDelimited_2
                        .close();
                final java.util.Map<String, Object> resultMap_tFileOutputDelimited_2 = writer_tFileOutputDelimited_2
                        .getWriteOperation()
                        .finalize(java.util.Arrays.<org.talend.components.api.component.runtime.Result> asList(
                                resultObject_tFileOutputDelimited_2), container_tFileOutputDelimited_2);
                if (resultMap_tFileOutputDelimited_2 != null) {
                    for (java.util.Map.Entry<String, Object> entry_tFileOutputDelimited_2 : resultMap_tFileOutputDelimited_2
                            .entrySet()) {
                        switch (entry_tFileOutputDelimited_2.getKey()) {
                        case org.talend.components.api.component.ComponentDefinition.RETURN_ERROR_MESSAGE:
                            container_tFileOutputDelimited_2.setComponentData("tFileOutputDelimited_2", "ERROR_MESSAGE",
                                    entry_tFileOutputDelimited_2.getValue());
                            break;
                        case org.talend.components.api.component.ComponentDefinition.RETURN_TOTAL_RECORD_COUNT:
                            container_tFileOutputDelimited_2.setComponentData("tFileOutputDelimited_2", "NB_LINE",
                                    entry_tFileOutputDelimited_2.getValue());
                            break;
                        case org.talend.components.api.component.ComponentDefinition.RETURN_SUCCESS_RECORD_COUNT:
                            container_tFileOutputDelimited_2.setComponentData("tFileOutputDelimited_2", "NB_SUCCESS",
                                    entry_tFileOutputDelimited_2.getValue());
                            break;
                        case org.talend.components.api.component.ComponentDefinition.RETURN_REJECT_RECORD_COUNT:
                            container_tFileOutputDelimited_2.setComponentData("tFileOutputDelimited_2", "NB_REJECT",
                                    entry_tFileOutputDelimited_2.getValue());
                            break;
                        default:
                            StringBuilder studio_key_tFileOutputDelimited_2 = new StringBuilder();
                            for (int i_tFileOutputDelimited_2 = 0; i_tFileOutputDelimited_2 < entry_tFileOutputDelimited_2
                                    .getKey().length(); i_tFileOutputDelimited_2++) {
                                char ch_tFileOutputDelimited_2 = entry_tFileOutputDelimited_2.getKey()
                                        .charAt(i_tFileOutputDelimited_2);
                                if (Character.isUpperCase(ch_tFileOutputDelimited_2) && i_tFileOutputDelimited_2 > 0) {
                                    studio_key_tFileOutputDelimited_2.append('_');
                                }
                                studio_key_tFileOutputDelimited_2.append(ch_tFileOutputDelimited_2);
                            }
                            container_tFileOutputDelimited_2.setComponentData("tFileOutputDelimited_2",
                                    studio_key_tFileOutputDelimited_2.toString().toUpperCase(java.util.Locale.ENGLISH),
                                    entry_tFileOutputDelimited_2.getValue());
                            break;
                        }
                    }
                }

                /**
                 * [tFileOutputDelimited_2 end ] stop
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
                 * [tRowGenerator_2 finally ] start
                 */

                currentComponent = "tRowGenerator_2";

                /**
                 * [tRowGenerator_2 finally ] stop
                 */

                /**
                 * [tFileOutputDelimited_2 finally ] start
                 */

                currentComponent = "tFileOutputDelimited_2";

                // finally of generic

                if (resourceMap.get("finish_tFileOutputDelimited_2") == null) {
                    if (resourceMap.get("writer_tFileOutputDelimited_2") != null) {
                        try {
                            ((org.talend.components.api.component.runtime.Writer) resourceMap
                                    .get("writer_tFileOutputDelimited_2")).close();
                        } catch (java.io.IOException e_tFileOutputDelimited_2) {
                            String errorMessage_tFileOutputDelimited_2 = "failed to release the resource in tFileOutputDelimited_2 :"
                                    + e_tFileOutputDelimited_2.getMessage();
                            System.err.println(errorMessage_tFileOutputDelimited_2);
                        }
                    }
                }

                /**
                 * [tFileOutputDelimited_2 finally ] stop
                 */

            } catch (java.lang.Exception e) {
                // ignore
            } catch (java.lang.Error error) {
                // ignore
            }
            resourceMap = null;
        }

        globalMap.put("tRowGenerator_2_SUBPROCESS_STATE", 1);
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

    public static class row2Struct implements routines.system.IPersistableRow<row2Struct> {

        final static byte[] commonByteArrayLock_LOCAL_PROJECT_fileOutputDelimited = new byte[0];

        static byte[] commonByteArray_LOCAL_PROJECT_fileOutputDelimited = new byte[0];

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
                if (length > commonByteArray_LOCAL_PROJECT_fileOutputDelimited.length) {
                    if (length < 1024 && commonByteArray_LOCAL_PROJECT_fileOutputDelimited.length == 0) {
                        commonByteArray_LOCAL_PROJECT_fileOutputDelimited = new byte[1024];
                    } else {
                        commonByteArray_LOCAL_PROJECT_fileOutputDelimited = new byte[2 * length];
                    }
                }
                dis.readFully(commonByteArray_LOCAL_PROJECT_fileOutputDelimited, 0, length);
                strReturn = new String(commonByteArray_LOCAL_PROJECT_fileOutputDelimited, 0, length, "UTF8");
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

            synchronized (commonByteArrayLock_LOCAL_PROJECT_fileOutputDelimited) {

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
        public int compareTo(row2Struct other) {

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
