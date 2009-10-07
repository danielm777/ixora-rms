/**
 * 07-Sep-2005
 */
package com.ixora.rms.agents.jmxjsr77;

import java.io.File;
import java.util.Map;

import javax.naming.Context;

import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.jmx.jsr77.JMXJSR77AbstractAgent;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class JMXJSR77Agent extends JMXJSR77AbstractAgent {

	/**
	 * @param agentId
	 * @param listener
	 */
	public JMXJSR77Agent(AgentId agentId, Listener listener) {
		super(agentId, listener);
	}

	/**
	 * @throws RMSException
	 * @see com.ixora.rms.agents.impl.jmx.jsr77.JMXJSR77AbstractAgent#completeEnvironmentMap()
	 */
	protected void completeEnvironmentMap() throws RMSException {
		Configuration conf = (Configuration)fConfiguration.getAgentCustomConfiguration();
		// check that the path to root folder exists
		Object obj = conf.getObject(Configuration.ROOT_FOLDER);
		if(obj != null) {
			if(!new File(obj.toString()).exists()) {
				// TODO localize
				throw new RMSException("Root folder is an invalid path.");
			}
		}
		// complete the env map with extra props
		Map<String, ?> ep = conf.getExtraProperties();
		if(!Utils.isEmptyMap(ep)) {
			fEnvMap.putAll(ep);
		}
		String ictxtf = conf.getString(Configuration.INITIAL_CTXT_FACTORY);
		if(!Utils.isEmptyString(ictxtf)) {
			fEnvMap.put(Context.INITIAL_CONTEXT_FACTORY, ictxtf);
		}
	}
}
