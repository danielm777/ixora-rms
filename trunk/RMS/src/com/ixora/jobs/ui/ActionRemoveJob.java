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
final class ActionRemoveJob extends AbstractAction {
	private static final long serialVersionUID = -7085804597164637005L;
	/** Viewer */
    private JobManagerViewer jobManagerViewer;

    /**
     * Constructor.
     * @param vc
     */
	public ActionRemoveJob(JobManagerViewer vc) {
		super();
		this.jobManagerViewer = vc;
		UIUtils.setUsabilityDtls(MessageRepository.get(JobsComponent.NAME, Msg.ACTIONS_REMOVE_JOB), this);
		ImageIcon icon = UIConfiguration.getIcon("remove_job.gif");
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
		    JobId jid = dtls.getJobId();
		    this.jobManagerViewer.getJobHistory().removeJob(jid);
		    this.jobManagerViewer.refreshJobTable(null);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}