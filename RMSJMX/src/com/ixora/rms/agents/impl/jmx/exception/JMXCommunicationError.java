/**
 * 16-Jul-2005
 */
package com.ixora.rms.agents.impl.jmx.exception;

import com.ixora.rms.agents.impl.jmx.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class JMXCommunicationError extends RMSException {
	private static final long serialVersionUID = 7301704755972196761L;

	/**
	 * @param cause
	 */
	public JMXCommunicationError(Throwable cause) {
		super(Msg.JMX_ERROR_COMMUNICATION, cause, true);
	}
}
