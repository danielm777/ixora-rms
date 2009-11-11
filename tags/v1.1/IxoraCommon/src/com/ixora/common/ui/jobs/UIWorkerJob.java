/*
 * Created on 13-Dec-2003
 */
package com.ixora.common.ui.jobs;

import java.awt.Window;

import com.ixora.common.ProgressProvider;

/**
 * @author Daniel Moraru
 */
public interface UIWorkerJob {
	/**
	 * The work that needs to be done
	 * @throws Throwable
	 */
	void work() throws Throwable;
	/**
	 * Method invoked on the event dispatching thread
	 * when the work is done.
	 * @param ex not null if <code>work()</code> has thrown
	 * an exception
	 * @throws Throwable
	 */
	void finished(Throwable ex) throws Throwable;
	/**
	 * @return the cursor type
	 */
	int getCursor();
	/**
	 * @return the window that owns this job
	 */
	Window getOwnerComponent();
	/**
	 * @return the job description
	 */
	String getDescription();
	/**
	 * @return the progress provider for this job or null
	 * if the job doesn't have one
	 */
	ProgressProvider getProgressProvider();
}
