/**
 * 26-Jul-2005
 */
package com.ixora.rms.agents.weblogic.v8;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;

import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.weblogic.exception.CommunicationError;
import com.ixora.rms.agents.impl.jmx.JMXAbstractAgent;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.JMXConnectionMBeanServer;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class WeblogicAgent extends JMXAbstractAgent {

	/**
	 * @param agentId
	 * @param listener
	 * @throws Throwable
	 */
	public WeblogicAgent(AgentId agentId, Listener listener) throws Throwable {
		super(agentId, listener);
		fRootEntity = new WeblogicEntityRoot((JMXAgentExecutionContext)fContext);
		fEnvMap = new HashMap<String, Object>();
		fEnvMap.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
	}

	/**
	 * @throws Throwable
	 * @see com.ixora.rms.agents.impl.AbstractAgent#configCustomChanged()
	 */
	protected void configCustomChanged() throws Throwable {
		// set up credentials
		fEnvMap.put(Context.SECURITY_PRINCIPAL, fConfiguration.getAgentCustomConfiguration().getString(Configuration.USERNAME));
		fEnvMap.put(Context.SECURITY_CREDENTIALS, fConfiguration.getAgentCustomConfiguration().getString(Configuration.PASSWORD));
		fEnvMap.put(Context.PROVIDER_URL, fConfiguration.getAgentCustomConfiguration().getString(
				Configuration.JMX_PROVIDER_URL));
		try {
			InitialContext ctxt = new InitialContext(new Hashtable(fEnvMap));
			// weblogic.management.home.localhome or weblogic.management.adminhome
			Object home = ctxt.lookup(fConfiguration.getAgentCustomConfiguration().getString(
					Configuration.JNDI_NAME));
	        Class homeClass = home.getClass();
	        Method method = homeClass.getMethod("getMBeanServer", new Class[0]);
	        Object mbeanServerObject = method.invoke(home, new Object[0]);
			MBeanServer mb = (MBeanServer)mbeanServerObject;
			fJMXConnection = new JMXConnectionMBeanServer(mb);
		} catch(Exception e) {
			processException(e);
		}
	}

	/**
	 * Ignores counters Type and Name
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#acceptCounter(javax.management.ObjectName, javax.management.MBeanAttributeInfo)
	 */
	protected boolean acceptCounter(ObjectName oname, MBeanAttributeInfo ainfo) {
		if("Type".equals(ainfo.getName())) {
			return false;
		}
		if("Name".equals(ainfo.getName())) {
			return false;
		}
		return true;
	}


	/**
	 * Overriden to simplify description text.
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getEntityDescription(javax.management.MBeanInfo)
	 */
	protected String getEntityDescription(MBeanInfo binfo) {
		String desc = binfo.getDescription();
		int idx = desc.indexOf("<p");
		int idx2 = desc.indexOf("<h");
		idx = Math.min(idx, idx2);
		if(idx < 0) {
			return desc;
		} else {
			return desc.substring(0, idx);
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#acceptAttributeAsChild(javax.management.ObjectName, javax.management.MBeanAttributeInfo)
	 */
	protected boolean acceptAttributeAsChild(ObjectName objectName, MBeanAttributeInfo ai) {
		if("Parent".equals(ai.getName())) {
			return false;
		}
		return true;
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getEntityName(javax.management.ObjectName)
	 */
	protected String getEntityName(ObjectName on) {
		Hashtable<String, String> props = on.getKeyPropertyList();
		if(!Utils.isEmptyMap(props)) {
			StringBuffer buff = new StringBuffer();
			buff.append(on.getDomain()).append(":");
			// reorganize string, place Type first then Name and the rest sorted
			TreeMap<String, String> sorted = new TreeMap<String, String>(props);
			String type = props.get("Type");
			if(type != null) {
				buff.append("Type=").append(type).append(",");
				sorted.remove("Type");
			}
			String name = props.get("Name");
			if(name != null) {
				buff.append("Name=").append(name).append(",");
				sorted.remove("Name");
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
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#processException(java.lang.Throwable)
	 */
	protected void processException(Throwable t) throws Throwable {
		if(t instanceof RMSException) {
			throw t;
		}
		String trace = Utils.getTrace(t).toString();
		if(trace.contains("COMM_FAILURE")
				|| trace.contains("Connection refused")) {
			throw new CommunicationError();
		}
		super.processException(t);
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
	 * @see com.ixora.rms.agents.weblogic.v8.duplicates.JMXAbstractAgent#sortEntities()
	 */
	protected boolean sortEntities() {
		return true;
	}

	/**
	 * @see com.ixora.rms.agents.weblogic.v8.duplicates.JMXAbstractAgent#acceptEntity(javax.management.ObjectName)
	 */
	protected boolean acceptEntity(ObjectName oname) {
		if(fConfiguration
				.getAgentCustomConfiguration().getBoolean(Configuration.SHOW_JUST_RUNTIME_DATA)) {
			String type = oname.getKeyProperty("Type");
			if(!Utils.isEmptyString(type)) {
				if(type.endsWith("Runtime")) {
					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}
}
