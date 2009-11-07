/*
 * Created on 11-Apr-2005
 */
package com.ixora.rms.agents.impl.jmx;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.management.Attribute;
import javax.management.AttributeList;
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
import javax.management.openmbean.TabularData;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;

/**
 * @author Daniel Moraru
 */
public class JMXEntity extends Entity {
	private static final long serialVersionUID = 2053296470688671832L;
	/** Bean attributes that are counters for this entity */
	protected String[] fAttributesForCounters;
	/** Object name */
	protected ObjectName fObjectName;
	/** True if this entity has no JMX meaning */
	protected boolean fJustAPlaceHolder;

	/**
	 * Constructor.
	 * @param id
	 * @param c
	 */
	public JMXEntity(EntityId id, AgentExecutionContext c, ObjectName oname) {
		super(id, c);
		fObjectName = oname;
		fSafeToRefreshRecursivelly = getJMXContext().isEntitySafeToRefrehRecursivelly(fEntityId, oname);
	}

	/**
	 * Constructor.
	 * @param c
	 * @param parent
	 * @param name
	 * @param desc
	 */
	public JMXEntity(EntityId parent, AgentExecutionContext c, String name, String desc) {
		super(new EntityId(parent, name), desc, c);
		fJustAPlaceHolder = true;
		fSafeToRefreshRecursivelly = getJMXContext().isEntitySafeToRefrehRecursivelly(fEntityId, null);
	}

	/**
	 * Constructor.
	 * @param id
	 * @param c
	 * @param oname
	 * @param name
	 * @throws IntrospectionException
	 * @throws InstanceNotFoundException
	 * @throws ReflectionException
	 * @throws IOException
	 * @throws Throwable
	 * @throws MBeanException
	 * @throws AttributeNotFoundException
	 */
	public JMXEntity(EntityId parent, JMXAgentExecutionContext c, ObjectName oname, String name) throws IntrospectionException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, Throwable {
		super(createEntityId(c, parent, oname, name), c);
		fObjectName = oname;
		fSafeToRefreshRecursivelly = getJMXContext().isEntitySafeToRefrehRecursivelly(fEntityId, oname);
		try {
			MBeanInfo binfo = getJMXContext().getJMXConnection().getMBeanInfo(oname);
			fDescription = getJMXContext().getEntityDescription(binfo);
			MBeanAttributeInfo[] attrInfos = binfo.getAttributes();
			if(!Utils.isEmptyArray(attrInfos)) {
				List<String> attrNamesForCounters = new LinkedList<String>();
				for(MBeanAttributeInfo ai : attrInfos) {
					if(!getJMXContext().acceptCounter(oname, ai)) {
						continue;
					}
					String type = ai.getType();
					if(type == null) {
						type = String.class.getName();
					}
					CounterType ctype = convertType(type);
					if(ctype != null) {
						boolean discrete = isBoolean(type);
						String[] nameAndDesc = getJMXContext().getCounterNameAndDescription(oname, ai);
						addCounter(new JMXCounter(ai.getName(), nameAndDesc[0], nameAndDesc[1], ctype, discrete));
						attrNamesForCounters.add(ai.getName());
					} else {
						if(type.equals(CompositeData.class.getName())) {
							addChildEntity(new JMXEntityCompositeData(getId(), c, oname, ai.getName()));
						} else if(type.equals(TabularData.class.getName())) {
							addChildEntity(new JMXEntityTabularData(getId(), c, oname, ai.getName()));
						} else if(type.equals(Stats.class.getName())) {
							addChildEntity(new JMXJSR77Entity(getId(), c, oname, ai.getName()));
						} else {
							// complex type
							processComplexAttribute(ai, false);
						}
					}
				}
				fAttributesForCounters = attrNamesForCounters.toArray(new String[attrNamesForCounters.size()]);
			}
		} catch (InstanceNotFoundException e) {
			fJustAPlaceHolder = true;
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		if(fObjectName != null) {
			try {
				MBeanInfo binfo = getJMXContext().getJMXConnection().getMBeanInfo(fObjectName);
				MBeanAttributeInfo[] attrInfos = binfo.getAttributes();
				// keep track of entities that need to be removed
				resetTouchedByUpdateForChildren();
				if(!Utils.isEmptyArray(attrInfos)) {
					for(MBeanAttributeInfo ai : attrInfos) {
						if(!getJMXContext().acceptCounter(fObjectName, ai)) {
							continue;
						}
						String type = ai.getType();
						if(type != null) {
							CounterType ctype = convertType(type);
							if(ctype == null) {
								if(type.equals(CompositeData.class.getName())) {
									EntityId eid = JMXEntityCompositeData.createEntityId(getId(), ai.getName());
									Entity child = getChildEntity(eid);
									if(child == null) {
										child = new JMXEntityCompositeData(getId(), getJMXContext(), fObjectName, ai.getName());
										addChildEntity(child);
									} else {
										((JMXEntity)child).update(null);
									}
								}  else if(type.equals(TabularData.class.getName())) {
									EntityId eid = JMXEntityTabularData.createEntityId(getId(), ai.getName());
									Entity child = getChildEntity(eid);
									if(child == null) {
										child = new JMXEntityTabularData(getId(), getJMXContext(), fObjectName, ai.getName());
										addChildEntity(child);
									} else {
										((JMXEntity)child).update(null);
									}									
								} else if(type.equals(Stats.class.getName())) {
									EntityId eid = JMXJSR77Entity.createEntityId(getId(), ai.getName());
									Entity child = getChildEntity(eid);
									if(child == null) {
										child = new JMXJSR77Entity(getId(), getJMXContext(), fObjectName, ai.getName());
										addChildEntity(child);
									} else {
										((JMXEntity)child).update(null);
									}
									// complex type
									processComplexAttribute(ai, true);
								}
							}
						}
					}
				}
				// now remove all stale children
				removeStaleChildren();

				// sort if required
				if(getJMXContext().sortEntities()) {
					sortChildren();
				}

			} catch (InstanceNotFoundException e) {
				; // ignore
			}

		}
		if(recursive) {
			for(Entity child : fChildrenEntities.values()) {
				child.updateChildrenEntities(true);
			}
		}
	}

	/**
	 * Hook for subclasses that might want to process attributes of complex types
	 * (other than primitive, String or CompositeType).
	 * This method is invoked from constructor and every time the entity is updated.
	 * @param ai
	 * @param forUpdate if the call was when updating the children of this entity
	 * @return the id of a newly added entity derived from the given attribute
	 * or null if the complex attribute was of no interest
	 */
	protected EntityId processComplexAttribute(MBeanAttributeInfo ai, boolean forUpdate) {
		return null;
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#addChildEntity(com.ixora.rms.agents.impl.Entity)
	 */
	public void addChildEntity(Entity entity) throws Throwable {
		super.addChildEntity(entity);
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
	 */
	protected void retrieveCounterValues() throws Throwable {
		AttributeList values = getJMXContext().getJMXConnection().getAttributes(fObjectName, fAttributesForCounters);
		if(!Utils.isEmptyCollection(values)) {
			for(Counter c : fCounters.values()) {
				if(c.isEnabled()) {
					JMXCounter counter = (JMXCounter)c;
					String jmxName = counter.getJMXName();
					for(Object o : values) {
						Attribute attr = (Attribute)o;
						String sattr = attr.getName();
						if(sattr.equals(jmxName)) {
							counter.dataReceived(attr.getValue());
						}
					}
				}
			}
		}
	}

	/**
	 * @return the jmx execution context
	 */
	protected JMXAgentExecutionContext getJMXContext() {
		return (JMXAgentExecutionContext)fContext;
	}

	/**
	 * Converts the given jmx type to a CounterType.
	 * @param type
	 * @return null if the given type is unknown(not handled)
	 */
	protected CounterType convertType(String type) {
		if(type.equals("int") || type.equals(Integer.class.getName())
			|| type.equals("long") || type.equals(Long.class.getName())
			|| type.equals("short") || type.equals(Short.class.getName())
			|| type.equals("byte") || type.equals(Byte.class.getName())) {
			return CounterType.LONG;
		} else if(type.equals(Date.class.getName())) {
			return CounterType.DATE;
		} else if(type.equals("double") || type.equals(Double.class.getName())
				|| type.equals("float") || type.equals(Float.class.getName())) {
			return CounterType.DOUBLE;
		} else if(type.equals("char") || type.equals(Character.class.getName())
				|| type.equals(String.class.getName())
				|| type.equals("boolean") || type.equals(Boolean.class.getName())) {
			return CounterType.STRING;
		}
		return null;
	}

	/**
	 * @param type
	 * @return
	 */
	protected boolean isBoolean(String type) {
		return type.equals("boolean") || type.equals(Boolean.class.getName());
	}

	/**
	 * @return
	 */
	public ObjectName getObjectName() {
		return fObjectName;
	}

	/**
	 * @param context
	 * @param parent
	 * @param oname
	 * @return
	 */
	public static EntityId createEntityId(JMXAgentExecutionContext context, EntityId parent, ObjectName oname) {
		return new EntityId(parent, context.getEntityName(oname));
	}

	/**
	 * @param context
	 * @param parent
	 * @param oname
	 * @param name
	 * @return
	 */
	public static EntityId createEntityId(
			JMXAgentExecutionContext context, EntityId parent, ObjectName oname, String name) {
		return new EntityId(parent, name == null ? context.getEntityName(oname) : name);
	}

	/**
	 * @param oname
	 * @throws Throwable 
	 */
	protected void update(ObjectName oname) throws Throwable {
		// all that we need to do
		setTouchedByUpdate(true);
	}
}
