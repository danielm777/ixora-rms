package com.ixora.common.pooling;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.pooling.exception.FailedToCreateObject;



/**
 * Default implementation of a growable pool of poolable objects.
 * @author Daniel Moraru
 */
public final class PoolableObjectPoolDefault implements PoolableObjectPool {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(PoolableObjectPoolDefault.class);
	/**
	 * Pool.
	 */
	private Pool pool;
	/**
	 * Object factory providing objects to the pool.
	 */
	private PoolableObjectFactory factory;

	private class EventHandler implements Pool.Listener {
		/**
		 * @see com.ixora.common.pooling.Pool.Listener#objectEvicted(java.lang.Object)
		 */
		public void objectEvicted(Object obj) {
			handleObjectEvicted(obj);
		}
	}

	/**
	 * Constructor for PoolableObjectPoolDefault.
	 * @param factory
	 * @param inactivityController
	 * @param sizeController
	 */
	public PoolableObjectPoolDefault(
			PoolableObjectFactory factory,
			InactivityController inactivityController,
			PoolSizeController sizeController) {
		super();
		if(factory == null) {
			throw new IllegalArgumentException("null object factory");
		}
		this.factory = factory;
		this.pool = new Pool(new EventHandler(), inactivityController, sizeController);
	}

	/**
	 * @see com.ixora.common.pooling.PoolableObjectPool#getObject()
	 */
	public synchronized PoolableObject getObject() throws FailedToCreateObject {
		PoolableObject ret = (PoolableObject)this.pool.getObject();
		if(ret != null) {
			return ret;
		} else {
			ret = this.factory.createObject();
			ret.setOwner(this);
			this.pool.addObject(ret);
			return (PoolableObject)this.pool.getObject();
		}
	}

	/**
	 * @see com.ixora.common.pooling.PoolableObjectPool#releaseObject(com.ixora.common.pooling.PoolableObject)
	 */
	public synchronized void releaseObject(PoolableObject obj) {
		if(obj.isExpired()) {
			this.pool.removeObject(obj);
		}
		else {
			this.pool.returnObject(obj);
		}
	}

	/**
	 * @see com.ixora.common.pooling.PoolableObjectPool#getPoolInfo()
	 */
	public synchronized PoolInfo getPoolInfo() {
		return this.pool.getState();
	}

	/**
	 * @param obj
	 */
	private void handleObjectEvicted(Object obj) {
		try {
			((PoolableObject)obj).evicted();
		} catch(Exception e) {
			logger.error(e);
		}
	}
}
