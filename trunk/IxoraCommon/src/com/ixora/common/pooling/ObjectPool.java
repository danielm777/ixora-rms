package com.ixora.common.pooling;

import com.ixora.common.pooling.exception.FailedToCreateObject;



/**
 * Object pool.
 * @author Daniel Moraru
 */
public interface ObjectPool {
	/**
	 * Pool listener.
	 */
	interface Listener {
		/**
		 * Invoked when the object leaves the pool.
		 */
		void objectOut(Object obj);
		/**
		 * Invoked when the object is returned to the pool.
		 */
		void objectIn(Object obj);
		/**
		 * Invoked whe a new object has been added to the pool.
		 */
		void objectCreated(Object obj);
		/**
		 * Invoked whe an object has been removed from the pool.
		 */
		void objectRemoved(Object obj);
	}

	/**
	 * Gets a pooled object.
	 * @return A pooled object
	 */
	Object getObject() throws FailedToCreateObject;
	/**
	 * Releases the given object back into the pool.
	 * @param obj The object to release back to the pool
	 */
	void releaseObject(Object obj);
	/**
	 * @return Pool state information.
	 */
	PoolInfo getState();
}
