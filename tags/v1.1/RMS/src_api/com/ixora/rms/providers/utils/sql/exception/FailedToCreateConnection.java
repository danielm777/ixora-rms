/**
 * 28-Mar-2006
 */
package com.ixora.rms.providers.utils.sql.exception;

import com.ixora.rms.exception.RMSException;

/**
 * Used to wrap FailedToCreateObject of the pooling library in order to
 * avoid exposing it to the client code.
 * @author Daniel Moraru
 */
public final class FailedToCreateConnection extends RMSException {
	private static final long serialVersionUID = -2100343484941873300L;

	/**
	 *
	 */
	public FailedToCreateConnection(String error) {
		super(error);
	}
}
