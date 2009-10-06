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
import com.ixora.rms.services.ParserRepositoryService;
import com.ixora.rms.services.ProviderInstanceRepositoryService;
import com.ixora.rms.services.ProviderRepositoryService;
import com.ixora.rms.ui.artefacts.dashboard.DashboardSelectorPanel;
import com.ixora.rms.ui.artefacts.dataview.DataViewSelectorPanel;
import com.ixora.rms.ui.artefacts.provider.ProviderInstancePanel;
import com.ixora.rms.ui.messages.Msg;

/**
 * AgentOperationsPanel. Tabbed panel hosting other panels
 * that operate on the selected agent.
 * @author Daniel Moraru
 */
public final class AgentOperationsPanel extends OperationsPanel {
// controls
	private JTabbedPane jTabbedPane;
	private AgentConfigurationPanelStandAlone panelConfig;
	private ProviderInstancePanel panelProviderInstances;

	/** Log replay mode flag */
    private boolean logReplayMode;

	/**
	 * AgentOperationsPanel constructor used by the log replay view.
	 * @param vc
	 * @param dataEngineService
	 * @param viewsRepository
	 * @param dashboardRepository
	 * @param sessionData
	 * @param queryRealizer
	 * @param qcb
	 * @param qgcb
	 */
	public AgentOperationsPanel(
			RMSViewContainer vc,
			DataEngineService dataEngineService,
			DataViewBoardRepositoryService boardRepository,
			DataViewRepositoryService viewsRepository,
			DashboardRepositoryService dashboardRepository,
			SessionModel sessionData,
			SessionTreeExplorer treeExplorer,
			DataViewSelectorPanel.Callback qcb,
			DashboardSelectorPanel.Callback qgcb) {
		super(new BorderLayout());
		this.logReplayMode = true;
		this.panelConfig = new AgentConfigurationPanelStandAlone(
				vc,
				sessionData, true);
		this.panelViews = new DataViewSelectorPanel(
			vc, treeExplorer, boardRepository, viewsRepository, dataEngineService, sessionData, qcb);
		this.panelDashboards = new DashboardSelectorPanel(
				vc, dashboardRepository, viewsRepository, sessionData, treeExplorer, qgcb);
		add(getJTabbedPane(), BorderLayout.CENTER);
	}


	/**
	 * Constructor used by live session.
	 * @param vc
	 * @param dataEngineService
	 * @param viewRepository
	 * @param dashboardRepository
	 * @param providerInstanceRepository
	 * @param providerRepository
	 * @param parserRepository
	 * @param sessionService
	 * @param sessionData
	 * @param treeExplorer
	 * @param queryRealizer
	 * @param qcb
	 * @param qgcb
	 */
	public AgentOperationsPanel(
			RMSViewContainer vc,
			DataEngineService dataEngineService,
			DataViewBoardRepositoryService boardRepository,
			DataViewRepositoryService viewRepository,
			DashboardRepositoryService dashboardRepository,
			ProviderInstanceRepositoryService providerInstanceRepository,
			ProviderRepositoryService providerRepository,
			ParserRepositoryService parserRepository,
			MonitoringSessionService sessionService,
			SessionModel sessionData,
			SessionTreeExplorer treeExplorer,
			QueryRealizerImpl queryRealizer,
			DataViewSelectorPanel.Callback qcb,
			DashboardSelectorPanel.Callback qgcb) {
		super(new BorderLayout());
		this.panelConfig = new AgentConfigurationPanelStandAlone(
				vc,
				sessionService, sessionData, true);
		this.panelViews = new DataViewSelectorPanel(
			vc, treeExplorer, boardRepository, viewRepository, dataEngineService, sessionData, queryRealizer, qcb);
		this.panelDashboards = new DashboardSelectorPanel(
				vc, dashboardRepository, viewRepository, sessionData, queryRealizer, treeExplorer, qgcb);
		this.panelProviderInstances = new ProviderInstancePanel(
				vc, providerInstanceRepository, providerRepository, parserRepository, sessionData);
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
			if(!logReplayMode) {
			    jTabbedPane.addTab(MessageRepository.get(Msg.TEXT_PROVIDERS),
					panelProviderInstances);
			}
		}
		return jTabbedPane;
	}

	/**
	 * @return the agent configuration panel.
	 */
	public AgentConfigurationPanelStandAlone getPanelConfig() {
		return panelConfig;
	}

	/**
	 * @return the provider instance panel
	 */
	public ProviderInstancePanel getPanelProviderInstances() {
		return panelProviderInstances;
	}
}
