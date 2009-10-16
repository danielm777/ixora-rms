/*
 * Created on 25-Jun-2005
 */
package com.ixora.rms.ui.views.session.activation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.ixora.rms.RMSComponent;
import com.ixora.rms.RMSConfigurationConstants;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.ComponentConfiguration;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentActivationData;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentLocation;
import com.ixora.rms.agents.ui.AgentCustomConfigurationPanel;
import com.ixora.rms.agents.ui.AgentCustomConfigurationPanelContext;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.repository.VersionableAgentInstallationData;
import com.ixora.rms.services.ProviderInstanceRepositoryService;
import com.ixora.rms.ui.AgentConfigurationPanel;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.messages.Msg;

/**
 * Panel that edits agent activation data.
 * @author Daniel Moraru
 */
public class AgentActivationDataPanel extends JPanel {
	private static final long serialVersionUID = -8636582750801633007L;
	private static final String LABEL_SELECT_LOCATION = MessageRepository.get(Msg.TEXT_SELECT_LOCATION);
    private static final String LABEL_SELECT_SYSTEM_VERSION = MessageRepository.get(Msg.TEXT_SELECT_SYSTEM_VERSION);
    /** RMS configuration service */
    private ComponentConfiguration fRMSConfig;
    /** Agent config panel */
    private AgentConfigurationPanel fPanelAgentConfig;
    /** Possible deployment locations */
    private JComboBox fComboBoxLocation;
    /** System under observation version */
    private JComboBox fComboBoxSUOVersion;
    /** In edit agent activation data */
    private AgentActivationData fInEditData;
	/** Context for the agent configuration panels */
	private AgentCustomConfigurationPanelContext fAgentConfPanelContext;
    private AgentInstallationData fAgentInstallationData;
    private FormPanel fForm;
    private VersionableAgentInstallationData fCurrentVersionableAgentInstallationData;

    /**
     * EventHandler.
     */
    private final class EventHandler implements ItemListener {
        /**
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        public void itemStateChanged(ItemEvent e) {
            if(e.getSource() == fComboBoxSUOVersion) {
                handleSytemVersionChanged(e);
            }
        }
    }

    /**
     * @param vc
     * @param rmsPrs
     */
    public AgentActivationDataPanel(
            RMSViewContainer vc,
            ProviderInstanceRepositoryService rmsPrs) {
        super(new BorderLayout());
        setBorder(UIFactoryMgr.createTitledBorder(
				MessageRepository.get(Msg.TEXT_AGENTCONFIGURATION)));
        fRMSConfig = ConfigurationMgr.get(RMSComponent.NAME);
        fComboBoxLocation = UIFactoryMgr.createComboBox();
        fComboBoxLocation.setModel(
                new DefaultComboBoxModel(
                        new AgentLocation[] {AgentLocation.LOCAL, AgentLocation.REMOTE}));
        fComboBoxLocation.setMaximumSize(new Dimension(100, fComboBoxLocation.getPreferredSize().height));
        fComboBoxSUOVersion = UIFactoryMgr.createComboBox();
        fComboBoxSUOVersion.addItemListener(new EventHandler());
        fAgentConfPanelContext = new AgentCustomConfigurationPanelContext(vc);
        fPanelAgentConfig = new AgentConfigurationPanel(vc, rmsPrs, false);
        fForm = new FormPanel(FormPanel.HORIZONTAL);
        fForm.addPairs(new String[] {
                LABEL_SELECT_LOCATION,
                LABEL_SELECT_SYSTEM_VERSION
            }, new Component[] {
                fComboBoxLocation,
                fComboBoxSUOVersion,
            });
        fForm.setVisible(LABEL_SELECT_SYSTEM_VERSION, false);
        add(fForm, BorderLayout.NORTH);
        add(fPanelAgentConfig, BorderLayout.CENTER);
    }

    /**
     * @param data
     * @param agentInstallationData
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     * @throws IllegalArgumentException
     * @throws RMSException
     * @throws SecurityException
     */
    public void setAgentInstanceData(AgentActivationData data,
            AgentInstallationData agentInstallationData) throws SecurityException, RMSException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        this.fInEditData = data;
        this.fAgentInstallationData = agentInstallationData;

        AgentConfiguration conf = data.getConfiguration();
        String suoVersion = conf != null ? conf.getSystemUnderObservationVersion() : null;
        String[] vers = fAgentInstallationData.getSystemUnderObservationVersions();
        if(!Utils.isEmptyArray(vers)) {
        	Arrays.sort(vers);
            if(suoVersion == null) {
            	suoVersion = vers[0];
            }
            fComboBoxSUOVersion.setModel(new DefaultComboBoxModel(vers));
            // this should trigger an update on the location combo box
            this.fComboBoxSUOVersion.setSelectedItem(suoVersion);
            fForm.setVisible(LABEL_SELECT_SYSTEM_VERSION, true);
        } else {
            fForm.setVisible(LABEL_SELECT_SYSTEM_VERSION, false);
        }
        // configure panel for the right suoVersion
        configurePanelForSUOVersion(suoVersion, false, true);
    }

    /**
     * Applies changes to instance data.
     * @throws InvalidConfiguration
     */
    public AgentActivationData applyChanges() throws InvalidConfiguration {
        // apply changes to agent config
        this.fPanelAgentConfig.applyChanges();
        // update the deployment details with
        // the configuration
        this.fInEditData.setConfiguration(this.fPanelAgentConfig.getAgentConfiguration());
        // set location
        this.fInEditData.setLocation((AgentLocation)fComboBoxLocation.getSelectedItem());
        // set SUO version
        if(fComboBoxSUOVersion.isVisible()) {
            this.fInEditData.getConfiguration().setSystemUnderObservationVersion(
                    (String)fComboBoxSUOVersion.getSelectedItem());
        }
        return this.fInEditData;
    }

    /**
     * @param e
     */
    private void handleSytemVersionChanged(ItemEvent e) {
        try {
            configurePanelForSUOVersion((String)fComboBoxSUOVersion.getSelectedItem(), true, false);
        } catch(Exception ex) {
            UIExceptionMgr.userException(ex);
        }
    }

    /**
     * @param suoVersion
     * @param cleanCustomConfig
     * @throws SecurityException
     * @throws RMSException
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void configurePanelForSUOVersion(String suoVersion,
    		boolean cleanCustomConfig, boolean cleanLocation) throws SecurityException, RMSException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        VersionableAgentInstallationData vad =
            fAgentInstallationData.getVersionData(suoVersion);
        if(fCurrentVersionableAgentInstallationData != null
                && fCurrentVersionableAgentInstallationData.equals(vad)) {
            // nothing to do if the two agent intstallation data are equivalent
            return;
        }

        // update location combo box with new values
        AgentLocation location = fInEditData.getLocation();
        if(location == null) {
	        if(!cleanLocation) {
	        	AgentLocation selLocation = (AgentLocation)fComboBoxLocation.getSelectedItem();
	        	AgentLocation[] locations = vad.getLocations();
	        	if(Utils.arrayContains(locations, selLocation)) {
	        		location = selLocation;
	        	}
	        }
	        AgentLocation[] locations = vad.getLocations();
	        fComboBoxLocation.setModel(new DefaultComboBoxModel(locations));
	        if(location == null) {
	        	location = vad.getDefaultLocation();
	        }
        }
        fComboBoxLocation.setSelectedItem(location);

        // update agent configuration panel
        String configCustomPanelClass = vad.getConfigPanelClass();
        AgentConfiguration acd = fInEditData.getConfiguration();
        AgentCustomConfigurationPanel acp = null;
        if(configCustomPanelClass != null) {
            acp = AgentCustomConfigurationPanel.createImplementation(
                    configCustomPanelClass,
                    fAgentInstallationData.getMessageCatalog(),
                    fAgentConfPanelContext);
        }
        if(acd == null || cleanCustomConfig) {
            acd = new AgentConfiguration(
                    this.fRMSConfig.getInt(RMSConfigurationConstants.AGENT_CONFIGURATION_SAMPLING_INERVAL),
                    true,
                    vad.getDefaultMonitoringLevel(),
                    acp != null ?
                            acp.createAgentCustomConfiguration() : null);
        }
        acd.setSystemUnderObservationVersion(suoVersion);
        this.fPanelAgentConfig.setAgentConfiguration(acd, fAgentInstallationData, acp);
        this.fPanelAgentConfig.setAgentVersion(suoVersion);
    }
}
