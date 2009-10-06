/*
 * Created on 09-May-2004
 */
package com.ixora.rms.client.model;

import com.ixora.rms.ResourceId;
import com.ixora.rms.repository.DashboardId;
import com.ixora.rms.repository.DataViewId;

/**
 * Locates resource information.
 * @author Daniel Moraru
 */
public interface ResourceInfoLocator {
	/**
	 * @param id the resource id identifying the resources for
	 * which information is requested.
	 * @return the resource info for all resources
	 * matching the given id
	 */
	ResourceInfo[] getResourceInfo(ResourceId id, boolean aggresive);

	/**
	 * @param id
	 * @return
	 */
	DashboardInfo getDashboardInfo(DashboardId id, boolean aggresive);

	/**
	 * @param id
	 * @return
	 */
	DataViewInfo getDataViewInfo(DataViewId id, boolean aggresive);

    /**
     * @param id
     * @return
     */
    CounterInfo getCounterInfo(ResourceId id, boolean aggresive);
}
