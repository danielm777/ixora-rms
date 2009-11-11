/**
 * 26-Jul-2005
 */
package com.ixora.rms.agents.sunapp.v8;

import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.naming.Context;

import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.jsr160.JMXJSR160AbstractAgent;
import com.ixora.rms.exception.InvalidConfiguration;

/**
 * @author Daniel Moraru
 */
public class SunAppAgent extends JMXJSR160AbstractAgent {

	/**
	 * @param agentId
	 * @param listener
	 */
	public SunAppAgent(AgentId agentId, Listener listener) {
		super(agentId, listener);
		fRootEntity = new SunAppEntityRoot((JMXAgentExecutionContext)fContext);
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#configCustomChanged()
	 */
	protected void configCustomChanged() throws InvalidConfiguration, Throwable {
		// set up credentials
		String user = fConfiguration.getAgentCustomConfiguration().getString(Configuration.USERNAME);
		String pass = fConfiguration.getAgentCustomConfiguration().getString(Configuration.PASSWORD);
		fEnvMap.put(Context.SECURITY_PRINCIPAL, user);
		fEnvMap.put(Context.SECURITY_CREDENTIALS, pass);
    	fEnvMap.put(Context.INITIAL_CONTEXT_FACTORY,  "com.sun.jndi.rmi.registry.RegistryContextFactory");
        String credentials[] = {user, pass};
    	fEnvMap.put(JMXConnector.CREDENTIALS, credentials);
		super.configCustomChanged();
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getJMXConnectionURL()
	 */
	protected String getJMXConnectionURL() {
		return fConfiguration.getAgentCustomConfiguration().getString(Configuration.JMX_CONNECTION_STRING);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getDomainFilter()
	 */
	protected String getDomainFilter() {
		return ".*";
	}

	/**
	 * Ignores counters Type and Name
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#acceptCounter(javax.management.ObjectName, javax.management.MBeanAttributeInfo)
	 */
	protected boolean acceptCounter(ObjectName oname, MBeanAttributeInfo ainfo) {
		return true;
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#acceptEntity(javax.management.ObjectName)
	 */
	protected boolean acceptEntity(ObjectName oname) {
		if(fConfiguration
				.getAgentCustomConfiguration().getBoolean(Configuration.SHOW_JUST_RUNTIME_DATA)) {
			String domain = oname.getDomain();
			if("amx".equals(domain)) {
				String type = oname.getKeyProperty("j2eeType");
				if(!Utils.isEmptyString(type)) {
					if(type.endsWith("Monitor")) {
						return true;
					} else {
						return false;
					}
				}
			}
		}
		return true;

	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#processException(java.lang.Throwable)
	 */
	protected void processException(Throwable t) throws Throwable {
		if(Utils.getTrace(t).indexOf("javax.management.MalformedObjectNameException") >= 0) {
			// ignore
			return;
		}
		super.processException(t);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getEntityName(javax.management.ObjectName)
	 */
	protected String getEntityName(ObjectName on) {
		Hashtable<String, String> props = on.getKeyPropertyList();
		if(!Utils.isEmptyMap(props)) {
			StringBuffer buff = new StringBuffer();
			buff.append(on.getDomain()).append(":");
			// reorganize string, place j2eeType first then name and the rest sorted
			TreeMap<String, String> sorted = new TreeMap<String, String>(props);
			String j2eeType = props.get("j2eeType");
			if(j2eeType != null) {
				buff.append("j2eeType=").append(j2eeType).append(",");
				sorted.remove("j2eeType");
			}
			String type = props.get("type");
			if(type != null) {
				buff.append("type=").append(type).append(",");
				sorted.remove("type");
			}
			String name = props.get("name");
			if(name != null) {
				buff.append("name=").append(name).append(",");
				sorted.remove("name");
			}
			for(Map.Entry<String, String> e : sorted.entrySet()) {
				buff.append(e.getKey()).append("=").append(e.getValue()).append(",");
			}
			buff.delete(buff.length() - 1, buff.length());
			return buff.toString();
		}
		return on.toString();
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#sortEntities()
	 */
	protected boolean sortEntities() {
		return true;
	}
}
