/*
 * Created on 11-Jul-2004
 */
package com.ixora.rms.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JTabbedPane;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.rms.client.QueryRealizer;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.services.DashboardRepositoryService;
import com.ixora.rms.services.DataEngineService;
import com.ixora.rms.services.DataViewBoardRepositoryService;
import com.ixora.rms.services.DataViewRepositoryService;
import com.ixora.rms.ui.artefacts.dashboard.DashboardSelectorPanel;
import com.ixora.rms.ui.artefacts.dataview.DataViewSelectorPanel;
import com.ixora.rms.ui.messages.Msg;

/**
 * HostOperationsPanel. Tabbed panel hosting other panels
 * that operate on the selected host.
 * @author Daniel Moraru
 */
public final class HostOperationsPanel extends OperationsPanel {
// controls
	private JTabbedPane jTabbedPane;
	private HostDetailsPanel panelDetails;
	/** Log replay mode flag */
	private boolean logReplayMode;

	/**
	 * HostOperationsPanel constructor used by a log replay session.
	 * @param vc
	 * @param dataEngineService
	 * @param viewRepository
	 * @param sessionData
	 * @param treeExplorer
	 * @param ecl
	 * @param qcb
	 * @param qgcb
	 */
	public HostOperationsPanel(
			RMSViewContainer vc,
			DataEngineService dataEngineService,
			DataViewBoardRepositoryService boardRepository,
			DataViewRepositoryService viewRepository,
			DashboardRepositoryService dashboardRepository,
			SessionModel sessionData,
			SessionTreeExplorer treeExplorer,
			DataViewSelectorPanel.Callback qcb,
			DashboardSelectorPanel.Callback qgcb) {
		super(new BorderLayout());
		this.logReplayMode = true;
		this.panelViews = new DataViewSelectorPanel(
			vc, treeExplorer, boardRepository, viewRepository, dataEngineService, sessionData, qcb);
		this.panelDashboards = new DashboardSelectorPanel(
				vc, dashboardRepository, viewRepository, sessionData, treeExplorer, qgcb);
		add(getJTabbedPane(), BorderLayout.CENTER);
	}

	/**
	 * HostOperationsPanel constructor used by a live session.
	 * @param vc
	 * @param dataEngineService
	 * @param viewRepository
	 * @param sessionData
	 * @param treeExplorer
	 * @param queryRealizer
	 * @param ecl
	 * @param qcb
	 * @param qgcb
	 */
	public HostOperationsPanel(
			RMSViewContainer vc,
			DataEngineService dataEngineService,
			DataViewBoardRepositoryService boardRepository,
			DataViewRepositoryService viewRepository,
			DashboardRepositoryService dashboardRepository,
			SessionModel sessionData,
			SessionTreeExplorer treeExplorer,
			QueryRealizer queryRealizer,
			DataViewSelectorPanel.Callback qcb,
			DashboardSelectorPanel.Callback qgcb) {
		super(new BorderLayout());
		this.panelDetails = new HostDetailsPanel();
		this.panelViews = new DataViewSelectorPanel(
			vc, treeExplorer, boardRepository, viewRepository, dataEngineService, sessionData, queryRealizer, qcb);
		this.panelDashboards = new DashboardSelectorPanel(
				vc, dashboardRepository, viewRepository, sessionData, queryRealizer, treeExplorer, qgcb);
		add(getJTabbedPane(), BorderLayout.CENTER);
	}

	/**
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if(jTabbedPane == null) {
			jTabbedPane = UIFactoryMgr.createTabbedPane();
			jTabbedPane.setMinimumSize(new Dimension(0, 50));
			if(!logReplayMode) {
			    jTabbedPane.addTab(MessageRepository.get(Msg.TEXT_DETAILS),
			            panelDetails);
			}
			jTabbedPane.addTab(MessageRepository.get(Msg.TEXT_DATAVIEWS),
					panelViews);
			jTabbedPane.addTab(MessageRepository.get(Msg.TEXT_DASHBOARDS),
					panelDashboards);
		}
		return jTabbedPane;
	}

	/**
	 * @return the agent configuration panel.
	 */
	public HostDetailsPanel getPanelDetails() {
		return panelDetails;
	}
}
