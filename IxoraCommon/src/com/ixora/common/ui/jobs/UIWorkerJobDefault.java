/*
 * Created on 21-Dec-2003
 */
package com.ixora.common.ui.jobs;

import java.awt.Window;

import com.ixora.common.ProgressProvider;

/**
 * @author Daniel Moraru
 */
public abstract class UIWorkerJobDefault
		implements UIWorkerJob {
	/** Owner window */
	protected Window fOwner;
	/** Cursor */
	protected int fCursor;
	/** Job description */
	protected String fDescription;
	/** Result object */
	protected Object fResult;
	/** Whether or not the job was canceled */
	protected volatile boolean fCanceled;

	/**
	 * Constructor.
	 * @param owner window
	 * @param cursor the cursor type
	 * @param description the job description
	 */
	protected UIWorkerJobDefault(
				Window owner,
				int cursor,
				String description) {
		super();
		this.fOwner = owner;
		this.fCursor = cursor;
		this.fDescription = description;
	}

	/**
	 * @see com.ixora.common.ui.jobs.UIWorkerJob#getOwnerComponent()
	 */
	public Window getOwnerComponent() {
		return this.fOwner;
	}

	/**
	 * @see com.ixora.common.ui.jobs.UIWorkerJob#getCursor()
	 */
	public int getCursor() {
		return this.fCursor;
	}

	/**
	 * @see com.ixora.common.ui.jobs.UIWorkerJob#getDescription()
	 */
	public String getDescription() {
		return this.fDescription;
	}

	/**
	 * @see com.ixora.common.Cancelable#cancel()
	 */
	public void cancel() {
		this.fCanceled = true;
	}

	/**
	 * @see com.ixora.common.ui.jobs.UIWorkerJob#getProgressProvider()
	 */
	public ProgressProvider getProgressProvider() {
		return null;
	}

	/**
	 * Holds this job's thread until <code>wakeUp()</code> is called.
	 * @throws InterruptedException
	 */
	protected void hold() throws InterruptedException {
		synchronized(this) {
			wait();
		}
	}

	/**
	 * Wakes up this job's thread.
	 */
	protected void wakeUp() {
		synchronized(this) {
			notify();
		}
	}
}
