/**
 * 17-Mar-2006
 */
package com.ixora.rms.ui.views.logreplay;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.forms.FormFieldDateSelector;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.logging.DataLogReplayConfiguration;
import com.ixora.rms.logging.LogComponent;
import com.ixora.rms.logging.LogConfigurationConstants;

/**
 * @author Daniel Moraru
 */
public class DataLogReplayConfigurationDialog extends AppDialog {
	private static final long serialVersionUID = -25876768115386442L;
	// TODO localize
	private static final String LABEL_TIME_START = "Timestamp Begin";
	private static final String LABEL_TIME_END = "Timestamp End";
	private static final String LABEL_AGGREGATION_STEP = "Aggregation Step(seconds)";

	private FormPanel fForm;
	private FormFieldDateSelector fFormDateSelectorStart;
	private FormFieldDateSelector fFormDateSelectorEnd;
	private JTextField fTextFieldAggStep;
	private JCheckBox fCheckBoxNoAgg;

	private long fTimeMin;
	private long fTimeMax;

	private DataLogReplayConfiguration fResult;

	private final class EventHandler implements ItemListener {
		/**
		 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
		 */
		public void itemStateChanged(ItemEvent e) {
			if(e.getSource() == fCheckBoxNoAgg) {
				handleNoAggCheckBoxEvent(e);
			}
		}

	}

	/**
	 * @param parent
	 * @param timeMin
	 * @param timeMax
	 * @param conf
	 */
	public DataLogReplayConfigurationDialog(Frame parent,
			long timeMin, long timeMax, DataLogReplayConfiguration conf) {
		super(parent, VERTICAL);
		setModal(true);
		setTitle("Replay Configuration"); // TODO localize
		fForm = new FormPanel(FormPanel.VERTICAL1);
		fTimeMin = timeMin;
		fTimeMax = timeMax;

		fFormDateSelectorStart = new FormFieldDateSelector(this, new Date(conf == null ? timeMin : conf.getTimeBegin()));
		fFormDateSelectorEnd = new FormFieldDateSelector(this, new Date(conf == null ? timeMax : conf.getTimeEnd()));
		fTextFieldAggStep = UIFactoryMgr.createTextField();
		fCheckBoxNoAgg = UIFactoryMgr.createCheckBox();
		// TODO localize
		fCheckBoxNoAgg.setText("No aggregation");

		Box box = new Box(BoxLayout.X_AXIS);
		box.add(fTextFieldAggStep);
		box.add(Box.createHorizontalStrut(UIConfiguration.getPanelPadding()));
		box.add(fCheckBoxNoAgg);

		fForm.addPairs(
			new String[]{
				LABEL_TIME_START,
				LABEL_TIME_END,
				LABEL_AGGREGATION_STEP
			},
			new Component[]{
				fFormDateSelectorStart,
				fFormDateSelectorEnd,
				box
			});

		int aggStep;
		if(conf == null) {
			aggStep = ConfigurationMgr.getInt(
					LogComponent.NAME,
					LogConfigurationConstants.LOG_AGGREGATION_PERIOD);
		} else {
			aggStep = conf.getAggregationStep();
		}
		fTextFieldAggStep.setText(String.valueOf(aggStep));
		if(aggStep == 0) {
			fCheckBoxNoAgg.setSelected(true);
		} else {
			fCheckBoxNoAgg.setSelected(false);
		}

		fCheckBoxNoAgg.addItemListener(new EventHandler());
		buildContentPane();
	}

	/**
	 * @param e
	 */
	private void handleNoAggCheckBoxEvent(ItemEvent e) {
		try {
			if(fCheckBoxNoAgg.isSelected()) {
				fTextFieldAggStep.setEnabled(false);
				fTextFieldAggStep.setText("0");
			} else {
				fTextFieldAggStep.setEnabled(true);
				fTextFieldAggStep.setText(
					String.valueOf(ConfigurationMgr.getInt(
							LogComponent.NAME,
							LogConfigurationConstants.LOG_AGGREGATION_PERIOD)));
			}
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[]{fForm};
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	@SuppressWarnings("serial")
	protected JButton[] getButtons() {
		return new JButton[]{
				UIFactoryMgr.createButton(new ActionOk(){
					public void actionPerformed(ActionEvent e) {
						handleOk();
					}}),
				UIFactoryMgr.createButton(new ActionCancel(){
					public void actionPerformed(ActionEvent e) {
						fResult = null;
						dispose();
					}})
		};
	}

	private void handleOk() {
		try {
			Date start = fFormDateSelectorStart.getResult();
			if(start == null) {
				start = new Date(fTimeMin);
			}
			if(start.getTime() < fTimeMin) {
				// TODO localize
				throw new RMSException(
						"Invalid start timestamp: "
						+ start
						+ ". It must be greater than or equal to "
						+ new Date(fTimeMin));
			}
			Date end = fFormDateSelectorEnd.getResult();
			if(end == null) {
				end = new Date(fTimeMax);
			}
			if(end.getTime() > fTimeMax) {
				// TODO localize
				throw new RMSException(
						"Invalid end timestamp: "
						+ start
						+ ". It must be smaller than or equal to "
						+ new Date(fTimeMax));
			}
			String aggStepString = fTextFieldAggStep.getText().trim();
			int aggStep = 0;
			try {
				aggStep = Integer.parseInt(aggStepString);
			} catch(NumberFormatException e) {
				throw new RMSException("The aggregation step must be an integer");
			}

			if(fCheckBoxNoAgg.isSelected()) {
				aggStep = 0;
			}
			fResult = new DataLogReplayConfiguration(start.getTime(), end.getTime(), aggStep);
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @return.
	 */
	public DataLogReplayConfiguration getResult() {
		return fResult;
	}
}
