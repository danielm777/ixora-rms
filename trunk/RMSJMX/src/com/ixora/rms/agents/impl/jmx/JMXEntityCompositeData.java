/*
 * Created on 11-Apr-2005
 */
package com.ixora.rms.agents.impl.jmx;

import java.util.Set;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.Counter;

/**
 * @author Daniel Moraru
 */
public class JMXEntityCompositeData extends JMXEntity {
	private static final long serialVersionUID = 1867706330174974877L;
	private String fAttributeName;

	/**
	 * Constructor.
	 * @param id
	 * @param c
	 * @param cdata
	 * @throws Exception
	 */
	public JMXEntityCompositeData(EntityId parent, JMXAgentExecutionContext c,
			ObjectName oname, String attrName) throws Exception {
		super(createEntityId(parent, attrName), c, oname);
		fHasChildren = false;
		fAttributeName = attrName;
		// need to get the composite data to form counters
		CompositeData compData = (CompositeData)getJMXContext().getJMXConnection()
				.getAttribute(oname, attrName);
		if(compData != null) {
			CompositeType compType = compData.getCompositeType();
			Set<String> items =compType.keySet();
			if(!Utils.isEmptyCollection(items)) {
				for(Object item : items) {
					String sitem = (String)item;
					OpenType<?> otype = compType.getType(sitem);
					if(!getJMXContext().acceptCounter(oname, attrName, sitem, otype)) {
						continue;
					}
					String type = otype.getTypeName();
					CounterType ctype = convertType(type);
					if(ctype != null) {
						boolean discrete = isBoolean(type);
						String[] nameAndDesc = getJMXContext().getCounterNameAndDescription(oname, attrName, compType, sitem);
						addCounter(new JMXCounter(sitem, nameAndDesc[0], nameAndDesc[1], ctype, discrete));
					}
				}
			}
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
				counter.dataReceived(value);
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
