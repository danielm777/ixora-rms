/*
 * Created on 25-Sep-2004
 */
package com.ixora.jobs.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import com.ixora.jobs.JobHistoryDetails;
import com.ixora.jobs.JobId;
import com.ixora.jobs.JobLogEvent;
import com.ixora.jobs.JobState;
import com.ixora.jobs.JobStateEvent;
import com.ixora.jobs.JobsComponent;
import com.ixora.jobs.services.JobEngineService;
import com.ixora.jobs.ui.messages.Msg;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.popup.PopupListener;
import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public final class JobManagerPanel extends JPanel {
// controls
	private JTable jTableJobHistory;
	private JPanel jPanelJobs;
	private JPanel jPanelLog;
	private JTextArea jTextAreaOutput;
	private JTextArea jTextAreaError;
	private JPopupMenu jPopupMenu;

// actions
	private Action actionRemoveJob;
	private Action actionCancelJob;
	private Action actionViewJob;
	private Action actionCreateJobLike;

	/** Job history table model */
	private JobHistoryTableModel tableModel;
	/** Job engine */
	private JobEngineService rmsJobEngineService;
	/** Event handler */
	private EventHandler eventHandler;

    /**
	 * Event handler.
	 */
	private final class EventHandler extends PopupListener
			implements ListSelectionListener, JobEngineService.Listener {
        /**
         * @see com.ixora.jobs.HostJobManager.Listener#jobStateEvent(com.ixora.rms.internal.jobs.JobStateEvent)
         */
        public void jobStateEvent(final JobStateEvent ev) {
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    handleJobStateEvent(ev);
                }
            });
        }
        /**
         * @see com.ixora.jobs.HostJobManager.Listener#jobLogEvent(com.ixora.rms.internal.jobs.JobLogEvent)
         */
        public void jobLogEvent(final JobLogEvent ev) {
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    handleJobLogEvent(ev);
                }
            });
        }
		/**
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()) {
				return;
			}
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			if(lsm.isSelectionEmpty()) {
				return;
			} else {
				if(lsm == getJTableJobHistory().getSelectionModel()) {
					// row selected
					handleJobSelected();
					return;
				}
			}
		}
        /**
		 * @see com.ixora.common.ui.popup.PopupListener#showPopup(java.awt.event.MouseEvent)
		 */
		protected void showPopup(MouseEvent e) {
			if(e.getSource() == getJTableJobHistory()) {
				handleShowPopupOnJobTable(e);
				return;
			}
		}
	}

	/**
     * Constructor.
     * @param acj
     * @param arj
     * @param avj
     * @param acjl
     * @param jobEngine
     */
    public JobManagerPanel(
            ActionCancelJob acj,
            ActionRemoveJob arj,
            ActionViewJob avj,
            ActionCreateJobLike acjl,
            JobEngineService jobEngine) {
        super(new BorderLayout());
        if(jobEngine == null) {
            throw new IllegalArgumentException("null job engine service");
        }
        this.actionCancelJob = acj;
        this.actionRemoveJob = arj;
        this.actionViewJob = avj;
        this.actionCreateJobLike = acjl;
        this.eventHandler = new EventHandler();
        this.rmsJobEngineService = jobEngine;
        this.tableModel = new JobHistoryTableModel(
                jobEngine.getJobHistory().getJobs().values());
        add(getJPanelJobs(), BorderLayout.NORTH);
        add(getJPanelLog(), BorderLayout.CENTER);

        jobEngine.addListener(eventHandler);
        getJTableJobHistory().getSelectionModel().addListSelectionListener(eventHandler);
        getJTableJobHistory().addMouseListener(eventHandler);

    }


	/**
	 * @return
	 */
	private JPanel getJPanelJobs() {
	    if(jPanelJobs == null) {
			JScrollPane jScrollPaneJobs = UIFactoryMgr.createScrollPane();
			Dimension d = new Dimension(500, 150);
			jScrollPaneJobs.setPreferredSize(d);
			jScrollPaneJobs.setMinimumSize(d);
			jScrollPaneJobs.setVerticalScrollBarPolicy(
					javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jScrollPaneJobs.setHorizontalScrollBarPolicy(
					javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jScrollPaneJobs.setViewportView(getJTableJobHistory());
			jPanelJobs = new JPanel(new BorderLayout());
			jPanelJobs.setBorder(UIFactoryMgr.createTitledBorder(
				MessageRepository.get(JobsComponent.NAME, Msg.TEXT_JOBS)));
			jPanelJobs.add(jScrollPaneJobs);
	    }
	    return jPanelJobs;
	}

	/**
	 * @return
	 */
	private JPanel getJPanelLog() {
	    if(jPanelLog == null) {
	        jPanelLog = new JPanel(new BorderLayout());
			JSplitPane sp = UIFactoryMgr.createSplitPane("jobmanagerpanel1");
			sp.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			sp.setBorder(null);
			jPanelLog.add(sp, BorderLayout.CENTER);
			JScrollPane scrollp = UIFactoryMgr.createScrollPane();
			scrollp.setVerticalScrollBarPolicy(
					javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollp.setHorizontalScrollBarPolicy(
					javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollp.setViewportView(getJTextAreaOutput());
			JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(UIFactoryMgr.createTitledBorder(
				MessageRepository.get(JobsComponent.NAME, Msg.TEXT_JOB_OUTPUT)));
			panel.add(scrollp);
			sp.setLeftComponent(panel);
			scrollp = UIFactoryMgr.createScrollPane();
			scrollp.setVerticalScrollBarPolicy(
					javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollp.setHorizontalScrollBarPolicy(
					javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollp.setViewportView(getJTextAreaError());
			panel = new JPanel(new BorderLayout());
			panel.setBorder(UIFactoryMgr.createTitledBorder(
					MessageRepository.get(JobsComponent.NAME, Msg.TEXT_JOB_ERROR)));
			panel.add(scrollp);
			sp.setRightComponent(panel);
			sp.setResizeWeight(0.5);
	    }
	    return jPanelLog;
	}

	/**
	 * @return
	 */
	private JTextArea getJTextAreaOutput() {
	    if(jTextAreaOutput == null) {
	        jTextAreaOutput = UIFactoryMgr.createTextArea();
	        jTextAreaOutput.setEditable(false);
	        jTextAreaOutput.setWrapStyleWord(true);
	        jTextAreaOutput.setLineWrap(true);
	    }
	    return jTextAreaOutput;
	}

	/**
	 * @return
	 */
	private JTextArea getJTextAreaError() {
	    if(jTextAreaError == null) {
	        jTextAreaError = UIFactoryMgr.createTextArea();
	        jTextAreaError.setEditable(false);
	        jTextAreaError.setWrapStyleWord(true);
	        jTextAreaError.setLineWrap(true);
	    }
	    return jTextAreaError;
	}

    /**
     * Refreshed the job history table
     * @param focus
     */
    public void refreshJobHistoryTable(JobId focus) {
        this.tableModel.setJobHistoryDetails(this.rmsJobEngineService
                .getJobHistory().getJobs().values());
        if(this.tableModel.getRowCount() == 0) {
            // clear log panels
            getJTextAreaError().setText("");
            getJTextAreaOutput().setText("");
        } else {
	        int idx;
	        if(focus != null) {
	            idx = this.tableModel.getRowForJob(focus);
	        } else {
	            idx = this.getJTableJobHistory().getSelectionModel().getLeadSelectionIndex();
	        }
	        // the second part is a workaround as it seems the selection interval
	        // is not updated if an element has just been remove from the table
            if(idx < 0 || idx >= this.tableModel.getRowCount()) {
                idx = 0;
            }
	        getJTableJobHistory().getSelectionModel().setSelectionInterval(idx, idx);
        }
    }

    /**
	 * This method initializes jTable
	 * @return javax.swing.JTable
	 */
	public JTable getJTableJobHistory() {
		if(jTableJobHistory == null) {
			jTableJobHistory = new JTable(this.tableModel);
			jTableJobHistory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			// job id
			TableColumn tc = jTableJobHistory.getColumnModel().getColumn(0);
			tc.setPreferredWidth(150);
			// name
			tc = jTableJobHistory.getColumnModel().getColumn(1);
			tc.setPreferredWidth(150);
			// state
			tc = jTableJobHistory.getColumnModel().getColumn(2);
			tc.setPreferredWidth(100);
			tc.setMaxWidth(100);
			// date scheduled
			tc = jTableJobHistory.getColumnModel().getColumn(3);
			tc.setPreferredWidth(150);
			// date started
			tc = jTableJobHistory.getColumnModel().getColumn(4);
			tc.setPreferredWidth(150);
			// date ended
			tc = jTableJobHistory.getColumnModel().getColumn(5);
			tc.setPreferredWidth(150);
		}
		return jTableJobHistory;
	}

	/**
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getJPopupMenu() {
		if(jPopupMenu == null) {
			jPopupMenu = UIFactoryMgr.createPopupMenu();
			jPopupMenu.add(UIFactoryMgr.createMenuItem(actionCancelJob));
			jPopupMenu.add(UIFactoryMgr.createMenuItem(actionRemoveJob));
			jPopupMenu.add(UIFactoryMgr.createMenuItem(actionViewJob));
			jPopupMenu.add(UIFactoryMgr.createMenuItem(actionCreateJobLike));
		}
		return jPopupMenu;
	}

    /**
     * @param ev
     */
    private void handleJobStateEvent(JobStateEvent ev) {
        try {
            if(ev.getState() == JobState.ERROR) {
                if(ev.getErrorData() != null) {
                    handleJobLogEvent(new JobLogEvent(
                               ev.getHost(), ev.getJid(),
                               JobLogEvent.ERROR, ev.getErrorData()));
                }
            }
            refreshJobHistoryTable(null);
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

    /**
     * @param ev
     */
    private void handleJobLogEvent(JobLogEvent ev) {
        try {
            // get selected job in the table
            int idx = getJTableJobHistory().getSelectedRow();
            if(idx < 0) {
                return;
            }
            if(idx != tableModel.getRowForJob(ev.getJid())) {
            	return;
            }
            // update the out and err logs
            JTextArea textArea;
            if(ev.getType() == JobLogEvent.OUTPUT) {
                textArea = getJTextAreaOutput();
            } else {
                textArea = getJTextAreaError();
            }
            textArea.append(ev.getData());
            textArea.append(Utils.getNewLine());
            textArea.setCaretPosition(textArea.getDocument().getLength());
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

	/**
     * @param ev
     */
    private void handleJobSelected() {
        try {
            int idx = getJTableJobHistory().getSelectedRow();
            if(idx < 0) {
                return;
            }
            JobHistoryDetails dtls = this.tableModel
            		.getJobHistoryDetailsAtRow(idx);
            // update the output and error panels
            getJTextAreaError().setText(dtls.getJobLogErr());
            getJTextAreaOutput().setText(dtls.getJobLogOut());

            JobState state = dtls.getJobState();
            if(state == JobState.STARTED
                    || state == JobState.SCHEDULED) {
                this.actionCancelJob.setEnabled(true);
                this.actionRemoveJob.setEnabled(false);
            } else {
                this.actionCancelJob.setEnabled(false);
                this.actionRemoveJob.setEnabled(true);
            }
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

    /**
     * @param ev
     */
    private void handleShowPopupOnJobTable(MouseEvent ev) {
        try {
            getJPopupMenu().show(ev.getComponent(), ev.getX(), ev.getY());
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }
}
