package com.ixora.rms.ui.dataviewboard;

import java.util.Set;

import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.services.ReactionLogService;
import com.ixora.rms.ui.RMSViewContainer;

/**
 * Context for view boards.
 * @author Daniel Moraru
 */
public interface DataViewBoardContext {
	/**
	 * @return
	 */
	DataViewBoard.Callback getDataViewBoardCallback();
	/**
	 * @return the context for controls
	 */
	DataViewControlContext getDataViewControlContext();
	/**
	 * @return the view container
	 */
	RMSViewContainer getViewContainer();	
    /**
     * @return
     */
    SessionArtefactInfoLocator getSessionArtefactLocator();
    /**
     * @return
     */
    ReactionLogService getReactionLogService();
    /**
     * @param board
     * @return
     */
    Set<String> getAvailableDataViewScreens(DataViewBoard board);
}
