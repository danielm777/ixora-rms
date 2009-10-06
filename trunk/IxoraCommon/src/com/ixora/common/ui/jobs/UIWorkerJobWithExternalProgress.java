/*
 * Created on 08-Jul-2004
 */
package com.ixora.common.ui.jobs;

import java.awt.Window;

import com.ixora.common.Progress;
import com.ixora.common.ProgressProvider;

/**
 * A job that provides progress feedback from an external provider.
 * @author Daniel Moraru
 */
public abstract class UIWorkerJobWithExternalProgress
		extends UIWorkerJobDefault {
	/** Progress which will be passed to another object during <code>work()</code> */
	protected Progress fProgress;

	/**
	 * Constructor.
	 * @param owner window
	 * @param cursor the cursor type
	 * @param description the job description
	 */
	protected UIWorkerJobWithExternalProgress(
				Window owner,
				int cursor,
				String description) {
		super(owner, cursor, description);
		init(owner, cursor, description);
	}

	/**
	 * @see com.ixora.common.ui.jobs.UIWorkerJob#getProgressProvider()
	 */
	public ProgressProvider getProgressProvider() {
		return this.fProgress;
	}

	/**
	 * Init this instance.
	 * @param owner
	 * @param cursor
	 * @param description
	 * @param progressProvider
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
		this.fProgress = new Progress();
	}
}
