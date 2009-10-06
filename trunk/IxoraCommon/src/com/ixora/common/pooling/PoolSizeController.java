package com.ixora.common.pooling;

/**
 * Controller that manages the size of the pool.
 * @author Daniel Moraru
 */
public interface PoolSizeController extends ObjectPool.Listener {
	/**
	 * Sets the pool owing this policy.
	 */
	void setPool(Pool pool);
}
