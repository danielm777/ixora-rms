/**
 * 02-Apr-2006
 */
package com.ixora.rms.agents.ldap;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPSearchConstraints;

/**
 * @author Daniel Moraru
 */
public interface LDAPAgentExecutionContext {
	/**
	 * @return
	 */
	public LDAPSearchConstraints getSearchConstraints();
	/**
	 * @return
	 */
	public LDAPConnection getConnection();
}