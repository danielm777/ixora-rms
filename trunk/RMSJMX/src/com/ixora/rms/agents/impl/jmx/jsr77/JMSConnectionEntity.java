/**
 * 26-Oct-2005
 */
package com.ixora.rms.agents.impl.jmx.jsr77;

import javax.management.j2ee.statistics.JMSConnectionStats;
import javax.management.j2ee.statistics.JMSSessionStats;
import javax.management.j2ee.statistics.Stats;

import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;

/**
 * @author Daniel Moraru
 */
public class JMSConnectionEntity extends JMXJSR77EntityStats {
	private static final long serialVersionUID = -9098700177885620627L;

	/**
	 * @param parent
	 * @param c
	 * @param stats
	 * @param name
	 * @param desc
	 * @throws Throwable
	 */
	public JMSConnectionEntity(EntityId parent, JMXAgentExecutionContext c, JMSConnectionStats stats, String name, String desc) throws Throwable {
		super(parent, c, stats, name, desc);
		updateChildrenFromStats(stats);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.jsr77.JMXJSR77EntityStats#updateChildrenFromStats(javax.management.j2ee.statistics.Stats)
	 */
	protected void updateChildrenFromStats(Stats stats) throws Throwable {
		JMSConnectionStats jmsStats = (JMSConnectionStats)stats;
		JMSSessionStats[] sessStats = jmsStats.getSessions();
		if(!Utils.isEmptyArray(sessStats)) {
			int i = 0;
			for(JMSSessionStats sessStat : sessStats) {
				++i;
				String sessName = "Session " + i;
				EntityId connEntityId = new EntityId(getId(), sessName);
				JMXJSR77EntityStats entity = (JMXJSR77EntityStats)getChildEntity(connEntityId);
				if(entity != null) {
					entity.setStats(sessStat);
				} else {
					addChildEntity(
						new JMSSessionEntity(getId(), getJMXContext(), sessStat,
								sessName, sessName));
				}
			}
		}
	}
}
