/*
 * Created on 20-Aug-2005
 */
package com.ixora.rms.agents.snmp.messages;

/**
 * Msg
 */
public class Msg {
	public static final String SNMP_NAME = "agents.snmp";
	public static final String PORT_NUMBER = "port_number";
	public static final String USER_MIBS_FOLDER = "user_mibs_folder";
	public static final String COMPILED_MIB_FILE_NAME = "compiled_mib_file_name";
	public static final String PORT_TIMEOUT = "port_timeout";
	public static final String COMUNITY = "comunity";

	public static final String USER_NAME = "user_name";
	public static final String AUTHENTICATION = "authentication";
	public static final String AUTH_PASSWORD = "auth_password";
	public static final String USER_PRIVACY = "user_privacy";
	public static final String PRIVACY_PASSWORD = "privacy_password";
	public static final String CONTEXT_NAME = "context_name";
	public static final String CONTEXT_ENGINE = "context_engine";
	public static final String AUTH_CONTEXT_ENGINE = "auth_context_engine";

	public static final String VAL_AUTH_NONE = "None";
	public static final String VAL_AUTH_MD5 = "MD5";
	public static final String VAL_AUTH_SHA = "SHA";

	public static final String VAL_PRIV_NONE = "None";
	public static final String VAL_PRIV_DES = "DES";
	public static final String VAL_PRIV_AES128 = "AES128";
	public static final String VAL_PRIV_AES192 = "AES192";
	public static final String VAL_PRIV_AES256 = "AES256";

	public static final String ERROR_MIB_PARSE = "error.mib_parse";
	public static final String ERROR_SERVER_UNAVAILABLE = "error.server_unavailable";
}
