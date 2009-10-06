package com.ixora.common.pooling;

/**
 * PoolableObject.
 * @author Daniel Moraru
 */
public interface PoolableObject {
	/**
	 * Sets the pool that owns this object.
	 */
	void setOwner(PoolableObjectPool pool);
	/**
	 * Releases this object back to the pool.
	 */
	void release();
	/**
	 * @return True if this object is expired and it shouldn't
	 * be cached anymore.
	 */
	boolean isExpired();
	/**
	 * Invoked when this object has been evicted from the pool.
	 */
	void evicted();
}
