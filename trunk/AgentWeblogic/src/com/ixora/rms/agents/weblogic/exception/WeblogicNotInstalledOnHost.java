/*
 * Created on 13-Jan-2005
 */
package com.ixora.rms.agents.weblogic.exception;

import com.ixora.rms.agents.weblogic.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class WeblogicNotInstalledOnHost extends RMSException {
	private static final long serialVersionUID = -1193556501034849760L;

	/**
	 * Constructor.
	 */
	public WeblogicNotInstalledOnHost(String host) {
		super(Msg.NAME, Msg.ERROR_WEBLOGIC_NOT_INSTALLED_ON_HOST, new String[] {host});
	}
}
