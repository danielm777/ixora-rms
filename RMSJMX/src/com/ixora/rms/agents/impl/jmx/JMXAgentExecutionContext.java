/*
 * Created on 11-Apr-2005
 */
package com.ixora.rms.agents.impl.jmx;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;

import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;

/**
 * @author Daniel Moraru
 */
public interface JMXAgentExecutionContext extends AgentExecutionContext {
	/**
	 * @return
	 */
	JMXConnection getJMXConnection();
	/**
	 * @return the regex pattern to apply for domains
	 */
	String getDomainFilter();
	/**
	 * @param domain
	 * @return the JMX pattern to apply to the object name keys
	 */
	String getKeyFilter(String domain);

	/**
	 * @param oname
	 * @param ainfo
	 * @return the description for the counter represented by the given attribute
	 */
	String[] getCounterNameAndDescription(ObjectName oname, MBeanAttributeInfo ainfo);

	/**
	 * @param oname
	 * @param attrName
	 * @param ctype
	 * @param item
	 * @return the description for the counter represented by the given composite type item
	 */
	String[] getCounterNameAndDescription(ObjectName oname, String attrName, CompositeType ct, String item);

	/**
	 * @param binfo
	 * @return
	 */
	String getEntityDescription(MBeanInfo binfo);

	/**
	 * Invoked for counters of entities derived from an attribute <code>attrName</code>
	 * of <code>oname</code>
	 * is a composite type.
	 * @param oname
	 * @param attrName
	 * @param sitem the item name in the composite data
	 * @param otype the type of <code>sitem</code>
	 * @return
	 */
	boolean acceptCounter(ObjectName oname, String attrName, String sitem, OpenType<?> otype);

	/**
	 * Invoked for counters of entities derived from the object name <code>oname</code>.
	 * @param oname
	 * @param ainfo
	 * @return true if the given attribute is accepted as a counter
	 */
	boolean acceptCounter(ObjectName oname, MBeanAttributeInfo ainfo);
	/**
	 * @param objectName
	 * @param ai
	 * @return
	 */
	boolean acceptAttributeAsChild(ObjectName objectName, MBeanAttributeInfo ai);
	/**
	 * @param on
	 * @return
	 */
	String getEntityName(ObjectName on);
	/**
	 * @param entityId
	 * @param on
	 * @return true if the entity corresponding to the given object name can safely refresh
	 * its subtree
	 */
	boolean isEntitySafeToRefrehRecursivelly(EntityId entityId, ObjectName on);
	/**
	 * Implementations should process here exceptions thrown when using this generic JMX classes.<br>
	 * This gives the agent a chance to return a meaningfull error to the client.
	 * @param t
	 * @throws the processed exception
	 */
	void processException(Throwable t) throws Throwable;
	/**
	 * @return the maximum depth in the entity tree inspected by recursive operations
	 */
	int getMaximumEntityTreeRecursivityLevel();
	/**
	 * @param oname
	 * @return
	 */
	boolean acceptEntity(ObjectName oname);
}
