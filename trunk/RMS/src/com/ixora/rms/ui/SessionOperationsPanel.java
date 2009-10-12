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
 * SchemeOperationsPanel. Tabbed panel hosting other panels
 * that operate on the selected scheme.
 * @author Daniel Moraru
 */
public final class SessionOperationsPanel extends OperationsPanel {
	private static final long serialVersionUID = -9084264894328724778L;
	// controls
	private JTabbedPane jTabbedPane;
	/**
	 * SchemeOperationsPanel constructor used by the log replay view.
	 * @param vc
	 * @param dataEngineService
	 * @param viewRepository
	 * @param dashboardRepository
	 * @param sessionData
	 * @param treeExplorer
	 * @param ecl
	 * @param qsl
	 */
	public SessionOperationsPanel(
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
		this.panelViews = new DataViewSelectorPanel(
			vc, treeExplorer, boardRepository, viewRepository, dataEngineService, sessionData, qcb);
		this.panelDashboards = new DashboardSelectorPanel(
				vc, dashboardRepository, viewRepository, sessionData, treeExplorer, qgcb);
		add(getJTabbedPane(), BorderLayout.CENTER);
	}

	/**
	 * SchemeOperationsPanel constructor used by the live session view.
	 * @param vc
	 * @param dataEngineService
	 * @param queryService
	 * @param dashboardRepository
	 * @param sessionData
	 * @param treeExplorer
	 * @param queryRealizer
	 * @param ecl
	 * @param qsl
	 */
	public SessionOperationsPanel(
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
			jTabbedPane.addTab(MessageRepository.get(Msg.TEXT_DATAVIEWS),
					panelViews);
			jTabbedPane.addTab(MessageRepository.get(Msg.TEXT_DASHBOARDS),
					panelDashboards);
		}
		return jTabbedPane;
	}
}
