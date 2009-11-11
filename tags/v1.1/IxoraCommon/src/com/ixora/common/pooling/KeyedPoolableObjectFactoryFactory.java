package com.ixora.common.pooling;

/**
 * KeyedPoolableObjectFactoryFactory.
 * @author Daniel Moraru
 */
public interface KeyedPoolableObjectFactoryFactory {
	/**
	 * @return A poolable object factory for the given key.
	 * @param key
	 */
	PoolableObjectFactory getFactory(Object key);
}
