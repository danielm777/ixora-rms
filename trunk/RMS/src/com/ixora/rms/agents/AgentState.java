package com.ixora.rms.agents;

import com.ixora.common.Enum;
import com.ixora.common.MessageRepository;
import com.ixora.rms.messages.Msg;

/**
 * Enumeration type for the monitoring agent state.
 * @author: Daniel Moraru
 */
public final class AgentState extends Enum {
    public static final AgentState READY =
    	new AgentState(0, MessageRepository.get(Msg.RMS_ENUM_AGENTSTATE_READY));
    public static final AgentState STARTED =
    	new AgentState(1, MessageRepository.get(Msg.RMS_ENUM_AGENTSTATE_STARTED));
    public static final AgentState FINISHED =
    	new AgentState(2, MessageRepository.get(Msg.RMS_ENUM_AGENTSTATE_FINISHED));
    public static final AgentState STOPPED =
    	new AgentState(3, MessageRepository.get(Msg.RMS_ENUM_AGENTSTATE_STOPPED));
    public static final AgentState ERROR =
    	new AgentState(4, MessageRepository.get(Msg.RMS_ENUM_AGENTSTATE_ERROR));
	public static final AgentState UNKNOWN =
		new AgentState(5, MessageRepository.get(Msg.RMS_ENUM_AGENTSTATE_UNKNOWN));
	public static final AgentState DEACTIVATED =
		new AgentState(6, MessageRepository.get(Msg.RMS_ENUM_AGENTSTATE_DEACTIVATED));

	/**
	 * Constructor.
	 * @param s
	 * @param name
	 */
    private AgentState(int s, String name) {
		super(s, name);
    }

	/**
	 * @see com.ixora.common.Enum#readResolve()
	 */
    protected Object readResolve() throws java.io.ObjectStreamException {
        switch(key) {
        case 0:
            return READY;

        case 1:
            return STARTED;

        case 2:
            return FINISHED;

        case 3:
            return STOPPED;

        case 4:
            return ERROR;

        case 5:
        	return UNKNOWN;

        case 6:
        	return DEACTIVATED;
        }

        return null;
    }
}
