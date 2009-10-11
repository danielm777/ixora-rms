package com.ixora.rms.repository;

import com.ixora.common.Enum;
import com.ixora.common.MessageRepository;
import com.ixora.rms.repository.messages.Msg;

/**
 * Enumeration type for the monitoring agent category.
 * @author: Daniel Moraru
 */
public final class AgentCategory extends Enum {
	private static final long serialVersionUID = -7301560753349821330L;
	public static final AgentCategory MISCELLANEOUS =
    	new AgentCategory(0, MessageRepository.get(RepositoryComponent.NAME, Msg.AGENT_CATEGORY_MISCELLANEOUS), Msg.AGENT_CATEGORY_MISCELLANEOUS);
    public static final AgentCategory DATABASES =
    	new AgentCategory(1, MessageRepository.get(RepositoryComponent.NAME, Msg.AGENT_CATEGORY_DATABASES), Msg.AGENT_CATEGORY_DATABASES);
    public static final AgentCategory APPLICATIONS =
    	new AgentCategory(2, MessageRepository.get(RepositoryComponent.NAME, Msg.AGENT_CATEGORY_APPLICATIONS), Msg.AGENT_CATEGORY_APPLICATIONS);
    public static final AgentCategory APP_SERVERS =
    	new AgentCategory(3, MessageRepository.get(RepositoryComponent.NAME, Msg.AGENT_CATEGORY_APP_SERVERS), Msg.AGENT_CATEGORY_APP_SERVERS);
    public static final AgentCategory NETWORK =
    	new AgentCategory(4, MessageRepository.get(RepositoryComponent.NAME, Msg.AGENT_CATEGORY_NETWORK), Msg.AGENT_CATEGORY_NETWORK);
    public static final AgentCategory OPERATING_SYSTEMS =
    	new AgentCategory(5, MessageRepository.get(RepositoryComponent.NAME, Msg.AGENT_CATEGORY_OPERATING_SYSTEMS), Msg.AGENT_CATEGORY_OPERATING_SYSTEMS);
    public static final AgentCategory WEB_SERVERS =
    	new AgentCategory(6, MessageRepository.get(RepositoryComponent.NAME, Msg.AGENT_CATEGORY_WEB_SERVERS), Msg.AGENT_CATEGORY_WEB_SERVERS);

    private String fStringID;

	/**
	 * Constructor.
	 * @param s
	 * @param name
	 * @param sid
	 */
    private AgentCategory(int s, String name, String sid) {
		super(s, name);
		fStringID = sid;
    }

    /**
     * @return
     */
    public String getStringID() {
    	return fStringID;
    }

	/**
	 * Returns the enum element corresponding to the given key.
	 * @param stringID
	 * @return
	 */
	public static AgentCategory resolve(String stringID) {
		if(stringID.equals(Msg.AGENT_CATEGORY_MISCELLANEOUS)) {
			return MISCELLANEOUS;
		} else if(stringID.equals(Msg.AGENT_CATEGORY_APP_SERVERS)) {
			return APP_SERVERS;
		} else if(stringID.equals(Msg.AGENT_CATEGORY_APPLICATIONS)) {
			return APPLICATIONS;
		} else if(stringID.equals(Msg.AGENT_CATEGORY_DATABASES)) {
			return DATABASES;
		} else if(stringID.equals(Msg.AGENT_CATEGORY_NETWORK)) {
			return NETWORK;
		} else if(stringID.equals(Msg.AGENT_CATEGORY_OPERATING_SYSTEMS)) {
			return OPERATING_SYSTEMS;
		} else if(stringID.equals(Msg.AGENT_CATEGORY_WEB_SERVERS)) {
			return WEB_SERVERS;
		}
		return null;
	}

	/**
	 * @see com.ixora.common.Enum#readResolve()
	 */
    protected Object readResolve() throws java.io.ObjectStreamException {
        switch(key) {
        case 0:
            return MISCELLANEOUS;
        case 1:
            return DATABASES;
        case 2:
            return APPLICATIONS;
        case 3:
            return APP_SERVERS;
        case 4:
            return NETWORK;
        case 5:
        	return OPERATING_SYSTEMS;
        case 6:
        	return WEB_SERVERS;
        }
        return null;
    }
}
