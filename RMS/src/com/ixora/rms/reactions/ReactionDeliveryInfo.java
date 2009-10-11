/**
 * 30-Jul-2005
 */
package com.ixora.rms.reactions;

import java.io.Serializable;

/**
 * @author Daniel Moraru
 */
public abstract class ReactionDeliveryInfo implements Serializable {
	private static final long serialVersionUID = 5730673938904107041L;
	protected String fMessage;
	protected Throwable fError;

	/**
	 * @param msg
	 */
	protected ReactionDeliveryInfo(String msg) {
		fMessage = msg;
	}
	/**
	 * @return message.
	 */
	public String getMessage() {
		return fMessage;
	}
	/**
	 * @return
	 */
	public Throwable getError() {
		return fError;
	}

	/**
	 * @param err
	 */
	protected void setError(Throwable err) {
		fError = err;
	}
}
