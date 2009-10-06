/*
 * Created on 24-Feb-2005
 */
package com.ixora.common.security;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.ixora.common.security.exception.SecException;
import com.ixora.common.utils.HexConverter;

/**
 * @author Daniel Moraru
 */
public class SecurityHandlerDefault implements SecurityHandler {
	/** Keystore entry alias */
	protected final static String ALIAS = "redbox";
	/** Symmetric encryption default key */
	protected final static byte[] KEY;
	/** Cached public key */
	protected PublicKey publicKey;

	static {
		byte[] key;
		try {
			key = "key0002133326564567884343".getBytes("UTF-8");
		} catch(UnsupportedEncodingException e) {
			key = "key0002133326564567884343".getBytes();
		}
		KEY = key;
	}

	/**
	 * Constructor.
	 * @throws CertificateException
	 */
	public SecurityHandlerDefault() throws CertificateException {
		super();
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate cert = (X509Certificate)cf.generateCertificate(
			getClass().getResourceAsStream("cert.cer"));
		publicKey = cert.getPublicKey();
	}

	/**
	 * @see com.ixora.common.security.SecurityHandler#verify(byte[], byte[])
	 */
	public boolean verify(byte[] content, byte[] sigbytes) {
		try {
			Signature sig = Signature.getInstance("SHA1withDSA");
			sig.initVerify(publicKey);
			sig.update(content);
			return sig.verify(sigbytes);
		} catch(Exception e) {
			throw new SecException(e);
		}
	}

	/**
	 * @see com.ixora.common.security.SecurityHandler#encrypt(java.lang.String)
	 */
	public String encrypt(String source) {
		try {
	    	Key key = getSymmetricKey();
	    	Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	    	desCipher.init(Cipher.ENCRYPT_MODE, key);
	    	byte[] cleartext = source.getBytes();
	    	byte[] ciphertext = desCipher.doFinal(cleartext);
	    	return HexConverter.encode(ciphertext);
	    } catch(Exception e) {
	    	throw new SecException(e);
	    }
	  }

	/**
	 * @return a key used for symmetric encryption
	 */
	private Key getSymmetricKey() {
		try {
			DESKeySpec pass = new DESKeySpec(KEY);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
			SecretKey s = skf.generateSecret(pass);
			return s;
		} catch(Exception e) {
			throw new SecException(e);
	    }
	}

	/**
	 * @see com.ixora.common.security.SecurityHandler#decrypt(java.lang.String)
	 */
	public String decrypt(String source) {
		try {
	      Key key = getSymmetricKey();
	      Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	      byte[] ciphertext = HexConverter.decode(source);
	      desCipher.init(Cipher.DECRYPT_MODE, key);
	      byte[] cleartext = desCipher.doFinal(ciphertext);
	      return new String(cleartext);
	    } catch(Exception e) {
	    	throw new SecException(e);
	    }
	}
}
