/**
 * 26-Jul-2005
 */
package com.ixora.rms.agents.weblogic.v9;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServerFactory;
import javax.naming.Context;

import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.jsr160.JMXJSR160AbstractAgent;
import com.ixora.rms.agents.weblogic.exception.CommunicationError;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class WeblogicAgent extends JMXJSR160AbstractAgent {

	/**
	 * @param agentId
	 * @param listener
	 */
	public WeblogicAgent(AgentId agentId, Listener listener) {
		super(agentId, listener);
		// do not allow refresh as the entity tree is huge
		fSafeToRefreshEntitiesRecursivelly = false;
		fRootEntity = new WeblogicEntityRoot((JMXAgentExecutionContext)fContext);
		fEnvMap.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
		fEnvMap.put(JMXConnectorServerFactory.PROTOCOL_PROVIDER_PACKAGES, "weblogic.management.remote");
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getJMXConnectionURL()
	 */
	protected String getJMXConnectionURL() {
		// set up credentials
		fEnvMap.put(Context.SECURITY_PRINCIPAL, fConfiguration.getAgentCustomConfiguration().getString(Configuration.USERNAME));
		fEnvMap.put(Context.SECURITY_CREDENTIALS, fConfiguration.getAgentCustomConfiguration().getString(Configuration.PASSWORD));
		return fConfiguration.getAgentCustomConfiguration().getString(Configuration.JMX_CONNECTION_STRING);
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
		if(on.toString().equals("com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean")) {
			return "com.bea";
		}
		return on.getKeyProperty("Name");
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#processException(java.lang.Throwable)
	 */
	protected void processException(Throwable t) throws Throwable {
		if(t instanceof RMSException) {
			throw t;
		}
		if(Utils.getTrace(t).toString().contains("COMM_FAILURE")) {
			throw new CommunicationError();
		}
		super.processException(t);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#isEntitySafeToRefreshRecursivelly(com.ixora.rms.EntityId, javax.management.ObjectName)
	 */
	protected boolean isEntitySafeToRefreshRecursivelly(EntityId eid, ObjectName on) {
		if(eid.getPathLength() <= 4) {
			return false;
		}
		String ename = eid.getName();
		if(ename.equals("Targets")
				|| ename.equals("Components")
				|| ename.equals("ActivatedTargets")
				|| ename.equals("ApplicationRuntimes")
				|| ename.equals("EJBComponents")
				|| ename.equals("WebAppComponents")) {
			return false;
		}
		return true;
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getMaximumEntityTreeRecursivityLevel()
	 */
	public int getMaximumEntityTreeRecursivityLevel() {
		return 13;
	}
}
