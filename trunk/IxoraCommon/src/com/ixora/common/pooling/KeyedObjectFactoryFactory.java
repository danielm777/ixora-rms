package com.ixora.common.pooling;

/**
 * KeyedObjectFactoryFactory
 * @author Daniel Moraru
 */
public interface KeyedObjectFactoryFactory {
	/**
	 * @return An object factory for the given key
	 * @param key
	 */
	ObjectFactory getFactory(Object key);
}
