package com.ixora.common.pooling;

import com.ixora.common.pooling.exception.FailedToCreateObject;

/**
 * KeyedObjectPool.
 * @author Daniel Moraru
 */
public interface KeyedObjectPool {
	/**
	 * Gets a pooled object.
	 * @param key the key
	 * @return a pooled object
	 */
	Object getObject(Object key) throws FailedToCreateObject;
	/**
	 * Releases the given object back into the pool.
	 * @param key the key
	 * @param obj the object to release back to the pool
	 */
	void releaseObject(Object key, Object obj);
	/**
	 * @return pool information.
	 * @param key
	 */
	PoolInfo getPoolInfo(Object key);
}
