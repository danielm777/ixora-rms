/**
 * 07-Sep-2005
 */
package com.ixora.rms.agents.impl.jmx.jsr77;

import java.io.IOException;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.JMXEntityDomain;

/**
 * @author Daniel Moraru
 */
public class JMXJSR77EntityDomain extends JMXEntityDomain {
	private static final long serialVersionUID = 9066831009196750013L;

	/**
	 * @param parent
	 * @param c
	 * @param domain
	 * @throws IntrospectionException
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 * @throws Throwable
	 */
	public JMXJSR77EntityDomain(EntityId parent, JMXAgentExecutionContext c, String domain) throws IntrospectionException,
			AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException, Throwable {
		super(parent, c, domain);
	}

	/**
	 * @param parent
	 * @param c
	 * @param domain
	 * @param domainServiceBeanObjectName
	 * @throws IntrospectionException
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 * @throws Throwable
	 */
	public JMXJSR77EntityDomain(EntityId parent, JMXAgentExecutionContext c, String domain,
			ObjectName domainServiceBeanObjectName)
			throws IntrospectionException, AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException,
			IOException, Throwable {
		super(parent, c, domain, domainServiceBeanObjectName);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXEntityDomain#updateEntityHierarchy(java.util.Set)
	 */
	protected void updateEntityHierarchy(Set<ObjectName> names) throws IntrospectionException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, Throwable {
		// build the J2EE entity hierarchy according to spec
		// TODO temporary
		super.updateEntityHierarchy(names);
	}
}
