/**
 * 26-Oct-2005
 */
package com.ixora.rms.agents.impl.jmx.jsr77;

import javax.management.ObjectName;
import javax.management.j2ee.statistics.JCAConnectionPoolStats;
import javax.management.j2ee.statistics.JCAStats;
import javax.management.j2ee.statistics.Stats;

import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;

/**
 * @author Daniel Moraru
 */
public class JCAEntity extends JMXJSR77EntityStats {

	/**
	 * @param parent
	 * @param c
	 * @param stats
	 * @param name
	 * @param desc
	 * @throws Throwable
	 */
	public JCAEntity(EntityId parent, JMXAgentExecutionContext c, JCAStats stats) throws Throwable {
		super(parent, c, stats, "JCAStats", "JCA statistics");
		updateChildrenFromStats(stats);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.jsr77.JMXJSR77EntityStats#updateChildrenFromStats(javax.management.j2ee.statistics.Stats)
	 */
	protected void updateChildrenFromStats(Stats stats) throws Throwable {
		JCAStats jcaStats = (JCAStats)stats;
		// NOTE: ignore non-pooled connections for now
		// add connections
//		JCAConnectionStats[] connStats = jcaStats.getConnections();
//		if(!Utils.isEmptyArray(connStats)) {
//			int i = 0;
//			for(JCAConnectionStats connStat : connStats) {
//				++i;
//				String connName = "Connection " + i;
//				EntityId connEntityId = new EntityId(getId(), connName);
//				JMXJSR77EntityStats entity = (JMXJSR77EntityStats)getChildEntity(connEntityId);
//				if(entity != null) {
//					entity.setStats(connStat);
//				} else {
//					addChildEntity(
//						new JMXJSR77EntityStats(getId(), getJMXContext(), connStat, connName, connName));
//				}
//			}
//		}
		// add connection pools
		// NOTE: no clear how you are supposed to differentiate between these pools
		// just use the first one for the moment
		JCAConnectionPoolStats[] connPoolStats = jcaStats.getConnectionPools();
		if(!Utils.isEmptyArray(connPoolStats)) {
			for(JCAConnectionPoolStats connPoolStat : connPoolStats) {
				ObjectName on = new ObjectName(connPoolStat.getConnectionFactory());
				String connFactory = on.getKeyProperty("name");
				String connPoolName = "Connection pool (" + connFactory + ")";
				EntityId connEntityId = new EntityId(getId(), connPoolName);
				JMXJSR77EntityStats entity = (JMXJSR77EntityStats)getChildEntity(connEntityId);
				if(entity != null) {
					entity.setStats(connPoolStat);
				} else {
					addChildEntity(
						new JMXJSR77EntityStats(getId(), getJMXContext(), connPoolStat,
								connPoolName, connPoolName));
				}
			}
		}
	}
}
