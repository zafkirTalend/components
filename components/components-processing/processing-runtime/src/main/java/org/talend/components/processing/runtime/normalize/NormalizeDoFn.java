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
package org.talend.components.processing.runtime.normalize;

import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.processing.normalize.NormalizeProperties;
import org.talend.components.processing.runtime.Utils;
import org.talend.daikon.avro.converter.IndexedRecordConverter;

public class NormalizeDoFn extends DoFn<IndexedRecord, IndexedRecord> {

    private static final Logger LOG = LoggerFactory.getLogger(NormalizeDoFn.class);

    private NormalizeProperties properties = null;

    private IndexedRecordConverter converter = null;

    @Setup
    public void setup() throws Exception {
    }

    @ProcessElement
    public void processElement(ProcessContext context) throws Exception {
        IndexedRecord inputRecord = context.element();
        Schema schema = inputRecord.getSchema();

        String columnToNormalize = properties.columnToNormalize.getValue();
        String delim = properties.fieldSeparator.getValue();
        boolean isDiscardTrailingEmptyStr = properties.discardTrailingEmptyStr.getValue();
        boolean isTrim = properties.trim.getValue();

        if (!StringUtils.isEmpty(columnToNormalize)) {

            // structure hierarchique
            if (StringUtils.contains(columnToNormalize, ".")) {

                String[] path = columnToNormalize.split("\\.");

                ObjectMapper mapper = new ObjectMapper();

                // transform inputRecord to jsonNode
                JsonNode hierarchicalNode = mapper.readTree(inputRecord.toString());

                // search the normalized node
                JsonNode normalizedNode = Utils.searchNode(hierarchicalNode, path);

                // get the normalized values delimited
                String[] delimited = delimit(normalizedNode.getTextValue(), delim, isDiscardTrailingEmptyStr, isTrim); // Utils.tmp(actualObj,
                // path, 0);

                // get the index of the parent column to normalize: c.g.h : index c = 2
                int indexParentColumnToNormalize = schema.getField(path[0]).pos();

                // int combienNormalize = Utils.combienNormalize(inputRecord, columnToNormalize, delim);

                for (int l = 0; l < delimited.length; l++) {

                    // put outputRecord for the columns non normalized
                    GenericRecord outputRecord = new GenericData.Record(schema);
                    for (int k = 0; k < schema.getFields().size(); k++) {
                        if (k != indexParentColumnToNormalize) {
                            outputRecord.put(k, inputRecord.get(k));
                        }
                    }

                    // put outputRecord for the normalized column
                    Schema columnSchemaToNormalize = schema.getField(path[0]).schema();
                    IndexedRecord indexedRecordToNormalize = new GenericData.Record(columnSchemaToNormalize);

                    Object fieldNormalized = Utils.getFieldNormalizedFromNode(hierarchicalNode, path, delimited[l], 0);
                    // Utils.getFieldNormalized(inputRecord, path, indexColumnToNormalizeParent, l, delim);

                    JsonNode jsonFieldNormalized = (JsonNode) fieldNormalized;

                    // Replace the normalized node

                    JsonNode hierarchicalNodeTmp = hierarchicalNode;
                    for (int i = 0; i < path.length; i++) {

                        try {
                            hierarchicalNodeTmp = hierarchicalNodeTmp.with(path[i]);
                        } catch (Exception e) {
                            hierarchicalNodeTmp = hierarchicalNodeTmp.path(path[i]);
                        } finally {
                            ((ObjectNode) hierarchicalNode).put(path[i], hierarchicalNodeTmp);
                        }
                    }

                    int indexChildColumnToNormalize = columnSchemaToNormalize.getField(path[1]).pos();
                    indexedRecordToNormalize.put(indexChildColumnToNormalize, hierarchicalNode.get(path[0]));

                    outputRecord.put(indexParentColumnToNormalize, indexedRecordToNormalize); // actualObj.get(path[0]));

                    context.output(outputRecord);
                }

                // Object elementNormalized = generateElementNormalized();

                // outputRecord.put(indexColumnToNormalizeParent, elementNormalized);

            }

            // simple structure
            else {
                int indexColumnToNormalize = schema.getField(columnToNormalize).pos();
                List<Object> inputValues = Utils.getInputFields(inputRecord, columnToNormalize);
                for (int i = 0; i < inputValues.size(); i++) {
                    String[] strDelimited = delimit(inputValues.get(i), delim, isDiscardTrailingEmptyStr, isTrim);
                    for (int j = 0; j < strDelimited.length; j++) {
                        GenericRecord outputRecord = new GenericData.Record(schema);
                        for (int k = 0; k < schema.getFields().size(); k++) {
                            if (k != indexColumnToNormalize) {
                                outputRecord.put(k, inputRecord.get(k));
                            }
                        }
                        outputRecord.put(indexColumnToNormalize, strDelimited[j]);
                        context.output(outputRecord);
                    }
                }
            }
        }
    }

    /**
     * Splits obj around matches of the given delim parameter.
     * 
     * @param obj
     * @param delim the delimiting regular expression
     * @param isDiscardTrailingEmptyStr
     * @param isTrim
     * @return
     * @throws Exception if cannot cast obj to String.
     */
    private String[] delimit(Object obj, String delim, boolean isDiscardTrailingEmptyStr, boolean isTrim) throws Exception {
        String[] strDelimited = {};
        try {
            String str = (String) obj;
            strDelimited = str.split(delim);
            for (int i = 0; i < strDelimited.length; i++) {
                if (isDiscardTrailingEmptyStr) {
                    strDelimited[i] = strDelimited[i].replaceAll("\\s+$", "");
                }
                if (isTrim) {
                    strDelimited[i] = strDelimited[i].trim();
                }
            }
        } catch (Exception e) {
            LOG.debug("Cannot cast Object to String {}", e.getMessage());
        }
        return strDelimited;
    }

    public NormalizeDoFn withProperties(NormalizeProperties properties) {
        this.properties = properties;
        return this;
    }
}
