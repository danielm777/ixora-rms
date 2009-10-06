/*
 * Created on Nov 7, 2004
 */
package com.ixora.rms.ui.dataviewboard;

import com.ixora.rms.ResourceId;
import com.ixora.rms.repository.QueryId;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.dataviewboard.handler.DataViewPlotter;

/**
 * This is the execution context for data view controls. Through this interface they can get
 * access to various services.
 * @author Daniel Moraru
 */
public interface DataViewControlContext {
	/**
	 * @return the DataViewPlotter
	 */
	DataViewPlotter getDataViewPlotter();
	/**
	 * @return the view container
	 */
	RMSViewContainer getViewContainer();
    /**
     * @param queryId
     * @return
     */
    boolean isQueryRegistered(QueryId queryId);
    /**
     * @param context
     * @return a data view name that is guarranteed to be valid
     */
    String createValidDataViewName(ResourceId context);
}
