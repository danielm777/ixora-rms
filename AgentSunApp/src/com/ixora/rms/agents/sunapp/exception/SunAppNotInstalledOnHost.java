/*
 * Created on 13-Jan-2005
 */
package com.ixora.rms.agents.sunapp.exception;

import com.ixora.rms.agents.sunapp.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class SunAppNotInstalledOnHost extends RMSException {

	/**
	 * Constructor.
	 */
	public SunAppNotInstalledOnHost(String host) {
		super(Msg.NAME, Msg.ERROR_SUNAPP_NOT_INSTALLED_ON_HOST, new String[] {host});
	}
}
