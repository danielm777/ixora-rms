/**
 * 29-Jul-2005
 */
package com.ixora.rms.reactions;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;

/**
 * @author Daniel Moraru
 */
public final class ReactionEvent implements Serializable {
	private long fTimestamp;
	private String fMessage;
	private Object[] fParameters;
	private String fSeverity;

	/**
	 * @param message
	 * @param parameters
	 * @param severity
	 */
	public ReactionEvent(String message, Object[] parameters, String severity) {
		super();
		fTimestamp = System.currentTimeMillis();
		fMessage = message;
		fParameters = parameters;
		fSeverity = severity;
	}

	/**
	 * @return message.
	 */
	public String getMessage() {
		return fMessage;
	}

	/**
	 * @return message parameters.
	 */
	public Object[] getParameters() {
		return fParameters;
	}

	/**
	 * @return
	 */
	public long getTimestamp() {
		return fTimestamp;
	}

	/**
	 * @return severity
	 */
	public String getSeverity() {
		return fSeverity;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[" + new Date(fTimestamp) + "][" + fSeverity + "] "
			+ MessageFormat.format(fMessage, fParameters);
	}
}
