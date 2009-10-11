/*
 * Created on Feb 18, 2004
 */
package com.ixora.rms.ui.dataviewboard.legend;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class makes available information on the plotted
 * resources of a control.
 * @author Daniel Moraru
 */
public class Legend {
	/**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * Invoked when the values have changed.
		 * @param values
		 * @param time
		 */
		public void valuesChanged(Number[] values, long time);
		/**
		 * Invoked when the set of represented items has changed.
		 * @param ii the new set of items info
		 */
		public void itemsInfoUpdated(LegendItemInfo[] ii);
	}

	/** Items info */
	private LegendItemInfo[] itemsInfo;
	/** Timestamp for the values */
	private long timestamp;
	/** Listeners */
	private List<Listener> listeners;
	/** Subject's name */
	private String html;
	/**
	 * Constructor.
	 * @param html
	 */
	public Legend(String html) {
		super();
		this.listeners = new LinkedList<Listener>();
		this.html = html;
	}

	/**
	 * Sets legend info.
	 * @param colors
	 * @param rds
	 */
	public void setInfo(LegendItemInfo[] counters) {
		this.itemsInfo = counters;
		for(Iterator<Listener> iter = listeners.iterator(); iter.hasNext(); ) {
			iter.next().itemsInfoUpdated(counters);
		}
	}

	/**
	 * Sets the new values.
	 * @param values
	 * @param time
	 */
	public void setValues(Number[] values, long time) {
		if(values.length != itemsInfo.length) {
			throw new IllegalArgumentException("values array's length is different than counterInfo");
		}
		for(int i = 0; i < values.length; i++) {
			this.itemsInfo[i].setValue(values[i]);
		}
		this.timestamp = time;
		for(Iterator<Listener> iter = listeners.iterator(); iter.hasNext(); ) {
			iter.next().valuesChanged(values, time);
		}
	}

	/**
	 * @return items info
	 */
	public LegendItemInfo[] getItemsInfo() {
		return itemsInfo;
	}

	/**
	 * @return the values timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Adds a listener.
	 * @param listener
	 */
	public void addListener(Listener listener) {
		if(listeners.contains(listener)) {
			return;
		}
		listeners.add(listener);
	}

	/**
	 * Removes a listener.
	 * @param listener
	 */
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	/**
	 * @return the name.
	 */
	public String getHtml() {
		return html;
	}
}
