/*
 * Created on 16-Jan-2005
 */
package com.ixora.rms.repository.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.RepositoryComponent;
import com.ixora.rms.repository.messages.Msg;

/**
 * AgentAlreadyInstalled
 */
public class AgentAlreadyInstalled extends RMSException {

    public AgentAlreadyInstalled(String agentId) {
        super(RepositoryComponent.NAME, Msg.REPOSITORY_ERROR_AGENT_ALREADY_INSTALLED, new String[]{agentId});
    }
}
