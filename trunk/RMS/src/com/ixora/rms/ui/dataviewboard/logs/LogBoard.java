/**
 * 29-Jan-2006
 */
package com.ixora.rms.ui.dataviewboard.logs;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;

import com.ixora.rms.ResourceId;
import com.ixora.rms.client.QueryRealizer;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.dataengine.Style;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.services.DataEngineService;
import com.ixora.rms.services.ReactionLogService;
import com.ixora.rms.ui.dataviewboard.DataViewBoard;
import com.ixora.rms.ui.dataviewboard.DataViewControl;
import com.ixora.rms.ui.dataviewboard.DataViewControlContext;
import com.ixora.rms.ui.dataviewboard.exception.FailedToCreateControl;

/**
 * @author Daniel Moraru
 */
public class LogBoard extends DataViewBoard {

	/**
	 * @param qr
	 * @param des
	 * @param rls
	 * @param locator
	 */
	public LogBoard(QueryRealizer qr, DataEngineService des, ReactionLogService rls, SessionArtefactInfoLocator locator) {
		super(qr, des, rls, locator, "",
				true, //resizable
				true, //closable
				true, //maximizable
				true);// iconifiable
		setSize(new Dimension(600, 550));
		fBoard = new JPanel(new BorderLayout());
		fBoard.setOpaque(true);
		setContentPane(fBoard);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#createControl(com.ixora.rms.ui.dataviewboard.DataViewControlContext, com.ixora.rms.ResourceId, com.ixora.rms.repository.DataView)
	 */
	protected DataViewControl createControl(DataViewControlContext context,
			ResourceId resourceContext, DataView view)
			throws FailedToCreateControl {
		return new LogControl(this, fEventHandler, context, fLocator, fReactionLog, resourceContext, view);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#createDataViewForCounter(com.ixora.rms.ResourceId, java.lang.String, java.lang.String, com.ixora.rms.dataengine.Style)
	 */
	protected DataView createDataViewForCounter(ResourceId counter, String name, String desc, Style style) {
		return new LogCounterDataView(counter);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#createDataViewForCounters(com.ixora.rms.ResourceId, java.util.List, java.lang.String, java.util.List)
	 */
	protected DataView createDataViewForCounters(ResourceId context,
			List<ResourceId> counters, String viewName,
			List<ResourceId> rejected) {
		return null; // not supported
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#reachedMaximumControls()
	 */
	public boolean reachedMaximumControls() {
		return fControls.size() == 1;
	}
}
