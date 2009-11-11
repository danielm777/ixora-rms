/*
 * Created on 21-Dec-2003
 */
package com.ixora.common.ui.jobs;

import java.awt.Window;

/**
 * @author Daniel Moraru
 */
public abstract class UIWorkerJobDefaultCancelable
	extends UIWorkerJobDefault	implements UIWorkerJobCancelable {

	/**
	 * Constructor.
	 * @param owner window
	 * @param cursor the cursor type
	 * @param description the job description
	 */
	protected UIWorkerJobDefaultCancelable(
				Window owner,
				int cursor,
				String description) {
		super(owner, cursor, description);
	}

	/**
	 * @see com.ixora.common.Cancelable#cancel()
	 */
	public void cancel() {
		this.fCanceled = true;
		// call this in case this job is in the hold mode
		wakeUp();
	}

	/**
	 * @return true if the job was canceled
	 */
	protected boolean canceled() {
		return fCanceled;
	}
}
