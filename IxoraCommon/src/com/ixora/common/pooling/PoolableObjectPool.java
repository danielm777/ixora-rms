package com.ixora.common.pooling;

import com.ixora.common.pooling.exception.FailedToCreateObject;

/**
 * PoolableObjectPool.
 * @author Daniel Moraru
 */
public interface PoolableObjectPool {
	/**
	 * @return An object from the pool.
	 * @throws FailedToCreateObject
	 */
	PoolableObject getObject() throws FailedToCreateObject;
	/**
	 * Releases the given object back to the pool.
	 * @param obj
	 */
	void releaseObject(PoolableObject obj);
	/**
	 * @return info on the pool.
	 */
	PoolInfo getPoolInfo();
}
