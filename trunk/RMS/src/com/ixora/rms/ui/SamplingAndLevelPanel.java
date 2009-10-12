/*
 * Created on 12-Feb-2005
 */
package com.ixora.rms.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ixora.rms.RMSComponent;
import com.ixora.rms.RMSConfigurationConstants;
import com.ixora.common.ComponentConfiguration;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.rms.MonitoringConfiguration;
import com.ixora.rms.MonitoringLevel;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public class SamplingAndLevelPanel extends JPanel {
	private static final long serialVersionUID = -7479366440058114390L;
	public static final String LEVEL = MessageRepository.get(Msg.TEXT_MONITORINGLEVEL);
	public static final String SAMPLING = MessageRepository.get(Msg.TEXT_SAMPLINGINTERVAL);
	/** Sampling interval config panel */
	private FormPanel fFormPanelSampling;
	/** Monitoring level config panel */
	private FormPanel fFormPanelLevel;
	/** Panel holding both the sampling and the level forms */
	private JPanel fSamplingAndLevelPanel;
	/** Global sampling check box */
	private JCheckBox fGlobalSamplingCheckBox;
	/** Recursive level check box */
	private JCheckBox fRecursiveLevelCheckBox;
	/** Sampling interval spinner */
	private JSpinner fSamplingSpinner;
	/** Monioring level spinner */
	private JSpinner fLevelSpinner;
	/** RMS Configuration reference */
	private ComponentConfiguration fRmsConf;
	/** Event handler */
	private EventHandler fEventHandler;

	/**
	 * Event handler.
	 */
	private final class EventHandler
		implements ChangeListener,
			Observer {
		/**
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {
			handleStateChanges(e);
		}

		/**
		 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
		 */
		public void update(Observable o, Object arg) {
			if(RMSConfigurationConstants
					.AGENT_CONFIGURATION_SAMPLING_INERVAL.equals(arg)) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						handleDefaultSamplingIntervalChanged();
					}
				});
			}
		}
	}

	/**
	 * Constructor.
	 */
	public SamplingAndLevelPanel() {
		super(new BorderLayout());
		fEventHandler = new EventHandler();
		fRmsConf = ConfigurationMgr.get(RMSComponent.NAME);
		fRmsConf.addObserver(fEventHandler);
		fSamplingSpinner = RMSUIUtils.createSamplingIntervalSpinner(
				fRmsConf.getInt(RMSConfigurationConstants.AGENT_CONFIGURATION_SAMPLING_INERVAL));
		fLevelSpinner = RMSUIUtils.createMonitoringLevelSpinner(
				MonitoringLevel.NONE);

		fGlobalSamplingCheckBox = UIFactoryMgr.createCheckBox();
		fGlobalSamplingCheckBox.setText(MessageRepository.get(Msg.TEXT_USEDEFAULT));

		fRecursiveLevelCheckBox = UIFactoryMgr.createCheckBox();
		fRecursiveLevelCheckBox.setText(MessageRepository.get(Msg.TEXT_RECURSIVE));

		// the sampling panel will hold the sampling checkbox and
		// the global sampling checkbox
		JPanel samplingPanel = new JPanel(new BorderLayout());
		samplingPanel.add(fSamplingSpinner, BorderLayout.WEST);
		samplingPanel.add(fGlobalSamplingCheckBox, BorderLayout.CENTER);

		// the level panel will hold the sampling checkbox and
		// the recursive level checkbox
		JPanel levelPanel = new JPanel(new BorderLayout());
		levelPanel.add(fLevelSpinner, BorderLayout.WEST);
		levelPanel.add(fRecursiveLevelCheckBox, BorderLayout.CENTER);

		fFormPanelSampling = new FormPanel(FormPanel.HORIZONTAL);
		fFormPanelSampling.addPairs(
				new String[]{
						SAMPLING,
				},
				new Component[]{
						samplingPanel,
				}
		);
		fFormPanelLevel = new FormPanel(FormPanel.HORIZONTAL);
		fFormPanelLevel.addPairs(
				new String[]{
						LEVEL,
				},
				new Component[]{
						levelPanel
				}
		);

		fSamplingAndLevelPanel = new JPanel();
		fSamplingAndLevelPanel.setLayout(new BoxLayout(fSamplingAndLevelPanel, BoxLayout.X_AXIS));

		fSamplingAndLevelPanel.add(fFormPanelSampling);
		fSamplingAndLevelPanel.add(Box.createHorizontalStrut(UIConfiguration.getPanelPadding()));
		fSamplingAndLevelPanel.add(fFormPanelLevel);

		fGlobalSamplingCheckBox.addChangeListener(fEventHandler);
	}

	/**
	 * @param e
	 */
	private void handleStateChanges(ChangeEvent e) {
		try {
			if(e.getSource() == fGlobalSamplingCheckBox) {
				if(fGlobalSamplingCheckBox.isSelected()) {
					// disable sampling interval spinner
					fSamplingSpinner.setEnabled(false);
				} else {
					// enable sampling interval spinner
					fSamplingSpinner.setEnabled(true);
					fSamplingSpinner.setValue(
						new Integer(this.fRmsConf.getInt(
							RMSConfigurationConstants.AGENT_CONFIGURATION_SAMPLING_INERVAL)));
				}
			}
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Updates the widgets in this panel with info from the given configuration.
	 * @param conf
	 * @param availableLevels
	 * @param showSampling
	 * @param showRecursiveLevel
	 */
	public void setMonitoringConfiguration(MonitoringConfiguration conf, MonitoringLevel[] availableLevels, boolean showSampling, boolean showRecursiveLevel) {
		// sampling
		if(showSampling) {
			fSamplingAndLevelPanel.add(fFormPanelSampling, 0);
		} else {
			fSamplingAndLevelPanel.remove(fFormPanelSampling);
		}
		if(conf.isGlobalSamplingInterval()) {
			fGlobalSamplingCheckBox.setSelected(true);
			fSamplingSpinner.setValue(
					new Integer(this.fRmsConf.getInt(
						RMSConfigurationConstants.AGENT_CONFIGURATION_SAMPLING_INERVAL)));
		} else {
			fGlobalSamplingCheckBox.setSelected(false);
			fSamplingSpinner.setValue(conf.getSamplingInterval());
		}
		// levels
		if(availableLevels == null || availableLevels.length <= 1) {
			fSamplingAndLevelPanel.remove(fFormPanelLevel);
		} else {
			fSamplingAndLevelPanel.add(fFormPanelLevel, -1);
			((SpinnerListModel)fLevelSpinner.getModel())
				.setList(Arrays.asList(availableLevels));
			MonitoringLevel configuredLevel = conf.getMonitoringLevel();
			if(configuredLevel != null) {
				fLevelSpinner.getModel().setValue(configuredLevel);
				if(conf.isRecursiveMonitoringLevel() != null && conf.isRecursiveMonitoringLevel().booleanValue()) {
					fRecursiveLevelCheckBox.setSelected(true);
				} else {
					fRecursiveLevelCheckBox.setSelected(false);
				}
			}
			fRecursiveLevelCheckBox.setVisible(showRecursiveLevel);
		}
		if(fSamplingAndLevelPanel.getComponentCount() == 0) {
			remove(fSamplingAndLevelPanel);
		} else {
			add(fSamplingAndLevelPanel, BorderLayout.CENTER);
		}

		validate();
		repaint();
	}

	/**
	 * @param conf
	 */
	public void apply(MonitoringConfiguration conf) {
		if(conf == null) {
			return;
		}
		if(fFormPanelLevel.getParent() == fSamplingAndLevelPanel) {
			MonitoringLevel ml = (MonitoringLevel)fLevelSpinner.getValue();
			conf.setMonitoringLevel(ml);
			conf.setRecursiveMonitoringLevel(fRecursiveLevelCheckBox.isSelected());
		} else {
			conf.setMonitoringLevel(null);
		}
		Integer si = (Integer)fSamplingSpinner.getValue();
		conf.setSamplingInterval(si.intValue());
		conf.setGlobalSamplingInterval(fGlobalSamplingCheckBox.isSelected());

	}

	/**
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		fFormPanelLevel.setEnabled(enabled);
		fFormPanelSampling.setEnabled(enabled);
		fGlobalSamplingCheckBox.setEnabled(enabled);
		fLevelSpinner.setEnabled(enabled);
		fRecursiveLevelCheckBox.setEnabled(enabled);
		fSamplingAndLevelPanel.setEnabled(enabled);
	}

	/**
	 * Handles the change of the global sampling interval value.
	 */
	private void handleDefaultSamplingIntervalChanged() {
		try {
			if(!fGlobalSamplingCheckBox.isSelected()) {
				return;
			}
			fSamplingSpinner.setValue(
				new Integer(
					this.fRmsConf.getInt(RMSConfigurationConstants.AGENT_CONFIGURATION_SAMPLING_INERVAL)));
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
	}


// package access

	JCheckBox getGlobalSamplingCheckBox() {
		return fGlobalSamplingCheckBox;
	}
	JCheckBox getRecursiveLevelCheckBox() {
		return fRecursiveLevelCheckBox;
	}
	JSpinner getLevelSpinner() {
		return fLevelSpinner;
	}
	JSpinner getSamplingSpinner() {
		return fSamplingSpinner;
	}
}
