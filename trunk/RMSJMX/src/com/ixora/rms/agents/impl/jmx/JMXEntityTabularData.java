/*
 * Created on 11-Apr-2005
 */
package com.ixora.rms.agents.impl.jmx;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.TabularData;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;

/**
 * @author Daniel Moraru
 */
public class JMXEntityTabularData extends JMXEntity {
	private static final long serialVersionUID = 6488719974776307095L;
	private String fAttributeName;

	private static class ChildEntity extends JMXEntity {
		private static final long serialVersionUID = -1166526914495536506L;

		ChildEntity(EntityId eid, CompositeData cd, TabularData td,
				ObjectName oname, String attrName, JMXAgentExecutionContext ctxt) {
			super(eid, ctxt, oname);
			CompositeType rowType = cd.getCompositeType();
			fDescription = rowType.getDescription();
			List<String> itemsToIgnore = td.getTabularType().getIndexNames();
			if(!Utils.isEmptyCollection(itemsToIgnore)) {
				Set<String> items = rowType.keySet();
				if(!Utils.isEmptyCollection(items)) {
					for(String sitem : items) {
						if(!itemsToIgnore.contains(sitem)) {
							OpenType<?> otype = rowType.getType(sitem);
							if(!ctxt.acceptCounter(oname, attrName, sitem, otype)) {
								continue;
							}
							String type = otype.getTypeName();
							CounterType ctype = convertType(type);
							if(ctype != null) {
								boolean discrete = isBoolean(type);
								String[] nameAndDesc = ctxt.getCounterNameAndDescription(oname, attrName, rowType, sitem);
								addCounter(new JMXCounter(sitem, nameAndDesc[0], nameAndDesc[1], ctype, discrete));
							}
						}
					}
				}
			}
		}
		
		void update(CompositeData cd) {
			for(Counter counter : fCounters.values()) {
				JMXCounter jmxCounter = (JMXCounter)counter;
				Object value = cd.get(jmxCounter.getJMXName());
				jmxCounter.reset();
				jmxCounter.dataReceived(value);
			}
		}
		
		static EntityId createEntityId(EntityId parent, TabularData td, CompositeData cd) {
			Object[] index = td.calculateIndex(cd);
			// form the name from the elements of the index
			StringBuilder buff = new StringBuilder();
			for(int i = 0; i < index.length; i++) {
				buff.append(index[i]);
				if(i < index.length - 1) {
					buff.append("#");
				}
			}
			return new EntityId(parent, buff.toString());
		}
	}

	/**
	 * Constructor.
	 * @param id
	 * @param c
	 * @param cdata
	 * @throws Exception
	 */
	public JMXEntityTabularData(EntityId parent, JMXAgentExecutionContext c,
			ObjectName oname, String attrName) throws Throwable {
		super(createEntityId(parent, attrName), c, oname);
		fHasChildren = true;
		fAttributeName = attrName;
		updateChildrenEntities(false);
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
	 */
	@SuppressWarnings("unchecked")
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		// recursivness can be ignored
		resetTouchedByUpdateForChildren();
		TabularData data = (TabularData)getJMXContext().getJMXConnection()
			.getAttribute(fObjectName, fAttributeName);
		if(data != null) {
			Collection<CompositeData> vals = (Collection<CompositeData>)data.values();
			if(!Utils.isEmptyCollection(vals)) {
				for(CompositeData val : vals) {
					EntityId childId = ChildEntity.createEntityId(fEntityId, data, val);
					Entity child = getChildEntity(childId);
					if(child != null) {
						ChildEntity jmxChild = (ChildEntity)child;
						jmxChild.update(val);
					} else {
						addChildEntity(new ChildEntity(childId, val, data, fObjectName, fAttributeName, getJMXContext()));
					}
				}
			}			
						
		}
		removeStaleChildren();
	}
	
	/**
	 * @throws Throwable 
	 * @see com.ixora.rms.agents.impl.jmx.JMXEntity#update(javax.management.ObjectName)
	 */
	protected void update(ObjectName oname) throws Throwable {
		updateChildrenEntities(false);
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
	 */
	protected void retrieveCounterValues() throws Throwable {
		updateChildrenEntities(false);
	}

	/**
	 * @param parent
	 * @param attrName
	 * @return
	 */
	public static EntityId createEntityId(EntityId parent, String attrName) {
		return new EntityId(parent, attrName);
	}	
}
