/**
 * 18-Jan-2006
 */
package com.ixora.rms.agents.impl.jmx;

import java.util.Set;

import javax.management.AttributeList;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.QueryExp;

/**
 * JMXConnection that delegates to an MBeanServer.
 * In older versions of JMX MBeanServer is not a subclass of MBeanServerConnection.
 * @author Daniel Moraru
 */
public class JMXConnectionMBeanServer implements JMXConnection {
	private MBeanServer fServer;

	/**
	 * @param server
	 */
	public JMXConnectionMBeanServer(MBeanServer server) {
		fServer = server;
	}
	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXConnection#getDefaultDomain()
	 */
	public String getDefaultDomain() throws Exception {
		return fServer.getDefaultDomain();
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXConnection#getDomains()
	 */
	public String[] getDomains() throws Exception {
		return fServer.getDomains();
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXConnection#getAttributes(javax.management.ObjectName, java.lang.String[])
	 */
	public AttributeList getAttributes(ObjectName name, String[] attributes)
			throws Exception {
		return fServer.getAttributes(name, attributes);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXConnection#getAttribute(javax.management.ObjectName, java.lang.String)
	 */
	public Object getAttribute(ObjectName name, String attribute)
			throws Exception {
		return fServer.getAttribute(name, attribute);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXConnection#queryNames(javax.management.ObjectName, javax.management.QueryExp)
	 */
	public Set queryNames(ObjectName name, QueryExp query) throws Exception {
		return fServer.queryNames(name, query);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXConnection#queryMBeans(javax.management.ObjectName, javax.management.QueryExp)
	 */
	public Set queryMBeans(ObjectName name, QueryExp query) throws Exception {
		return fServer.queryMBeans(name, query);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXConnection#getMBeanInfo(javax.management.ObjectName)
	 */
	public MBeanInfo getMBeanInfo(ObjectName objectName) throws Exception {
		return fServer.getMBeanInfo(objectName);
	}
}
