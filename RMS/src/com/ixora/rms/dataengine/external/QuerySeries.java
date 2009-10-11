/*
 * Created on 16-Jan-2005
 */
package com.ixora.rms.dataengine.external;

import java.util.Iterator;
import java.util.LinkedList;

import com.ixora.rms.dataengine.QueryResultData;
import com.ixora.rms.exception.QueryNoSuchResultException;


/**
 * QuerySeries
 * A list of QueryResult/CounterValue pairs (encapsulated in
 * QueryResultData objects).
 */
public class QuerySeries extends LinkedList<QueryResultData> {
	private static final long serialVersionUID = -3417035197966571993L;

	/**
	 * Constructs a QuerySeries
	 */
	public QuerySeries() {
	}

    /**
     * Searches for a QueryResult specified by ID (the one used
     * when defining style) and returns its data. Throws exception if no
     * such result is defined by the query.
     *
     * @param id the ID of the result for which to return data
     * @return data associated with the specified result
     * @throws QueryNoSuchResultException if result is not defined in this query
     */
	public QueryResultData getData(String id) throws QueryNoSuchResultException {
		for (Iterator<QueryResultData> it = this.iterator(); it.hasNext();) {
			QueryResultData qrd = it.next();
			if (qrd.getQueryResult().getStyle().getID().equals(id))
				return qrd;
		}
		throw new QueryNoSuchResultException(id);
	}

	/**
	 * For debug purposes
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = "";
		for (QueryResultData queryResultData : this) {
			if (queryResultData != null) {
				s += queryResultData.getQueryResult().getStyle().getName();
				s += "=";
				s += String.valueOf(queryResultData.getValue());
				s += "\n";
			} else {
				s += "null\n";
			}
		}
		s += "\n";
		return s;
	}
}
