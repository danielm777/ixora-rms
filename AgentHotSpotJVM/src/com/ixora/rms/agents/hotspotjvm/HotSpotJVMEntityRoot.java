/*
 * Created on 13-Apr-2005
 */
package com.ixora.rms.agents.hotspotjvm;

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
public class HotSpotJVMEntityRoot extends JMXEntityRoot {
	private static final long serialVersionUID = -3285675331091533116L;

	/**
	 * Constructor.
	 * @param c
	 */
	public HotSpotJVMEntityRoot(JMXAgentExecutionContext c) {
		super(c);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXEntityRoot#buildDomainEntity(com.ixora.rms.EntityId, com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext, java.lang.String)
	 */
	protected Entity buildDomainEntity(EntityId id,
			JMXAgentExecutionContext context, String domain)
			throws IntrospectionException, AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException,
			IOException, Throwable {
		return new HotSpotJVMEntityDomain(id, context, domain);
	}
}
