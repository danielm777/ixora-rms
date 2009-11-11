/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.jmxjsr77;

import com.ixora.rms.agents.jmxjsr77.messages.Msg;

/**
 * Configuration for Weblogic agent.
 * @author Daniel Moraru
 */
public class Configuration extends com.ixora.rms.agents.impl.jmx.jsr77.Configuration {
	public static final String INITIAL_CTXT_FACTORY = Msg.INITIAL_CTXT_FACTORY;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
        setProperty(INITIAL_CTXT_FACTORY, TYPE_STRING, true, false);
	}
}