/**
 * 26-Oct-2005
 */
package com.ixora.rms.agents.impl.jmx.jsr77;

import javax.management.j2ee.statistics.JMSConnectionStats;
import javax.management.j2ee.statistics.JMSStats;
import javax.management.j2ee.statistics.Stats;

import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;

/**
 * @author Daniel Moraru
 */
public class JMSEntity extends JMXJSR77EntityStats {
	private static final long serialVersionUID = 5055883947862938405L;

	/**
	 * @param parent
	 * @param c
	 * @param stats
	 * @param name
	 * @param desc
	 * @throws Throwable
	 */
	public JMSEntity(EntityId parent, JMXAgentExecutionContext c, JMSStats stats) throws Throwable {
		super(parent, c, stats, "JMSStats", "JMS statistics");
		updateChildrenFromStats(stats);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.jsr77.JMXJSR77EntityStats#updateChildrenFromStats(javax.management.j2ee.statistics.Stats)
	 */
	protected void updateChildrenFromStats(Stats stats) throws Throwable {
		JMSStats jmsStats = (JMSStats)stats;
		// add connections
		// NOTE: no clear how you are supposed to differentiate between these pools
		// just use the first one for the moment
		JMSConnectionStats[] connStats = jmsStats.getConnections();
		if(!Utils.isEmptyArray(connStats)) {
			int i = 0;
			for(JMSConnectionStats conntStat : connStats) {
				++i;
				String connName = "Connection " + i;
				EntityId connEntityId = new EntityId(getId(), connName);
				JMXJSR77EntityStats entity = (JMXJSR77EntityStats)getChildEntity(connEntityId);
				if(entity != null) {
					entity.setStats(conntStat);
				} else {
					addChildEntity(
						new JMSConnectionEntity(getId(), getJMXContext(), conntStat,
								connName, connName));
				}
			}
		}
	}
}
