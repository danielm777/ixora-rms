/*
 * Created on 12-Apr-2005
 */
package com.ixora.rms.agents.weblogic.v9;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.JMXEntitySelfExploring;

/**
 * @author Daniel Moraru
 */
public class WeblogicEntityDomain extends JMXEntitySelfExploring {
	private static final long serialVersionUID = 5019601375439580427L;

	/**
	 * Constructor.
	 * @param parent
	 * @param c
	 * @throws IntrospectionException
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 * @throws Throwable
	 */
	public WeblogicEntityDomain(EntityId parent, JMXAgentExecutionContext c) throws IntrospectionException,
			AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException, Throwable {
		super(parent, c, new ObjectName(
				"com.bea:Name=DomainRuntimeService," +
				"Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean"));
	}

	/**
	 * Overriden to filter out some mbeans
	 * @see com.ixora.rms.agents.impl.jmx.JMXEntity#processComplexAttribute(javax.management.MBeanAttributeInfo)
	 */
	protected EntityId processComplexAttribute(MBeanAttributeInfo ai, boolean forUpdate) {
		if(ai.getName().equals("DomainRuntime")) {
			return null;
		}
		if(ai.getName().equals("DomainConfiguration")) {
			return null;
		}
		if(ai.getName().equals("DomainPending")) {
			return null;
		}
		return super.processComplexAttribute(ai, forUpdate);
	}
}
