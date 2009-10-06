/*
 * Created on 12-Dec-2003
 */
package com.ixora.rms.services;

import com.ixora.common.Service;
import com.ixora.rms.dataengine.Cube;
import com.ixora.rms.dataengine.external.QueryListener;
import com.ixora.rms.repository.QueryId;

/**
 * @author Daniel Moraru
 * @author Cristinan Costache
 */
/*
 * Modification history
 * --------------------------------------------------
 * 14 Nov 2004 - DM refactored the class to allow the registration of several listeners for the same query;
 * this allows Notification, Advisors and any other clients to register their own listeners
 * for a particular query
 */
public interface DataEngineService extends Service {
	/**
	 * Adds a query based on a cube's definition, and attaches its
	 * output to a listener
	 * @param qid the query id used by the client to refere to the registered query
	 * @param cube query to be registered with the data engine
	 * @param listener query executor listener
	 */
	void addQuery(QueryId qid, Cube cube, QueryListener listener);
	/**
	 * Unregisters the query with the given id from the data engine.
	 * @param qid
	 */
	void removeQuery(QueryId qid);
	/**
	 * Adds a query listener to the query registered with the given id. If no query is found, this method
	 * has no effect.
	 * @param qid the query id with which the query was registered with the data engine
	 * @param listener
	 */
	void addQueryListener(QueryId qid, QueryListener listener);
	/**
	 * Removes a query listener. If this the last listener the query will be unregistered
	 * from the data engine and any subsequent calls to <code>isQueryRegistered(QueryId)</code>
	 * will return false.
	 * @param qid the query id with which the query was registered with the data engine
	 * @param listener
	 */
	void removeQueryListener(QueryId qid, QueryListener listener);
	/**
	 * Returns true if a query has been registered with the given id.
	 * @param qid
	 * @return
	 */
	boolean isQueryRegistered(QueryId qid);
	/**
	 * Sets the mode of the data engine.
	 * @param lrm
	 */
	void setLogReplayMode(boolean lrm);
}
