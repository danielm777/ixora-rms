/**
 * 26-Oct-2005
 */
package com.ixora.rms.agents.impl.jmx.jsr77;

import javax.management.ObjectName;
import javax.management.j2ee.statistics.JDBCConnectionPoolStats;
import javax.management.j2ee.statistics.JDBCStats;
import javax.management.j2ee.statistics.Stats;

import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;

/**
 * @author Daniel Moraru
 */
public class JDBCEntity extends JMXJSR77EntityStats {
//	private Entity fConnections;
//	private Entity fConnectionPools;

	/**
	 * @param parent
	 * @param c
	 * @param stats
	 * @param name
	 * @param desc
	 * @throws Throwable
	 */
	public JDBCEntity(EntityId parent, JMXAgentExecutionContext c, JDBCStats stats) throws Throwable {
		super(parent, c, stats, "JDBCStats", "JDBC statistics");
//		fConnections = new Entity
		updateChildrenFromStats(stats);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.jsr77.JMXJSR77EntityStats#updateChildrenFromStats(javax.management.j2ee.statistics.Stats)
	 */
	protected void updateChildrenFromStats(Stats stats) throws Throwable {
		JDBCStats jdbcStats = (JDBCStats)stats;
		// add connections
		// NOTE: ignore non-pooled connections for now
//		JDBCConnectionStats[] connStats = jdbcStats.getConnections();
//		if(!Utils.isEmptyArray(connStats)) {
//			for(JDBCConnectionStats connStat : connStats) {
//				String connName = connStat.toString();
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
		JDBCConnectionPoolStats[] connPoolStats = jdbcStats.getConnectionPools();
		if(!Utils.isEmptyArray(connPoolStats)) {
			for(JDBCConnectionPoolStats connPoolStat : connPoolStats) {
				ObjectName on = new ObjectName(connPoolStat.getJdbcDataSource());
				String dataSource = on.getKeyProperty("name");
				String connPoolName = "Connection pool (" + dataSource + ")";
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
