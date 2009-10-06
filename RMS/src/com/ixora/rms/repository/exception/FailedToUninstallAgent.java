/*
 * Created on 16-Jan-2005
 */
package com.ixora.rms.repository.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.RepositoryComponent;
import com.ixora.rms.repository.messages.Msg;

/**
 * FailedToUninstallAgent
 */
public class FailedToUninstallAgent extends RMSException {

    public FailedToUninstallAgent(String agentId, Exception e) {
        super(RepositoryComponent.NAME, Msg.REPOSITORY_ERROR_FAILED_TO_UNINSTALL_AGENT,
        		new String[]{agentId, e.getLocalizedMessage()});
    }

    public FailedToUninstallAgent(String agentId, String e) {
        super(RepositoryComponent.NAME, Msg.REPOSITORY_ERROR_FAILED_TO_UNINSTALL_AGENT,
        		new String[]{agentId, e});
    }
}
