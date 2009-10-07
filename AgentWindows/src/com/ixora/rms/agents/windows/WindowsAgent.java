package com.ixora.rms.agents.windows;

import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgent;
import com.ixora.rms.agents.windows.exception.WinPerfNotSupportedException;

/**
 * WindowsAgent
 * Monitors the activity of Windows systems, retrieving information
 * from the common performance repository. Thus, depending on the
 * installed applications, more information can be available, for
 * example .NET, SQL Server, DB2, Oracle and so on.
 */
public class WindowsAgent extends AbstractAgent {
	/**
	 * Constructor, can throw exception if the native DLL is not
	 * found, which really means we're not running on Windows.
	 * @throws WinPerfNotSupportedException
	 * @throws Throwable
	 */
	public WindowsAgent(AgentId agentId, Listener listener) throws WinPerfNotSupportedException {
        super(agentId, listener);
		// Create entities
		fRootEntity = new WinPerfRootEntity(this.fContext);
	}
}
