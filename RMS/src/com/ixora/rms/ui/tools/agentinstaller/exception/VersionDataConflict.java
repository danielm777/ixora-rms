/*
 * Created on 23-Aug-2004
 */
package com.ixora.rms.ui.tools.agentinstaller.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.tools.agentinstaller.AgentInstallerComponent;
import com.ixora.rms.ui.tools.agentinstaller.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class VersionDataConflict extends RMSException {
    /**
     * Constructor.
     */
    public VersionDataConflict() {
        super(AgentInstallerComponent.NAME, Msg.ERROR_ADD_VERSION_DATA_CONFLICT, true);
    }

}
