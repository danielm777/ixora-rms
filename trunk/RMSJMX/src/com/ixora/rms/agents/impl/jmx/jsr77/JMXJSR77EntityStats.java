/*
 * Created on 11-Apr-2005
 */
package com.ixora.rms.agents.impl.jmx.jsr77;

import javax.management.ObjectName;
import javax.management.j2ee.statistics.BoundaryStatistic;
import javax.management.j2ee.statistics.BoundedRangeStatistic;
import javax.management.j2ee.statistics.CountStatistic;
import javax.management.j2ee.statistics.RangeStatistic;
import javax.management.j2ee.statistics.Statistic;
import javax.management.j2ee.statistics.Stats;
import javax.management.j2ee.statistics.TimeStatistic;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.JMXCounter;
import com.ixora.rms.agents.impl.jmx.JMXEntity;
import com.ixora.rms.data.CounterValueDouble;

/**
 * Entity derived from a J2EE Stats attribute as specified in JSR77.
 * It's used in the generic JMXEntity to extract info from attributes of type J2EE Stats.
 * @author Daniel Moraru
 */
public class JMXJSR77EntityStats extends JMXEntity {
	private static final long serialVersionUID = -4631082898766607295L;
	protected static final String LOWER_BOUND = " (lower bound)";
	protected static final String UPPER_BOUND = " (upper bound)";
	protected static final String HIGH_WATER_MARK = " (high water bound)";
	protected static final String LOW_WATER_MARK = " (low water bound)";
	protected static final String CURRENT = " (current)";
	protected static final String MIN_TIME = " (min time)";
	protected static final String MAX_TIME = " (max time)";
	protected static final String TOTAL_TIME = " (total time)";
	protected static final String AVG_TIME = " (avg time)";

	protected Stats fStats;

	/**
	 * @param parent
	 * @param c
	 * @param stats
	 * @param name
	 * @param desc
	 */
	protected JMXJSR77EntityStats(EntityId parent, JMXAgentExecutionContext c, Stats stats, String name, String desc) {
		super(parent, c, name, desc);
		fCanRefreshChildren = false;
		init(stats);
	}


	/**
	 * @param id
	 * @param c
	 * @param oname
	 */
	protected JMXJSR77EntityStats(EntityId id, JMXAgentExecutionContext c, ObjectName oname) {
		super(id, c, oname);
		fCanRefreshChildren = false;
	}

	/**
	 * @param stats
	 * @throws Throwable
	 */
	protected void updateChildrenFromStats(Stats stats) throws Throwable {
		fStats = stats;
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		; // overriden to do nothing as it's children are set from the stats
		//  during every cycle
	}

	/**
	 * Retrieves counter values from the given Stats objects.
	 * @param stats
	 * @throws Throwable
	 */
	protected void retrieveCounterValues(Stats stats) throws Throwable {
		updateChildrenFromStats(stats);
		Statistic[] statistics = stats.getStatistics();
		if(!Utils.isEmptyArray(statistics)) {
			for(Statistic s : statistics) {
				if(s instanceof BoundedRangeStatistic) {
					BoundedRangeStatistic brs = (BoundedRangeStatistic)s;
					// lowerBound
					Counter counter = getCounter(new CounterId(s.getName() + LOWER_BOUND));
					if(counter != null && counter.isEnabled()) {
						counter.dataReceived(new CounterValueDouble(brs.getLowerBound()));
					}
					// upperBound
					counter = getCounter(new CounterId(s.getName() + UPPER_BOUND));
					if(counter != null && counter.isEnabled()) {
						counter.dataReceived(new CounterValueDouble(brs.getUpperBound()));
					}
					// current
					counter = getCounter(new CounterId(s.getName() + CURRENT));
					if(counter != null && counter.isEnabled()) {
						counter.dataReceived(new CounterValueDouble(brs.getCurrent()));
					}
					// highWaterMark
					counter = getCounter(new CounterId(s.getName() + HIGH_WATER_MARK));
					if(counter != null && counter.isEnabled()) {
						counter.dataReceived(new CounterValueDouble(brs.getHighWaterMark()));
					}
					// lowWaterMark
					counter = getCounter(new CounterId(s.getName() + LOW_WATER_MARK));
					if(counter != null && counter.isEnabled()) {
						counter.dataReceived(new CounterValueDouble(brs.getLowWaterMark()));
					}
				} else if(s instanceof BoundaryStatistic) {
					BoundaryStatistic bs = (BoundaryStatistic)s;
					// lowerBound
					Counter counter = getCounter(new CounterId(s.getName() + LOWER_BOUND));
					if(counter != null && counter.isEnabled()) {
						counter.dataReceived(new CounterValueDouble(bs.getLowerBound()));
					}
					// upperBound
					counter = getCounter(new CounterId(s.getName() + UPPER_BOUND));
					if(counter != null && counter.isEnabled()) {
						counter.dataReceived(new CounterValueDouble(bs.getUpperBound()));
					}
				} else if(s instanceof RangeStatistic) {
					RangeStatistic rs = (RangeStatistic)s;
					// current
					Counter counter = getCounter(new CounterId(s.getName() + CURRENT));
					if(counter != null && counter.isEnabled()) {
						counter.dataReceived(new CounterValueDouble(rs.getCurrent()));
					}
					// highWaterMark
					counter = getCounter(new CounterId(s.getName() + HIGH_WATER_MARK));
					if(counter != null && counter.isEnabled()) {
						counter.dataReceived(new CounterValueDouble(rs.getHighWaterMark()));
					}
					// lowWaterMark
					counter = getCounter(new CounterId(s.getName() + LOW_WATER_MARK));
					if(counter != null && counter.isEnabled()) {
						counter.dataReceived(new CounterValueDouble(rs.getLowWaterMark()));
					}
				} else if(s instanceof CountStatistic) {
					CountStatistic cs = (CountStatistic)s;
					Counter counter = getCounter(new CounterId(s.getName()));
					if(counter != null && counter.isEnabled()) {
						counter.dataReceived(new CounterValueDouble(cs.getCount()));
					}
				} else if(s instanceof TimeStatistic) {
					// count
					TimeStatistic ts = (TimeStatistic)s;
					Counter counter = getCounter(new CounterId(s.getName()));
					if(counter != null && counter.isEnabled()) {
						counter.dataReceived(new CounterValueDouble(ts.getCount()));
					}
					// min time
					counter = getCounter(new CounterId(s.getName() + MIN_TIME));
					if(counter != null && counter.isEnabled()) {
						counter.dataReceived(new CounterValueDouble(ts.getMinTime()));
					}
					// max time
					counter = getCounter(new CounterId(s.getName() + MAX_TIME));
					if(counter != null && counter.isEnabled()) {
						counter.dataReceived(new CounterValueDouble(ts.getMaxTime()));
					}
					// total time
					counter = getCounter(new CounterId(s.getName() + TOTAL_TIME));
					if(counter != null && counter.isEnabled()) {
						counter.dataReceived(new CounterValueDouble(ts.getTotalTime()));
					}
					// avg time
					counter = getCounter(new CounterId(s.getName() + AVG_TIME));
					if(counter != null && counter.isEnabled()) {
						counter.dataReceived(
							new CounterValueDouble(ts.getTotalTime()/(float)ts.getCount()));
					}
				}
			}
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
	 */
	protected void retrieveCounterValues() throws Throwable {
		if(fStats != null) {
			retrieveCounterValues(fStats);
		}
	}

	/**
	 * Initializes this entity with data from the given Stats object.
	 * @param stats
	 */
	protected void init(Stats stats) {
		Statistic[] statistics = stats.getStatistics();
		// create counters from statistics, expanding the related values (low, high...) into
		// independent counters
		if(!Utils.isEmptyArray(statistics)) {
			for(Statistic s : statistics) {
				if(s instanceof BoundedRangeStatistic) {
					// lowerBound
					addCounter(new JMXCounter(s.getName() + LOWER_BOUND, s.getDescription() + LOWER_BOUND, CounterType.LONG));
					// upperBound
					addCounter(new JMXCounter(s.getName() + UPPER_BOUND, s.getDescription() + UPPER_BOUND, CounterType.LONG));
					// current
					addCounter(new JMXCounter(s.getName() + CURRENT, s.getDescription() + CURRENT, CounterType.LONG));
					// highWaterMark
					addCounter(new JMXCounter(s.getName() + HIGH_WATER_MARK, s.getDescription() + HIGH_WATER_MARK, CounterType.LONG));
					// lowWaterMark
					addCounter(new JMXCounter(s.getName() + LOW_WATER_MARK, s.getDescription() + LOW_WATER_MARK, CounterType.LONG));
				} else if(s instanceof BoundaryStatistic) {
					// lowerBound
					addCounter(new JMXCounter(s.getName() + LOWER_BOUND, s.getDescription() + LOWER_BOUND, CounterType.LONG));
					// upperBound
					addCounter(new JMXCounter(s.getName() + UPPER_BOUND, s.getDescription() + UPPER_BOUND, CounterType.LONG));
				} else if(s instanceof RangeStatistic) {
					// current
					addCounter(new JMXCounter(s.getName() + CURRENT, s.getDescription() + CURRENT, CounterType.LONG));
					// highWaterMark
					addCounter(new JMXCounter(s.getName() + HIGH_WATER_MARK, s.getDescription() + HIGH_WATER_MARK, CounterType.LONG));
					// lowWaterMark
					addCounter(new JMXCounter(s.getName() + LOW_WATER_MARK, s.getDescription() + LOW_WATER_MARK, CounterType.LONG));
				} else if(s instanceof CountStatistic) {
					addCounter(new JMXCounter(s.getName(), s.getDescription(), CounterType.LONG));
				} else if(s instanceof TimeStatistic) {
					// count
					addCounter(new JMXCounter(s.getName(), s.getDescription(), CounterType.LONG));
					// min time
					addCounter(new JMXCounter(s.getName() + MIN_TIME, s.getDescription() + MIN_TIME, CounterType.LONG));
					// max time
					addCounter(new JMXCounter(s.getName() + MAX_TIME, s.getDescription() + MAX_TIME, CounterType.LONG));
					// total time
					addCounter(new JMXCounter(s.getName() + TOTAL_TIME, s.getDescription() + TOTAL_TIME, CounterType.LONG));
					// avg time
					addCounter(new JMXCounter(s.getName() + AVG_TIME, s.getDescription() + AVG_TIME, CounterType.DOUBLE));
				}
			}
		}
	}

	/**
	 * Sets the Stats object from which this entity will extract it's counter values.
	 * @param stats
	 * @throws Throwable
	 */
	public void setStats(Stats stats) throws Throwable {
		fStats = stats;
		updateChildrenFromStats(stats);
		// sort if required
		if(getJMXContext().sortEntities()) {
			sortChildren();
		}
	}
}
