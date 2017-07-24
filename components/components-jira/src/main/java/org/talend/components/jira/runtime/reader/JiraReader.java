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
package org.talend.components.jira.runtime.reader;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.avro.generic.IndexedRecord;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.Reader;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.jira.avro.IssueAdapterFactory;
import org.talend.components.jira.avro.IssueIndexedRecord;
import org.talend.components.jira.connection.JiraResponse;
import org.talend.components.jira.connection.Rest;
import org.talend.components.jira.datum.Entity;
import org.talend.components.jira.runtime.JiraSource;
import org.talend.daikon.avro.converter.IndexedRecordConverter;
import org.talend.daikon.exception.ExceptionContext;
import org.talend.daikon.exception.error.DefaultErrorCode;

import static javax.servlet.http.HttpServletResponse.SC_OK;

/**
 * Jira reader implementation
 */
public abstract class JiraReader implements Reader<IndexedRecord> {

    private static final Logger LOG = LoggerFactory.getLogger(JiraReader.class);

    /**
     * {@link Source} instance, which had created this {@link Reader}
     */
    private final JiraSource source;

    /**
     * Apache Http components library wrapper, which provides connection methods
     */
    private Rest rest;

    /**
     * Issue adaptor factory
     */
    private IndexedRecordConverter<String, IssueIndexedRecord> factory;

    /**
     * Jira resource to get
     */
    private String resource;

    /**
     * JSON string, which represents result obtained from Jira server
     */
    private String response;

    /**
     * A list of Jira {@link Entity} obtained from Jira server
     */
    private List<Entity> entities;

    /**
     * Index of current Jira entity
     */
    private int entityIndex = 0;

    /**
     * Stores http parameters which are shared between requests
     */
    private final Map<String, Object> sharedParameters;

    /**
     * Denotes this {@link Reader} was started
     */
    private boolean started;

    /**
     * Denotes this {@link Reader} has more records
     */
    private boolean hasMoreRecords;

    /**
     * Return result
     */
    private Result result;

    /**
     * Constructor sets required properties for http connection
     *
     * @param source instance of {@link Source}, which had created this {@link Reader}
     * @param resource REST resource to communicate
     */
    public JiraReader(JiraSource source, String resource) {
        this.source = source;
        this.resource = resource;
        this.sharedParameters = createSharedParameters();
        this.result = new Result();
        setRest(new Rest(source.getHostPort()));
        String userId = source.getUserId();
        if (userId != null && !userId.isEmpty()) {
            rest.setCredentials(userId, source.getUserPassword());
            LOG.debug("{} user is used", userId);
        } else {
            LOG.debug("{} user is used", "Anonymous");
        }

        factory = new IssueAdapterFactory();
        factory.setSchema(source.getSchema());
    }

    /**
     *
     * @param rest initialized REST to set
     */
    void setRest(Rest rest) {
        this.rest = rest;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException in case of exception during http connection
     */
    @Override
    public boolean start() throws IOException {
        started = true;
        makeHttpRequest();
        return hasMoreRecords;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException in case {@link JiraReader#start()} wasn't invoked or
     * in case of exception during http connection
     */
    @Override
    public boolean advance() throws IOException {
        if (!started) {
            throw new IOException("Reader wasn't started");
        }
        entityIndex++;

        if (entityIndex < entities.size()) {
            return true;
        } else {
            hasMoreRecords = false;
            requestMoreRecords();
        }
        return hasMoreRecords;
    }

    /**
     * Tries to get more records
     *
     * @throws IOException in case of exception during http connection
     */
    protected abstract void requestMoreRecords() throws IOException;

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexedRecord getCurrent() throws NoSuchElementException {
        if (!started) {
            throw new NoSuchElementException("Reader wasn't started");
        }
        if (!hasMoreRecords) {
            throw new NoSuchElementException("No records available");
        }

        Entity entity = entities.get(entityIndex);
        String json = entity.getJson();
        return factory.convertToAvro(json);
    }

    /**
     * @return null
     * @throws NoSuchElementException in case {@link JiraReader} wasn't started or
     * there is no more records available
     */
    @Override
    public Instant getCurrentTimestamp() throws NoSuchElementException {
        if (!started) {
            throw new NoSuchElementException("Reader wasn't started");
        }
        if (!hasMoreRecords) {
            throw new NoSuchElementException("No records available");
        }
        return null;
    }

    /**
     * Does nothing
     */
    @Override
    public void close() throws IOException {
        // nothing to be done here
    }

    /**
     * Returns {@link JiraSource} instance
     *
     * @return {@link JiraSource} instance, which had created this {@link Reader}
     */
    @Override
    public JiraSource getCurrentSource() {
        return source;
    }

    /**
     * Returns return values
     *
     * @return map with return values
     */
    @Override
    public Map<String, Object> getReturnValues() {
        return result.toMap();
    }

    /**
     * Makes http request to the server and process its response
     *
     * @throws ComponentException in case of response code is not SC_OK (200)
     * @throws IOException      in case of exception during http connection
     */
    protected void makeHttpRequest() throws IOException {
        Map<String, Object> parameters = prepareParameters();
        JiraResponse jiraResponse = rest.get(resource, parameters);
        if (jiraResponse.getStatusCode() != SC_OK) {
            throw generateJiraException(jiraResponse.getStatusCode(), jiraResponse.getBody());
        }
        response = jiraResponse.getBody();
        entities = processResponse(response);
        if (!entities.isEmpty()) {
            hasMoreRecords = true;
            entityIndex = 0;
            result.totalCount = result.totalCount + entities.size();
        }
    }

    /**
     * Prepares and returns map with http parameters suitable for current REST API resource.
     * Returns shared parameters by default
     *
     * @return map with shared parameters
     */
    protected Map<String, Object> prepareParameters() {
        return sharedParameters;
    }

    /**
     * Process response. Updates http parameters for next request if needed.
     * Retrieves entities from response
     *
     * @param response http response
     * @return {@link List} of entities retrieved from response
     */
    protected abstract List<Entity> processResponse(String response);

    /**
     * Creates and returns map with shared http query parameters
     *
     * @return shared http parameters
     */
    private Map<String, Object> createSharedParameters() {
        Map<String, Object> sharedParameters = new HashMap<>();
        String jqlValue = source.getJql();
        if (jqlValue != null && !jqlValue.isEmpty()) {
            String jqlKey = "jql";
            sharedParameters.put(jqlKey, jqlValue);
        }
        int batchSize = source.getBatchSize();
        String maxResultsKey = "maxResults";
        sharedParameters.put(maxResultsKey, batchSize);
        return Collections.unmodifiableMap(sharedParameters);
    }

    /**
     * Creates and returns RuntimeException with prepared error message
     *
     * @return ComponentException with error message when jira server return not OK status code
     */
    private ComponentException generateJiraException(int code, String errorMessage) {

        return new ComponentException(new DefaultErrorCode(code), ExceptionContext.build()
                .put("message", "Can't get response from server, error code is " + code + "\n" + errorMessage));

    }

}
