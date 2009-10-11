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

import com.ixora.jobs.JobDefinition;
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
 * ActionScheduleJob.
 */
final class ActionCreateJobLike extends AbstractAction {
	private static final long serialVersionUID = 3698789332788718364L;
	/** Job engine */
    private JobEngineService jobEngine;
    /** Job manager viewer */
    private JobManagerViewer jobManagerViewer;
    /**
     * Constructor.
     * @param jv view
     * @param jes the job engine
     */
	public ActionCreateJobLike(JobManagerViewer jv, JobEngineService jes) {
		super();
		this.jobManagerViewer = jv;
		this.jobEngine = jes;
		UIUtils.setUsabilityDtls(MessageRepository.get(JobsComponent.NAME, Msg.ACTIONS_CREATE_JOB_LIKE), this);
		ImageIcon icon = UIConfiguration.getIcon("create_job_like.gif");
		if(icon != null) {
			putValue(Action.SMALL_ICON, icon);
		}
	}
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		try {
		    // find first the selected job
		    // get the selected job
		    JTable jobHistoryTable = this.jobManagerViewer.getJobTable();
		    int idx = jobHistoryTable.getSelectedRow();
		    if(idx < 0) {
		        return;
		    }
		    JobHistoryDetails dtls = ((JobHistoryTableModel)jobHistoryTable.getModel())
		    	.getJobHistoryDetailsAtRow(idx);

		    // get the job definition
		    jobManagerViewer.createJobDefinition(
		    		new JobManagerViewer.Callback(){
						public void jobDefinition(final JobDefinition jdef){
						    jobManagerViewer.getViewContainer().runJob(
						       new UIWorkerJobDefault(
						            jobManagerViewer.getWindow(),
									Cursor.WAIT_CURSOR,
									MessageRepository.get(JobsComponent.NAME, Msg.TEXT_SCHEDULING_JOB)) {
								public void work() throws Exception {
								    this.fResult = jobEngine.runJob(jdef);
								}
								public void finished(Throwable ex) {
									if(ex != null) {
										UIExceptionMgr.userException(ex);
									} else {
									    jobManagerViewer.refreshJobTable(((JobId[])this.fResult)[0]);
									}
								}
							});
						}},
		            dtls.getJobDefinition());
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}