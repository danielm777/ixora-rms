/*
 * Created on 13-Jan-2005
 */
package com.ixora.rms.agents.websphere.exception;

import com.ixora.rms.agents.websphere.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class WebSphereNotInstalledOnHost extends RMSException {

	/**
	 * Constructor.
	 */
	public WebSphereNotInstalledOnHost(String host) {
		super(Msg.WEBSPHEREAGENT_NAME, Msg.WEBSPHEREAGENT_ERROR_NOT_INSTALLED_ON_HOST, new String[] {host});
	}
}
