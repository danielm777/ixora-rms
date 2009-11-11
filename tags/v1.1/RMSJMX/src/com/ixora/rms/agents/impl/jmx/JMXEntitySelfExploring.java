/**
 * 27-Apr-2005
 */
package com.ixora.rms.agents.impl.jmx;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.j2ee.statistics.Stats;
import javax.management.openmbean.CompositeData;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Entity;

/**
 * @author Daniel Moraru
 */
public class JMXEntitySelfExploring extends JMXEntity {
	private static final long serialVersionUID = 4827580022162413818L;
	/** Attributes of complex types that might be children of this entity */
	private List<MBeanAttributeInfo> fComplexAttributes;

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
	public JMXEntitySelfExploring(EntityId parent, JMXAgentExecutionContext context, ObjectName oname) throws IntrospectionException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, Throwable {
		super(parent, context, oname, null);
	}

	/**
	 * @param id
	 * @param context
	 * @param attr
	 * @param names
	 * @throws Throwable
	 * @throws IOException
	 * @throws ReflectionException
	 * @throws MBeanException
	 * @throws InstanceNotFoundException
	 * @throws AttributeNotFoundException
	 * @throws IntrospectionException
	 */
	protected JMXEntitySelfExploring(EntityId parent, JMXAgentExecutionContext context,
			MBeanAttributeInfo attr, ObjectName[] names) throws IntrospectionException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, Throwable {
		super(parent, context, attr.getName(), attr.getDescription());
		// create children from the given object names
		for(ObjectName on : names) {
			addChildEntity(new JMXEntitySelfExploring(getId(), getJMXContext(), on));
		}
		// children are passed in from parent
		fCanRefreshChildren = false;
	}

	/**
	 * @param id
	 * @param c
	 * @param oname
	 */
	protected JMXEntitySelfExploring(EntityId id, AgentExecutionContext c, ObjectName oname) {
		super(id, c, oname);
	}

	/**
	 * @param parent
	 * @param c
	 * @param name
	 * @param desc
	 */
	protected JMXEntitySelfExploring(EntityId parent, AgentExecutionContext c, String name, String desc) {
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
	protected JMXEntitySelfExploring(EntityId parent, JMXAgentExecutionContext c, ObjectName oname, String name) throws IntrospectionException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, Throwable {
		super(parent, c, oname, name);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXEntity#processComplexAttribute(javax.management.MBeanAttributeInfo)
	 */
	protected EntityId processComplexAttribute(MBeanAttributeInfo ai, boolean forUpdate) {
		if(!getJMXContext().acceptAttributeAsChild(fObjectName, ai)) {
			return null;
		}
		if(fComplexAttributes == null) {
			fComplexAttributes = new LinkedList<MBeanAttributeInfo>();
		}
		fComplexAttributes.add(ai);
		fHasChildren = true;
		return null;
	}


	/**
	 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		try {
			// check if it;s safe to invoke this recursivelly
			if(!this.fSafeToRefreshRecursivelly) {
				recursive = false;
			}
			// check if we reached the max level
			if(reachedMaximumRecursivityLevel()) {
				return;
			}

			// keep track of stale entities
			resetTouchedByUpdateForChildren();

			// retrieve complex attributes again
			retrieveComplexAttributes();

			// transform complex attributes in children entities
			if(!Utils.isEmptyCollection(fComplexAttributes)) {
				for(MBeanAttributeInfo child : fComplexAttributes) {
					Object obj = getJMXContext().getJMXConnection().getAttribute(fObjectName, child.getName());
					if(obj instanceof ObjectName[]) {
						addOrUpdateChildEntity(child, (ObjectName[])obj);
					} else if(obj instanceof ObjectName){
						addOrUpdateChildEntity((ObjectName)obj);
					} else if(obj instanceof String[]){
						// some beans have their object names as strings...
						String[] sns = (String[])obj;
						// try converting them to ObjectNames
						try {
							ObjectName[] ons = new ObjectName[sns.length];
							for(int i = 0; i < sns.length; i++) {
								ons[i] = new ObjectName(sns[i]);
							}
							addOrUpdateChildEntity(child, ons);
						} catch(Exception e) {
							; // ignore, probably not an object name
						}
					}
				}
			}
			// update existing children
			for(Entity child : fChildrenEntities.values()) {
				((JMXEntity)child).update();
			}

			// now remove stale children
			removeStaleChildren();

			// sort children if required
			if(getJMXContext().sortEntities()) {
				sortChildren();
			}

			// recurse if required
			if(recursive) {
				for(Entity child : fChildrenEntities.values()) {
					child.updateChildrenEntities(true);
				}
			}
		} catch(Throwable t) {
			getJMXContext().processException(t);
		}
	}

	/**
	 * Retrieves the complex attributes for this ObjectName.
	 * @throws Exception
	 */
	private void retrieveComplexAttributes() throws Exception {
		if(fComplexAttributes != null) {
			fComplexAttributes.clear();
		}
		if(fObjectName != null) {
			try {
				MBeanInfo binfo = getJMXContext().getJMXConnection().getMBeanInfo(fObjectName);
				MBeanAttributeInfo[] attrInfos = binfo.getAttributes();
				if(!Utils.isEmptyArray(attrInfos)) {
					for(MBeanAttributeInfo ai : attrInfos) {
						String type = ai.getType();
						CounterType ctype = convertType(type);
						if(ctype == null) { // not a counter
							if(!type.equals(CompositeData.class.getName())
									&& !type.equals(Stats.class.getName())) {
								// complex type
								processComplexAttribute(ai, true);
							}
						}
					}
				}
			} catch (InstanceNotFoundException e) {
				; // ignore
			}
		}
	}

	/**
	 * Adds a new entity to the hierarchy.
	 * @param parent
	 * @param ai
	 * @param children
	 * @return the id of the entity derived from the given parameters
	 * @throws Throwable
	 */
	protected EntityId addOrUpdateChildEntity(MBeanAttributeInfo ai, ObjectName[] children) throws Throwable {
		EntityId childId = new EntityId(getId(), ai.getName());
		Entity child = getChildEntity(childId);
		if(child == null) {
			child = new JMXEntitySelfExploring(getId(), getJMXContext(), ai, children);
			addChildEntity(child);
		} else {
			// update the children of this child
			((JMXEntitySelfExploring)child).updateChildren(children);
		}
		return child.getId();
	}

	/**
	 * This method is inoked on children who have their children updated
	 * by their parent.
	 * @param children
	 * @throws Throwable
	 */
	private void updateChildren(ObjectName[] children) throws Throwable {
		setTouchedByUpdate(true);
		// keep track of entities that need to be removed
		resetTouchedByUpdateForChildren();
		// create children from the given object names
		for(ObjectName on : children) {
			addOrUpdateChildEntity(on);
		}
		// now remove all existing entities which are not part of
		// the new set
		removeStaleChildren();
	}

	/**
	 * Adds a new entity to the hierarchy. The default implementation adds a new child entity
	 * for this entity if necessary. If subclasses need to create more complex hierarchies they
	 * can override this method.
	 * @param ai
	 * @param child
	 * @return the id of the entity derived from the given parameters
	 * @throws Throwable
	 */
	protected EntityId addOrUpdateChildEntity(ObjectName child) throws Throwable {
		EntityId childId = new EntityId(getId(), getJMXContext().getEntityName(child));
		Entity childEnt = getChildEntity(childId);
		if(childEnt == null) {
			JMXEntitySelfExploring ent = new JMXEntitySelfExploring(getId(), getJMXContext(), child);
			addChildEntity(ent);
		} else {
			((JMXEntitySelfExploring)childEnt).update();
		}
		return childId;
	}
}

