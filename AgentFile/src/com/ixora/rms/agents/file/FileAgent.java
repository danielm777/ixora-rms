/*
 * Created on 13-Dec-2003
 */
package com.ixora.rms.agents.file;

import java.io.File;

import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.file.exception.InvalidPath;
import com.ixora.rms.agents.impl.AbstractAgent;
import com.ixora.rms.exception.InvalidConfiguration;

/**
 * FileAgent.
 * @author Daniel Moraru
 */
public class FileAgent extends AbstractAgent {
	/**
	 * @param agentId
	 * @param listener
	 */
	public FileAgent(AgentId agentId, Listener listener) {
        super(agentId, listener, true); // use private collector
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#configCustomChanged()
	 */
	protected void configCustomChanged() throws InvalidConfiguration, Throwable {
		Configuration conf = (Configuration)fContext.getAgentConfiguration().getAgentCustomConfiguration();
		String root = conf.getString(Configuration.ROOT_FOLDER);
		File folder = new File(root);
		if(!folder.exists()) {
			throw new InvalidPath(folder.getAbsolutePath());
		}
		fRootEntity.addChildEntity(new FileEntity(fRootEntity.getId(), folder, fContext));
	}
}
