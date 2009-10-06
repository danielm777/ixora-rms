/**
 * 10-Jul-2005
 */
package com.ixora.rms.ui.tools.agentinstaller.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.tools.agentinstaller.AgentInstallerComponent;
import com.ixora.rms.ui.tools.agentinstaller.messages.Msg;

/**
 * @author Daniel Moraru
 */
public class VersionDataMissing extends RMSException {
	public VersionDataMissing() {
		super(AgentInstallerComponent.NAME, Msg.ERROR_VERSION_DATA_MISSING, true);
	}
}
