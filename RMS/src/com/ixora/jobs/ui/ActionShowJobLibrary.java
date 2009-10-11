/*
 * Created on 25-Sep-2004
 */
package com.ixora.jobs.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import com.ixora.jobs.JobsComponent;
import com.ixora.jobs.services.JobLibraryService;
import com.ixora.jobs.ui.library.JobLibraryDialog;
import com.ixora.jobs.ui.messages.Msg;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIUtils;

/**
 * ActionScheduleJob.
 */
final class ActionShowJobLibrary extends AbstractAction {
	private static final long serialVersionUID = 5186980761164081964L;
	/** Job engine */
    private JobLibraryService jobLibrary;
    /** Job manager viewer */
    private JobManagerViewer jobManagerViewer;
    /**
     * Constructor.
     * @param jv view
     * @param jls the job library
     */
	public ActionShowJobLibrary(JobManagerViewer jv, JobLibraryService jls) {
		super();
		this.jobManagerViewer = jv;
		this.jobLibrary = jls;
		UIUtils.setUsabilityDtls(MessageRepository.get(JobsComponent.NAME, Msg.ACTIONS_SHOW_JOB_LIBRARY), this);
		ImageIcon icon = UIConfiguration.getIcon("show_job_lib.gif");
		if(icon != null) {
			putValue(Action.SMALL_ICON, icon);
		}
	}
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		try {
			JobLibraryDialog dlg = new JobLibraryDialog(
					this.jobManagerViewer.getViewContainer(), this.jobLibrary, false);
			UIUtils.centerDialogAndShow(this.jobManagerViewer.getWindow(), dlg);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}