/*
 * Created on Nov 7, 2004
 */
package com.ixora.rms.ui.dataviewboard;

import java.util.List;
import java.util.Map;

import com.ixora.rms.ResourceId;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.repository.QueryId;
import com.ixora.rms.services.ReactionLogService;
import com.ixora.rms.ui.RMSViewContainer;

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
	 * @return
	 */
	DataViewControl.Callback getDataViewControlCallback();
	/**
	 * @param control
	 * @return a map with view boards grouped by screen names
	 */
	Map<String, List<DataViewBoard>> getAvailableDataViewBoards(DataViewControl control);
    /**
     * @param queryId
     * @return
     */
    boolean isQueryRegistered(QueryId queryId);
    /**
     * @param context
     * @return a data view name that is guaranteed to be valid
     */
    String createValidDataViewName(ResourceId context);    
    /**
     * @return
     */
    SessionArtefactInfoLocator getSessionArtefactLocator();
    /**
     * @return
     */
    ReactionLogService getReactionLogService();
}
