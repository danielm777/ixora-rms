/*
 * Created on 31-Dec-2004
 */
package com.ixora.rms.ui.tools.providermanager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.ixora.common.MessageRepository;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.ProviderConfiguration;
import com.ixora.rms.providers.ProviderLocation;
import com.ixora.rms.providers.ProvidersComponent;
import com.ixora.rms.providers.exception.ProviderNotInstalled;
import com.ixora.rms.providers.ui.ProviderCustomConfigurationPanel;
import com.ixora.rms.repository.ProviderInstallationData;
import com.ixora.rms.repository.ProviderInstance;
import com.ixora.rms.services.ProviderRepositoryService;
import com.ixora.rms.ui.tools.providermanager.messages.Msg;

/**
 * The panel that edits a provider instance.
 * @author Daniel Moraru
 */
public final class ProviderPanel extends JPanel {
	private static final String LABEl_NAME = MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PROVIDER_NAME);
	private static final String LABEl_LOCATION = MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PROVIDER_LOCATION);
	private static final String LABEl_OPTIONAL = MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PROVIDER_OPTIONAL);
	private static final String LABEL_SELECTED_BY_DEFAULT = MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PROVIDER_SELECTED_BY_DEFAULT);

	private FormPanel form;
	private ProviderConfigurationPanel confPanel;
	private JPanel locationPanel;
	private JComboBox providerName;
	private JComboBox providerLocation;
	private JCheckBox inheritCheckBox;
	private JCheckBox optionalCheckBox;
	private JCheckBox selectedByDefaultCheckBox;
	private ProviderRepositoryService providerRepository;
	private EventHandler eventHandler;
	private Map<String, ProviderNameData> fProviderNameData;

	/**
	 * Packs edited provider data.
	 */
	public static final class ProviderData {
		public String providerName;
		public ProviderConfiguration configuration;
		public boolean remote;
		public boolean optional;
		public boolean selectedByDefault;
		public boolean inheritsLocationFromAgent;

		public ProviderData(String providerName, ProviderConfiguration configuration,
				boolean remote, boolean optional,
				boolean selectedByDefault, boolean inheritsLocationFromAgent) {
			super();
			this.providerName = providerName;
			this.configuration = configuration;
			this.remote = remote;
			this.optional = optional;
			this.selectedByDefault = selectedByDefault;
			this.inheritsLocationFromAgent = inheritsLocationFromAgent;
		}
	}

	/** Event handler */
	private final class EventHandler implements ItemListener {
		/**
		 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
		 */
		public void itemStateChanged(ItemEvent e) {
			if(e.getSource() == providerName) {
				handleProviderNameChanged(e);
				return;
			}
			if(e.getSource() == inheritCheckBox) {
				handleStateChangedOnInheritCheckBox(e);
				return;
			}
			if(e.getSource() == optionalCheckBox) {
				handleStateChangedOnOptionalCheckBox(e);
				return;
			}
		}
	}

	/**
	 * Object for the combo box with provider names.
	 */
	private static final class ProviderNameData {
		String name;
		String translatedName;

		/**
		 * Constructor.
		 * @param name
		 */
		ProviderNameData(String name) {
			this.name = name;
			this.translatedName = MessageRepository.get(ProvidersComponent.NAME, name);
		}
		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return translatedName;
		}
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if(obj == this) {
				return true;
			}
			if(!(obj instanceof ProviderNameData)) {
				return false;
			}
			ProviderNameData that = (ProviderNameData)obj;
			return name.equals(that.name) && translatedName.equals(that.translatedName);
		}
		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return this.name.hashCode() ^ this.translatedName.hashCode();
		}
	}

	/**
	 * Constructor.
	 * @param prs
	 */
	public ProviderPanel(ProviderRepositoryService prs) {
		super(new BorderLayout());
		if(prs == null) {
			throw new IllegalArgumentException("null params");
		}
		this.eventHandler = new EventHandler();
		this.providerRepository = prs;
		confPanel = new ProviderConfigurationPanel();
		providerName = UIFactoryMgr.createComboBox();
		providerName.setModel(new DefaultComboBoxModel(buildProviderNamesData()));
		providerLocation = UIFactoryMgr.createComboBox();
		providerLocation.setModel(new DefaultComboBoxModel(new ProviderLocation[] {ProviderLocation.LOCAL, ProviderLocation.REMOTE}));
		inheritCheckBox = UIFactoryMgr.createCheckBox();
		inheritCheckBox.setText(MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PROVIDER_INHERIT_LOCATION));
       		locationPanel = new JPanel(new BorderLayout());
		locationPanel.add(providerLocation, BorderLayout.WEST);
		locationPanel.add(inheritCheckBox);
		optionalCheckBox = UIFactoryMgr.createCheckBox();
		optionalCheckBox.setText(LABEl_OPTIONAL);
		selectedByDefaultCheckBox = UIFactoryMgr.createCheckBox();
		selectedByDefaultCheckBox.setText(LABEL_SELECTED_BY_DEFAULT);

		form = new FormPanel(FormPanel.VERTICAL1);
		form.addPairs(
			new String[]{LABEl_NAME, LABEl_LOCATION},
			new Component[] {providerName, locationPanel});

		JPanel checkboxes = new JPanel();
		checkboxes.setLayout(new BoxLayout(checkboxes, BoxLayout.X_AXIS));
		int pad = UIConfiguration.getPanelPadding();
		checkboxes.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
		checkboxes.add(optionalCheckBox);
		checkboxes.add(selectedByDefaultCheckBox);

		JPanel north = new JPanel(new BorderLayout());
		north.add(form, BorderLayout.NORTH);
		north.add(checkboxes, BorderLayout.CENTER);

		add(north, BorderLayout.NORTH);
		add(confPanel, BorderLayout.CENTER);

		providerName.addItemListener(this.eventHandler);
		inheritCheckBox.addItemListener(eventHandler);
		optionalCheckBox.addItemListener(this.eventHandler);

		inheritCheckBox.setSelected(true);
	}

    /**
     * Stops all editing
     */
    public void stopEditing() {
        ProviderCustomConfigurationPanel cc = confPanel.getCustomConfigurationPanel();
        if(cc != null) {
            cc.stopEditing();
        }
    }

	/**
	 * Sets the provider instance to edit.
	 * @param pi
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws RMSException
	 */
	public void setProviderInstanceData(ProviderInstance pi) throws ClassNotFoundException, InstantiationException, IllegalAccessException, RMSException {
		String name = null;
		boolean remote = false;
		boolean inheritsLocation = true;
		boolean optional = true;
		boolean selectedByDefault= true;
		boolean collector = false;
		ProviderConfiguration conf = null;
		if(pi == null) {
			name = ((ProviderNameData)this.providerName.getItemAt(0)).name;
		} else {
			name = pi.getProviderName();
			remote = pi.isRemote();
			optional = pi.isOptional();
			selectedByDefault = pi.isSelectedByDefault();
			conf = pi.getConfiguration();
			inheritsLocation = pi.inheritsLocationFromAgent();
		}
		ProviderNameData pnd = fProviderNameData.get(name);
		if(pnd == null) {
			throw new ProviderNotInstalled(name);
		}
		providerName.setSelectedItem(pnd);
		if(remote) {
			providerLocation.setSelectedItem(ProviderLocation.REMOTE);
		} else {
			providerLocation.setSelectedItem(ProviderLocation.LOCAL);
		}
		inheritCheckBox.setSelected(inheritsLocation);
		selectedByDefaultCheckBox.setSelected(selectedByDefault);
		optionalCheckBox.setSelected(optional);
		selectedByDefaultCheckBox.setEnabled(optional);
		confPanel.setProviderConfiguration(conf, getProviderCustomConfigPanelFor(name));
	}

	/**
	 * @return
	 * @throws VetoException
	 * @throws InvalidPropertyValue
	 */
	public ProviderData getProviderData() throws InvalidPropertyValue, VetoException {
		return new ProviderData(
				((ProviderNameData)providerName.getSelectedItem()).name,
				confPanel.getProviderConfiguration(),
				providerLocation.getSelectedItem() == ProviderLocation.REMOTE,
				optionalCheckBox.isSelected(),
				selectedByDefaultCheckBox.isSelected(),
				inheritCheckBox.isSelected());
	}

	/**
	 * @param name the name of the provider
	 * @param conf null to create a new configuration
	 * @throws RMSException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws RMSException
	 */
	private ProviderCustomConfigurationPanel getProviderCustomConfigPanelFor(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException, RMSException {
		String confPanelClass = getProviderCustomConfigPanelClass(name);
		Class c = Class.forName(confPanelClass);
		return (ProviderCustomConfigurationPanel)c.newInstance();
	}

	/**
	 * @return the names of all providers
	 */
	private ProviderNameData[] buildProviderNamesData() {
		Map<String, ProviderInstallationData> providers = this.providerRepository.getInstalledProviders();
		ProviderNameData[] ret = new ProviderNameData[providers.size()];
		fProviderNameData = new HashMap<String, ProviderNameData>(providers.size());
		int i = 0;
		for(ProviderInstallationData pid : providers.values()) {
			ProviderNameData pnd = new ProviderNameData(pid.getProviderName());
			ret[i] = pnd;
			fProviderNameData.put(pid.getProviderName(), pnd);
			++i;
		}
		return ret;
	}

	/**
	 * @param providerName
	 * @return
	 * @throws RMSException
	 */
	private String getProviderCustomConfigPanelClass(String providerName) throws RMSException {
		Map<String, ProviderInstallationData> providers = this.providerRepository.getInstalledProviders();
		ProviderInstallationData pid = providers.get(providerName);
		if(pid == null) {
			RMSException ex = new ProviderNotInstalled(providerName);
			ex.setIsInternalAppError();
			throw ex;
		}
		return pid.getProviderConfPanelClass();
	}

	/**
	 * Changes the provider custom configuration panel to match the newly
	 * selected provider.
	 * @param e
	 */
	private void handleProviderNameChanged(ItemEvent e) {
		try {
            if(e.getStateChange() == ItemEvent.SELECTED) {
    			String name = ((ProviderNameData)providerName.getSelectedItem()).name;
    			this.confPanel.setProviderConfiguration(
    					new ProviderConfiguration(),
    					getProviderCustomConfigPanelFor(name));
            }
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Disables the location combo box when the check box is selected.
	 * @param e
	 */
	private void handleStateChangedOnInheritCheckBox(ItemEvent e) {
		try {
			if(inheritCheckBox.isSelected()) {
				providerLocation.setEnabled(false);
			} else {
				providerLocation.setEnabled(true);
			}
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * @param e
	 */
	private void handleStateChangedOnOptionalCheckBox(ItemEvent e) {
		try {
			if(optionalCheckBox.isSelected()) {
				selectedByDefaultCheckBox.setEnabled(true);
			} else {
				selectedByDefaultCheckBox.setEnabled(false);
			}
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}
}
