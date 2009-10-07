/**
 * 10-Sep-2005
 */
package com.ixora.rms.agents.jboss.v4;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.JMXEntityRoot;

/**
 * @author Daniel Moraru
 */
public class JBossEntityRoot extends JMXEntityRoot {

	/**
	 * @param c
	 */
	public JBossEntityRoot(JMXAgentExecutionContext c) {
		super(c);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXEntityRoot#buildDomainEntity(com.ixora.rms.EntityId, com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext, java.lang.String)
	 */
	protected Entity buildDomainEntity(EntityId parent, JMXAgentExecutionContext context, String domain) throws IntrospectionException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, Throwable {
		if(domain.equals("jboss.management.local")) {
			return new JBossEntityDomainJSR77(parent, context);
		}
		return super.buildDomainEntity(parent, context, domain);
	}


}
