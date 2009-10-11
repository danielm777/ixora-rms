/*
 * Created on Feb 8, 2004
 */
package com.ixora.rms.ui.views.logreplay;

import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ixora.common.ComponentConfiguration;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.logging.LogComponent;
import com.ixora.rms.logging.LogConfigurationConstants;
import com.ixora.rms.services.DataLogReplayService;
import com.ixora.rms.ui.RMSUIUtils;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class ReplaySpeedPanel
		extends JPanel implements Observer {
	private static final long serialVersionUID = -5472696884322136514L;
	private static final int MAX = 1000;
	private static final int MIN = 200;
	private JSlider speedSlider;
	private ComponentConfiguration logConf;
	/** Log player */
	private DataLogReplayService player;

	/**
	 * Constructor.
	 * @param player
	 * @throws RMSException
	 */
	public ReplaySpeedPanel(DataLogReplayService player) throws RMSException {
		super();
		if(player == null) {
			throw new IllegalArgumentException("null data log replay service");
		}
		this.player = player;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.logConf = ConfigurationMgr.get(LogComponent.NAME);
		speedSlider = RMSUIUtils.createReplaySpeedSlider(
			logConf.getInt(LogConfigurationConstants.LOG_REPLAY_SPEED), MAX);
		speedSlider.setMinimum(MIN);
		speedSlider.setToolTipText(
		        MessageRepository.get(Msg.TEXT_CHANGE_REPLAY_SPEED,
		        new String[]{String.valueOf(speedSlider.getValue())}));
		add(Box.createHorizontalStrut(UIConfiguration.getPanelPadding()));
		add(speedSlider);
		add(Box.createHorizontalStrut(UIConfiguration.getPanelPadding()));

		int aggPeriod = this.logConf.getInt(LogConfigurationConstants.LOG_AGGREGATION_PERIOD);
		if(aggPeriod == 0) {
			// disable this component
			speedSlider.setEnabled(true);
		} else {
			speedSlider.setEnabled(false);
		}

		speedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				handleChangeReplaySpeed();
			}
		});
		logConf.addObserver(this);
	}

	/**
	 * Changes the replay speed.
	 */
	private void handleChangeReplaySpeed() {
		try {
		    int val = speedSlider.getValue();
		    this.player.setReplaySpeed(val);
			speedSlider.setToolTipText(MessageRepository.get(Msg.TEXT_CHANGE_REPLAY_SPEED,
			        new String[]{String.valueOf(val)}));
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

// Observer methods

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if(o == logConf) {
			if(arg.equals(LogConfigurationConstants.LOG_REPLAY_SPEED)) {
				int val = logConf.getInt(
				        LogConfigurationConstants.LOG_REPLAY_SPEED);
				if(val > MAX) {
				    speedSlider.setMaximum(val);
				}
			    speedSlider.setValue(val);
			}
			if(arg.equals(LogConfigurationConstants.LOG_AGGREGATION_PERIOD)) {
				int aggPeriod = this.logConf.getInt(LogConfigurationConstants.LOG_AGGREGATION_PERIOD);
				if(aggPeriod == 0) {
					// disable this component
					speedSlider.setEnabled(true);
				} else {
					speedSlider.setEnabled(false);
				}
			}
		}
	}

	/**
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		super.finalize();
		// remove observer from log config
		logConf.deleteObserver(this);
	}
}
