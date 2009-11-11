/*
 * Created on 13-Jan-2005
 */
package com.ixora.rms.agents.jboss.exception;

import com.ixora.rms.agents.jboss.messages.Msg;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class JBossNotInstalledOnHost extends RMSException {

	/**
	 * Constructor.
	 */
	public JBossNotInstalledOnHost(String host) {
		super(Msg.NAME, Msg.ERROR_JBOSS_NOT_INSTALLED_ON_HOST, new String[] {host});
	}
}
