/*
 * Created on 13-Dec-2003
 */
package com.ixora.rms.agents.logfile;

import java.io.File;

import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgent;
import com.ixora.rms.agents.logfile.exception.InvalidPath;
import com.ixora.rms.exception.InvalidConfiguration;

/**
 * Agent that monitors a log file and extracts log records as they are added.
 * @author Daniel Moraru
 */
public class LogFileAgent extends AbstractAgent {
	/**
	 * @param agentId
	 * @param listener
	 */
	public LogFileAgent(AgentId agentId, Listener listener) {
        super(agentId, listener);
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#configCustomChanged()
	 */
	protected void configCustomChanged() throws InvalidConfiguration, Throwable {
		Configuration conf = (Configuration)fContext.getAgentConfiguration().getAgentCustomConfiguration();
		String root = conf.getString(Configuration.LOG_FILE_PATH);
		File folder = new File(root);
		if(!folder.exists()) {
			throw new InvalidPath(folder.getAbsolutePath());
		}
		fRootEntity.addChildEntity(new LogFileEntity(fRootEntity.getId(), folder, fContext));
	}


}
