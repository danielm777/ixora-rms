/**
 * 17-Mar-2006
 */
package com.ixora.rms.ui.views.logreplay;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionBrowse;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.logging.DataLogCompareAndReplayConfiguration;
import com.ixora.rms.logging.DataLogRepository;
import com.ixora.rms.logging.LogComponent;
import com.ixora.rms.logging.LogConfigurationConstants;
import com.ixora.rms.logging.LogRepositoryInfo;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.logchooser.LogChooser;
import com.ixora.rms.ui.logchooser.LogChooserImpl;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public class DataLogCompareAndReplayConfigurationDialog extends AppDialog {
	private static final long serialVersionUID = -25876768115555442L;

	private RMSViewContainer fViewContainer;
	private FormPanel fForm;
	
	private JTextField fTextFieldLogOne;
	private JTextField fTextFieldLogTwo;
	private JTextField fTextFieldAggStep;

	private JCheckBox fCheckBoxNoAgg;
	
	private JEditorPane fPaneSelectTimeIntervalOne;
	private JEditorPane fPaneSelectTimeIntervalTwo;
	
	private JButton fButtonBrowseOne;
	private JButton fButtonBrowseTwo;

	private LogRepositoryInfo fLogRepInfoOne;
	private LogRepositoryInfo fLogRepInfoTwo;
	
	private DataLogCompareAndReplayConfiguration fResult;
	private EventHandler fEventHandler;
	private DataLogRepository fLogRepository;

	private final class EventHandler implements ItemListener, HyperlinkListener  {
		/**
		 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
		 */
		public void itemStateChanged(ItemEvent e) {
			if(e.getSource() == fCheckBoxNoAgg) {
				handleNoAggCheckBoxEvent(e);
			}
		}

		/**
		 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
		 */
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if(e.getEventType() == EventType.ACTIVATED) {
				if(e.getSource() == fPaneSelectTimeIntervalOne) {
					handleSelectTimeIntervalOne();
				} else if(e.getSource() == fPaneSelectTimeIntervalTwo) {
					handleSelectTimeIntervalTwo();
				}
			}
		}		
	}

	/**
	 * @param parent
	 * @param conf
	 * @param rep
	 */
	@SuppressWarnings("serial")
	public DataLogCompareAndReplayConfigurationDialog(RMSViewContainer container,
			DataLogCompareAndReplayConfiguration conf, DataLogRepository rep) {
		super(container.getAppFrame(), VERTICAL);
		setModal(true);
		setTitle(MessageRepository.get(Msg.TITLE_COMPARE_AND_REPLAY_CONFIGURATION));
		fViewContainer = container;
		fLogRepository = rep;
		fEventHandler = new EventHandler();
		fForm = new FormPanel(FormPanel.VERTICAL1);

		fTextFieldAggStep = UIFactoryMgr.createTextField();
		fTextFieldLogOne = UIFactoryMgr.createTextField();
		fTextFieldLogTwo = UIFactoryMgr.createTextField();
		fCheckBoxNoAgg = UIFactoryMgr.createCheckBox();
		fCheckBoxNoAgg.setText(MessageRepository.get(Msg.TEXT_NO_AGGREGATION));
		
		fPaneSelectTimeIntervalOne = UIFactoryMgr.createHtmlPane();
		fPaneSelectTimeIntervalOne.setPreferredSize(new Dimension(300, 20));
		fPaneSelectTimeIntervalOne.setText("<html><a href='#'>" + 
				MessageRepository.get(Msg.LINK_SETUP_TIME_INTERVAL)
				+ "</a></html>");
		fPaneSelectTimeIntervalOne.addHyperlinkListener(fEventHandler);	

		fPaneSelectTimeIntervalTwo = UIFactoryMgr.createHtmlPane();
		fPaneSelectTimeIntervalTwo.setPreferredSize(new Dimension(300, 20));
		fPaneSelectTimeIntervalTwo.setText("<html><a href='#'>" + 
				MessageRepository.get(Msg.LINK_SETUP_TIME_INTERVAL)
				+ "</a></html>");
		fPaneSelectTimeIntervalTwo.addHyperlinkListener(fEventHandler);	
		fButtonBrowseOne = UIFactoryMgr.createButton(new ActionBrowse(){
			public void actionPerformed(ActionEvent e) {
				handleBrowseOne();
				
			}});
		fButtonBrowseTwo = UIFactoryMgr.createButton(new ActionBrowse(){
			public void actionPerformed(ActionEvent e) {
				handleBrowseTwo();
			}});
		
		Box box1 = new Box(BoxLayout.Y_AXIS);
		box1.add(fTextFieldLogOne);
		box1.add(Box.createHorizontalStrut(UIConfiguration.getPanelPadding()));
		box1.add(fPaneSelectTimeIntervalOne);
		
		Box box11 = new Box(BoxLayout.X_AXIS);
		box11.add(box1);
		box11.add(Box.createHorizontalStrut(UIConfiguration.getPanelPadding()));
		box11.add(fButtonBrowseOne);
		
		Box box2 = new Box(BoxLayout.Y_AXIS);
		box2.add(fTextFieldLogTwo);
		box2.add(Box.createHorizontalStrut(UIConfiguration.getPanelPadding()));
		box2.add(fPaneSelectTimeIntervalTwo);
		
		Box box21 = new Box(BoxLayout.X_AXIS);
		box21.add(box2);
		box21.add(Box.createHorizontalStrut(UIConfiguration.getPanelPadding()));
		box21.add(fButtonBrowseTwo);

		Box box3 = new Box(BoxLayout.X_AXIS);
		box3.add(fTextFieldAggStep);
		box3.add(Box.createHorizontalStrut(UIConfiguration.getPanelPadding()));
		box3.add(fCheckBoxNoAgg);

		fForm.addPairs(
			new String[]{
				MessageRepository.get(Msg.LABEL_LOG_ONE),
				MessageRepository.get(Msg.LABEL_LOG_TWO),
				MessageRepository.get(Msg.LABEL_AGGREGATION_STEP)
			},
			new Component[]{
				box11,
				box21,
				box3
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

		fCheckBoxNoAgg.addItemListener(fEventHandler);
		buildContentPane();
	}

	private void handleBrowseOne() {
		LogChooser logChooser = new LogChooserImpl(fViewContainer);
		fLogRepInfoOne = logChooser.getLogInfoForRead();
		if(fLogRepInfoOne != null) {
			fTextFieldLogOne.setText(fLogRepInfoOne.getRepositoryName());
		}
		
	}

	private void handleBrowseTwo() {
		LogChooser logChooser = new LogChooserImpl(fViewContainer);
		fLogRepInfoTwo = logChooser.getLogInfoForRead();
		if(fLogRepInfoTwo != null) {
			fTextFieldLogTwo.setText(fLogRepInfoTwo.getRepositoryName());
		}
		
	}

	private void handleSelectTimeIntervalOne() {
		try {
/*			final DataLogReader reader = fLogRepository.getReader(fLogRepInfoOne);
			reader.scan(new DataLogReader.ScanCallback(){
				public void finishedScanning(long beginTimestamp,
						long endTimestamp) {
				}
				public void handleAgent(HostId host, AgentDescriptor ad) {
				}
				public void handleEntity(HostId host, AgentId aid,
						EntityDescriptor ed) {
				}
				public void handleScanFatalError(Exception e) {
				}});
*/		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}		
	}

	private void handleSelectTimeIntervalTwo() {
		try {
//			final DataLogReader reader = fLogRepository.getReader(fLogRepInfoTwo);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}		
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
			fResult = new DataLogCompareAndReplayConfiguration(null, null, aggStep);
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @return.
	 */
	public DataLogCompareAndReplayConfiguration getResult() {
		return fResult;
	}
}
