/**
 * 26-Oct-2005
 */
package com.ixora.rms.agents.impl.jmx.jsr77;

import javax.management.j2ee.statistics.JMSConsumerStats;
import javax.management.j2ee.statistics.JMSProducerStats;
import javax.management.j2ee.statistics.JMSSessionStats;
import javax.management.j2ee.statistics.Stats;

import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;

/**
 * @author Daniel Moraru
 */
public class JMSSessionEntity extends JMXJSR77EntityStats {

	/**
	 * @param parent
	 * @param c
	 * @param stats
	 * @param name
	 * @param desc
	 * @throws Throwable
	 */
	public JMSSessionEntity(EntityId parent, JMXAgentExecutionContext c,
			JMSSessionStats stats, String name, String desc) throws Throwable {
		super(parent, c, stats, name, desc);
		updateChildrenFromStats(stats);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.jsr77.JMXJSR77EntityStats#updateChildrenFromStats(javax.management.j2ee.statistics.Stats)
	 */
	protected void updateChildrenFromStats(Stats stats) throws Throwable {
		JMSSessionStats jmsStats = (JMSSessionStats)stats;
		JMSConsumerStats[] consStats = jmsStats.getConsumers();
		if(!Utils.isEmptyArray(consStats)) {
			int i = 0;
			for(JMSConsumerStats consStat : consStats) {
				++i;
				String consName = consStat.getOrigin();
				EntityId connEntityId = new EntityId(getId(), consName);
				JMXJSR77EntityStats entity = (JMXJSR77EntityStats)getChildEntity(connEntityId);
				if(entity != null) {
					entity.setStats(consStat);
				} else {
					addChildEntity(
						new JMXJSR77EntityStats(getId(), getJMXContext(), consStat,
								consName, consName));
				}
			}
		}
		JMSProducerStats [] prodStats = jmsStats.getProducers();
		if(!Utils.isEmptyArray(prodStats)) {
			int i = 0;
			for(JMSProducerStats prodStat : prodStats) {
				++i;
				String prodName = prodStat.getDestination();
				EntityId connEntityId = new EntityId(getId(), prodName);
				JMXJSR77EntityStats entity = (JMXJSR77EntityStats)getChildEntity(connEntityId);
				if(entity != null) {
					entity.setStats(prodStat);
				} else {
					addChildEntity(
						new JMXJSR77EntityStats(getId(), getJMXContext(), prodStat,
								prodName, prodName));
				}
			}
		}
	}
}
