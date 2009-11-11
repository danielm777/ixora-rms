/*
 * Created on 08-Jul-2004
 */
package com.ixora.common.ui.jobs;

import java.awt.Window;

import com.ixora.common.Progress;
import com.ixora.common.ProgressProvider;

/**
 * A job that provides progress feedback.
 * @author Daniel Moraru
 */
public abstract class UIWorkerJobWithProgress
		extends UIWorkerJobDefault {
	/** Job progress */
	protected Progress fProgress;

	/**
	 * Constructor.
	 * @param owner window
	 * @param cursor the cursor type
	 * @param description the job description
	 * @param completion the value that signals that the job is complete<br>
	 * If this value is negative then progress is not provided else
	 * subclasses must call <code>setProgress(String, int)</code> repeatedly
	 * with the last call having as parameter the value of <code>completion</code>.
	 */
	protected UIWorkerJobWithProgress(
				Window owner,
				int cursor,
				String description,
				int completion) {
		super(owner, cursor, description);
		init(owner, cursor, description);
		if(completion > 0) {
			this.fProgress.setMax(completion);
		}
	}

	/**
	 * @see com.ixora.common.ui.jobs.UIWorkerJob#getProgressProvider()
	 */
	public ProgressProvider getProgressProvider() {
		return this.fProgress;
	}

	/**
	 * This must be call before starting the given
	 * step.
	 * @param step
	 */
	protected void setProgressStep(String step) {
		if(this.fProgress != null) {
			this.fProgress.setTask(step);
		}
	}

	/**
	 * This must be called after completing a discrete part
	 * of a job.
	 * @param p
	 */
	protected void setProgressLevel(int p) {
		if(this.fProgress != null) {
			this.fProgress.setDelta(p);
		}
	}

	/**
	 * Called by subclasses when the job is completed.
	 */
	protected void jobCompleted() {
		if(this.fProgress != null) {
			this.fProgress.done();
		}
	}

	/**
	 * Init this instance.
	 * @param owner
	 * @param cursor
	 * @param description
	 * @param completion
	 */
	private void init(Window owner,
			int cursor,
			String description) {
		if(owner == null) {
			throw new IllegalArgumentException("null owner component");
		}
		this.fOwner = owner;
		this.fCursor = cursor;
		this.fDescription = description;
	}
}
