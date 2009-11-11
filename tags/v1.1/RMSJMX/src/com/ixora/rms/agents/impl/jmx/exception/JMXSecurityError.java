/**
 * 16-Jul-2005
 */
package com.ixora.rms.agents.impl.jmx.exception;

import com.ixora.rms.agents.impl.jmx.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class JMXSecurityError extends RMSException {
	private static final long serialVersionUID = 8316903357348735250L;

	/**
	 * @param cause
	 */
	public JMXSecurityError(Throwable cause) {
		super(Msg.JMX_ERROR_SECURITY, cause, true);
	}
}
