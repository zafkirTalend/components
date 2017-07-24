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

package org.talend.components.netsuite.test;

import java.net.URL;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.talend.components.netsuite.NetSuitePortTypeMockAdapter;

import com.netsuite.webservices.test.platform.AsyncFault;
import com.netsuite.webservices.test.platform.ExceededConcurrentRequestLimitFault;
import com.netsuite.webservices.test.platform.ExceededRecordCountFault;
import com.netsuite.webservices.test.platform.ExceededRequestLimitFault;
import com.netsuite.webservices.test.platform.ExceededRequestSizeFault;
import com.netsuite.webservices.test.platform.ExceededUsageLimitFault;
import com.netsuite.webservices.test.platform.InsufficientPermissionFault;
import com.netsuite.webservices.test.platform.InvalidAccountFault;
import com.netsuite.webservices.test.platform.InvalidCredentialsFault;
import com.netsuite.webservices.test.platform.InvalidSessionFault;
import com.netsuite.webservices.test.platform.InvalidVersionFault;
import com.netsuite.webservices.test.platform.NetSuitePortType;
import com.netsuite.webservices.test.platform.UnexpectedErrorFault;
import com.netsuite.webservices.test.platform.core.CustomizationRefList;
import com.netsuite.webservices.test.platform.core.DataCenterUrls;
import com.netsuite.webservices.test.platform.core.GetCustomizationIdResult;
import com.netsuite.webservices.test.platform.core.GetDataCenterUrlsResult;
import com.netsuite.webservices.test.platform.core.Status;
import com.netsuite.webservices.test.platform.core.StatusDetail;
import com.netsuite.webservices.test.platform.faults.types.StatusDetailCodeType;
import com.netsuite.webservices.test.platform.faults.types.StatusDetailType;
import com.netsuite.webservices.test.platform.messages.AddListRequest;
import com.netsuite.webservices.test.platform.messages.AddListResponse;
import com.netsuite.webservices.test.platform.messages.AddRequest;
import com.netsuite.webservices.test.platform.messages.AddResponse;
import com.netsuite.webservices.test.platform.messages.AsyncAddListRequest;
import com.netsuite.webservices.test.platform.messages.AsyncDeleteListRequest;
import com.netsuite.webservices.test.platform.messages.AsyncGetListRequest;
import com.netsuite.webservices.test.platform.messages.AsyncInitializeListRequest;
import com.netsuite.webservices.test.platform.messages.AsyncSearchRequest;
import com.netsuite.webservices.test.platform.messages.AsyncStatusResponse;
import com.netsuite.webservices.test.platform.messages.AsyncUpdateListRequest;
import com.netsuite.webservices.test.platform.messages.AsyncUpsertListRequest;
import com.netsuite.webservices.test.platform.messages.AttachRequest;
import com.netsuite.webservices.test.platform.messages.AttachResponse;
import com.netsuite.webservices.test.platform.messages.ChangeEmailRequest;
import com.netsuite.webservices.test.platform.messages.ChangeEmailResponse;
import com.netsuite.webservices.test.platform.messages.ChangePasswordRequest;
import com.netsuite.webservices.test.platform.messages.ChangePasswordResponse;
import com.netsuite.webservices.test.platform.messages.CheckAsyncStatusRequest;
import com.netsuite.webservices.test.platform.messages.DeleteListRequest;
import com.netsuite.webservices.test.platform.messages.DeleteListResponse;
import com.netsuite.webservices.test.platform.messages.DeleteRequest;
import com.netsuite.webservices.test.platform.messages.DeleteResponse;
import com.netsuite.webservices.test.platform.messages.DetachRequest;
import com.netsuite.webservices.test.platform.messages.DetachResponse;
import com.netsuite.webservices.test.platform.messages.GetAllRequest;
import com.netsuite.webservices.test.platform.messages.GetAllResponse;
import com.netsuite.webservices.test.platform.messages.GetAsyncResultRequest;
import com.netsuite.webservices.test.platform.messages.GetAsyncResultResponse;
import com.netsuite.webservices.test.platform.messages.GetBudgetExchangeRateRequest;
import com.netsuite.webservices.test.platform.messages.GetBudgetExchangeRateResponse;
import com.netsuite.webservices.test.platform.messages.GetConsolidatedExchangeRateRequest;
import com.netsuite.webservices.test.platform.messages.GetConsolidatedExchangeRateResponse;
import com.netsuite.webservices.test.platform.messages.GetCurrencyRateRequest;
import com.netsuite.webservices.test.platform.messages.GetCurrencyRateResponse;
import com.netsuite.webservices.test.platform.messages.GetCustomizationIdRequest;
import com.netsuite.webservices.test.platform.messages.GetCustomizationIdResponse;
import com.netsuite.webservices.test.platform.messages.GetDataCenterUrlsRequest;
import com.netsuite.webservices.test.platform.messages.GetDataCenterUrlsResponse;
import com.netsuite.webservices.test.platform.messages.GetDeletedRequest;
import com.netsuite.webservices.test.platform.messages.GetDeletedResponse;
import com.netsuite.webservices.test.platform.messages.GetItemAvailabilityRequest;
import com.netsuite.webservices.test.platform.messages.GetItemAvailabilityResponse;
import com.netsuite.webservices.test.platform.messages.GetListRequest;
import com.netsuite.webservices.test.platform.messages.GetListResponse;
import com.netsuite.webservices.test.platform.messages.GetPostingTransactionSummaryRequest;
import com.netsuite.webservices.test.platform.messages.GetPostingTransactionSummaryResponse;
import com.netsuite.webservices.test.platform.messages.GetRequest;
import com.netsuite.webservices.test.platform.messages.GetResponse;
import com.netsuite.webservices.test.platform.messages.GetSavedSearchRequest;
import com.netsuite.webservices.test.platform.messages.GetSavedSearchResponse;
import com.netsuite.webservices.test.platform.messages.GetSelectValueRequest;
import com.netsuite.webservices.test.platform.messages.GetSelectValueResponse;
import com.netsuite.webservices.test.platform.messages.GetServerTimeRequest;
import com.netsuite.webservices.test.platform.messages.GetServerTimeResponse;
import com.netsuite.webservices.test.platform.messages.InitializeListRequest;
import com.netsuite.webservices.test.platform.messages.InitializeListResponse;
import com.netsuite.webservices.test.platform.messages.InitializeRequest;
import com.netsuite.webservices.test.platform.messages.InitializeResponse;
import com.netsuite.webservices.test.platform.messages.LoginRequest;
import com.netsuite.webservices.test.platform.messages.LoginResponse;
import com.netsuite.webservices.test.platform.messages.LogoutRequest;
import com.netsuite.webservices.test.platform.messages.LogoutResponse;
import com.netsuite.webservices.test.platform.messages.MapSsoRequest;
import com.netsuite.webservices.test.platform.messages.MapSsoResponse;
import com.netsuite.webservices.test.platform.messages.ReadResponseList;
import com.netsuite.webservices.test.platform.messages.SearchMoreRequest;
import com.netsuite.webservices.test.platform.messages.SearchMoreResponse;
import com.netsuite.webservices.test.platform.messages.SearchMoreWithIdRequest;
import com.netsuite.webservices.test.platform.messages.SearchMoreWithIdResponse;
import com.netsuite.webservices.test.platform.messages.SearchNextRequest;
import com.netsuite.webservices.test.platform.messages.SearchNextResponse;
import com.netsuite.webservices.test.platform.messages.SearchRequest;
import com.netsuite.webservices.test.platform.messages.SearchResponse;
import com.netsuite.webservices.test.platform.messages.SsoLoginRequest;
import com.netsuite.webservices.test.platform.messages.SsoLoginResponse;
import com.netsuite.webservices.test.platform.messages.UpdateInviteeStatusListRequest;
import com.netsuite.webservices.test.platform.messages.UpdateInviteeStatusListResponse;
import com.netsuite.webservices.test.platform.messages.UpdateInviteeStatusRequest;
import com.netsuite.webservices.test.platform.messages.UpdateInviteeStatusResponse;
import com.netsuite.webservices.test.platform.messages.UpdateListRequest;
import com.netsuite.webservices.test.platform.messages.UpdateListResponse;
import com.netsuite.webservices.test.platform.messages.UpdateRequest;
import com.netsuite.webservices.test.platform.messages.UpdateResponse;
import com.netsuite.webservices.test.platform.messages.UpsertListRequest;
import com.netsuite.webservices.test.platform.messages.UpsertListResponse;
import com.netsuite.webservices.test.platform.messages.UpsertRequest;
import com.netsuite.webservices.test.platform.messages.UpsertResponse;

/**
 *
 */
@WebService(endpointInterface = "com.netsuite.webservices.test.platform.NetSuitePortType",
        targetNamespace = "urn:platform_2016_2.webservices.netsuite.com",
        serviceName = "NetSuiteService", portName = "NetSuitePort")
public class NetSuitePortTypeMockAdapterImpl implements NetSuitePortTypeMockAdapter<NetSuitePortType>, NetSuitePortType {

    private NetSuitePortType port;

    private URL endpointAddress;

    @Resource
    private WebServiceContext context;

    public URL getEndpointAddress() {
        return endpointAddress;
    }

    public void setEndpointAddress(URL endpointAddress) {
        NetSuitePortTypeMockAdapterImpl.this.endpointAddress = endpointAddress;
    }

    public NetSuitePortType getPort() {
        return port;
    }

    public void setPort(NetSuitePortType port) {
        this.port = port;
    }

    public GetPostingTransactionSummaryResponse getPostingTransactionSummary(GetPostingTransactionSummaryRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.getPostingTransactionSummary(parameters);
    }

    public UpsertResponse upsert(UpsertRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.upsert(parameters);
    }

    public ChangePasswordResponse changePassword(ChangePasswordRequest parameters)
            throws InvalidSessionFault, UnexpectedErrorFault, ExceededRequestLimitFault, InvalidAccountFault,
            InsufficientPermissionFault, InvalidCredentialsFault, InvalidVersionFault {
        return port.changePassword(parameters);
    }

    public GetAllResponse getAll(GetAllRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.getAll(parameters);
    }

    public AsyncStatusResponse asyncSearch(AsyncSearchRequest parameters)
            throws InvalidSessionFault, UnexpectedErrorFault, ExceededRequestLimitFault, ExceededRequestSizeFault,
            ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault, ExceededRecordCountFault {
        return port.asyncSearch(parameters);
    }

    public AddResponse add(AddRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.add(parameters);
    }

    public UpsertListResponse upsertList(UpsertListRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.upsertList(parameters);
    }

    public AsyncStatusResponse asyncInitializeList(AsyncInitializeListRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.asyncInitializeList(parameters);
    }

    public InitializeResponse initialize(InitializeRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.initialize(parameters);
    }

    public SsoLoginResponse ssoLogin(SsoLoginRequest parameters)
            throws InvalidSessionFault, UnexpectedErrorFault, ExceededRequestLimitFault, InvalidAccountFault,
            InsufficientPermissionFault, InvalidCredentialsFault, InvalidVersionFault {
        return port.ssoLogin(parameters);
    }

    public GetItemAvailabilityResponse getItemAvailability(GetItemAvailabilityRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.getItemAvailability(parameters);
    }

    public AsyncStatusResponse checkAsyncStatus(CheckAsyncStatusRequest parameters)
            throws InvalidSessionFault, UnexpectedErrorFault, ExceededRequestLimitFault, ExceededConcurrentRequestLimitFault,
            AsyncFault, InsufficientPermissionFault, InvalidCredentialsFault {
        return port.checkAsyncStatus(parameters);
    }

    public SearchMoreResponse searchMore(SearchMoreRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.searchMore(parameters);
    }

    public GetSelectValueResponse getSelectValue(GetSelectValueRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.getSelectValue(parameters);
    }

    public DetachResponse detach(DetachRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.detach(parameters);
    }

    public AsyncStatusResponse asyncAddList(AsyncAddListRequest parameters)
            throws InvalidSessionFault, UnexpectedErrorFault, ExceededRequestLimitFault, ExceededRequestSizeFault,
            ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault, ExceededRecordCountFault {
        return port.asyncAddList(parameters);
    }

    public ChangeEmailResponse changeEmail(ChangeEmailRequest parameters)
            throws InvalidSessionFault, UnexpectedErrorFault, ExceededRequestLimitFault, InvalidAccountFault,
            InsufficientPermissionFault, InvalidCredentialsFault, InvalidVersionFault {
        return port.changeEmail(parameters);
    }

    public UpdateInviteeStatusListResponse updateInviteeStatusList(UpdateInviteeStatusListRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.updateInviteeStatusList(parameters);
    }

    public AsyncStatusResponse asyncDeleteList(AsyncDeleteListRequest parameters)
            throws InvalidSessionFault, UnexpectedErrorFault, ExceededRequestLimitFault, ExceededRequestSizeFault,
            ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault, ExceededRecordCountFault {
        return port.asyncDeleteList(parameters);
    }

    public GetCustomizationIdResponse getCustomizationId(GetCustomizationIdRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {

        GetCustomizationIdResponse response = null;
        if (port != null) {
            response = port.getCustomizationId(parameters);
        }

        if (response == null) {
            response = new GetCustomizationIdResponse();
            GetCustomizationIdResult result = new GetCustomizationIdResult();
            result.setStatus(createSuccessStatus());
            result.setTotalRecords(0);
            result.setCustomizationRefList(new CustomizationRefList());
            response.setGetCustomizationIdResult(result);
        }

        return response;
    }

    public UpdateResponse update(UpdateRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.update(parameters);
    }

    public GetSavedSearchResponse getSavedSearch(GetSavedSearchRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.getSavedSearch(parameters);
    }

    public DeleteResponse delete(DeleteRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.delete(parameters);
    }

    public GetServerTimeResponse getServerTime(GetServerTimeRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededConcurrentRequestLimitFault, InsufficientPermissionFault, InvalidCredentialsFault {
        return port.getServerTime(parameters);
    }

    public LoginResponse login(LoginRequest parameters)
            throws InvalidSessionFault, UnexpectedErrorFault, ExceededRequestLimitFault, InvalidAccountFault,
            InsufficientPermissionFault, InvalidCredentialsFault, InvalidVersionFault {

//        List<Header> headers = getHeaders();
        try {
            if (context != null) {
                MessageContext messageContext = context.getMessageContext();
                MessageContextHolder.set(messageContext);
            }
            return port.login(parameters);
        } finally {
            MessageContextHolder.remove();
        }
    }

    public GetDataCenterUrlsResponse getDataCenterUrls(GetDataCenterUrlsRequest parameters)
            throws InvalidSessionFault, UnexpectedErrorFault, InsufficientPermissionFault, InvalidCredentialsFault,
            ExceededRequestSizeFault {

        GetDataCenterUrlsResponse response = null;
        if (port != null) {
            response = port.getDataCenterUrls(parameters);
        }

        if (response == null) {
            response = new GetDataCenterUrlsResponse();
            GetDataCenterUrlsResult result = new GetDataCenterUrlsResult();
            Status status = new Status();
            status.setIsSuccess(true);
            result.setStatus(status);
            DataCenterUrls urls = new DataCenterUrls();
            urls.setWebservicesDomain(endpointAddress.toString());
            urls.setSystemDomain(endpointAddress.toString());
            result.setDataCenterUrls(urls);
            response.setGetDataCenterUrlsResult(result);
            return response;
        }
        return response;
    }

    public AsyncStatusResponse asyncGetList(AsyncGetListRequest parameters)
            throws InvalidSessionFault, UnexpectedErrorFault, ExceededRequestLimitFault, ExceededRequestSizeFault,
            ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault, ExceededRecordCountFault {
        return port.asyncGetList(parameters);
    }

    public DeleteListResponse deleteList(DeleteListRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.deleteList(parameters);
    }

    public GetConsolidatedExchangeRateResponse getConsolidatedExchangeRate(GetConsolidatedExchangeRateRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.getConsolidatedExchangeRate(parameters);
    }

    public AsyncStatusResponse asyncUpdateList(AsyncUpdateListRequest parameters)
            throws InvalidSessionFault, UnexpectedErrorFault, ExceededRequestLimitFault, ExceededRequestSizeFault,
            ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault, ExceededRecordCountFault {
        return port.asyncUpdateList(parameters);
    }

    public GetBudgetExchangeRateResponse getBudgetExchangeRate(GetBudgetExchangeRateRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.getBudgetExchangeRate(parameters);
    }

    public GetCurrencyRateResponse getCurrencyRate(GetCurrencyRateRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.getCurrencyRate(parameters);
    }

    public AttachResponse attach(AttachRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.attach(parameters);
    }

    public SearchMoreWithIdResponse searchMoreWithId(SearchMoreWithIdRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.searchMoreWithId(parameters);
    }

    public AddListResponse addList(AddListRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.addList(parameters);
    }

    public MapSsoResponse mapSso(MapSsoRequest parameters)
            throws InvalidSessionFault, UnexpectedErrorFault, ExceededRequestLimitFault, InvalidAccountFault,
            InsufficientPermissionFault, InvalidCredentialsFault, InvalidVersionFault {
        return port.mapSso(parameters);
    }

    public SearchNextResponse searchNext(SearchNextRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, InvalidCredentialsFault, InsufficientPermissionFault, ExceededRecordCountFault {
        return port.searchNext(parameters);
    }

    public UpdateListResponse updateList(UpdateListRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.updateList(parameters);
    }

    public UpdateInviteeStatusResponse updateInviteeStatus(UpdateInviteeStatusRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.updateInviteeStatus(parameters);
    }

    public LogoutResponse logout(LogoutRequest parameters)
            throws InvalidSessionFault, UnexpectedErrorFault, ExceededRequestLimitFault, InsufficientPermissionFault,
            InvalidCredentialsFault {
        return port.logout(parameters);
    }

    public SearchResponse search(SearchRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.search(parameters);
    }

    public GetAsyncResultResponse getAsyncResult(GetAsyncResultRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, AsyncFault, InvalidCredentialsFault,
            InsufficientPermissionFault, ExceededRecordCountFault {
        return port.getAsyncResult(parameters);
    }

    public InitializeListResponse initializeList(InitializeListRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.initializeList(parameters);
    }

    public AsyncStatusResponse asyncUpsertList(AsyncUpsertListRequest parameters)
            throws InvalidSessionFault, UnexpectedErrorFault, ExceededRequestLimitFault, ExceededRequestSizeFault,
            ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault, ExceededRecordCountFault {
        return port.asyncUpsertList(parameters);
    }

    public GetResponse get(GetRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.get(parameters);
    }

    public GetListResponse getList(GetListRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {

        GetListResponse response = null;
        if (port != null) {
            response = port.getList(parameters);
        }

        if (response == null) {
            response = new GetListResponse();
            ReadResponseList readResponseList = new ReadResponseList();
            readResponseList.setStatus(createSuccessStatus());
            response.setReadResponseList(readResponseList);
        }

        return response;
    }

    public GetDeletedResponse getDeleted(GetDeletedRequest parameters)
            throws InvalidSessionFault, ExceededUsageLimitFault, UnexpectedErrorFault, ExceededRequestLimitFault,
            ExceededRequestSizeFault, ExceededConcurrentRequestLimitFault, InvalidCredentialsFault, InsufficientPermissionFault,
            ExceededRecordCountFault {
        return port.getDeleted(parameters);
    }

    public static Status createSuccessStatus() {
        Status status = new Status();
        status.setIsSuccess(true);
        return status;
    }

    public static Status createNotFoundStatus() {
        Status status = new Status();
        status.setIsSuccess(false);
        return status;
    }

    public static Status createErrorStatus(StatusDetailCodeType code, String message) {
        Status status = new Status();
        status.setIsSuccess(false);
        StatusDetail statusDetail = new StatusDetail();
        statusDetail.setType(StatusDetailType.ERROR);
        statusDetail.setCode(code);
        statusDetail.setMessage(message);
        status.getStatusDetail().add(statusDetail);
        return status;
    }
}
