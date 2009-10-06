package com.ixora.common.pooling;

import com.ixora.common.pooling.exception.FailedToCreateObject;

/**
 * PoolableObjectFactory.
 * @author Daniel Moraru
 */
public interface PoolableObjectFactory {
	/**
	 * @return A poolable object.
	 * @throws FailedToCreateObject
	 */
	PoolableObject createObject() throws FailedToCreateObject;
}
