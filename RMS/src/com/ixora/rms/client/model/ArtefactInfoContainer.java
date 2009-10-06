/*
 * Created on 28-Jul-2004
 */
package com.ixora.rms.client.model;

import java.util.Collection;


/**
 * ArtefactInfoContainer.
 * @author Daniel Moraru
 */
public interface ArtefactInfoContainer {
	/**
	 * @param queryId
	 * @return the query with the given id
	 */
	QueryInfo getQueryInfo(String queryId);
	/**
	 * @return all queries
	 */
	Collection<QueryInfo> getQueryInfo();
	/**
	 * @return the queries that are enabled but not committed.
	 */
	Collection<QueryInfo> getQueriesToRealize();
	/**
	 * @return the queries that are not enabled and not committed.
	 * Collection of QueryInfo
	 */
	Collection<QueryInfo> getQueriesToUnRealize();
	/**
	 * @return the dashboards that are enabled but not committed.
	 */
	Collection<DashboardInfo> getDashboardsToRealize();
	/**
	 * @return the dashboards that are not enabled and not committed.
	 */
	Collection<DashboardInfo> getDashboardsToUnRealize();
	/**
	 * @param db
	 * @return the dashboard with the given name
	 */
	DashboardInfo getDashboardInfo(String db);
	/**
	 * @return all dashboards
	 */
	Collection<DashboardInfo> getDashboardInfo();
	/**
	 * @return the data views that are enabled but not committed.
	 */
	Collection<DataViewInfo> getDataViewsToRealize();
	/**
	 * @return the data views that are not enabled and not committed.
	 * Collection of DataViewInfo
	 */
	Collection<DataViewInfo> getDataViewsToUnRealize();
	/**
	 * @param dv
	 * @return the data view with the given name
	 */
	DataViewInfo getDataViewInfo(String dv);
	/**
	 * @return all data views
	 */
	Collection<DataViewInfo> getDataViewInfo();
    /**
     * Returns true if there are uncommitted queries.
     * @return
     */
    boolean uncommittedVisibleQueries();
    /**
     * Returns true if there are uncommitted visible dashboards.
     * @return
     */
    boolean uncommittedVisibleDashboards();
    /**
     * Returns true if there are uncommitted visible data views.
     * @return
     */
    boolean uncommittedVisibleDataViews();
	/**
	 * Returns true if there are data views inside this container.
	 * @return
	 */
    boolean hasDataViews();
	/**
	 * Returns true if there are dashboards inside this container.
	 * @return
	 */
	boolean hasDashboards();
	/**
	 * Returns true if there are queries inside this container.
	 * @return
	 */
	boolean hasQueries();
    /**
     * @return true if identifiers must be shown
     */
    boolean showIdentifiers();
    /**
     * @return
     */
    boolean hasDataViewsWithReactions();
}
