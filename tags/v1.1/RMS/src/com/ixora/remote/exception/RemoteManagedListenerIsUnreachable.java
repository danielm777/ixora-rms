/*
 * Created on 19-Feb-2005
 */
package com.ixora.remote.exception;

import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class RemoteManagedListenerIsUnreachable extends RMSException {
	private static final long serialVersionUID = -8433642088647044740L;

	/**
	 * Constructor.
	 * @param e
	 */
	public RemoteManagedListenerIsUnreachable(Exception e) {
		super(e);
	}
}
