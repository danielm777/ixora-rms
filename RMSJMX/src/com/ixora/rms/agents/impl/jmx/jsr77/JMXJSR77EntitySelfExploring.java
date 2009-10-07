/**
 * 10-Sep-2005
 */
package com.ixora.rms.agents.impl.jmx.jsr77;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.JMXEntity;
import com.ixora.rms.agents.impl.jmx.JMXEntitySelfExploring;

/**
 * @author Daniel Moraru
 */
public class JMXJSR77EntitySelfExploring extends JMXEntitySelfExploring {

	/**
	 * @param parent
	 * @param context
	 * @param oname
	 * @throws IntrospectionException
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 * @throws Throwable
	 */
	public JMXJSR77EntitySelfExploring(EntityId parent,
			JMXAgentExecutionContext context, ObjectName oname)
			throws IntrospectionException, AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException,
			IOException, Throwable {
		super(parent, context, oname);
	}

	/**
	 * @param parent
	 * @param context
	 * @param attr
	 * @param names
	 * @throws IntrospectionException
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 * @throws Throwable
	 */
	public JMXJSR77EntitySelfExploring(EntityId parent, JMXAgentExecutionContext context, MBeanAttributeInfo attr, ObjectName[] names) throws IntrospectionException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, Throwable {
		super(parent, context, attr, names);
	}

	/**
	 * @param parent
	 * @param c
	 * @param name
	 * @param desc
	 */
	public JMXJSR77EntitySelfExploring(EntityId parent, AgentExecutionContext c, String name, String desc) {
		super(parent, c, name, desc);
	}

	/**
	 * @param parent
	 * @param c
	 * @param oname
	 * @param name
	 * @throws IntrospectionException
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 * @throws Throwable
	 */
	public JMXJSR77EntitySelfExploring(EntityId parent, JMXAgentExecutionContext c, ObjectName oname, String name) throws IntrospectionException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, Throwable {
		super(parent, c, oname, name);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXEntitySelfExploring#addOrUpdateChildEntity(javax.management.MBeanAttributeInfo, javax.management.ObjectName[])
	 */
	protected EntityId addOrUpdateChildEntity(MBeanAttributeInfo ai, ObjectName[] children) throws Throwable {
		JMXJSR77EntitySelfExploring ret = new JMXJSR77EntitySelfExploring(getId(), getJMXContext(), ai, children);
		insertEntityIntoHierarchy(ret);
		return ret.getId();
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXEntitySelfExploring#addOrUpdateChildEntity(javax.management.ObjectName)
	 */
	protected EntityId addOrUpdateChildEntity(ObjectName child) throws Throwable {
		JMXJSR77EntitySelfExploring ret = new JMXJSR77EntitySelfExploring(getId(), getJMXContext(), child);
		insertEntityIntoHierarchy(ret);
		return ret.getId();
	}

	/**
	 * @param entity
	 * @throws Throwable
	 */
	private void insertEntityIntoHierarchy(JMXJSR77EntitySelfExploring entity) throws Throwable {
		String name = entity.getName();
		ObjectName on = entity.getObjectName();
		if(on == null) {
			// placeholder
			addChildEntity(entity);
			return;
		}
		// get the j2eeType and it should be a parent to the <code>entity</code>
		String j2eeType = on.getKeyProperty("j2eeType");
		if(j2eeType == null) {
			addChildEntity(entity);
			return;
		}
		JMXEntity parent = (JMXEntity)findEntity(new EntityId(getId(), j2eeType),  false);
		if(parent == null) {
			// add it now
			parent = new JMXJSR77EntitySelfExploring(getId(), getJMXContext(), j2eeType, j2eeType);
			addChildEntity(parent);
		}
		// adjust the entity id as the hierarchy might have been modified
		entity.fEntityId = new EntityId(parent.getId(), entity.fName);
		parent.addChildEntity(entity);
	}
}
