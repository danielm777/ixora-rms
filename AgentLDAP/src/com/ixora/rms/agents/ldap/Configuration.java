/*
 * Created on Aug 20, 2005
 */
package com.ixora.rms.agents.ldap;

import com.ixora.rms.agents.ldap.messages.Msg;
import com.ixora.rms.agents.utils.AuthenticationAndPortConfiguration;

/**
 * Configuration
 * @author Daniel Moraru
 */
public class Configuration extends AuthenticationAndPortConfiguration {
	private static final long serialVersionUID = -8525414794572112021L;
	public static final String BASE_DN = Msg.LDAP_CONFIG_BASE_DN;
	public static final String TIMEOUT = Msg.LDAP_CONFIG_TIMEOUT;
	public static final String FILTER = Msg.LDAP_CONFIG_FILTER;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();

		setProperty(BASE_DN, TYPE_STRING, true, true);
		setProperty(FILTER, TYPE_STRING, true, false);
		setProperty(TIMEOUT, TYPE_INTEGER, true, false);

		// Default LDAP port, the SSL version is 636
		setInt(PORT, 389);
		setInt(TIMEOUT, 10000);
		setString(FILTER, "(objectclass=*)");
		//setString(BASE_DN, "cn=monitor");
	}
}
