/*
 * Created on Feb 8, 2004
 */
package com.ixora.rms.ui.views.session;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ixora.rms.RMSComponent;
import com.ixora.rms.RMSConfigurationConstants;
import com.ixora.common.ComponentConfiguration;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.ToolBarComponent;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.RMSUIUtils;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class SamplingIntervalPanel
		extends JPanel implements ToolBarComponent, Observer {
	private RMSViewContainer fViewContainer;
	private JSpinner fSamplingSpinner;
	private JButton fApplyButton;
	private ActionChangeSamplingInterval fAction;
	private ComponentConfiguration fRMSConf;
	/**
	 * Apply change action.
	 */
	private final class ActionChangeSamplingInterval extends AbstractAction {
		public ActionChangeSamplingInterval() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_CHANGE_SAMPLING_INTERVAL), this);
			ImageIcon icon = UIConfiguration.getIcon("change_sampling_interval.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
			enabled = false;
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleChangeSamplingInterval();
		}
	}

	/**
	 * Constructor.
	 * @throws RMSException
	 */
	public SamplingIntervalPanel(RMSViewContainer vc) throws RMSException {
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.fViewContainer = vc;
		//super.setBorder(BorderFactory.createEtchedBorder());
		this.fRMSConf = ConfigurationMgr.get(RMSComponent.NAME);
		fSamplingSpinner = RMSUIUtils.createSamplingIntervalSpinner(
			fRMSConf.getInt(RMSConfigurationConstants.AGENT_CONFIGURATION_SAMPLING_INERVAL));
		// override size
		Dimension d = new Dimension(40, 20);
		fSamplingSpinner.setMinimumSize(d);
		fSamplingSpinner.setMaximumSize(d);
		fSamplingSpinner.setPreferredSize(d);
		add(Box.createHorizontalStrut(UIConfiguration.getPanelPadding()));
		add(fSamplingSpinner);
		fAction = new ActionChangeSamplingInterval();
		fApplyButton = UIFactoryMgr.createButton(fAction);
		fApplyButton.setText(null);
		add(fApplyButton);
		add(Box.createHorizontalStrut(UIConfiguration.getPanelPadding()));
		fSamplingSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				fAction.setEnabled(true);
			}
		});
		fRMSConf.addObserver(this);
	}

	/**
	 * Changes the sampling interval
	 */
	private void handleChangeSamplingInterval() {
		try {
			fViewContainer.runJob(new UIWorkerJobDefault(
					fViewContainer.getAppFrame(), Cursor.WAIT_CURSOR,
					// TODO localize
					"Changing sampling interval..."){
				public void work() throws Throwable {
					fRMSConf.setInt(
							RMSConfigurationConstants.AGENT_CONFIGURATION_SAMPLING_INERVAL,
							Integer.parseInt(fSamplingSpinner.getValue().toString()));
				}
				public void finished(Throwable ex) throws Throwable {
					if(ex != null) {
						UIExceptionMgr.exception(ex);
					} else {
						fAction.setEnabled(false);
					}
				}
			});
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see com.ixora.common.ui.ToolBarComponent#setText(java.lang.String)
	 */
	public void setText(String text) {
		this.fApplyButton.setText(text);
	}

	/**
	 * @see com.ixora.common.ui.ToolBarComponent#setIcon(javax.swing.Icon)
	 */
	public void setIcon(Icon icon) {
		this.fApplyButton.setIcon(icon);
	}

	/**
	 * @see com.ixora.common.ui.ToolBarComponent#setMnemonic(int)
	 */
	public void setMnemonic(int m) {
		this.fApplyButton.setMnemonic(m);
	}

	/**
	 * @see com.ixora.common.ui.ToolBarComponent#setMargin(java.awt.Insets)
	 */
	public void setMargin(Insets insets) {
		this.fApplyButton.setMargin(insets);
	}

	/**
	 * @see com.ixora.common.ui.ToolBarComponent#setRolloverEnabled(boolean)
	 */
	public void setRolloverEnabled(boolean rollover) {
		this.fApplyButton.setRolloverEnabled(rollover);
	}

	/**
	 * @see com.ixora.common.ui.ToolBarComponent#setBorder(Border)
	 */
	public void setBorder(Border border) {
		if(this.fApplyButton != null) {
			this.fApplyButton.setBorder(border);
		}
	}

// Observer methods

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if(o == fRMSConf) {
			if(arg.equals(RMSConfigurationConstants.AGENT_CONFIGURATION_SAMPLING_INERVAL)) {
				fSamplingSpinner.setValue(new Integer(fRMSConf.getInt(RMSConfigurationConstants.AGENT_CONFIGURATION_SAMPLING_INERVAL)));
			}
		}
	}
}
