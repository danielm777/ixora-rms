/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.serviceavailability;

import com.ixora.common.typedproperties.ui.ExtendedEditorMultilineText;
import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.serviceavailability.messages.Msg;

/**
 * Configuration for host availability agent.
 * @author Daniel Moraru
 */
public final class Configuration extends AgentCustomConfiguration {
	private static final long serialVersionUID = 8749655691369981096L;
	public static final String DATA = Msg.DATA;
	public static final String PORT = Msg.PORT;
	public static final String READ_TIMEOUT = Msg.READ_TIMEOUT;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
		setProperty(DATA, TYPE_SERIALIZABLE, true, true, null, ExtendedEditorMultilineText.class.getName());
		setProperty(PORT, TYPE_INTEGER, true, true);
		setProperty(READ_TIMEOUT, TYPE_INTEGER, true, true);
		setInt(READ_TIMEOUT, 4000);
	}
}