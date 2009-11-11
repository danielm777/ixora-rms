/**
 * 02-Feb-2006
 */
package com.ixora.rms.ui.views.session.activation;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.history.HistoryPanel;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.rms.agents.AgentActivationData;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.services.ProviderInstanceRepositoryService;
import com.ixora.rms.ui.RMSViewContainer;

/**
 * @author Daniel Moraru
 */
public class AgentActivationDataPanelWithHistory extends JPanel {
	private static final long serialVersionUID = 8512928845397393705L;
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(AgentActivationDataPanelWithHistory.class);
	private HistoryPanel fHistoryPanel;
	private EventHandler fEventHandler;
	private AgentActivationDataPanel fAgentActivationDataPanel;
	private AgentInstallationData fLastAgentInstallationData;

	private class EventHandler implements HistoryPanel.Listener {
		/**
		 * @see com.ixora.common.ui.history.HistoryPanel.Listener#selected(com.ixora.common.xml.XMLExternalizable)
		 */
		public void selected(XMLExternalizable obj) {
			handleAgentActivationDataFromHistory((AgentActivationData)obj);
		}
	}

	/**
	 * @param vc
	 * @param rmsPrs
	 */
	public AgentActivationDataPanelWithHistory(RMSViewContainer vc,
            ProviderInstanceRepositoryService rmsPrs) {
		super(new BorderLayout());
		fEventHandler = new EventHandler();
		fHistoryPanel = new HistoryPanel(fEventHandler);
		fAgentActivationDataPanel = new AgentActivationDataPanel(vc, rmsPrs);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(fHistoryPanel, BorderLayout.EAST);
		panel.add(new JPanel(), BorderLayout.CENTER);
		add(panel, BorderLayout.NORTH);
		add(fAgentActivationDataPanel, BorderLayout.CENTER);
	}

   /**
     * Applies changes to instance data.
     * @throws InvalidConfiguration
     */
    public AgentActivationData applyChanges() throws InvalidConfiguration {
    	return (AgentActivationData)fAgentActivationDataPanel.applyChanges().clone();
    }

    /**
     * @param data
     * @param agentInstallationData
     * @throws SecurityException
     * @throws RMSException
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
	public void setAgentInstanceData(AgentActivationData data,
	        AgentInstallationData agentInstallationData)
				throws SecurityException, RMSException, IllegalArgumentException,
					ClassNotFoundException, NoSuchMethodException,
					InstantiationException, IllegalAccessException,
					InvocationTargetException {
		try {
			fHistoryPanel.setHistoryGroup("activation_" + agentInstallationData.getAgentInstallationId());
		} catch(Exception e) {
			logger.error("Failed to set the history for agent " + agentInstallationData.getAgentInstallationId(), e);
		}
		fAgentActivationDataPanel.setAgentInstanceData(data, agentInstallationData);
		fLastAgentInstallationData = agentInstallationData;
	}

	/**
	 * @param configuration
	 */
	private void handleAgentActivationDataFromHistory(AgentActivationData aad) {
		try {
			fAgentActivationDataPanel.setAgentInstanceData(aad, fLastAgentInstallationData);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}
