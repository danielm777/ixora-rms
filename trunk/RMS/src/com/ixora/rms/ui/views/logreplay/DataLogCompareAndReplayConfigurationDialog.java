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
import com.ixora.rms.logging.BoundedTimeInterval;
import com.ixora.rms.logging.DataLogCompareAndReplayConfiguration;
import com.ixora.rms.logging.LogComponent;
import com.ixora.rms.logging.LogConfigurationConstants;
import com.ixora.rms.logging.LogRepositoryInfo;
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
	private String fLogOne;
	private String fLogTwo;
	private BoundedTimeInterval fTimeIntervalOne;
	private BoundedTimeInterval fTimeIntervalTwo;
	private LogRepositoryInfo.Type fRepositoryType;

	private DataLogCompareAndReplayConfiguration fResult;

	private DataLogScanningService fScanningService;
	
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
		fTextFieldLogOne.setEnabled(false);
		fTextFieldLogTwo.setEnabled(false);
		fButtonBrowseTwo.setEnabled(false);
		
		fCheckBoxNoAgg.addItemListener(fEventHandler);
		fTextFieldLogOne.getDocument().addDocumentListener(fEventHandler);
		fTextFieldLogTwo.getDocument().addDocumentListener(fEventHandler);
		
		fScanningService = RMS.getDataLogScanning();
		
		buildContentPane();
	}

	/**
	 * @return
	 */
	public DataLogScanningService getScanningService() {
		return fScanningService;
	}
	
	private void handleChangeInLogNameTextField(Document document) {
		if(document == fTextFieldLogOne.getDocument()) {
			String txt = fTextFieldLogOne.getText().trim();
			if(!Utils.isEmptyString(txt)) {
				fLogOne = txt;
				if(!Utils.isEmptyString(fLogTwo)) {
					fActionOk.setEnabled(true);
				}
				fButtonBrowseTwo.setEnabled(true);
			} else {
				fActionOk.setEnabled(false);
				fTextFieldLogTwo.setEnabled(false);
				fButtonBrowseTwo.setEnabled(false);
			}
		} else if(document == fTextFieldLogTwo.getDocument()) {
			String txt = fTextFieldLogTwo.getText().trim();
			if(!Utils.isEmptyString(txt)) {
				fLogTwo = txt;
				if(!Utils.isEmptyString(fLogOne)) {
					fActionOk.setEnabled(true);
				}
			} else {
				fActionOk.setEnabled(false);
			}
		}
	}

	private void handleBrowseOne() {
		LogChooser logChooser = new LogChooserImpl(fViewContainer);
		LogRepositoryInfo log = logChooser.getLogInfoForRead();
		if(log != null) {
			fRepositoryType = log.getRepositoryType();
			fTextFieldLogOne.setText(log.getRepositoryName());
			fTextFieldLogOne.setEnabled(true);
		}
	}

	private void handleBrowseTwo() {
		LogChooser logChooser = new LogChooserImpl(fViewContainer);
		LogRepositoryInfo log = logChooser.getLogInfoForRead();
		if(log != null) {
			fRepositoryType = log.getRepositoryType();
			fTextFieldLogTwo.setText(log.getRepositoryName());
			fTextFieldLogTwo.setEnabled(true);
		}
	}

	/**
	 * @param logRepositoryReplayConfig
	 */
	private void scanLogForTimestamps(final String logName) {
		if(fTimeIntervalOne == null) {
			runScanJob(logName, new ScanEndCallback(){
				public void finishedScanning(Throwable err) {
					if(err == null) {
						showTimeIntervalSelectorDialog(logName);
					}
				}
			});
		} else {
			// skip scanning
			showTimeIntervalSelectorDialog(logName);
		}

	}

	/**
	 * @param logName
	 * @param scanEndCallback
	 */
	private void runScanJob(final String logName, final ScanEndCallback scanEndCallback) {
		fViewContainer.getAppWorker().runJob(new UIWorkerJobDefaultCancelable(this,
				Cursor.WAIT_CURSOR, MessageRepository
						.get(Msg.TEXT_SCANNING_LOG)) {
			private DataLogScanningService.ScanListener listener = new DataLogScanningService.ScanListener(){
				public void fatalScanError(LogRepositoryInfo rep, Exception e) {
					wakeUp();
				}
				public void finishedScanningLog(LogRepositoryInfo rep,
						BoundedTimeInterval ti) {
					handleFinishedScanning(rep, ti);
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
					fScanningService.stopScanning();
				} catch (DataLogException e) {
					UIExceptionMgr.userException(e);
				}
			}
			public void work() throws Throwable {				
				fScanningService.addScanListener(listener);
				fScanningService.startScanning(new LogRepositoryInfo(fRepositoryType, logName));
				hold();
			}
			public void finished(Throwable ex) {
				fScanningService.removeScanListener(listener);
				if(!canceled()) {
					scanEndCallback.finishedScanning(ex);
				}
			}
		});
	}
	
	/**
	 * @param rep
	 * @param ti
	 */
	private void handleFinishedScanning(
			LogRepositoryInfo rep, BoundedTimeInterval ti) {
		if(rep.getRepositoryName().equals(fLogOne)) {
			fTimeIntervalOne = ti;
		} else if(rep.getRepositoryName().equals(fLogTwo)) {
			fTimeIntervalTwo = ti;
		}
	}

	private void handleSelectTimeIntervalOne() {
		try {
			if(fLogOne != null) {
				scanLogForTimestamps(fLogOne);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}		
	}
	
	private void handleSelectTimeIntervalTwo() {
		try {
			if(fLogTwo != null) {
				scanLogForTimestamps(fLogTwo);
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
	
	private interface ScanEndCallback {
		void finishedScanning(Throwable err);
	}

	private void handleOk() {
		try {
			if(fLogOne == null) {
				throw new RMSException(Msg.ERROR_LOG_ONE_IS_MISSING, true);
			}
			if(fLogTwo == null) {
				throw new RMSException(Msg.ERROR_LOG_TWO_IS_MISSING, true);
			}
			// check that files are not the same
			if(fLogOne.equals(fLogTwo)) {
				throw new RMSException(Msg.ERROR_LOGS_FOR_COMPARISON_ARE_THE_SAME, 
					new String[]{fLogOne});
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
			final int faggStep = aggStep;
			if(fTimeIntervalOne == null || fTimeIntervalTwo == null) {
				// scan first file
				runScanJob(fLogOne, new ScanEndCallback(){
					public void finishedScanning(Throwable err) {
						try {
							// scan second file
							runScanJob(fLogTwo, new ScanEndCallback(){
								public void finishedScanning(Throwable err) {
									try {
										if(err == null) {
											fResult = new DataLogCompareAndReplayConfiguration(
													new DataLogCompareAndReplayConfiguration.LogRepositoryReplayConfig(
															new LogRepositoryInfo(fRepositoryType, fLogOne), fTimeIntervalOne),
													new DataLogCompareAndReplayConfiguration.LogRepositoryReplayConfig(
															new LogRepositoryInfo(fRepositoryType, fLogTwo), fTimeIntervalTwo),
													faggStep							
											);
											dispose();
										}
									} catch(Exception e) {
										UIExceptionMgr.userException(e);
									}
								}
							});
						} catch(Exception e) {
							UIExceptionMgr.userException(e);
						}
					}
				});
			}
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

	private void showTimeIntervalSelectorDialog(
			final String logName) {
		if(fLogOne.equals(logName)) {
			if(fTimeIntervalOne != null) {
				TimeIntervalSelectorDialog dlg = new TimeIntervalSelectorDialog(fViewContainer, fTimeIntervalOne);
				UIUtils.centerDialogAndShow(fViewContainer.getAppFrame(), dlg);			
				fTimeIntervalOne = dlg.getResult();
			}
		} else if(fLogTwo.equals(logName)) {
			if(fTimeIntervalTwo != null) {
				TimeIntervalSelectorDialog dlg = new TimeIntervalSelectorDialog(fViewContainer, fTimeIntervalTwo);
				UIUtils.centerDialogAndShow(fViewContainer.getAppFrame(), dlg);
				fTimeIntervalTwo = dlg.getResult();
			}
		}
	}
}
