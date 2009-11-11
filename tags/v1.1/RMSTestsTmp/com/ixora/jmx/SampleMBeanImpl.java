package com.ixora.jmx;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

public class SampleMBeanImpl implements SampleMBean {
	private static final String[] ITEM_NAMES = new String[] { "Index1", "Index2",
			"Counter1", "Counter2" };

	private static final String[] ITEM_DESCRIPTIONS = new String[] {
			"The name of the item which is an index 1 in the tabular data",
			"The name of the item which is an index 2 in the tabular data",
			"Counter - first", "Counter - second" };

	private static final OpenType<?>[] ITEM_TYPES = new OpenType<?>[] {
			SimpleType.STRING, SimpleType.STRING, SimpleType.LONG, SimpleType.LONG };

	private static CompositeType ROW_TYPE;
	private static TabularType TABULAR_TYPE;
	private static Map<String, Map<String, Stats>> stats1 = new ConcurrentHashMap<String, Map<String, Stats>>();

	private static class Stats {
		AtomicLong counter1 = new AtomicLong();
		AtomicLong counter2 = new AtomicLong();
	}

	static {
		try {
			ROW_TYPE = new CompositeType("Index item", "Index item statistics",
					ITEM_NAMES, ITEM_DESCRIPTIONS, ITEM_TYPES);
		} catch (OpenDataException e) {
			System.err.println(e);
		}

		try {
			TABULAR_TYPE = new TabularType("Items stats", "Items statistics",
					ROW_TYPE, new String[] { ITEM_NAMES[0], ITEM_NAMES[1] });
		} catch (OpenDataException e) {
			System.err.println(e);
		}
	}

	public static void updateStats(String itemidx1, String itemidx2, long val1, long val2) {
		Map<String, Stats> is = stats1.get(itemidx1);
		if(is == null) {
			is = new HashMap<String, Stats>();
			stats1.put(itemidx1, is);
		} else {
			Stats s = is.get(itemidx2);
			if(s == null) {
				s = new Stats();
				is.put(itemidx2, s);
			}
			s.counter1.set(val1);
			s.counter2.set(val2);
		}
	}

	/**
	 * @see com.ixora.jmx.SampleMBean#getStatsAsTabularData()
	 */
	public TabularData getStatsAsTabularData() throws OpenDataException {
		TabularDataSupport ts = new TabularDataSupport(TABULAR_TYPE);
		for(Map.Entry<String, Map<String, Stats>> entry : stats1.entrySet()) {
			for(Map.Entry<String, Stats> entry2 : entry.getValue().entrySet()) {
				CompositeDataSupport cs = new CompositeDataSupport(
						ROW_TYPE, ITEM_NAMES, new Object[]{
								entry.getKey(), 
								entry2.getKey(),
								entry2.getValue().counter1.get(), 
								entry2.getValue().counter2.get()});
				ts.put(cs);
			}
		}
		return ts;
	}
	
	/**
	 * @see com.ixora.jmx.SampleMBean#getStatsAsTabularData()
	 */
	public CompositeData[] getStatsAsArray() throws OpenDataException {
		List<CompositeData> ret = new LinkedList<CompositeData>();
/*		for(Map.Entry<String, Stats> entry : stats1.entrySet()) {
			CompositeDataSupport cs = new CompositeDataSupport(
					ROW_TYPE, ITEM_NAMES, new Object[]{
							entry.getKey(),
							entry.getValue().index2,
							entry.getValue().counter1.get(), 
							entry.getValue().counter2.get()});
			ret.add(cs);
		}
*/
		return ret.toArray(new CompositeData[0]);
	}	
}
