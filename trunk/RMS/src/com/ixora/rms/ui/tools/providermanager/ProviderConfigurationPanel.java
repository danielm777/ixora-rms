/*
 * Created on 16-Mar-2005
 */
package com.ixora.rms.ui.tools.providermanager;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.ixora.common.MessageRepository;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.rms.providers.ProviderConfiguration;
import com.ixora.rms.providers.ProviderCustomConfiguration;
import com.ixora.rms.providers.ui.ProviderCustomConfigurationPanel;
import com.ixora.rms.ui.tools.providermanager.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class ProviderConfigurationPanel extends JPanel {
	private static final String LABEL_PRIVATE_COLLECTOR =
        MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PRIVATE_COLLECTOR);

	private JCheckBox fCollectorCheckBox;
	private ProviderCustomConfigurationPanel fCustomConfPanel;

	/**
	 * Constructor.
	 */
	public ProviderConfigurationPanel() {
		super(new BorderLayout());
		setBorder(UIFactoryMgr.createTitledBorder(
                MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PROVIDER_INSTANCE_CONFIGURATION)));

		fCollectorCheckBox = UIFactoryMgr.createCheckBox();
		fCollectorCheckBox.setText(LABEL_PRIVATE_COLLECTOR);
		int pad = UIConfiguration.getPanelPadding();
		fCollectorCheckBox.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
		add(fCollectorCheckBox, BorderLayout.NORTH);
	}

	/**
	 * @param conf
	 */
	public void setProviderConfiguration(ProviderConfiguration conf, ProviderCustomConfigurationPanel cp) {
		if(conf != null) {
			fCollectorCheckBox.setSelected(conf.usePrivateCollector());
		}
		if(this.fCustomConfPanel != null) {
			remove(this.fCustomConfPanel);
		}
		ProviderCustomConfiguration customConfig = conf == null ? null : conf.getProviderCustomConfiguration();
		if(customConfig == null) {
			// create empty config
			customConfig = cp.createCustomConfiguration();
		}
		cp.setConfiguration(customConfig);
		add(cp, BorderLayout.CENTER);
		this.fCustomConfPanel = cp;

		revalidate();
		repaint();
	}

	/**
	 * @return
	 * @throws VetoException
	 * @throws InvalidPropertyValue
	 */
	public ProviderConfiguration getProviderConfiguration() throws InvalidPropertyValue, VetoException {
		ProviderConfiguration provConf = new ProviderConfiguration();
		provConf.setUsePrivateCollector(fCollectorCheckBox.isSelected());
		this.fCustomConfPanel.applyChanges();
		provConf.setCustom(this.fCustomConfPanel != null
                ? this.fCustomConfPanel.getConfiguration()
                : null);
		return provConf;
	}

    /**
     * @return Returns the custom configuration panel.
     */
    public ProviderCustomConfigurationPanel getCustomConfigurationPanel() {
        return fCustomConfPanel;
    }
}
