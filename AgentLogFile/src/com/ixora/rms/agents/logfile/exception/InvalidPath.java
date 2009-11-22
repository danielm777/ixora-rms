package com.ixora.rms.agents.logfile.exception;

import com.ixora.rms.agents.logfile.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class InvalidPath extends RMSException {
	private static final long serialVersionUID = -2002201892911276737L;

	/**
	 * Constructor.
	 */
	public InvalidPath(String path) {
		super(Msg.LOG_FILE_NAME, Msg.ERROR_INVALID_PATH, new String[]{path});
	}
}
