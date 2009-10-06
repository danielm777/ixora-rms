/*
 * Created on 24-Dec-2003
 */
package com.ixora.rms.ui.artefacts.dashboard;

import java.util.List;

import com.ixora.rms.client.model.DashboardInfo;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.artefacts.SelectableArtefactTableModel;

/**
 * The model for the queries table.
 * @author Daniel Moraru
 */
public final class DashboardTableModel
	extends SelectableArtefactTableModel {

	/**
	 * Constructor.
	 * @param vc
	 * @param sm
	 * @param logReplayMode
	 */
	public DashboardTableModel(RMSViewContainer vc,
	        SessionModel sm, boolean logReplayMode) {
		super(vc, sm, logReplayMode);
	}

	/**
	 * @return the dashboards that must be realized
	 * (enabled but not committed)
	 * List of QueryGroupInfo
	 */
	public List getDashboardsToRealize() {
		return getArtefactsToRealize();
	}

	/**
	 * @return the dashboards that must be unrealized
	 * (disabled but not committed)
	 * List of QueryGroupInfo
	 */
	public List getDashboardsToUnRealize() {
		return getArtefactsToUnRealize();
	}

    /**
     * @see com.ixora.rms.ui.artefacts.SelectableArtefactTableModel#setArtefactEnabled(int, boolean)
     */
    protected void setArtefactEnabled(int rowIndex, boolean e) {
		DashboardInfo ginfo = (DashboardInfo)this.fArtefactData.get(rowIndex);
		fSessionModel.getDashboardHelper().setDashboardFlag(
		        DashboardInfo.ENABLED,
				fContext,
				ginfo.getDashboard().getName(),
				e, false);
    }

	/**
	 * @param sel
	 */
	public void enableDashboard(int sel) {
		setArtefactEnabled(sel, true);
	}
}