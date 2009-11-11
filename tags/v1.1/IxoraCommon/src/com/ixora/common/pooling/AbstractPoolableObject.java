package com.ixora.common.pooling;

/**
 * Base class for AbstractPoolableObject
 * @author Daniel Moraru
 */
public abstract class AbstractPoolableObject implements PoolableObject {
	/**
	 * Reference to the pool this object belongs to.
	 */
	private PoolableObjectPool pool;
	/**
	 * Expired flag.
	 */
	protected volatile boolean expired = false;

	/**
	 * Constructor for AbstractPoolableObject.
	 */
	public AbstractPoolableObject() {
		super();
	}

	/**
	 * @see PoolableObject#setOwner(PoolableObjectPool).
	 */
	public void setOwner(PoolableObjectPool pool) {
		if(pool == null) {
			throw new IllegalArgumentException("null pool");
		}
		this.pool = pool;
	}

	/**
	 * @see PoolableObject#release().
	 */
	public void release() {
		if(this.pool != null) {
			this.pool.releaseObject(this);
		}
	}

	/**
	 * @see PoolableObject#expired().
	 */
	public boolean isExpired() {
		return this.expired;
	}
}
