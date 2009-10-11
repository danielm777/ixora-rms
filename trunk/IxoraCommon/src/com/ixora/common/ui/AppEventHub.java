package com.ixora.common.ui;

/**
 * Dispatches application wide events.
 * @author Daniel Moraru
 */
public interface AppEventHub {
	public interface Listener {
		void eventRaised(String event, Object obj);
	}
	/**
	 * @param event
	 * @param obj
	 */
	void raiseEvent(String event, Object obj);
	/**
	 * @param list
	 */
	void addListener(Listener list);
	/**
	 * @param list
	 */
	void removeListener(Listener list);
}
