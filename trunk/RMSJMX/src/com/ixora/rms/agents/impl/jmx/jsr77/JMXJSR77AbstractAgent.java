/**
 * 03-Sep-2005
 */
package com.ixora.rms.agents.impl.jmx.jsr77;

import java.util.HashMap;
import java.util.Hashtable;

import javax.management.ObjectName;
import javax.management.j2ee.Management;
import javax.management.j2ee.ManagementHome;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.jmx.JMXAbstractAgent;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.JMXConnectionMBeanServer;
import com.ixora.rms.agents.impl.jmx.MBeanServerProxy;
import com.ixora.rms.exception.InvalidConfiguration;

/**
 * @author Daniel Moraru
 */
public abstract class JMXJSR77AbstractAgent extends JMXAbstractAgent {
	/** Management bean */
	protected Management fManagementBean;

	/**
	 * @param agentId
	 * @param listener
	 */
	public JMXJSR77AbstractAgent(AgentId agentId, Listener listener) {
		super(agentId, listener);
		fRootEntity = new JMXJSR77EntityRoot((JMXAgentExecutionContext)fContext);
		fEnvMap = new HashMap<String, Object>();
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#configCustomChanged()
	 */
	protected void configCustomChanged() throws InvalidConfiguration, Throwable {
		super.configCustomChanged();
		Configuration conf = (Configuration)fConfiguration.getAgentCustomConfiguration();
		fEnvMap.put(Context.SECURITY_PRINCIPAL, conf.getString(Configuration.USERNAME));
		fEnvMap.put(Context.SECURITY_CREDENTIALS, conf.getObject(Configuration.PASSWORD));
		fEnvMap.put(Context.PROVIDER_URL, conf.getString(Configuration.SERVER_URL));

		// allow implementations to complete env map
		completeEnvironmentMap();

        InitialContext context = new InitialContext(new Hashtable(fEnvMap));
        Object objref = context.lookup(conf.getString(Configuration.JNDI_NAME));
        ManagementHome home = (ManagementHome)PortableRemoteObject.narrow(
        		objref, javax.management.j2ee.ManagementHome.class);
        fManagementBean = home.create();
        fJMXConnection = new JMXConnectionMBeanServer(MBeanServerProxy.buildProxy(fManagementBean));
	}

	/**
	 * Subclasses should add information to the environment map in this method.
	 */
	protected abstract void completeEnvironmentMap() throws Throwable;

	/**
	 * @see com.ixora.rms.agents.Agent#deactivate()
	 */
	public synchronized void deactivate() throws Throwable {
		if(fManagementBean != null) {
			fManagementBean.remove();
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getDomainFilter()
	 */
	protected String getDomainFilter() {
		return ".*";
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getKeyFilter(java.lang.String)
	 */
	protected String getKeyFilter(String domain) {
		return "*";
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getEntityName(javax.management.ObjectName)
	 */
	protected String getEntityName(ObjectName on) {
		String name = on.getKeyProperty("name");
		if(name != null) {
			return name;
		}
		return super.getEntityName(on);
	}
}
