/*
 * Created on Jun 30, 2004
 */
package com.ixora.common.security;

import java.security.cert.CertificateException;

import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.security.exception.SecException;

/**
 * @author Daniel Moraru
 */
public final class SecurityMgr {
	private static SecurityHandlerDefault handler;

	/**
	 * Constructor.
	 */
	private SecurityMgr() throws CertificateException {
		super();
	}

	/**
	 * Initializes the security manager
	 * @throws AppRuntimeException
	 */
	public static synchronized void initialize() {
		try {
			if(handler == null) {
				handler = new SecurityHandlerDefault();
			}
		} catch(Exception e) {
			throw new SecException(e);
		}
	}

	/**
	 * Verifies the given signature for the given content.
	 * @param content
	 * @param sig
	 * @return
	 * @throws AppRuntimeException
	 */
	public static boolean verifySignature(byte[] content, byte[] sig) {
		return handler().verify(content, sig);
	}

	/**
	 * Encrypts the given text.
	 * @param source
	 * @return
	 */
	public static String encrypt(String source) {
		return handler().encrypt(source);
	}

	/**
	 * Decrypts the given text.
	 * @param source
	 * @return
	 */
	public static String decrypt(String source) {
		return handler().decrypt(source);
	}

	/**
	 * @return
	 */
	private static synchronized SecurityHandlerDefault handler() {
		initialize();
		return handler;
	}
}
