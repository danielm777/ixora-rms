/**
 * 24-Nov-2005
 */
package com.ixora.common;

/**
 * @author Daniel Moraru
 */
public interface ProgressMonitor {

	/**
	 * Sets the max value indicating the task is completed.
	 * @param max
	 */
	void setMax(long max);

	/**
	 * Sets the task or stage name.
	 * @param task
	 */
	void setTask(String task);

	/**
	 * Invoke this to report a non-fatal error.
	 * @param error
	 * @param t
	 */
	void nonFatalError(String error, Throwable t);

	/**
	 * Increases the progress level.
	 * @param delta
	 */
	void setDelta(long delta);

	/**
	 * Signals that the job has completed.
	 */
	void done();

	/**
	 * @see com.ixora.common.Reusable#reset()
	 */
	void reset();

}