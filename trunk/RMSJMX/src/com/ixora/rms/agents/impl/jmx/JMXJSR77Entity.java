/**
 * 26-Oct-2005
 */
package com.ixora.rms.agents.impl.jmx;

import javax.management.ObjectName;
import javax.management.j2ee.statistics.JCAStats;
import javax.management.j2ee.statistics.JDBCStats;
import javax.management.j2ee.statistics.JMSStats;
import javax.management.j2ee.statistics.Stats;

import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.impl.jmx.jsr77.JCAEntity;
import com.ixora.rms.agents.impl.jmx.jsr77.JDBCEntity;
import com.ixora.rms.agents.impl.jmx.jsr77.JMSEntity;
import com.ixora.rms.agents.impl.jmx.jsr77.JMXJSR77EntityStats;

/**
 * @author Daniel Moraru
 */
public class JMXJSR77Entity extends JMXJSR77EntityStats {
	/** Attribute name */
	private String fAttributeName;

	/**
	 * Constructor.
	 * @param id
	 * @param c
	 * @param cdata
	 * @throws Throwable
	 */
	public JMXJSR77Entity(EntityId parent, JMXAgentExecutionContext c,
			ObjectName oname, String attrName) throws Throwable {
		super(createEntityId(parent, attrName), c, oname);
		fHasChildren = false;
		fAttributeName = attrName;
		Stats stats = (Stats)getJMXContext().getJMXConnection()
				.getAttribute(oname, attrName);
		init(stats);
		if(stats instanceof JDBCStats) {
			addChildEntity(new JDBCEntity(getId(), getJMXContext(), (JDBCStats)stats));
		} else if(stats instanceof JCAStats) {
			addChildEntity(new JCAEntity(getId(), getJMXContext(), (JCAStats)stats));
		} else if(stats instanceof JMSStats) {
			addChildEntity(new JMSEntity(getId(), getJMXContext(), (JMSStats)stats));
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		; // overriden to do nothing as it's children are set from the stats
		//  during every cycle
	}

	/**
	 * Need to retrieve the Stats attribute to be fed to its children.
	 * @see com.ixora.rms.agents.impl.Entity#beginCycle()
	 */
	public void beginCycle() throws Throwable {
		super.beginCycle();
		fStats = (Stats)getJMXContext().getJMXConnection()
				.getAttribute(fObjectName, fAttributeName);
		for(Entity child : fChildrenEntities.values()) {
			if(child instanceof JMXJSR77EntityStats) {
				((JMXJSR77EntityStats)child).setStats(fStats);
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
