/*
 * Created on 12-Apr-2005
 */
package com.ixora.rms.agents.hotspotjvm;

import java.io.IOException;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.JMXEntity;
import com.ixora.rms.agents.impl.jmx.JMXEntityDomain;

/**
 * @author Daniel Moraru
 */
public class HotSpotJVMEntityDomain extends JMXEntityDomain {
	private static final long serialVersionUID = -2365302925156213521L;

	/**
	 * Constructor.
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
	public HotSpotJVMEntityDomain(EntityId parent, JMXAgentExecutionContext c, String domain) throws IntrospectionException,
			AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException, Throwable {
		super(parent, c, domain);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXEntityDomain#buildEntityHierarchy(null)
	 */
	protected void updateEntityHierarchy(Set<ObjectName> names)
			throws IntrospectionException, AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException,
			IOException, Throwable {
		for(ObjectName oname : names) {
			String type = oname.getKeyProperty("type");
			String name = oname.getKeyProperty("name");

			// try to find parent
			EntityId eid = new EntityId(getId(), type);
			Entity e = findEntity(eid, false);
			if(e == null) {
				// add type parent entity
				e = new JMXEntity(getId(), getJMXContext(),
						new ObjectName("java.lang:type=" + type), type);
				addChildEntity(e);
			}
			if(name != null) {
				EntityId childId = JMXEntity.createEntityId(getJMXContext(), e.getId(), oname, name);
				if(e.getChildEntity(childId) == null) {
					((JMXEntity)e).addChildEntity(
						new JMXEntity(e.getId(), getJMXContext(), oname, name));
				} else {
					; // no need to update JMX entities don't change
				}
			}
		}
	}
}
