/*
 * Created on 16-Jan-2005
 */
package com.ixora.rms.dataengine.external;

import java.util.LinkedList;

/**
 * QueryData
 * As a result of processing the query, the listener receives
 * this structure which is a list of Series.
 *
 * Data inside this object is organized into QuerySeries, which store
 * the actual pairs of QueryResult/CounterValue. Each QueryResult can
 * be either a counter, or a function based on counters.
 *
 * <pre>
 *               | QueryResult 1 | QueryResult 2 | QueryResult 3 | ...
 * --------------+---------------+---------------+---------------+-----
 * QuerySeries 1 |      val11    |     val12     |     val13     |
 * --------------+---------------+---------------+---------------+-----
 * QuerySeries 2 |      val21    |     val22     |     val23     |
 * --------------+---------------+---------------+---------------+-----
 *      ...
 * </pre>
 */
public class QueryData extends LinkedList<QuerySeries> {

	/**
	 * Adds a Series object to the list
	 */
	public void addSeries(QuerySeries s) {
		add(s);
	}

	/**
	 * Returns the Series object at index, or null
	 */
	public QuerySeries getSeries(int index) {
		if (index >= size())
			return null;
		return get(index);
	}

	/**
	 * For debug purposes
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = "-----------------------------";
		for (QuerySeries querySeries : this) {
			s += "\n";
			s += querySeries.toString();
		}
		s += "\n-----------------------------\n";
		return s;
	}
}
