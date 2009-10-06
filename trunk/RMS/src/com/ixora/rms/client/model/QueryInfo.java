/*
 * Created on 28-Jul-2004
 */
package com.ixora.rms.client.model;

import com.ixora.rms.dataengine.definitions.QueryDef;


/**
 * Query model info.
 * @author Daniel Moraru
 */
public interface QueryInfo extends ArtefactInfo {
	/**
	 * @return the query
	 */
	QueryDef getQuery();
}
