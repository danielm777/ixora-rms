/*
 * Created on 25-Sep-2004
 */
package com.ixora.jobs.ui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JTable;

import com.ixora.jobs.JobHistoryDetails;
import com.ixora.jobs.JobId;
import com.ixora.jobs.JobsComponent;
import com.ixora.jobs.services.JobEngineService;
import com.ixora.jobs.ui.messages.Msg;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;


/**
 * ActionCancelJob.
 */
final class ActionCancelJob extends AbstractAction {
    /** Job engine */
    private JobEngineService jobEngine;
    /** Viewer */
    private JobManagerViewer jobManagerViewer;

    /**
     * Constructor.
     * @param vc
     * @param jes the job engine
     */
	public ActionCancelJob(JobManagerViewer vc, JobEngineService jes) {
		super();
		this.jobManagerViewer = vc;
		this.jobEngine = jes;
		UIUtils.setUsabilityDtls(MessageRepository.get(JobsComponent.NAME, Msg.ACTIONS_CANCEL_JOB), this);
		ImageIcon icon = UIConfiguration.getIcon("cancel_job.gif");
		if(icon != null) {
			putValue(Action.SMALL_ICON, icon);
		}
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		try {
		    // get the selected job
		    JTable jobHistoryTable = this.jobManagerViewer.getJobTable();
		    int idx = jobHistoryTable.getSelectedRow();
		    if(idx < 0) {
		        return;
		    }
		    JobHistoryDetails dtls = ((JobHistoryTableModel)jobHistoryTable.getModel())
		    	.getJobHistoryDetailsAtRow(idx);
		    final JobId jid = dtls.getJobId();
		    jobManagerViewer.getViewContainer().runJob(
		            new UIWorkerJobDefault(
		            jobManagerViewer.getWindow(),
					Cursor.WAIT_CURSOR,
					MessageRepository.get(JobsComponent.NAME, Msg.TEXT_CANCELING_JOB,
					        new String[]{jid.toString()})) {
				public void work() throws Exception {
				    jobEngine.cancelJob(jid);
				}
				public void finished(Throwable ex) {
					if(ex != null) {
						UIExceptionMgr.userException(ex);
					}
				}
			});
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}