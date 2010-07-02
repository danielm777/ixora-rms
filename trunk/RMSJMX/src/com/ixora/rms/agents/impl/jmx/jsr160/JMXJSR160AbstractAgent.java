/*
 * Created on 11-Apr-2005
 */
package com.ixora.rms.agents.impl.jmx.jsr160;

import java.io.IOException;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.jmx.JMXAbstractAgent;
import com.ixora.rms.agents.impl.jmx.JMXConnectionMBeanServerConnection;
import com.ixora.rms.agents.impl.jmx.exception.JMXCommunicationError;
import com.ixora.rms.agents.impl.jmx.exception.JMXSecurityError;
import com.ixora.rms.exception.InvalidConfiguration;

/**
 * @author Daniel Moraru
 */
public abstract class JMXJSR160AbstractAgent extends JMXAbstractAgent {
	/** JMX Connector */
	protected JMXConnector fConnector;


	/**
	 * Constructor.
	 */
	public JMXJSR160AbstractAgent(AgentId agentId, Listener listener) {
		super(agentId, listener);
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#configCustomChanged()
	 */
	protected void configCustomChanged() throws InvalidConfiguration, Throwable {
		super.configCustomChanged();
		String jmxURL = getJMXConnectionURL();
		if(Utils.isEmptyString(jmxURL)) {
			throw new InvalidConfiguration("JMX connection URL is missing");
		}
        JMXServiceURL url = new JMXServiceURL(jmxURL);
        // convert communication and security exceptions to RMSException to make sure
        // it is not going to be marked as an internal app error
        try {
        	fConnector = JMXConnectorFactory.connect(url, fEnvMap);
        	fJMXConnection = new JMXConnectionMBeanServerConnection(
        			fConnector.getMBeanServerConnection());
        } catch(IOException e) {
        	throw new JMXCommunicationError(e);
        } catch(SecurityException e) {
        	throw new JMXSecurityError(e);
        } catch(Exception e) {
        	processException(e);
		}
	}


	/**
	 * @see com.ixora.rms.agents.Agent#deactivate()
	 */
	public synchronized void deactivate() throws Throwable {
		super.deactivate();
		if(fConnector != null) {
			fConnector.close();
		}
	}

	/**
	 * Subclasses must return the jmx connection string.
	 * @return
	 * @throws Throwable
	 */
	protected abstract String getJMXConnectionURL() throws Throwable;

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getKeyFilter(java.lang.String)
	 */
	protected String getKeyFilter(String domain) {
		return "*";
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#sortEntities()
	 */
	protected boolean sortEntities() {
		return true;
	}
}
