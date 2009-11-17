/**
 * 10-Sep-2005
 */
package com.ixora.rms.agents.jboss.v4;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.jsr77.JMXJSR77EntitySelfExploring;

/**
 * @author Daniel Moraru
 */
public class JBossEntityDomainJSR77 extends JMXJSR77EntitySelfExploring {
	private static final long serialVersionUID = 1605579567402686626L;

	/**
	 * @param id
	 * @param c
	 * @throws Throwable
	 * @throws IOException
	 * @throws ReflectionException
	 * @throws MBeanException
	 * @throws InstanceNotFoundException
	 * @throws AttributeNotFoundException
	 * @throws IntrospectionException
	 */
	public JBossEntityDomainJSR77(EntityId parent, AgentExecutionContext c) throws IntrospectionException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, Throwable {
		super(parent, (JMXAgentExecutionContext)c, new ObjectName("jboss.management.local:j2eeType=J2EEDomain,name=Manager"));
	}
}
