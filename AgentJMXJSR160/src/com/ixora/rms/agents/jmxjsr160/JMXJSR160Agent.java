/**
 * 03-Sep-2005
 */
package com.ixora.rms.agents.jmxjsr160;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Map;

import javax.management.remote.JMXConnector;

import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.jmx.jsr160.JMXJSR160AbstractAgent;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class JMXJSR160Agent extends JMXJSR160AbstractAgent {

	/**
	 * @param agentId
	 * @param listener
	 */
	public JMXJSR160Agent(AgentId agentId, Listener listener) {
		super(agentId, listener);
	}

	/**
	 * @see com.ixora.rms.agents.Agent#configure(com.ixora.rms.agents.AgentConfiguration)
	 */
	public synchronized EntityDescriptorTree configure(AgentConfiguration newConf) throws InvalidConfiguration, Throwable {
		try {
			return super.configure(newConf);
		} catch(MalformedURLException e) {
			// TODO localize
			throw new RMSException("The connection url is invalid.", e);
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.jsr160.JMXJSR160AbstractAgent#getJMXConnectionURL()
	 */
	protected String getJMXConnectionURL() throws Throwable {
		// check that the path to root folder exists
		Object obj = fConfiguration.getAgentCustomConfiguration().getObject(Configuration.ROOT_FOLDER);
		if(obj != null) {
			if(!new File(obj.toString()).exists()) {
				// TODO localize
				throw new RMSException("Root folder is an invalid path.");
			}
		}

		// complete the env map with extra props
		Configuration conf = (Configuration)fConfiguration.getAgentCustomConfiguration();
		Map<String, ?> ep = conf.getExtraProperties();
		if(!Utils.isEmptyMap(ep)) {
			fEnvMap.putAll(ep);
		}

		// pass credentials if present
		String username = fConfiguration.getAgentCustomConfiguration().getString(Configuration.USERNAME);
		if(!Utils.isEmptyString(username)) {
			String password = fConfiguration.getAgentCustomConfiguration().getString(Configuration.PASSWORD);
			if(password == null) {
				password = "";
			}
			String[] credentials = new String[] { username , password };
	        fEnvMap.put(JMXConnector.CREDENTIALS, credentials);
		}

		return fConfiguration.getAgentCustomConfiguration().getString(Configuration.JMX_CONNECTION_STRING);
	}
}
