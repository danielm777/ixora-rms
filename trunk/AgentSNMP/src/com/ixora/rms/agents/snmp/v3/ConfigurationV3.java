/*
 * Created on 20-Aug-2005
 */
package com.ixora.rms.agents.snmp.v3;

import com.ixora.rms.agents.snmp.Configuration;
import com.ixora.rms.agents.snmp.messages.Msg;

/**
 * Configuration
 */
public class ConfigurationV3 extends Configuration {
	public static final String USER_NAME = Msg.USER_NAME;
	public static final String AUTHENTICATION = Msg.AUTHENTICATION;
	public static final String AUTH_PASSWORD = Msg.AUTH_PASSWORD;
	public static final String USER_PRIVACY = Msg.USER_PRIVACY;
	public static final String PRIVACY_PASSWORD = Msg.PRIVACY_PASSWORD;
	public static final String CONTEXT_NAME = Msg.CONTEXT_NAME;
	public static final String AUTH_CONTEXT_ENGINE = Msg.AUTH_CONTEXT_ENGINE;
	public static final String CONTEXT_ENGINE = Msg.CONTEXT_ENGINE;

	// Values
	public static final String VAL_AUTH_NONE = Msg.VAL_AUTH_NONE;
	public static final String VAL_AUTH_MD5 = Msg.VAL_AUTH_MD5;
	public static final String VAL_AUTH_SHA = Msg.VAL_AUTH_SHA;

	public static final String VAL_PRIV_NONE = Msg.VAL_PRIV_NONE;
	public static final String VAL_PRIV_DES = Msg.VAL_PRIV_DES;
	public static final String VAL_PRIV_AES128 = Msg.VAL_PRIV_AES128;
	public static final String VAL_PRIV_AES192 = Msg.VAL_PRIV_AES192;
	public static final String VAL_PRIV_AES256 = Msg.VAL_PRIV_AES256;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public ConfigurationV3() {
		super();
		setProperty(USER_NAME, TYPE_STRING, true, false);
		setProperty(AUTHENTICATION, TYPE_STRING, true, new String[] {VAL_AUTH_NONE, VAL_AUTH_MD5, VAL_AUTH_SHA});
		setProperty(AUTH_PASSWORD, TYPE_STRING, true, false);
		setProperty(USER_PRIVACY, TYPE_STRING, true, new String[] {VAL_PRIV_NONE, VAL_PRIV_DES, VAL_PRIV_AES128, VAL_PRIV_AES192, VAL_PRIV_AES256});
		setProperty(PRIVACY_PASSWORD, TYPE_STRING, true, false);
		setProperty(CONTEXT_NAME, TYPE_STRING, true, false);
		setProperty(AUTH_CONTEXT_ENGINE, TYPE_STRING, true, false);
		setProperty(CONTEXT_ENGINE, TYPE_STRING, true, false);

		setString(USER_NAME, "initial");
		setString(AUTHENTICATION, VAL_AUTH_NONE);
		setString(USER_PRIVACY, VAL_PRIV_NONE);
	}
}
