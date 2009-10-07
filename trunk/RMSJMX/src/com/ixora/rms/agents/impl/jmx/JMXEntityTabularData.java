/*
 * Created on 11-Apr-2005
 */
package com.ixora.rms.agents.impl.jmx;

import java.util.Set;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.Counter;

/**
 * @author Daniel Moraru
 */
public class JMXEntityTabularData extends JMXEntity {
	private String fAttributeName;

	private class ChildEntity {
		ChildEntity(ObjectName oname, String attrName, CompositeType rowType) {
			Set items = rowType.keySet();
			if(!Utils.isEmptyCollection(items)) {
				for(Object item : items) {
					String sitem = (String)item;
					OpenType otype = rowType.getType(sitem);
					if(!getJMXContext().acceptCounter(oname, attrName, sitem, otype)) {
						continue;
					}
					String type = otype.getTypeName();
					CounterType ctype = convertType(type);
					if(ctype != null) {
						boolean discrete = isBoolean(type);
						String[] nameAndDesc = getJMXContext().getCounterNameAndDescription(oname, attrName, rowType, sitem);
						addCounter(new JMXCounter(sitem, nameAndDesc[0], nameAndDesc[1], ctype, discrete));
					}
				}
			}
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
			ObjectName oname, String attrName) throws Exception {
		super(createEntityId(parent, attrName), c, oname);
		fHasChildren = true;
		fAttributeName = attrName;
		// need to get the tabular data to form entities
		TabularData tabData = (TabularData)getJMXContext().getJMXConnection()
				.getAttribute(oname, attrName);
		if(tabData != null) {
			TabularType tabType = tabData.getTabularType();
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		;// overriden to do nothing as this entity will never have children
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
	 */
	protected void retrieveCounterValues() throws Throwable {
		CompositeData data = (CompositeData)getJMXContext().getJMXConnection()
				.getAttribute(fObjectName, fAttributeName);
		for(Counter counter : fCounters.values()) {
			if(counter.isEnabled()) {
				Object value = data.get(((JMXCounter)counter).getJMXName());
				counter.dataReceived(convertValue(counter.getType(), value));
			}
		}
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
