/*
 * Created on 28-Jul-2004
 */
package com.ixora.rms.client.model;

import com.ixora.rms.dataengine.definitions.QueryDef;

/**
 * Class that holds model data on a query.
 */
public final class QueryInfoImpl extends ArtefactInfoImpl
			implements QueryInfo {
	/** Query */
	private QueryDef query;

	/**
	 * @param messageRepositoryused to locate the translated
	 * name and description of the query
	 * @param query the cube descriptor
	 */
	public QueryInfoImpl(String messageRepository,
			QueryDef query, SessionModel model) {
	    super(messageRepository,
	            query.getIdentifier(), query.getIdentifier(), model);
		this.query = query;
	}

	/**
	 * @return the query
	 */
	public QueryDef getQuery() {
		return query;
	}
}