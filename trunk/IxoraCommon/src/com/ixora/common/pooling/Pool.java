package com.ixora.common.pooling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Pool.
 * @author Daniel Moraru
 */
final class Pool implements InactivityController.Listener {
	/**
	 * Pool Listener.
	 */
	interface Listener {
		/**
		 * Invoked when an object is removed from the pool.
		 * @param obj
		 */
		void objectEvicted(Object obj);
	}

	/**
	 * Available objects.
	 */
	private List available;
	/**
	 * Objects in use.
	 */
	private List inuse;
	/**
	 * Listener.
	 */
	private Listener listener;
	/**
	 * Inactivity controller.
	 */
	private InactivityController inactivityController;
	/**
	 * Pool Size controller.
	 */
	private PoolSizeController poolSizeController;

	/**
	 * Constructor.
	 * @param listener
	 * @param inactivityController
	 * @param poolSizeController
	 */
	public Pool(Listener listener,
			InactivityController inactivityController,
			PoolSizeController poolSizeController) {
		super();
		this.available = new LinkedList();
		this.inuse = new LinkedList();
		this.listener = listener;
		if(inactivityController != null) {
			inactivityController.setPool(this);
			this.inactivityController = inactivityController;
		}
		if(poolSizeController != null) {
			poolSizeController.setPool(this);
			this.poolSizeController = poolSizeController;
		}
	}

	/**
	 * @return An available object or null if none available.
	 */
	public synchronized Object getObject() {
		Object ret = null;
		if(this.available.size() > 0) {
			ret = this.available.remove(0);
			this.inuse.add(ret);
			fireObjectOut(ret);
		}
		return ret;
	}


	/**
	 * Returns a previously obtained object.
	 * @param obj
	 */
	public synchronized void returnObject(Object obj) {
		if(this.inuse.remove(obj)) {
			this.available.add(obj);
		}
		fireObjectIn(obj);
	}

	/**
	 * Removes the given object.
	 * @param obj
	 */
	public synchronized void removeObject(Object obj) {
		if(!this.inuse.remove(obj)) {
			this.available.remove(obj);
		}
		fireObjectRemoved(obj);
		if(listener != null) {
			listener.objectEvicted(obj);
		}
	}

	/**
	 * Adds the given object to the pool.
	 * @param obj
	 */
	public synchronized void addObject(Object obj) {
		this.available.add(obj);
		fireObjectCreated(obj);
	}

	/**
	 * @return Info on the pool state.
	 */
	public synchronized PoolInfo getState() {
		int available = this.available.size();
		int inuse = this.inuse.size();
		return new PoolInfo(inuse, available,  inuse + available, 0);
	}

	/**
	 * @return An iterator over the available objects.
	 */
	public synchronized Iterator getAvailableObjects() {
		List ret = new ArrayList(this.available);
		return ret.iterator();
	}

	/**
	 * @see Listener#objectExpired(Object)
	 */
	public void objectExpired(Object obj) {
		removeObject(obj);
	}

	/**
	 * Fires object out event.
	 */
	private void fireObjectOut(Object obj) {
		if(this.inactivityController != null) {
			this.inactivityController.objectOut(obj);
		}
		if(this.poolSizeController != null) {
			this.poolSizeController.objectOut(obj);
		}
	}

	/**
	 * Fires object in event.
	 */
	private void fireObjectIn(Object obj) {
		if(this.inactivityController != null) {
			this.inactivityController.objectIn(obj);
		}
		if(this.poolSizeController != null) {
			this.poolSizeController.objectIn(obj);
		}
	}

	/**
	 * Fires object created event.
	 */
	private void fireObjectCreated(Object obj) {
		if(this.inactivityController != null) {
			this.inactivityController.objectCreated(obj);
		}
		if(this.poolSizeController != null) {
			this.poolSizeController.objectCreated(obj);
		}
	}

	/**
	 * Fires object removed event.
	 */
	private void fireObjectRemoved(Object obj) {
		if(this.inactivityController != null) {
			this.inactivityController.objectRemoved(obj);
		}
		if(this.poolSizeController != null) {
			this.poolSizeController.objectRemoved(obj);
		}
	}
}