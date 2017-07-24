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

package org.talend.components.netsuite;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.talend.components.netsuite.test.NetSuitePortTypeMockAdapterImpl.createNotFoundStatus;
import static org.talend.components.netsuite.test.NetSuitePortTypeMockAdapterImpl.createSuccessStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.talend.components.netsuite.client.NetSuiteClientService;
import org.talend.components.netsuite.client.NsRef;
import org.talend.components.netsuite.client.model.RefType;
import org.talend.components.netsuite.client.model.beans.BeanInfo;
import org.talend.components.netsuite.client.model.beans.Beans;
import org.talend.components.netsuite.client.model.beans.PropertyInfo;
import org.talend.components.netsuite.input.NsObjectInputTransducer;
import org.talend.components.netsuite.test.NetSuitePortTypeMockAdapterImpl;
import org.talend.components.netsuite.test.client.TestNetSuiteClientFactory;
import org.talend.components.netsuite.test.client.model.TestRecordTypeEnum;

import com.netsuite.webservices.test.platform.NetSuitePortType;
import com.netsuite.webservices.test.platform.core.CustomFieldRef;
import com.netsuite.webservices.test.platform.core.Record;
import com.netsuite.webservices.test.platform.core.RecordList;
import com.netsuite.webservices.test.platform.core.SearchResult;
import com.netsuite.webservices.test.platform.core.types.RecordType;
import com.netsuite.webservices.test.platform.messages.GetListRequest;
import com.netsuite.webservices.test.platform.messages.GetListResponse;
import com.netsuite.webservices.test.platform.messages.GetRequest;
import com.netsuite.webservices.test.platform.messages.GetResponse;
import com.netsuite.webservices.test.platform.messages.LoginRequest;
import com.netsuite.webservices.test.platform.messages.LoginResponse;
import com.netsuite.webservices.test.platform.messages.ReadResponse;
import com.netsuite.webservices.test.platform.messages.ReadResponseList;
import com.netsuite.webservices.test.platform.messages.SearchMoreWithIdRequest;
import com.netsuite.webservices.test.platform.messages.SearchMoreWithIdResponse;
import com.netsuite.webservices.test.platform.messages.SearchRequest;
import com.netsuite.webservices.test.platform.messages.SearchResponse;
import com.netsuite.webservices.test.platform.messages.SessionResponse;
import com.netsuite.webservices.test.setup.customization.CustomFieldType;
import com.netsuite.webservices.test.setup.customization.CustomRecordType;
import com.netsuite.webservices.test.setup.customization.types.CustomizationFieldType;

/**
 *
 */
public abstract class NetSuiteMockTestBase extends AbstractNetSuiteTestBase {

    protected NetSuiteWebServiceMockTestFixture<NetSuitePortType, NetSuitePortTypeMockAdapterImpl> webServiceMockTestFixture;
    protected NetSuiteComponentMockTestFixture mockTestFixture;

    protected void installWebServiceMockTestFixture() throws Exception {
        webServiceMockTestFixture = createWebServiceMockTestFixture();
        testFixtures.add(webServiceMockTestFixture);
    }

    protected void installMockTestFixture() throws Exception {
        mockTestFixture = new NetSuiteComponentMockTestFixture(webServiceMockTestFixture);
        testFixtures.add(mockTestFixture);
    }

    protected <T extends Record> void mockGetRequestResults(final T record) throws Exception {
        final NetSuitePortType port = webServiceMockTestFixture.getPortMock();

        when(port.get(any(GetRequest.class))).then(new Answer<GetResponse>() {
            @Override public GetResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                GetResponse response = new GetResponse();
                ReadResponse readResponse = new ReadResponse();
                if (record != null) {
                    readResponse.setStatus(createSuccessStatus());
                } else {
                    readResponse.setStatus(createNotFoundStatus());
                }
                readResponse.setRecord(record);
                response.setReadResponse(readResponse);
                return response;
            }
        });
    }

    protected <T extends Record> void mockGetListRequestResults(final List<T> records) throws Exception {
        final NetSuitePortType port = webServiceMockTestFixture.getPortMock();

        when(port.getList(any(GetListRequest.class))).then(new Answer<GetListResponse>() {
            @Override public GetListResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                GetListRequest request = (GetListRequest) invocationOnMock.getArguments()[0];
                GetListResponse response = new GetListResponse();
                ReadResponseList readResponseList = new ReadResponseList();
                int count = request.getBaseRef().size();
                for (int i = 0; i < count; i++) {
                    ReadResponse readResponse = new ReadResponse();
                    T record = records != null ? records.get(i) : null;
                    if (record != null) {
                        readResponse.setStatus(createSuccessStatus());
                    } else {
                        readResponse.setStatus(createNotFoundStatus());
                    }
                    readResponse.setRecord(record);
                    readResponseList.getReadResponse().add(readResponse);
                }
                response.setReadResponseList(readResponseList);
                return response;
            }
        });
    }

    protected <T extends Record> void mockSearchRequestResults(List<T> recordList, int pageSize) throws Exception {
        final NetSuitePortType port = webServiceMockTestFixture.getPortMock();

        final List<SearchResult> pageResults = makeRecordPages(recordList, pageSize);
        when(port.search(any(SearchRequest.class))).then(new Answer<SearchResponse>() {
            @Override public SearchResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                SearchResponse response = new SearchResponse();
                response.setSearchResult(pageResults.get(0));
                return response;
            }
        });
        when(port.searchMoreWithId(any(SearchMoreWithIdRequest.class))).then(new Answer<SearchMoreWithIdResponse>() {
            @Override public SearchMoreWithIdResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                SearchMoreWithIdRequest request = (SearchMoreWithIdRequest) invocationOnMock.getArguments()[0];
                SearchMoreWithIdResponse response = new SearchMoreWithIdResponse();
                response.setSearchResult(pageResults.get(request.getPageIndex() - 1));
                return response;
            }
        });
    }

    protected void mockLoginResponse(NetSuitePortType port) throws Exception {
        SessionResponse sessionResponse = new SessionResponse();
        sessionResponse.setStatus(NetSuitePortTypeMockAdapterImpl.createSuccessStatus());
        LoginResponse response = new LoginResponse();
        response.setSessionResponse(sessionResponse);

        when(port.login(any(LoginRequest.class))).thenReturn(response);
    }

    protected Map<String, CustomFieldRef> createCustomFieldRefs(
            Map<String, CustomFieldSpec<RecordType, CustomizationFieldType>> customFieldSpecs) throws Exception {

        NetSuiteClientService<?> clientService = webServiceMockTestFixture.getClientService();

        Map<String, CustomFieldRef> map = new HashMap<>();
        for (CustomFieldSpec spec : customFieldSpecs.values()) {
            CustomFieldRef fieldRef = clientService.getBasicMetaData().createInstance(
                    spec.getFieldRefType().getTypeName());

            fieldRef.setScriptId(spec.getScriptId());
            fieldRef.setInternalId(spec.getInternalId());

            BeanInfo beanInfo = Beans.getBeanInfo(fieldRef.getClass());
            PropertyInfo valuePropInfo = beanInfo.getProperty("value");

            Object value = composeValue(valuePropInfo.getWriteType());
            if (value != null) {
                Beans.setProperty(fieldRef, "value", value);
            }

            map.put(fieldRef.getScriptId(), fieldRef);
        }

        return map;
    }

    protected Map<String, NsRef> createCustomFieldCustomizationRefs(
            Map<String, CustomFieldSpec<RecordType, CustomizationFieldType>> customFieldSpecs) throws Exception {

        Map<String, NsRef> map = new HashMap<>();
        for (CustomFieldSpec<RecordType, CustomizationFieldType> spec : customFieldSpecs.values()) {
            NsRef ref = new NsRef(RefType.CUSTOMIZATION_REF);

            ref.setScriptId(spec.getScriptId());
            ref.setInternalId(spec.getInternalId());
            ref.setType(spec.getRecordType().value());

            map.put(ref.getScriptId(), ref);
        }

        return map;
    }

    protected NsRef createCustomRecordCustomizationRef(CustomRecordType customRecordType) throws Exception {

        NsRef ref = new NsRef(RefType.CUSTOMIZATION_REF);

        ref.setScriptId(customRecordType.getScriptId());
        ref.setInternalId(customRecordType.getInternalId());
        ref.setType(TestRecordTypeEnum.CUSTOM_RECORD_TYPE.getType());
        ref.setName(customRecordType.getRecordName());

        return ref;
    }

    protected Map<String, CustomFieldType> createCustomFieldTypes(
            Map<String, CustomFieldSpec<RecordType, CustomizationFieldType>> customFieldSpecs) throws Exception {

        Map<String, CustomFieldType> customFieldTypeMap = new HashMap<>();
        for (CustomFieldSpec<RecordType, CustomizationFieldType> spec : customFieldSpecs.values()) {
            CustomFieldType fieldRecord = (CustomFieldType) spec.getFieldTypeClass().newInstance();

            Beans.setProperty(fieldRecord, "internalId", spec.getInternalId());
            fieldRecord.setScriptId(spec.getScriptId());
            fieldRecord.setFieldType(spec.getFieldType());

            if (spec.getAppliesTo() != null) {
                for (String appliesTo : spec.getAppliesTo()) {
                    Beans.setProperty(fieldRecord, appliesTo, Boolean.TRUE);
                }
            }

            customFieldTypeMap.put(fieldRecord.getScriptId(), fieldRecord);
        }

        return customFieldTypeMap;
    }

    public static NetSuiteWebServiceMockTestFixture<NetSuitePortType, NetSuitePortTypeMockAdapterImpl> createWebServiceMockTestFixture()
            throws Exception {
        return new NetSuiteWebServiceMockTestFixture(new TestNetSuiteClientFactory(),
                NetSuitePortType.class, NetSuitePortTypeMockAdapterImpl.class);
    }

    public static <T extends Record> List<SearchResult> makeRecordPages(List<T> recordList, int pageSize)
            throws Exception {

        int count = recordList.size();
        int totalPages = count / pageSize;
        if (count % pageSize != 0) {
            totalPages += 1;
        }

        String searchId = UUID.randomUUID().toString();

        List<SearchResult> pageResults = new ArrayList<>();
        SearchResult result = null;

        Iterator<T> recordIterator = recordList.iterator();

        while (recordIterator.hasNext() && count > 0) {
            T record = recordIterator.next();

            if (result == null) {
                result = new SearchResult();
                result.setSearchId(searchId);
                result.setTotalPages(totalPages);
                result.setTotalRecords(count);
                result.setPageIndex(pageResults.size() + 1);
                result.setPageSize(pageSize);
                result.setStatus(createSuccessStatus());
            }

            if (result.getRecordList() == null) {
                result.setRecordList(new RecordList());
            }
            result.getRecordList().getRecord().add(record);

            if (result.getRecordList().getRecord().size() == pageSize) {
                pageResults.add(result);
                result = null;
            }

            count--;
        }

        if (result != null) {
            pageResults.add(result);
        }

        return pageResults;
    }

    public static <T> List<IndexedRecord> makeIndexedRecords(
            NetSuiteClientService<?> clientService, Schema schema,
            AbstractNetSuiteTestBase.ObjectComposer<T> objectComposer, int count) throws Exception {

        NsObjectInputTransducer transducer = new NsObjectInputTransducer(clientService, schema, schema.getName());

        List<IndexedRecord> recordList = new ArrayList<>();

        while (count > 0) {
            T nsRecord = objectComposer.composeObject();

            IndexedRecord convertedRecord = transducer.read(nsRecord);
            Schema recordSchema = convertedRecord.getSchema();

            GenericRecord record = new GenericData.Record(recordSchema);
            for (Schema.Field field : schema.getFields()) {
                Object value = convertedRecord.get(field.pos());
                record.put(field.pos(), value);
            }

            recordList.add(record);

            count--;
        }

        return recordList;
    }
}
