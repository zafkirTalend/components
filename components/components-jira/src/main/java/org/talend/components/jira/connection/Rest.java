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
package org.talend.components.jira.connection;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apache Http components library wrapper. It uses Basic authentication by default.
 * 
 * @author ivan.honchar
 */
public class Rest {

    private static final Logger LOG = LoggerFactory.getLogger(Rest.class);

    /**
     * A list of common headers
     */
    private List<Header> headers;

    /**
     * Schema, host and port of REST API location
     */
    private String hostPort;

    /**
     * Http authorization type. It is used to set Http Authorization header
     */
    private String authorizationType = "Basic";

    /**
     * 
     */
    private ContentType contentType;

    /**
     * Request executor
     */
    private Executor executor;

    /**
     * Constructor
     */
    public Rest() {
        this(null);
    }

    /**
     * Constructor sets schema(http or https), host(google.com) and port number(8080) using one hostPort parameter
     * 
     * @param hostPort URL
     */
    public Rest(String hostPort) {
        headers = new LinkedList<>();
        if (!hostPort.endsWith("/")) {
            hostPort = hostPort + "/";
        }
        this.hostPort = hostPort;

        contentType = ContentType.create("application/json", StandardCharsets.UTF_8);

        executor = Executor.newInstance(createHttpClient());
        executor.use(new BasicCookieStore());
    }

    /**
     * Create {@code HttpClient} to be used for communicating with JIRA server.
     *
     * @return {@code HttpClient}
     */
    private CloseableHttpClient createHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(
                RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
        return httpClient;
    }

    /**
     * Checks connection to the host
     * 
     * @return HTTP status code
     * @throws IOException if host is unreachable
     */
    public int checkConnection() throws IOException {
        int statusCode = 0;
        try (CloseableHttpClient httpClient = createHttpClient()) {
            HttpHead httpHead = new HttpHead(hostPort);
            try (CloseableHttpResponse response = httpClient.execute(httpHead)) {
                statusCode = response.getStatusLine().getStatusCode();
            }
        }
        return statusCode;
    }

    /**
     * Executes Http Get request
     * 
     * @param resource REST API resource. E. g. issue/{issueId}
     * @return response status code and body
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public JiraResponse get(String resource) throws IOException {
        return get(resource, Collections.EMPTY_MAP);
    }

    /**
     * Executes Http Get request
     * 
     * @param resource REST API resource. E. g. issue/{issueId}
     * @param parameters http query parameters
     * @return response status code and body
     * @throws IOException
     */
    public JiraResponse get(String resource, Map<String, Object> parameters) throws IOException {
        try {
            URIBuilder builder = new URIBuilder(hostPort + resource);
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                builder.addParameter(entry.getKey(), entry.getValue().toString());
            }
            URI uri = builder.build();
            Request get = Request.Get(uri);
            for (Header header : headers) {
                get.addHeader(header);
            }
            executor.clearCookies();
            Response response = executor.execute(get);
            HttpResponse httpResponse = response.returnResponse();
            HttpEntity entity = httpResponse.getEntity();
            String entityBody = "";
            if (entity != null) {
                entityBody = EntityUtils.toString(entity);
            }
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            return new JiraResponse(statusCode, entityBody);
        } catch (URISyntaxException e) {
            LOG.debug("Wrong URI. {}", e.getMessage());
            throw new IOException("Wrong URI", e);
        }
    }

    /**
     * Executes Http Delete request
     * 
     * @param resource REST API resource. E. g. issue/{issueId}
     * @return response status code and body
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public JiraResponse delete(String resource) throws IOException {
        return delete(resource, Collections.EMPTY_MAP);
    }

    /**
     * Executes Http Delete request
     * 
     * @param resource REST API resource. E. g. issue/{issueId}
     * @param parameters http query parameters
     * @return response status code and body
     * @throws IOException
     */
    public JiraResponse delete(String resource, Map<String, Object> parameters) throws IOException {
        try {
            URIBuilder builder = new URIBuilder(hostPort + resource);
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                builder.addParameter(entry.getKey(), entry.getValue().toString());
            }
            URI uri = builder.build();
            Request delete = Request.Delete(uri);
            for (Header header : headers) {
                delete.addHeader(header);
            }
            executor.clearCookies();
            Response response = executor.execute(delete);
            HttpResponse httpResponse = response.returnResponse();
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            HttpEntity entity = httpResponse.getEntity();
            String entityBody = "";
            if (entity != null && statusCode != SC_UNAUTHORIZED) {
                entityBody = EntityUtils.toString(entity);
            }
            return new JiraResponse(statusCode, entityBody);
        } catch (URISyntaxException e) {
            LOG.debug("Wrong URI. {}", e.getMessage());
            throw new IOException("Wrong URI", e);
        }
    }

    /**
     * Executes Http Post request
     * 
     * @param resource REST API resource. E. g. issue/{issueId}
     * @param body message body
     * @return response status code and body
     * @throws ClientProtocolException
     * @throws IOException
     */
    public JiraResponse post(String resource, String body) throws IOException {
        Request post = Request.Post(hostPort + resource).bodyString(body, contentType);
        for (Header header : headers) {
            post.addHeader(header);
        }
        executor.clearCookies();
        Response response = executor.execute(post);
        HttpResponse httpResponse = response.returnResponse();
        StatusLine statusLine = httpResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        HttpEntity entity = httpResponse.getEntity();
        String entityBody = "";
        if (entity != null && statusCode != SC_UNAUTHORIZED) {
            entityBody = EntityUtils.toString(entity);
        }
        return new JiraResponse(statusCode, entityBody);
    }

    /**
     * Executes Http Put request
     * 
     * @param resource REST API resource. E. g. issue/{issueId}
     * @param body message body
     * @return response status code and body
     * @throws ClientProtocolException
     * @throws IOException
     */
    public JiraResponse put(String resource, String body) throws IOException {
        Request put = Request.Put(hostPort + resource).bodyString(body, contentType);
        for (Header header : headers) {
            put.addHeader(header);
        }
        executor.clearCookies();
        Response response = executor.execute(put);
        HttpResponse httpResponse = response.returnResponse();
        StatusLine statusLine = httpResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        HttpEntity entity = httpResponse.getEntity();
        String entityBody = "";
        if (entity != null && statusCode != SC_UNAUTHORIZED) {
            entityBody = EntityUtils.toString(entity);
        }
        return new JiraResponse(statusCode, entityBody);
    }

    public Rest setAuthorizationType(String type) {
        this.authorizationType = type;
        return this;
    }

    public Rest setCredentials(String user, String password) {

        String credentials = user + ":" + password;
        String encodedCredentials = base64(credentials);
        Header authorization = new BasicHeader("Authorization", authorizationType + " " + encodedCredentials);
        headers.add(authorization);
        return this;
    }

    public Rest setUrl(String url) {
        this.hostPort = url;
        return this;
    }

    private String base64(String str) {
        return Base64.encodeBase64String(str.getBytes());
    }
}
