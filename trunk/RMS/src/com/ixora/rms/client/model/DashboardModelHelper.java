/*
 * Created on 13-Aug-2004
 */
package com.ixora.rms.client.model;

import java.util.Collection;
import java.util.Map;

import com.ixora.rms.ResourceId;
import com.ixora.rms.repository.Dashboard;
import com.ixora.rms.repository.DashboardId;

/**
 * @author Daniel Moraru
 */
public interface DashboardModelHelper {
	/**
	 * Commits the given dashboard.
	 * @param context
	 * @param dashboard
	 */
	void commitDashboard(ResourceId context, String dashboard);
    /**
     * @param id
     */
    void rollbackDashboards(ResourceId id);

	/**
	 * Sets the flag for the given dashboard.
	 * @param flag
	 * @param context
	 * @param db
	 * @param value
	 * @param commit
	 */
	public void setDashboardFlag(
	        	int flag,
				ResourceId context,
				String db,
				boolean value,
				boolean commit);

    /**
     * Sets the dashboards associated with the given resource.
     * @param id a valid, non regex resource id
     * @param q
     */
    void setDashboards(ResourceId id, Dashboard[] groups);

    /**
     * Adds the given dashboard to the given context.
     * @param id
     * @param db
     */
    void addDashboard(ResourceId id, Dashboard db);

    /**
     * Removes the dashboard with the given name from the given context.
     * @param id
     * @param db
     */
    void removeDashboard(ResourceId id, String db);

    /**
     * Recalculates the enabled status of the given resource's dashboards
     * by checking all queries involved in each dashboard.
     * @param context
     */
    void recalculateDashboardsStatus(ResourceId context);

    /**
     * Key: ResourceId context
     * Value: Collection of DashboardInfo
     * @return all dashboards that are realizable
     */
    Map<ResourceId, Collection<DashboardInfo>> getAllDashboardsToRealize();

    /**
     * Key: ResourceId context
     * Value: Collection of DashboardInfo
     * @return all dashboards that are unrealizable
     */
    Map<ResourceId, Collection<DashboardInfo>> getAllDashboardsToUnRealize();

    /**
     * @param flag
     * @return all committed dashboards that maches the given flag
     */
    Collection<DashboardId> getAllCommittedDashboards(int flag);

	/**
	 * Returns true if all queries required by the given
	 * dashboard are present, are enabled and committed.<br>
	 * Used by the log replay view to filter out dashboards whose
	 * queries are not available.
	 * @return
	 */
	public boolean isDashboardReady(ResourceId context, Dashboard queryGroup);
}
