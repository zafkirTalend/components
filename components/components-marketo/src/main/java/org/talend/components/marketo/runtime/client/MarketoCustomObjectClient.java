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
package org.talend.components.marketo.runtime.client;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema.Field;
import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.marketo.MarketoConstants;
import org.talend.components.marketo.runtime.client.rest.response.CustomObjectResult;
import org.talend.components.marketo.runtime.client.rest.response.SyncResult;
import org.talend.components.marketo.runtime.client.type.MarketoError;
import org.talend.components.marketo.runtime.client.type.MarketoException;
import org.talend.components.marketo.runtime.client.type.MarketoRecordResult;
import org.talend.components.marketo.runtime.client.type.MarketoSyncResult;
import org.talend.components.marketo.tmarketoconnection.TMarketoConnectionProperties;
import org.talend.components.marketo.tmarketoinput.TMarketoInputProperties;
import org.talend.components.marketo.tmarketooutput.TMarketoOutputProperties;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MarketoCustomObjectClient extends MarketoLeadClient {

    public static final String API_PATH_CUSTOMOBJECTS = "/v1/customobjects/";

    private static final Logger LOG = LoggerFactory.getLogger(MarketoCustomObjectClient.class);

    public MarketoCustomObjectClient(TMarketoConnectionProperties connection) throws MarketoException {
        super(connection);
    }

    /**
     * This method implements
     * http://developers.marketo.com/rest-api/endpoint-reference/lead-database-endpoint-reference/#!/Custom_Objects/describeUsingGET_1
     */
    public MarketoRecordResult describeCustomObject(TMarketoInputProperties parameters) {
        String customObjectName = parameters.customObjectName.getValue();
        current_uri = new StringBuilder(basicPath)//
                .append(API_PATH_CUSTOMOBJECTS)//
                .append(customObjectName)//
                .append(API_PATH_URI_DESCRIBE)//
                .append(fmtParams(FIELD_ACCESS_TOKEN, accessToken, true));
        LOG.debug("describeCustomObject : {}.", current_uri);
        MarketoRecordResult mkto = new MarketoRecordResult();
        mkto.setRemainCount(0);
        try {
            CustomObjectResult result = (CustomObjectResult) executeGetRequest(CustomObjectResult.class);
            mkto.setSuccess(result.isSuccess());
            mkto.setRequestId(REST + "::" + result.getRequestId());
            if (mkto.isSuccess()) {
                mkto.setRecordCount(1);
                mkto.setRecords(result.getRecords());
            } else {
                if (result.getErrors() != null) {
                    mkto.setErrors(result.getErrors());
                }
            }
        } catch (MarketoException e) {
            LOG.error("{}.", e);
            mkto.setSuccess(false);
            mkto.setRecordCount(0);
            mkto.setErrors(Arrays.asList(new MarketoError(REST, e.getMessage())));
        }
        return mkto;
    }

    /**
     * This method implements
     * http://developers.marketo.com/rest-api/endpoint-reference/lead-database-endpoint-reference/#!/Custom_Objects/listCustomObjectsUsingGET
     */
    public MarketoRecordResult listCustomObjects(TMarketoInputProperties parameters) {
        String names = parameters.customObjectNames.getValue();

        current_uri = new StringBuilder(basicPath)//
                .append("/v1/customobjects.json")//
                .append(fmtParams(FIELD_ACCESS_TOKEN, accessToken, true))//
                .append(fmtParams("names", names))//
                .append(fmtParams(QUERY_METHOD, QUERY_METHOD_GET));
        LOG.debug("listCustomObjects : {}.", current_uri);
        MarketoRecordResult mkto = new MarketoRecordResult();
        mkto.setRemainCount(0);
        try {
            CustomObjectResult result = (CustomObjectResult) executeGetRequest(CustomObjectResult.class);
            mkto.setSuccess(result.isSuccess());
            mkto.setRequestId(REST + "::" + result.getRequestId());
            if (mkto.isSuccess()) {
                mkto.setRecordCount(result.getResult().size());
                mkto.setRecords(result.getRecords());
            } else {
                if (result.getErrors() != null) {
                    mkto.setErrors(result.getErrors());
                }
            }
        } catch (MarketoException e) {
            LOG.error("{}.", e);
            mkto.setSuccess(false);
            mkto.setRecordCount(0);
            mkto.setErrors(asList(new MarketoError(REST, e.getMessage())));
        }
        return mkto;
    }

    /**
     * This method implements
     * http://developers.marketo.com/rest-api/endpoint-reference/lead-database-endpoint-reference/#!/Custom_Objects/getCustomObjectsUsingGET
     * 
     * Retrieves a list of custom objects records based on filter and set of values. When action is createOnly, idField
     * may not be used as a key and marketoGUID cannot be a member of any object records.
     */
    public MarketoRecordResult getCustomObjects(TMarketoInputProperties parameters, String offset) {
        // mandatory fields for request
        String customObjectName = parameters.customObjectName.getValue();
        String filterType = parameters.customObjectFilterType.getValue();
        String filterValues = parameters.customObjectFilterValues.getValue();
        // if fields is unset : marketoGuid, dedupeFields (defined in mkto), updatedAt, createdAt will be returned.
        List<String> fields = new ArrayList<>();
        for (String f : parameters.getSchemaFields()) {
            if (!f.equals(MarketoConstants.FIELD_MARKETO_GUID) && !f.equals(MarketoConstants.FIELD_SEQ)) {
                fields.add(f);
            }
        }
        //
        int batchLimit = parameters.batchSize.getValue() > REST_API_BATCH_LIMIT ? REST_API_BATCH_LIMIT
                : parameters.batchSize.getValue();
        current_uri = new StringBuilder(basicPath)//
                .append(API_PATH_CUSTOMOBJECTS)//
                .append(customObjectName)//
                .append(API_PATH_JSON_EXT)//
                .append(fmtParams(FIELD_ACCESS_TOKEN, accessToken, true));//
        MarketoRecordResult mkto = new MarketoRecordResult();
        try {
            // Compound Key use
            if (parameters.useCompoundKey.getValue()) {
                JsonObject inputJson = new JsonObject();
                Gson gson = new Gson();
                if (offset != null) {
                    inputJson.addProperty(FIELD_NEXT_PAGE_TOKEN, offset);
                }
                inputJson.addProperty("filterType", "dedupeFields");
                if (!fields.isEmpty()) {
                    inputJson.add(FIELD_FIELDS, gson.toJsonTree(fields));
                }
                inputJson.add(FIELD_INPUT, parameters.compoundKey.getKeyValuesAsJson().getAsJsonArray());
                LOG.debug("getCustomObjects : {} body : {}", current_uri, inputJson);
                mkto = executeFakeGetRequest(parameters.schemaInput.schema.getValue(), inputJson.toString());
            } else {
                current_uri.append(fmtParams(FIELD_BATCH_SIZE, batchLimit))//
                        .append(fmtParams("filterType", filterType))//
                        .append(fmtParams("filterValues", filterValues));
                if (offset != null) {
                    current_uri.append(fmtParams(FIELD_NEXT_PAGE_TOKEN, offset));
                }
                if (!fields.isEmpty()) {
                    current_uri.append(fmtParams(FIELD_FIELDS, csvString(fields.toArray())));
                }
                LOG.debug("getCustomObjects : {}.", current_uri);
                mkto = executeGetRequest(parameters.schemaInput.schema.getValue());
            }
        } catch (MarketoException e) {
            LOG.error("{}.", e);
            mkto.setSuccess(false);
            mkto.setRecordCount(0);
            mkto.setErrors(Arrays.asList(e.toMarketoError()));
        }
        return mkto;
    }

    /**
     * This method implements
     * http://developers.marketo.com/rest-api/endpoint-reference/lead-database-endpoint-reference/#!/Custom_Objects/syncCustomObjectsUsingPOST
     * <p>
     * Inserts, updates, or upserts custom object records to the target instance.
     */
    public MarketoSyncResult syncCustomObjects(TMarketoOutputProperties parameters, List<IndexedRecord> records) {
        // mandatory fields for request
        String customObjectName = parameters.customObjectName.getValue();
        // others
        String action = parameters.customObjectSyncAction.getValue().name();
        String dedupeBy = parameters.customObjectDedupeBy.getValue();
        // input (Array[CustomObject]): List of input records
        JsonObject inputJson = new JsonObject();
        Gson gson = new Gson();
        inputJson.addProperty("action", action);
        if (!dedupeBy.isEmpty()) {
            inputJson.addProperty("dedupeBy", dedupeBy);
        }
        List<Map<String, Object>> leadsObjects = new ArrayList<>();
        for (IndexedRecord r : records) {
            Map<String, Object> lead = new HashMap<>();
            for (Field f : r.getSchema().getFields()) {
                lead.put(f.name(), r.get(f.pos()));
            }
            leadsObjects.add(lead);
        }
        inputJson.add(FIELD_INPUT, gson.toJsonTree(leadsObjects));
        MarketoSyncResult mkto = new MarketoSyncResult();

        current_uri = new StringBuilder(basicPath)//
                .append(API_PATH_CUSTOMOBJECTS)//
                .append(customObjectName)//
                .append(API_PATH_JSON_EXT)//
                .append(fmtParams(FIELD_ACCESS_TOKEN, accessToken, true));//

        try {
            LOG.debug("syncCustomObjects {}{}.", current_uri, inputJson);
            SyncResult rs = (SyncResult) executePostRequest(SyncResult.class, inputJson);
            //
            mkto.setRequestId(REST + "::" + rs.getRequestId());
            mkto.setStreamPosition(rs.getNextPageToken());
            mkto.setSuccess(rs.isSuccess());
            if (mkto.isSuccess()) {
                mkto.setRecordCount(rs.getResult().size());
                mkto.setRemainCount(mkto.getStreamPosition() != null ? mkto.getRecordCount() : 0);
                mkto.setRecords(rs.getResult());
            } else {
                mkto.setRecordCount(0);
                mkto.setErrors(rs.getErrors());
            }
            LOG.debug("rs = {}.", rs);
        } catch (MarketoException e) {
            mkto.setSuccess(false);
            mkto.setErrors(Arrays.asList(e.toMarketoError()));
        }
        return mkto;
    }

    /**
     * This method implements
     * http://developers.marketo.com/rest-api/endpoint-reference/lead-database-endpoint-reference/#!/Custom_Objects/deleteCustomObjectsUsingPOST
     * <p>
     * Deletes a given set of custom object records
     * <p>
     * <p>
     * <p>
     * DeleteCustomObjectRequest
     * <p>
     * { deleteBy (string, optional): Field to delete records by. Permissible values are idField or dedupeFields as
     * indicated by the result of the corresponding describe record,
     * <p>
     * input (Array[CustomObject]): List of input records }
     * <p>
     * <p>
     * CustomObject {
     * <p>
     * marketoGUID (string): Unique GUID of the custom object records ,
     * <p>
     * reasons (Array[Reason], optional),
     * <p>
     * seq (integer): Integer indicating the sequence of the record in response. This value is correlated to the order
     * of the records included in the request input. Seq should only be part of responses and should not be submitted. }
     * <p>
     * <p>
     * Reason {
     * <p>
     * code (string): Integer code of the reason ,
     * <p>
     * message (string): Message describing the reason for the status of the operation }
     */
    public MarketoSyncResult deleteCustomObjects(TMarketoOutputProperties parameters, List<IndexedRecord> records) {
        // mandatory fields for request
        String customObjectName = parameters.customObjectName.getValue();
        /*
         * deleteBy : idField || dedupeFields.
         *
         * Sample with describe smartphone : ... "idField": "marketoGUID", "dedupeFields": "[\"model\"]".
         */
        String deleteBy = parameters.customObjectDeleteBy.getValue().name();
        //
        // input (Array[CustomObject]): List of input records
        JsonObject inputJson = new JsonObject();
        Gson gson = new Gson();
        if (!deleteBy.isEmpty()) {
            inputJson.addProperty("deleteBy", deleteBy);
        }
        List<Map<String, Object>> leadsObjects = new ArrayList<>();
        for (IndexedRecord r : records) {
            Map<String, Object> lead = new HashMap<>();
            for (Field f : r.getSchema().getFields()) {
                lead.put(f.name(), r.get(f.pos()));
            }
            leadsObjects.add(lead);
        }
        inputJson.add(FIELD_INPUT, gson.toJsonTree(leadsObjects));
        //
        current_uri = new StringBuilder(basicPath)//
                .append(API_PATH_CUSTOMOBJECTS)//
                .append(customObjectName)//
                .append(API_PATH_URI_DELETE)//
                .append(fmtParams(FIELD_ACCESS_TOKEN, accessToken, true));
        MarketoSyncResult mkto = new MarketoSyncResult();
        try {
            LOG.debug("deleteCustomObject {}{}.", current_uri, inputJson);
            SyncResult rs = (SyncResult) executePostRequest(SyncResult.class, inputJson);
            mkto.setRequestId(REST + "::" + rs.getRequestId());
            mkto.setStreamPosition(rs.getNextPageToken());
            mkto.setSuccess(rs.isSuccess());
            if (mkto.isSuccess()) {
                mkto.setRecordCount(rs.getResult().size());
                mkto.setRemainCount(mkto.getStreamPosition() != null ? mkto.getRecordCount() : 0);
                mkto.setRecords(rs.getResult());
            } else {
                mkto.setRecordCount(0);
                mkto.setErrors(Arrays.asList(new MarketoError(REST, "Could not delete CustomObject.")));
            }
        } catch (MarketoException e) {
            mkto.setSuccess(false);
            mkto.setErrors(Arrays.asList(e.toMarketoError()));
        }
        return mkto;
    }
}
