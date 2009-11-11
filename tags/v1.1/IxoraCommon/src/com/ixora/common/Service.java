/*
 * Created on 02-Jan-2004
 */
package com.ixora.common;

/**
 * Service interface.
 * @author Daniel Moraru
 */
public interface Service {
	/**
	 * Shuts down the service.
	 * Service implementations should do cleanup work here.
	 */
	void shutdown();
}
