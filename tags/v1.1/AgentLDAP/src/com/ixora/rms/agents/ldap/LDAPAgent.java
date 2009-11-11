/*
 * Created on 20-Aug-2005
 */
package com.ixora.rms.agents.ldap;

import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgent;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.RMSException;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchConstraints;


/**
 * LDAPAgent
 * Uses a LDAP library to retrieve information through the Lightweight
 * Directory Access Protocol.
 * @author Daniel Moraru
 */
public class LDAPAgent extends AbstractAgent {
	private LDAPConnection fLDAPConnection;
	private LDAPSearchConstraints fLDAPConstraints;

	public class LDAPAgentContext extends ExecutionContext implements LDAPAgentExecutionContext {
		/**
		 * @see com.ixora.rms.agents.ldap.LDAPAgentExecutionContext#getSearchConstraints()
		 */
		public LDAPSearchConstraints getSearchConstraints() {
			return fLDAPConstraints;
		}
		/**
		 * @see com.ixora.rms.agents.ldap.LDAPAgentExecutionContext#getConnection()
		 */
		public LDAPConnection getConnection() {
			return fLDAPConnection;
		}
	}

	/**
	 * Constructor.
	 * @throws Throwable
	 */
	public LDAPAgent(AgentId agentId, Listener listener) throws Throwable {
        super(agentId, listener, true); // use private collector as each cycle might be expensive
		// Create entities
        replaceContext(new LDAPAgentContext());
		fRootEntity = new LDAPRootEntity(this.fContext);
	}


	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#configCustomChanged()
	 */
	protected void configCustomChanged() throws InvalidConfiguration, Throwable {
		try {
			if(fLDAPConnection != null && fLDAPConnection.isConnected()) {
				fLDAPConnection.disconnect();
			}
			fLDAPConstraints = new LDAPSearchConstraints();
			fLDAPConnection = new LDAPConnection();
	        Configuration cfg = (Configuration)fConfiguration.getAgentCustomConfiguration();
			String monitoredHost = fConfiguration.getMonitoredHost();
	        String loginDN = cfg.getString(Configuration.USERNAME);
	        String password = cfg.getString(Configuration.PASSWORD);
	        int ldapPort = cfg.getInt(Configuration.PORT);
	        int timeout = cfg.getInt(Configuration.TIMEOUT);
			fLDAPConstraints.setTimeLimit(timeout) ;

			// Connect to LDAP server or throw exception here
	        int ldapVersion  = LDAPConnection.LDAP_V3;
	    	fLDAPConnection.connect(monitoredHost, ldapPort);
			fLDAPConnection.bind(ldapVersion, loginDN, password.getBytes("UTF8"));
		} catch(LDAPException e) {
			throw new RMSException(e);
		}
	}


	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#deactivate()
	 */
	public synchronized void deactivate() throws Throwable {
		if(fLDAPConnection != null && fLDAPConnection.isConnected()) {
			try {
				fLDAPConnection.disconnect();
			} catch(Exception e) {
				;
			}
		}
		super.deactivate();
	}

}
