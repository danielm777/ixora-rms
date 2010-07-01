/**
 * 26-Jul-2005
 */
package com.ixora.rms.agents.sapnw.v7_1;

import java.io.File;

import javax.naming.Context;

import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.jmx.JMXAbstractAgent;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.JMXEntityRoot;
import com.ixora.rms.agents.sapnw.messages.Msg;
import com.ixora.rms.exception.InvalidConfiguration;

/**
 * @author Daniel Moraru
 */
public class SAPNetWeaverAgent extends JMXAbstractAgent {

	/**
	 * @param agentId
	 * @param listener
	 */
	public SAPNetWeaverAgent(AgentId agentId, Listener listener) {
		super(agentId, listener);
		init(new JMXEntityRoot((JMXAgentExecutionContext)fContext));
	}
	
	private void init(JMXEntityRoot root) {
		fRootEntity = root;
		fEnvMap.put(Context.INITIAL_CONTEXT_FACTORY, "com.sap.engine.services.jndi.InitialContextFactoryImpl");
	}

	protected void configValidateCustom() throws InvalidConfiguration {
		super.configValidateCustom();
        String sapHome = fConfiguration.getAgentCustomConfiguration().getString(Configuration.ROOT_FOLDER);
        File f = new File(sapHome);
        if(!f.exists()) {
            throw new InvalidConfiguration(Msg.ERROR_SAPNW_NOT_INSTALLED_ON_HOST, fConfiguration.getDeploymentHost());
        }
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.jsr160.JMXJSR160AbstractAgent#configCustomChanged()
	 */
	protected void configCustomChanged() throws InvalidConfiguration, Throwable {
		fEnvMap.put(Context.SECURITY_PRINCIPAL, fConfiguration.getAgentCustomConfiguration().getString(Configuration.USERNAME));
		fEnvMap.put(Context.SECURITY_CREDENTIALS, fConfiguration.getAgentCustomConfiguration().getString(Configuration.PASSWORD));
		super.configCustomChanged();
	}

	protected String getDomainFilter() {
		return "*";
	}
	
	protected String getKeyFilter(String domain) {
		return "*";
	}

}
