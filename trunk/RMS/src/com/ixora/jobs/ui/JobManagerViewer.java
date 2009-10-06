/*
 * Created on 28-Sep-2004
 */
package com.ixora.jobs.ui;

import java.awt.Window;

import javax.swing.JTable;

import com.ixora.jobs.JobDefinition;
import com.ixora.jobs.JobHistory;
import com.ixora.jobs.JobId;
import com.ixora.rms.ui.RMSViewContainer;

/**
 * @author Daniel Moraru
 */
public interface JobManagerViewer {
	public interface Callback {
		/**
		 * @param def
		 * @throws Exception
		 */
		void jobDefinition(JobDefinition def) throws Exception;
	}
    /**
     * @return
     * @param def
     */
    void createJobDefinition(Callback cb, JobDefinition def);
    /**
     * Shows the given job definition.
     * @param def
     */
    void showJobDefinition(JobDefinition def);
    /**
     * @return
     */
    RMSViewContainer getViewContainer();
    /**
     * @return
     */
    Window getWindow();
    /**
     * @return
     */
    JTable getJobTable();
    /**
     * Refresh the job table
     * @param focus the job id that would be in focus
     */
    void refreshJobTable(JobId focus);
    /**
     * @return the job history
     */
    JobHistory getJobHistory();
}
