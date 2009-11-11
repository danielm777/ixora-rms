/*
 * Created on 13-Aug-2004
 */
package com.ixora.rms.client.model;

import com.ixora.rms.ResourceId;
import com.ixora.rms.repository.DataView;

/**
 * @author Daniel Moraru
 */
public interface DataViewModelHelper {
    /**
     * @param id
     */
    void rollbackDataViews(ResourceId id);

    /**
     * @param id
     * @param name
     */
    void rollbackDataView(ResourceId id, String name);

	/**
	 * Sets the flag for the given data view.
	 * @param flag
	 * @param context
	 * @param dv
	 * @param value
	 * @param commit
	 */
	public void setDataViewFlag(
	        	int flag,
				ResourceId context,
				String dv,
				boolean value,
				boolean commit);

    /**
     * Sets the dashboards associated with the given resource.
     * @param id a valid, non regex resource id
     * @param views
     */
    void setDataViews(ResourceId id, DataView[] views);

    /**
     * Adds the given data view to the given context.
     * @param id
     * @param dv
     */
    void addDataView(ResourceId id, DataView dv);

    /**
     * Removes the data view with the given name from the given context.
     * @param id
     * @param dv
     */
    void removeDataView(ResourceId id, String dv);

    /**
     * @param context
     * @param dv
     * @return true if the query of the given view is ready, i.e has
     * all counters presents and enabled
     */
    boolean isDataViewReady(ResourceId context, DataView dv);

    /**
     * @param context
     */
    void recalculateDataViewsStatus(ResourceId context);
}
