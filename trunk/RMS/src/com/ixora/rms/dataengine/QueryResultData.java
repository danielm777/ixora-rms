/*
 * Created on 20-Aug-2004
 *
 */
package com.ixora.rms.dataengine;

import com.ixora.rms.ResourceId;
import com.ixora.rms.CounterType;
import com.ixora.rms.client.locator.SessionResourceInfo;
import com.ixora.rms.data.CounterValue;

/**
 * QueryResultData
 * Encapsulates the value for a result of a Query as a
 * pair of QueryResult/CounterValue. If this QueryResult is a resource, rather than
 * a function, also holds the completed ResourceId (after matching the regexp).
 */
public class QueryResultData {
	private QueryResult	fQueryResult;
	private CounterValue fValue;
	private CounterType fType;
	private ResourceId fMatchedResourceId;
    private String[] fCaptures;

    /**
     * Constructs a QueryResultData
     * @param qr
     * @param ct
     * @param cv
     * @param matchedResourceId
     * @param captures
     */
	public QueryResultData(QueryResult qr, CounterType ct, CounterValue cv,
            ResourceId matchedResourceId, String[] captures) {
		this.fQueryResult = qr;
		this.fValue = cv;
		this.fType = ct;
		this.fMatchedResourceId = matchedResourceId;
        this.fCaptures = captures;
	}

    /**
     * @return Returns the queryResult.
     */
    public QueryResult getQueryResult() {
        return fQueryResult;
    }

    /**
     * @return Returns the value.
     */
    public CounterValue getValue() {
        return fValue;
    }

    /**
     * @return the counter type
     */
    public CounterType getType() {
    	return fType;
    }

    /**
     * @return Returns the matched version of the regexp resource ID, or null
     * for functions.
     */
    public ResourceId getMatchedResourceId() {
        return fMatchedResourceId;
    }

    /**
     * Replaces IName and Name of the QueryResult with localized versions. After
     * calling this method it is safe to call QueryResult.getStyle().getIName() and
     * you will obtain a localized token.
     */
    public void localizeTokens(SessionResourceInfo rInfo) {
    	this.fQueryResult.localize(rInfo,
    			this.fMatchedResourceId, this.fCaptures, false);
    }

}
