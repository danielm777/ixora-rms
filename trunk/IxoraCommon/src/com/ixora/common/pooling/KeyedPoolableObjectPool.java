package com.ixora.common.pooling;

import com.ixora.common.pooling.exception.FailedToCreateObject;



/**
 * KeyedPoolableObjectPool.
 * @author Daniel Moraru
 */
public interface KeyedPoolableObjectPool {
	/**
	 * @return An object from the pool.
	 * @param key
	 * @throws BkFailedToCreateObject
	 */
	PoolableObject getObject(Object key) throws FailedToCreateObject;
	/**
	 * Releases the given object back to the pool.
	 * @param key
	 * @param obj
	 */
	void releaseObject(Object key, PoolableObject obj);
	/**
	 * @return Info on the state of the pool.
	 */
	PoolInfo getState(Object key);
}
