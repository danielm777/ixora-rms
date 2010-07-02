/*
 * Created on 11-Apr-2005
 */
package com.ixora.rms.agents.impl.jmx;

import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;

import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgent;
import com.ixora.rms.agents.impl.jmx.exception.JMXCommunicationError;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public abstract class JMXAbstractAgent extends AbstractAgent {
	/** MBean server connection */
	protected JMXConnection fJMXConnection;
	/** Naming environment */
	protected Map<String, Object> fEnvMap;

	/**
	 * Execution context.
	 */
	private final class JMXExecutionContext extends ExecutionContext
		implements JMXAgentExecutionContext {
		/**
		 * @see com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext#getJMXConnection()
		 */
		public JMXConnection getJMXConnection() {
			return fJMXConnection;
		}
		/**
		 * @see com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext#getDomainFilter()
		 */
		public boolean acceptDomain(String domain) {
			return JMXAbstractAgent.this.acceptDomain(domain);
		}
		/**
		 * @see com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext#getKeyFilter(java.lang.String)
		 */
		public String getKeyFilter(String domain) {
			return JMXAbstractAgent.this.getKeyFilter(domain);
		}
		/**
		 * @see com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext#acceptCounter(javax.management.ObjectName, javax.management.MBeanAttributeInfo)
		 */
		public boolean acceptCounter(ObjectName oname, MBeanAttributeInfo ainfo) {
			return JMXAbstractAgent.this.acceptCounter(oname, ainfo);
		}
		/**
		 * @see com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext#getCounterNameAndDescription(javax.management.ObjectName, javax.management.MBeanAttributeInfo)
		 */
		public String[] getCounterNameAndDescription(ObjectName oname, MBeanAttributeInfo ainfo) {
			return JMXAbstractAgent.this.getCounterNameAndDescription(oname, ainfo);
		}
		/**
		 * @see com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext#getEntityDescription(javax.management.MBeanInfo)
		 */
		public String getEntityDescription(MBeanInfo binfo) {
			return JMXAbstractAgent.this.getEntityDescription(binfo);
		}
		/**
		 * @see com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext#getCounterDescription(javax.management.ObjectName, javax.management.openmbean.CompositeType, java.lang.String)
		 */
		public String[] getCounterNameAndDescription(ObjectName oname, String attrName, CompositeType ct, String item) {
			return JMXAbstractAgent.this.getCounterNameAndDescription(oname, attrName, ct, item);
		}
		/**
		 * @see com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext#acceptCounter(javax.management.ObjectName, java.lang.String, java.lang.String, javax.management.openmbean.OpenType)
		 */
		public boolean acceptCounter(ObjectName oname, String attrName, String sitem, OpenType<?> otype) {
			return JMXAbstractAgent.this.acceptCounter(oname, attrName, sitem, otype);
		}
		/**
		 * @see com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext#acceptAttributeAsChild(javax.management.ObjectName, javax.management.MBeanAttributeInfo)
		 */
		public boolean acceptAttributeAsChild(ObjectName objectName, MBeanAttributeInfo ai) {
			return JMXAbstractAgent.this.acceptAttributeAsChild(objectName, ai);
		}
		/**
		 * @see com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext#getEntityName(javax.management.ObjectName)
		 */
		public String getEntityName(ObjectName on) {
			return JMXAbstractAgent.this.getEntityName(on);
		}
		/**
		 * @see com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext#processException(java.lang.Throwable)
		 */
		public void processException(Throwable t) throws Throwable {
			JMXAbstractAgent.this.processException(t);
		}
		/**
		 * @see com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext#isEntitySafeToRefrehRecursivelly(EntityId, javax.management.ObjectName)
		 */
		public boolean isEntitySafeToRefrehRecursivelly(EntityId eid, ObjectName on) {
			return JMXAbstractAgent.this.isEntitySafeToRefreshRecursivelly(eid, on);
		}
		/**
		 * @see com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext#getMaximumEntityTreeRecursivityLevel()
		 */
		public int getMaximumEntityTreeRecursivityLevel() {
			return JMXAbstractAgent.this.getMaximumEntityTreeRecursivityLevel();
		}
		/**
		 * @see com.ixora.rms.agents.weblogic.v8.duplicates.JMXAgentExecutionContext#acceptEntity(javax.management.ObjectName)
		 */
		public boolean acceptEntity(ObjectName oname) {
			return JMXAbstractAgent.this.acceptEntity(oname);
		}
	}

	/**
	 * Constructor.
	 */
	public JMXAbstractAgent(AgentId agentId, Listener listener) {
		super(agentId, listener);
		JMXExecutionContext jmxEC = new JMXExecutionContext();
		fContext = jmxEC;
		fRootEntity = new JMXEntityRoot(jmxEC);
		fEnvMap = new HashMap<String, Object>();
	}

	/**
	 * Subclasses should filter here the domain names
	 * @return
	 */
	protected boolean acceptDomain(String domain) {
		return true;
	}

	/**
	 * Subclasses must return here the JMX pattern used to match object names
	 * @param domain
	 * @return
	 */
	protected abstract String getKeyFilter(String domain);

	/**
	 * Subclasses could veto for this attribute to become a counter.
	 * @param oname
	 * @param ainfo
	 * @return
	 */
	protected boolean acceptCounter(ObjectName oname, MBeanAttributeInfo ainfo) {
		return true;
	}

	/**
	 * Subclasses could veto for this item of a composite attribute to become a counter.
	 * @param oname
	 * @param attrName
	 * @param sitem
	 * @param otype
	 * @return
	 */
	protected boolean acceptCounter(ObjectName oname, String attrName, String sitem, OpenType<?> otype) {
		return true;
	}

	/**
	 * Subclasses could provide here a different description for the given counter.
	 * @param oname
	 * @param ainfo
	 * @return
	 */
	protected String[] getCounterNameAndDescription(ObjectName oname, MBeanAttributeInfo ainfo) {
		return new String[] {ainfo.getName(), ainfo.getDescription()};
	}

	/**
	 * Subclasses could provide here a different description for the given counter.
	 * @param oname
	 * @param attrName
	 * @param ct
	 * @param item
	 * @return
	 */
	protected String[] getCounterNameAndDescription(ObjectName oname, String attrName, CompositeType ct, String item) {
		return new String[] {item, ct.getDescription(item)};
	}

	/**
	 * @param binfo
	 * @return
	 */
	protected String getEntityDescription(MBeanInfo binfo) {
		return binfo.getDescription();
	}

	/**
	 * Subclasses could veto if this attribute is allowed as a child of the entity with the given oname.
	 * @param objectName
	 * @param ai
	 * @return
	 */
	protected boolean acceptAttributeAsChild(ObjectName objectName, MBeanAttributeInfo ai) {
		return true;
	}

	/**
	 * Subclasses could provide a way to produce entity name from an oname. By default it returns
	 * <code>on.toString()</code>.
	 * @param on
	 * @return
	 */
	protected String getEntityName(ObjectName on) {
		return on.toString();
	}

	/**
	 * @param eid
	 * @return
	 */
	protected boolean isEntitySafeToRefreshRecursivelly(EntityId eid, ObjectName on) {
		return true;
	}

	/**
	 * @return 0 by default
	 */
	protected int getMaximumEntityTreeRecursivityLevel() {
		return 0; // no limit
	}

	/**
	 * Implementations should process here exceptions thrown when using this generic JMX classes.<br>
	 * This gives the agent a chance to return a meaningfull error to the client.
	 * @param t
	 * @throws
	 */
	protected void processException(Throwable t) throws Throwable {
		if(t instanceof RMSException) {
			throw t;
		}
		if(Utils.getTrace(t).indexOf("Connection refused to host") >= 0) {
			throw new JMXCommunicationError(t);
		}
		throw t;
	}

	/**
	 * @param oname
	 * @return
	 */
	protected boolean acceptEntity(ObjectName oname) {
		return true;
	}
}
