/*
 * Created on 22-Dec-2003
 */
package com.ixora.common;

/**
 * @author Daniel Moraru
 */
public interface ProgressProvider {
	/**
	 * Progress listener.
	 */
	public interface Listener {
		/**
		 * Invoked when the job is <code>pct</code> percent completed.
		 * @param pct percentage of the job that is completed
		 */
		void progress(float pct);
		/**
		 * Invoked when a given task is started as part of the
		 * bigger job.
		 * @param task task name
		 */
		void taskStarted(String task);
        /**
         * Invoked when a non-fatal error occurs during a work cycle.
         * @param error
         * @param t exception acompanying the error; it can be null
         */
        void nonFatalError(String error, Throwable t);
	}

	/**
	 * Adds the progress listener.
	 * @param listener
	 */
	void addListener(Listener listener);

	/**
	 * Removes the progress listener.
	 * @param listener
	 */
	void removeListener(Listener listener);
}
