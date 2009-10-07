package com.ixora.rms.agents.file.exception;

import com.ixora.rms.agents.file.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class InvalidPath extends RMSException {
	/**
	 * Constructor.
	 */
	public InvalidPath(String path) {
		super(Msg.FILE_NAME, Msg.ERROR_INVALID_PATH, new String[]{path});
	}
}
