package com.ixora.common.pooling;

import com.ixora.common.pooling.exception.FailedToCreateObject;



/**
 * Object factory to be passed to the object pools.
 * @author Daniel Moraru
 */
public interface ObjectFactory {
	/**
	 * Provides new objects to the pool.
	 * @return Object
	 * @throws BkFailedToCreateObject
	 */
	Object createObject() throws FailedToCreateObject;
}
