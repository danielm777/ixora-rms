/**
 * 17-Mar-2006
 */
package com.ixora.rms.ui.views.logreplay;

import java.awt.Component;
import java.awt.Cursor;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.Document;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionBrowse;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.ui.jobs.UIWorkerJobDefaultCancelable;
import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.HostId;
import com.ixora.rms.RMS;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.logging.DataLogCompareAndReplayConfiguration;
import com.ixora.rms.logging.LogComponent;
import com.ixora.rms.logging.LogConfigurationConstants;
import com.ixora.rms.logging.LogRepositoryInfo;
import com.ixora.rms.logging.TimeInterval;
import com.ixora.rms.logging.DataLogCompareAndReplayConfiguration.LogRepositoryReplayConfig;
import com.ixora.rms.logging.exception.DataLogException;
import com.ixora.rms.services.DataLogScanningService;
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
	private EventHandler fEventHandler;
	private ActionOk fActionOk;
	private LogRepositoryInfo fLogOne;
	private LogRepositoryInfo fLogTwo;
	private TimeInterval fTimeIntervalOne;
	private TimeInterval fTimeIntervalTwo;
	
	private final class EventHandler implements ItemListener, HyperlinkListener, DocumentListener {
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

		/**
		 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
		 */
		public void changedUpdate(DocumentEvent e) {
			handleChangeInLogNameTextField(e.getDocument());
		}

		/**
		 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
		 */
		public void insertUpdate(DocumentEvent e) {
			handleChangeInLogNameTextField(e.getDocument());
		}

		/**
		 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
		 */
		public void removeUpdate(DocumentEvent e) {
			handleChangeInLogNameTextField(e.getDocument());
		}		
	}

	/**
	 * @param parent
	 * @param conf
	 * @param rep
	 */
	@SuppressWarnings("serial")
	public DataLogCompareAndReplayConfigurationDialog(RMSViewContainer container) {
		super(container.getAppFrame(), VERTICAL);
		setModal(true);
		setTitle(MessageRepository.get(Msg.TITLE_COMPARE_AND_REPLAY_CONFIGURATION));
		fViewContainer = container;
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
		aggStep = ConfigurationMgr.getInt(
					LogComponent.NAME,
					LogConfigurationConstants.LOG_AGGREGATION_PERIOD);
		fTextFieldAggStep.setText(String.valueOf(aggStep));
		if(aggStep == 0) {
			fCheckBoxNoAgg.setSelected(true);
		} else {
			fCheckBoxNoAgg.setSelected(false);
		}
		fActionOk = new ActionOk(){
			public void actionPerformed(ActionEvent e) {
				handleOk();
			}};
		fActionOk.setEnabled(false);
		fTextFieldLogTwo.setEnabled(false);
		fButtonBrowseTwo.setEnabled(false);
		
		fCheckBoxNoAgg.addItemListener(fEventHandler);
		fTextFieldLogOne.getDocument().addDocumentListener(fEventHandler);
		fTextFieldLogTwo.getDocument().addDocumentListener(fEventHandler);
		buildContentPane();
	}

	private void handleChangeInLogNameTextField(Document document) {
		if(document == fTextFieldLogOne.getDocument()) {
			if(changeReplayConfig(fResult.getLogOne(), fTextFieldLogOne)){
				fActionOk.setEnabled(true);
				fTextFieldLogTwo.setEnabled(true);
				fButtonBrowseTwo.setEnabled(true);
			} else {
				fActionOk.setEnabled(false);
				fTextFieldLogTwo.setEnabled(false);
				fButtonBrowseTwo.setEnabled(false);
			}
		} else if(document == fTextFieldLogTwo.getDocument()) {
			changeReplayConfig(fResult.getLogTwo(), fTextFieldLogTwo);
		}
	}

	/**
	 * @param logConfig
	 * @param tf
	 * @return true if the log repository is setup
	 */
	private boolean changeReplayConfig(
			DataLogCompareAndReplayConfiguration.LogRepositoryReplayConfig logConfig, 
			JTextField tf) {
		logConfig.setTimestampBegin(0);
		logConfig.setTimestampEnd(0);
		String txt = tf.getText().trim();
		if(Utils.isEmptyString(txt)) {
			logConfig.setLogRepository(null);
			return false;
		} else {
			LogRepositoryInfo log = logConfig.getLogRepository(); 
			logConfig.setLogRepository(new LogRepositoryInfo(txt, 
					log.getRepositoryType()));
			return true;
		}
	}

	private void handleBrowseOne() {
		LogChooser logChooser = new LogChooserImpl(fViewContainer);
		if(fResult.getLogOne() == null) {
			fResult.setLogOne(new DataLogCompareAndReplayConfiguration.LogRepositoryReplayConfig(logChooser.getLogInfoForRead(), 0, 0));
		}
		fTextFieldLogOne.setText(logChooser.getLogInfoForRead().getRepositoryName());
	}

	private void handleBrowseTwo() {
		LogChooser logChooser = new LogChooserImpl(fViewContainer);
		if(fResult.getLogTwo() == null) {
			fResult.setLogTwo(new DataLogCompareAndReplayConfiguration.LogRepositoryReplayConfig(logChooser.getLogInfoForRead(), 0, 0));
		}
		fTextFieldLogTwo.setText(logChooser.getLogInfoForRead().getRepositoryName());
	}

	/**
	 * @param logRepositoryReplayConfig
	 */
	private void scanLogForTimestamps(final LogRepositoryReplayConfig logRepositoryReplayConfig) {
		// check if scanning is necessary
		if(logRepositoryReplayConfig.getTimeBegin() == 0 || logRepositoryReplayConfig.getTimeEnd() == 0) {
			// scan log 
			final DataLogScanningService scanningService = RMS.getDataLogScanning();
			fViewContainer.getAppWorker().runJob(new UIWorkerJobDefaultCancelable(this,
					Cursor.WAIT_CURSOR, MessageRepository
							.get(Msg.TEXT_SCANNING_LOG)) {
				private DataLogScanningService.ScanListener listener = new DataLogScanningService.ScanListener(){
					public void fatalScanError(LogRepositoryInfo rep, Exception e) {
						wakeUp();
					}
					public void finishedScanningLog(final LogRepositoryInfo rep,
							final long beginTimestamp, final long endTimestamp) {
						handleFinishedScanning(rep, beginTimestamp, endTimestamp);
						wakeUp();
					}
					public void newAgent(LogRepositoryInfo rep, HostId host,
							AgentDescriptor ad) {
					}
					public void newEntity(LogRepositoryInfo rep, HostId host,
							AgentId aid, EntityDescriptor ed) {
					}};			
				public void cancel() {
					super.cancel();
					try {
						scanningService.stopScanning();
					} catch (DataLogException e) {
						UIExceptionMgr.userException(e);
					}
				}
				public void work() throws Throwable {				
					scanningService.addScanListener(listener);
					scanningService.startScanning(logRepositoryReplayConfig.getLogRepository());
					hold();
				}
				public void finished(Throwable ex) {
					scanningService.removeScanListener(listener);
					if(!canceled()) {
						shotTimeIntervalSelectorDialog(logRepositoryReplayConfig);
					}
				}
			});
		} else {
			// skip scanning
			shotTimeIntervalSelectorDialog(logRepositoryReplayConfig);
		}
	}

	/**
	 * @param rep
	 * @param beginTimestamp
	 * @param endTimestamp
	 */
	private void handleFinishedScanning(
			LogRepositoryInfo rep, long beginTimestamp,
			long endTimestamp) {
		if(fResult.getLogOne() != null && rep == fResult.getLogOne().getLogRepository()) {
			fResult.getLogOne().setTimestampBegin(beginTimestamp);
			fResult.getLogOne().setTimestampEnd(endTimestamp);
		} else if(fResult.getLogTwo() != null && rep == fResult.getLogTwo().getLogRepository()) {
			fResult.getLogTwo().setTimestampBegin(beginTimestamp);
			fResult.getLogTwo().setTimestampEnd(endTimestamp);
		}
	}

	private void handleSelectTimeIntervalOne() {
		try {
			if(fResult.getLogOne() != null) {
				scanLogForTimestamps(fResult.getLogOne());
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}		
	}
	
	private void handleSelectTimeIntervalTwo() {
		try {
			if(fResult.getLogTwo() != null) {
				scanLogForTimestamps(fResult.getLogTwo());
			}
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
				UIFactoryMgr.createButton(fActionOk),
				UIFactoryMgr.createButton(new ActionCancel(){
					public void actionPerformed(ActionEvent e) {
						fResult = null;
						dispose();
					}})
		};
	}

	private void handleOk() {
		try {
			fResult.validate(true);
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
			fResult.setAggregationStep(aggStep);
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

	private void shotTimeIntervalSelectorDialog(
			final LogRepositoryReplayConfig logRepositoryReplayConfig) {
		TimeIntervalSelectorDialog dlg = new TimeIntervalSelectorDialog(fViewContainer, logRepositoryReplayConfig);
		UIUtils.centerDialogAndShow(fViewContainer.getAppFrame(), dlg);
	}
}
