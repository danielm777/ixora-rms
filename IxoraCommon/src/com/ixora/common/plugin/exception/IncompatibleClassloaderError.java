/**
 * 11-Mar-2006
 */
package com.ixora.common.plugin.exception;

import com.ixora.common.exception.AppException;

/**
 * @author Daniel Moraru
 */
public class IncompatibleClassloaderError extends AppException {

	/**
	 *
	 */
	public IncompatibleClassloaderError(
			String classLoaderId, String existingClasspath, String newClasspath) {
		// TODO localize
		super("A class loader with id '"
				+ classLoaderId + "' was already created and its "
				+ "classpath (" + existingClasspath + ") is not "
				+ "compatible with new classpath: ("
				+ newClasspath + ")");
	}
}
