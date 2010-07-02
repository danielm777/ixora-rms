package com.ixora.rms.agents.sapnw.v7_1;

import java.io.File;
import java.util.Properties;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.naming.Context;

import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.jmx.JMXAbstractAgent;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.JMXConnectionMBeanServer;
import com.ixora.rms.agents.impl.jmx.JMXEntityRoot;
import com.ixora.rms.agents.impl.jmx.MBeanServerProxy;
import com.ixora.rms.agents.sapnw.messages.Msg;
import com.ixora.rms.exception.InvalidConfiguration;
import com.sap.jmx.remote.JmxConnectionFactory;

/**
 * Sap NetWeaver J2EE monitoring agent.
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
		super.configCustomChanged();
		fEnvMap.put(Context.SECURITY_PRINCIPAL, fConfiguration.getAgentCustomConfiguration().getString(Configuration.USERNAME));
		fEnvMap.put(Context.SECURITY_CREDENTIALS, fConfiguration.getAgentCustomConfiguration().getString(Configuration.PASSWORD));
		fEnvMap.put(Context.PROVIDER_URL, fConfiguration.getMonitoredHost() 
				+ ":" + fConfiguration.getAgentCustomConfiguration().getInt(Configuration.P4_PORT));
		Properties props = new Properties();
		props.putAll(fEnvMap);
		MBeanServerConnection conn = JmxConnectionFactory.getMBeanServerConnection(JmxConnectionFactory.PROTOCOL_ENGINE_P4,	props);
		fJMXConnection = new JMXConnectionMBeanServer(MBeanServerProxy.buildProxy(conn));
	}
	
	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getKeyFilter(java.lang.String)
	 */
	protected String getKeyFilter(String domain) {
		if(domain.startsWith("com.sap.default")) {
			return "*";
			// TODO be more selective...
			//return "name=*,j2eeType=SAP_MonitorPerNode,*";
		} else {
			return "*";
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#acceptEntity(javax.management.ObjectName)
	 */
	protected boolean acceptEntity(ObjectName oname) {
		String soname = oname.toString();
		if(soname.startsWith("com.sap.default")) {
			String j2eeType = oname.getKeyProperty("j2eeType");
			if(Utils.isEmptyString(j2eeType) || !j2eeType.equals("SAP_MonitorPerNode")) {
				return false;
			}
			return true;
		} else {
			return true;
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getEntityName(javax.management.ObjectName)
	 */
	protected String getEntityName(ObjectName oname) {
		String soname = oname.toString();
		StringBuilder buff = new StringBuilder();
		if(soname.startsWith("com.sap.default")) {
			String j2eeCluster = oname.getKeyProperty("SAP_J2EECluster");
			if(!Utils.isEmptyString(j2eeCluster)) {
				buff.append(j2eeCluster);
			}
			String j2eeClusterNode = oname.getKeyProperty("SAP_J2EEClusterNode");
			if(!Utils.isEmptyString(j2eeClusterNode)) {
				buff.append("/");
				buff.append(j2eeClusterNode);
			}
			String name = oname.getKeyProperty("name");
			if(!Utils.isEmptyString(name)) {
				// the names start and end in double quotes, remove them
				if(name.startsWith("\"")) {
					name = name.substring(1);
					if(name.endsWith("\"")) {
						name = name.substring(0, name.length()-1);
					}
				}
				buff.append(name);
			}			
		}
		if(buff.length() != 0) {
			return buff.toString();
		}
		return super.getEntityName(oname);
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#sortEntities()
	 */
	protected boolean sortEntities() {
		return true;
	}
	
	
}
