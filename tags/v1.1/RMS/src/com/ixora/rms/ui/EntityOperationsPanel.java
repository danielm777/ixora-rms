/*
 * Created on 11-Jul-2004
 */
package com.ixora.rms.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JTabbedPane;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.rms.client.QueryRealizerImpl;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.services.DashboardRepositoryService;
import com.ixora.rms.services.DataEngineService;
import com.ixora.rms.services.DataViewBoardRepositoryService;
import com.ixora.rms.services.DataViewRepositoryService;
import com.ixora.rms.services.MonitoringSessionService;
import com.ixora.rms.ui.artefacts.dashboard.DashboardSelectorPanel;
import com.ixora.rms.ui.artefacts.dataview.DataViewSelectorPanel;
import com.ixora.rms.ui.messages.Msg;

/**
 * EntityOperationsPanel. Tabbed panel hosting other panels
 * that operate on the selected entity.
 * @author Daniel Moraru
 */
public final class EntityOperationsPanel extends OperationsPanel {
	private static final long serialVersionUID = -3388480870830095462L;
	// controls
	private JTabbedPane jTabbedPane;
	private EntityConfigurationPanel panelConfig;

	/**
	 * EntityOperationsPanel constructor used by the log replay view.
	 * @param vc
	 * @param dataEngineService
	 * @param viewService
	 * @param dashboardRepository
	 * @param sessionData
	 * @param treeExplorer
	 * @param ecl
	 * @param qcb
	 * @param qgcb
	 */
	public EntityOperationsPanel(
			RMSViewContainer vc,
			DataEngineService dataEngineService,
			DataViewBoardRepositoryService boardRepository,
			DataViewRepositoryService viewRepository,
			DashboardRepositoryService dashboardRepository,
			SessionModel sessionData,
			SessionTreeExplorer treeExplorer,
			EntityConfigurationPanel.Callback ecl,
			DataViewSelectorPanel.Callback qcb,
			DashboardSelectorPanel.Callback qgcb) {
		super(new BorderLayout());
		this.panelConfig = new EntityConfigurationPanel(vc,
				sessionData, ecl);
		this.panelViews = new DataViewSelectorPanel(
			vc, treeExplorer, boardRepository, viewRepository, dataEngineService, sessionData, qcb);
		this.panelDashboards = new DashboardSelectorPanel(
				vc, dashboardRepository, viewRepository, sessionData, treeExplorer, qgcb);
		add(getJTabbedPane(), BorderLayout.CENTER);
	}

	/**
	 * EntityOperationsPanel constructor used by the live session view.
	 * @param vc
	 * @param dataEngineService
	 * @param viewRepository
	 * @param dashboardRepository
	 * @param sessionService
	 * @param sessionData
	 * @param treeExplorer
	 * @param queryRealizer
	 * @param ecl
	 * @param qcb
	 * @param qgcb
	 */
	public EntityOperationsPanel(
			RMSViewContainer vc,
			DataEngineService dataEngineService,
			DataViewBoardRepositoryService boardRepository,
			DataViewRepositoryService viewRepository,
			DashboardRepositoryService dashboardRepository,
			MonitoringSessionService sessionService,
			SessionModel sessionData,
			SessionTreeExplorer treeExplorer,
			QueryRealizerImpl queryRealizer,
			EntityConfigurationPanel.Callback ecl,
			DataViewSelectorPanel.Callback qcb,
			DashboardSelectorPanel.Callback qgcb) {
		super(new BorderLayout());
		this.panelConfig = new EntityConfigurationPanel(vc,
				sessionService, sessionData, ecl);
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
			jTabbedPane.addTab(MessageRepository.get(Msg.TEXT_CONFIGURATION),
					panelConfig);
			jTabbedPane.addTab(MessageRepository.get(Msg.TEXT_DATAVIEWS),
					panelViews);
			jTabbedPane.addTab(MessageRepository.get(Msg.TEXT_DASHBOARDS),
					panelDashboards);
		}
		return jTabbedPane;
	}

	/**
	 * @return the entity configuration panel.
	 */
	public EntityConfigurationPanel getPanelConfig() {
		return panelConfig;
	}
}
