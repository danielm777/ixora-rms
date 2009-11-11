package com.ixora.common.ui.jobs;

/**
 * @author Daniel Moraru
 */
public interface UIWorker {
	/**
	 * Runs the given job synchronously. Must be called from the event dispatch
	 * thread.
	 * @param job
	 */
	void runJobSynch(UIWorkerJob job);

	/**
	 * Runs the given job. Must be called from the event dispatch
	 * thread.
	 * @param job
	 */
	void runJob(UIWorkerJob job);

}