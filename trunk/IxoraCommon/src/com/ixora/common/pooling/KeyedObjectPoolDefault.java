package com.ixora.common.pooling;

import java.util.HashMap;
import java.util.Map;

import com.ixora.common.pooling.exception.FailedToCreateObject;


/**
 * KeyedObjectPoolDefault
 * @author Daniel Moraru
 */
public final class KeyedObjectPoolDefault implements KeyedObjectPool {
	/**
	 * Pools map.
	 */
	private Map pools;
	/**
	 * Object factory factory.
	 */
	private KeyedObjectFactoryFactory factory;
	/**
	 * Controller for inactive objects.
	 */
	private InactivityController inactivityController;
	/**
	 * Controller of the size of the pool.
	 */
	private PoolSizeController poolSizeController;

	/**
	 * Constructor for KeyedObjectPoolDefault.
	 */
	public KeyedObjectPoolDefault(KeyedObjectFactoryFactory factory, InactivityController inactivityController, PoolSizeController sizeController) {
		super();
		if(factory == null) {
			throw new IllegalArgumentException("null object factory");
		}
		this.factory = factory;
		this.inactivityController = inactivityController;
		this.poolSizeController = sizeController;
		this.pools = new HashMap();
	}

	/**
	 * @see com.ixora.common.pooling.KeyedObjectPool#getObject(java.lang.Object)
	 */
	public synchronized Object getObject(Object key) throws FailedToCreateObject {
		ObjectPool pool = (ObjectPool)this.pools.get(key);
		if(pool == null) {
			pool = new ObjectPoolDefault(this.factory.getFactory(key), this.inactivityController, this.poolSizeController);
			this.pools.put(key, pool);
		}
		return pool.getObject();
	}

	/**
	 * @see com.ixora.common.pooling.KeyedObjectPool#releaseObject(java.lang.Object, java.lang.Object)
	 */
	public synchronized void releaseObject(Object key, Object obj) {
		((ObjectPool)this.pools.get(key)).releaseObject(obj);
	}

	/**
	 * @see com.ixora.common.pooling.KeyedObjectPool#getPoolInfo(java.lang.Object)
	 */
	public synchronized PoolInfo getPoolInfo(Object key) {
		return ((ObjectPool)this.pools.get(key)).getState();
	}
}
