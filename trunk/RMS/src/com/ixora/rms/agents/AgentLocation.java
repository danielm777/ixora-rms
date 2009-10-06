package com.ixora.rms.agents;

import com.ixora.common.Enum;
import com.ixora.common.MessageRepository;
import com.ixora.rms.messages.Msg;

/**
 * Enumeration type for the monitoring agent locations.
 * @author: Daniel Moraru
 */
public final class AgentLocation extends Enum {
    public static final AgentLocation LOCAL =
    	new AgentLocation(0, MessageRepository.get(Msg.RMS_ENUM_AGENTLOCATION_LOCAL));
    public static final AgentLocation REMOTE =
    	new AgentLocation(1, MessageRepository.get(Msg.RMS_ENUM_AGENTLOCATION_REMOTE));

	/**
	 * Returns the enum element corresponding to the given key.
	 * @param key
	 * @return
	 */
	public static AgentLocation resolve(int key) {
		switch(key) {
		case 0:
			return LOCAL;
		case 1:
			return REMOTE;
		}
		return null;
	}

	/**
	 * Constructor.
	 * @param l
	 * @param name
	 */
    private AgentLocation(int l, String name) {
		super(l, name);
    }

	/**
	 * @see com.ixora.common.Enum#readResolve()
	 */
    protected Object readResolve() throws java.io.ObjectStreamException {
		return resolve(key);
    }
}
