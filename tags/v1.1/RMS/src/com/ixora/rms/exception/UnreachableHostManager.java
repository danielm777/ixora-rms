/*
 * Created on 19-Nov-2003
 */
package com.ixora.rms.exception;

import com.ixora.rms.RMSComponent;
import com.ixora.rms.messages.Msg;

/**
 * UnreachableHostManager.
 * @author Daniel Moraru
 */
public final class UnreachableHostManager extends RMSException {
	private static final long serialVersionUID = -3811172840212271965L;

	/**
	 * Constructor.
	 * @param host
	 */
	public UnreachableHostManager(String host) {
		super(RMSComponent.NAME, Msg.RMS_UNREACHABLE_HOST_MANAGER,
				new String[] {host});
	}
}
