/*
 * Created on 13-Apr-2005
 */
package com.ixora.rms.agents.weblogic.v9;

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
public class WeblogicEntityRoot extends JMXEntityRoot {
	private static final long serialVersionUID = -1602925619254960850L;

	/**
	 * Constructor.
	 * @param c
	 */
	public WeblogicEntityRoot(JMXAgentExecutionContext c) {
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
		if("com.bea".equals(domain)) {
			return new WeblogicEntityDomain(id, context);
		}
		return null;
	}

}
