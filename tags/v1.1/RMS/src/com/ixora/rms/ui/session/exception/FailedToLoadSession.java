/*
 * Created on 31-Dec-2003
 */
package com.ixora.rms.ui.session.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.session.MonitoringSessionRepositoryComponent;
import com.ixora.rms.ui.session.messages.Msg;

/**
 * FailedToLoadScheme.
 * @author Daniel Moraru
 */
public final class FailedToLoadSession extends RMSException {
	private static final long serialVersionUID = -7885774394954208847L;

	/**
	 * @param scheme the name of the scheme
	 * @param e
	 */
	public FailedToLoadSession(String scheme, Throwable e) {
		super(MonitoringSessionRepositoryComponent.NAME,
		        Msg.SESSION_FAILED_TO_LOAD_SESSION, new String[]{scheme,
			       e.getMessage() != null ? e.getMessage() : e.toString()});
	}
}
