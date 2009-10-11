/*
 * Created on 31-Dec-2003
 */
package com.ixora.rms.ui.session.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.session.MonitoringSessionRepositoryComponent;
import com.ixora.rms.ui.session.messages.Msg;

/**
 * FailedToSaveScheme.
 * @author Daniel Moraru
 */
public final class FailedToSaveSession extends RMSException {
	private static final long serialVersionUID = 6258598479834165447L;

	/**
	 * @param e
	 */
	public FailedToSaveSession(Throwable e) {
		super(MonitoringSessionRepositoryComponent.NAME,
		        Msg.SESSION_FAILED_TO_SAVE_SESSION, new String[]{"",
			       e.getMessage() != null ? e.getMessage() : e.toString()});
	}

	/**
	 * @param e
	 */
	public FailedToSaveSession(String scheme, Throwable e) {
		super(MonitoringSessionRepositoryComponent.NAME,
		      Msg.SESSION_FAILED_TO_SAVE_SESSION, new String[]{scheme,
		        e.getMessage() != null ? e.getMessage() : e.toString()});
	}

}
