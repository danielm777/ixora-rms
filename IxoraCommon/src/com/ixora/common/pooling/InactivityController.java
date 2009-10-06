package com.ixora.common.pooling;

/**
 * Controller that handles inactive objects in the pool.
 * @author Daniel Moraru
 */
public interface InactivityController extends ObjectPool.Listener {
	/**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * Invoked when an object has expired.
		 * @param obj
		 */
		void objectExpired(Object obj);
	}

	/**
	 * Sets the pool owing this policy.
	 */
	void setPool(Pool pool);
}
