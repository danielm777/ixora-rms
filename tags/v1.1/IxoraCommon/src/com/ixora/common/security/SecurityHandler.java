/**
 * 04-Mar-2006
 */
package com.ixora.common.security;

import com.ixora.common.exception.AppRuntimeException;

/**
 * @author Daniel Moraru
 */
public interface SecurityHandler {

	/**
	 * Verifies the given signature for the given content.
	 * @param content
	 * @param sigbytes
	 * @return
	 * @throws AppRuntimeException
	 */
	boolean verify(byte[] content, byte[] sigbytes);

	/**
	 * @param source
	 * @return
	 */
	String encrypt(String source);

	/**
	 * Decrypts the given text.
	 * @param source
	 * @return
	 */
	String decrypt(String source);

}