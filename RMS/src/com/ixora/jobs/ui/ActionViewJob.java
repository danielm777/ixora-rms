/*
 * Created on 25-Sep-2004
 */
package com.ixora.jobs.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JTable;

import com.ixora.jobs.JobHistoryDetails;
import com.ixora.jobs.JobId;
import com.ixora.jobs.JobsComponent;
import com.ixora.jobs.ui.messages.Msg;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIUtils;


/**
 * ActionCancelJob.
 */
final class ActionViewJob extends AbstractAction {
    /** Viewer */
    private JobManagerViewer jobManagerViewer;

    /**
     * Constructor.
     * @param vc
     */
	public ActionViewJob(JobManagerViewer vc) {
		super();
		this.jobManagerViewer = vc;
		UIUtils.setUsabilityDtls(MessageRepository.get(JobsComponent.NAME, Msg.ACTIONS_VIEW_JOB), this);
		ImageIcon icon = UIConfiguration.getIcon("view_job.gif");
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
		    jobManagerViewer.showJobDefinition(dtls.getJobDefinition());
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}