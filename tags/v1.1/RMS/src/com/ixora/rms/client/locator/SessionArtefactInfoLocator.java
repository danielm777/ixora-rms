/*
 * Created on 26-Feb-2005
 */
package com.ixora.rms.client.locator;

import com.ixora.rms.ResourceId;
import com.ixora.rms.repository.DashboardId;
import com.ixora.rms.repository.DataViewId;

/**
 * @author Daniel Moraru
 */
public interface SessionArtefactInfoLocator {
    /**
     * @param r
     * @return info an the given resource
     */
	SessionResourceInfo getResourceInfo(ResourceId r);
    /**
     * @param vid
     * @return info on the given view
     */
	SessionDataViewInfo getDataViewInfo(DataViewId vid);
    /**
     * @param did
     * @return info on the given dashboard
     */
	SessionDashboardInfo getDashboardInfo(DashboardId did);
}
