package com.ixora.common.ui;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Daniel Moraru
 */
public class AppEventHubImpl implements AppEventHub {
	private List<Listener> fListeners;
	
	public AppEventHubImpl() {
		super();
		this.fListeners = new LinkedList<Listener>();		
	}
	
	/**
	 * @see com.ixora.common.ui.AppEventHub#addListener(com.ixora.common.ui.AppEventHub.Listener)
	 */
	public void addListener(Listener list) {
		if(fListeners.contains(list)) {
			return;
		}
		fListeners.add(list);
	}

	/**
	 * @see com.ixora.common.ui.AppEventHub#raiseEvent(java.lang.Object, java.lang.Object)
	 */
	public void raiseEvent(Object event, Object obj) {
		for(Listener listener : this.fListeners) {
			listener.eventRaised(event, obj);			
		}
	}

	/**
	 * @see com.ixora.common.ui.AppEventHub#removeListener(com.ixora.common.ui.AppEventHub.Listener)
	 */
	public void removeListener(Listener list) {
		this.fListeners.remove(list);
	}
}
