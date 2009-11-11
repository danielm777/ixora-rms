/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.providers.impl.java.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.ProvidersComponent;
import com.ixora.rms.providers.impl.java.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class ImplementationClassNotFound extends RMSException {
	private static final long serialVersionUID = -460068567722143027L;

	/**
	 * Constructor.
	 * @param className
	 */
	public ImplementationClassNotFound(String className) {
		super(ProvidersComponent.NAME, Msg.ERROR_IMPLEMENTATION_CLASS_NOT_FOUND, new String[] {className});
	}
}
