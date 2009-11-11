/*
 * Created on 22-May-2004
 */
package com.ixora.rms.agents.websphere.v50;

import java.awt.image.DataBuffer;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.pmi.PmiConstants;
import com.ibm.websphere.pmi.PmiException;
import com.ibm.websphere.pmi.PmiModuleConfig;
import com.ibm.websphere.pmi.client.CpdCollection;
import com.ibm.websphere.pmi.client.PerfLevelSpec;
import com.ibm.websphere.pmi.client.PmiClient;
import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityId;
import com.ixora.rms.MonitoringLevel;
import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.AgentDataBufferImpl;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgent;
import com.ixora.rms.agents.websphere.PmiClientProxyMgr;
import com.ixora.rms.agents.websphere.v50.proxy.PerfDescriptor;
import com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class WebSphereAgent extends AbstractAgent
	implements PmiConstants {
	/** Agent configuration */
	protected Configuration wsConfig;
	/** Current PMI client */
	protected PmiClientProxy client;
	/** Client process */
	protected PmiClientProxyMgr clientProcess;
	/**
	 * Cached entity configs
	 * Key: EntityId
	 * Value: EntityData
	 */
	protected Map<EntityId, EntityData> entityData;
	/**
	 * Cached Pmi module configs
	 * Key: module name
	 * Value: ModuleData
	 */
	protected Map<String, ModuleData> moduleData;
	/**
	 * Set with enabled performance descriptors.
	 */
	protected Map<EntityId, PerfDescriptor> enabledPerfDescriptors;
    /**
     * Version behaviour.
     */
	protected RelatedVersionBehaviour versionBehaviour;

	/**
	 * Custom execution context.
	 */
	private final class WebSphereContext extends ExecutionContext
		implements WebSphereAgentContext {
		/**
		 * @see com.ixora.rms.agents.websphere.v501.WebSphereContext#getClient()
		 */
		public PmiClientProxy getClient() {
			return client;
		}

		/**
		 * @see com.ixora.rms.agents.websphere.v501.WebSphereContext#getTranslatedText(String, String)
		 */
		public String getTranslatedText(String text, String module) {
			// PmiClient.getNLSValue(String) is deprecated
			// since WebSphere 5.0.1, if I use it here
			// I might be able to use the same agent implementation for
			// 5.0 and 5.0.1
			return PmiClient.getNLSValue(text, module);
		}

		/**
		 * @see com.ixora.rms.agents.websphere.v501.WebSphereContext#getEntityData(com.ixora.rms.EntityId)
		 */
		public EntityData getEntityData(EntityId id) {
			return entityData.get(id);
		}

		/**
		 * @see com.ixora.rms.agents.websphere.v501.WebSphereContext#getModuleData(java.lang.String)
		 */
		public ModuleData getModuleData(String module) {
			return moduleData.get(module);
		}

		/**
		 * @see com.ixora.rms.agents.websphere.v501.WebSphereContext#mapLevel(MonitoringLevel)
		 */
		public int mapLevel(MonitoringLevel l) {
			int k = l.getKey();
			switch(k) {
				case -1: // none
					return LEVEL_NONE;
				case 0: // low
					return LEVEL_LOW;
				case 1: // medium
					return LEVEL_MEDIUM;
				case 2: // high
					return LEVEL_HIGH;
				case 3: // max
					return LEVEL_MAX;
			}
			return -1;
		}

		/**
		 * @see com.ixora.rms.agents.websphere.v501.WebSphereContext#mapLevel(int)
		 */
		public MonitoringLevel mapLevel(int l) {
			switch(l) {
				case LEVEL_NONE: // none
					return MonitoringLevel.NONE;
				case LEVEL_LOW: // low
					return MonitoringLevel.LOW;
				case LEVEL_MEDIUM: // medium
					return MonitoringLevel.MEDIUM;
				case LEVEL_HIGH: // high
					return MonitoringLevel.HIGH;
				case LEVEL_MAX: // max
					return MonitoringLevel.MAXIMUM;
			}
			return null;
		}

		/**
		 * @see com.ixora.rms.agents.websphere.v50.WebSphereAgentContext#enablePerfDescriptor(com.ixora.rms.EntityId, com.ixora.rms.agents.websphere.v501.proxy.PerfDescriptor)
		 */
		public void enablePerfDescriptor(EntityId eid, PerfDescriptor desc) {
			enabledPerfDescriptors.put(eid, desc);
		}

		/**
		 * @see com.ixora.rms.agents.websphere.v50.WebSphereAgentContext#disablePerfDescriptor(com.ixora.rms.EntityId, com.ixora.rms.agents.websphere.v501.proxy.PerfDescriptor)
		 */
		public void disablePerfDescriptor(EntityId eid, PerfDescriptor desc) {
			enabledPerfDescriptors.remove(eid);
		}

		/**
		 * @throws PmiException
		 * @throws RemoteException
		 * @see com.ixora.rms.agents.websphere.v50.WebSphereAgentContext#rebuildInfoRepository()
		 */
		public void rebuildInfoRepository() throws RemoteException, PmiException {
			buildInfoRepository();
		}

		/**
		 * @see com.ixora.rms.agents.websphere.v50.WebSphereAgentContext#getNodes()
		 */
		public PerfDescriptor[] getNodes() throws RemoteException, PmiException {
			return getWASNodes();
		}

		/**
		 * @see com.ixora.rms.agents.websphere.v50.WebSphereAgentContext#getServers(java.lang.String)
		 */
		public PerfDescriptor[] getServers(String node) throws RemoteException, PmiException {
			return getWASServers(node);
		}

        /**
         * @throws Throwable
         * @throws InvalidConfiguration
         * @see com.ixora.rms.agents.websphere.v50.WebSphereAgentContext#reconnect()
         */
        public void reconnect() throws InvalidConfiguration, Throwable {
            connect();
        }

        /**
         * @see com.ixora.rms.agents.websphere.v50.WebSphereAgentContext#getVersionBehaviour()
         */
        public RelatedVersionBehaviour getVersionBehaviour() {
            return versionBehaviour;
        }
	}

	/**
	 * Constructor.
	 * @throws Throwable
	 */
	public WebSphereAgent(AgentId agentId, Listener listener) throws Throwable {
        super(agentId, listener);
		replaceContext(new WebSphereContext());
		entityData = new HashMap();
		moduleData = new HashMap();
		enabledPerfDescriptors = new HashMap<EntityId, PerfDescriptor>();
		fRootEntity = new WebSphereRootEntity(getWASContext());
        versionBehaviour = new VersionBehaviour();
	}

	/**
	 * Builds a repository with info about all entities
	 * discovered at this point. This info will be queried
	 * by entities when needed to complements their descriptors.
	 * @throws RemoteException
	 * @throws PmiException
	 */
	private void buildInfoRepository() throws RemoteException, PmiException {
		PerfDescriptor[] members = getWASNodes();
		if(members == null || members.length == 0) {
			return;
		}
		// get PerfLevelSpec with the monitoring level
		PerfDescriptor pd;
		String node;
		String server;
		for(int i = 0; i < members.length; i++) {
			try {
				pd = members[i];
				node = pd.getNodeName();
				PerfDescriptor[] servers = getWASServers(node);
				if(servers != null && servers.length > 0) {
					for (int j = 0; j < servers.length; j++) {
						server = servers[j].getServerName();
						try {
							PerfLevelSpec[] specs =
								client.getInstrumentationLevel(node, server);
							PerfLevelSpec spec;
							if(specs != null) {
								for(int k = 0; k < specs.length; k++) {
									spec = specs[k];
									String[] path = spec.getPath();
									// replace the 'pmi' root with 'root'
									String[] fullPath = new String[path.length + 2];
									fullPath[0] = "root";
									fullPath[1] = node;
									fullPath[2] = server;
									System.arraycopy(path, 1, fullPath, 3, path.length - 1);
									EntityId eid = new EntityId(fullPath);
									this.entityData.put(eid,
										new EntityData(spec, spec.getLevel(),
												getWASContext().mapLevel(spec.getLevel())));
								}
							}
						} catch(RemoteException e) {
							fContext.error(e);
						}
					}
				}
			} catch(Exception e) {
				fContext.error(e);
			}
		}
		// get pmi modules
		PmiModuleConfig[] pmiModules = client.getConfigs();
		PmiModuleConfig pmg;
		for(int i = 0; i < pmiModules.length; i++) {
			pmg = pmiModules[i];
			moduleData.put(pmg.getShortName(), new ModuleData(pmg));
		}
	}

	/**
	 * Creates the admin client.
	 * @throws Throwable if it can't create the client
	 */
	protected void connect() throws Throwable {
		wsConfig = (Configuration)fConfiguration.getCustom();
		Properties props = new Properties();
        String wasHost = wsConfig.getString(Configuration.WAS_HOST);
        if(Utils.isEmptyString(wasHost)) {
            wasHost = fConfiguration.getMonitoredHost();
        }
		props.setProperty(AdminClient.CONNECTOR_HOST, wasHost);
		props.setProperty(AdminClient.CONNECTOR_PORT, String.valueOf(wsConfig.getInt(Configuration.PORT)));

		String username = (String)wsConfig.getObject(Configuration.USERNAME);
		if(username != null) {
		    props.setProperty(AdminClient.USERNAME, username);
		}
		String password = (String)wsConfig.getObject(Configuration.PASSWORD);
		if(password != null) {
		    props.setProperty(AdminClient.PASSWORD, wsConfig.getString(Configuration.PASSWORD));
		}

		// transport
		props.setProperty(AdminClient.CONNECTOR_TYPE, wsConfig.getString(Configuration.CONNECTOR_TYPE));
		// security
		props.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, String.valueOf(wsConfig.getBoolean(Configuration.SECURITY_ENABLED)));
		String wasHome = wsConfig.getString(Configuration.WAS_HOME);
		props.setProperty("javax.net.ssl.trustStore",  wasHome + wsConfig.getString(Configuration.TRUST_STORE));
		props.setProperty("javax.net.ssl.keyStore", wasHome + wsConfig.getString(Configuration.KEY_STORE));
		props.setProperty("javax.net.ssl.trustStorePassword", wsConfig.getString(Configuration.TRUST_STORE_PASSWORD));
		props.setProperty("javax.net.ssl.keyStorePassword", wsConfig.getString(Configuration.KEY_STORE_PASSWORD));
		props.setProperty("com.ibm.SOAP.ConfigURL", "file:/" + wasHome + "/properties/soap.client.props");
		try {
			if(clientProcess != null) {
				clientProcess.stopPmiClientProxy();
			}
			clientProcess = new PmiClientProxyMgr();
		    client = clientProcess.startPmiClientProxy(fConfiguration.getMonitoredHost(),
		    		fConfiguration.getDeploymentHost(),
		    		wsConfig.getInt(Configuration.PROXY_RMI_PORT),
					wsConfig, props, versionBehaviour.getProxyClass());
		} catch(RMSException e) {
			throw e;
		} catch(Exception e) {
            throw e;
        }
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#configCustomChanged()
	 */
	protected void configCustomChanged() throws InvalidConfiguration, Throwable {
		connect();
	    // init nodes and servers as they are needed out of the box
		((WebSphereRootEntity)fRootEntity).initNodesAndServers();
	}

	/**
	 * @see com.ixora.rms.agents.Agent#deactivate()
	 */
	public synchronized void deactivate() throws Throwable {
		super.deactivate();
		if(client != null) {
			try {
				client.end();
			} catch(Exception e) {
			}
			client = null;
		}
		if(clientProcess != null) {
			clientProcess.stopPmiClientProxy();
			clientProcess = null;
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#configMonitoringLevelChanged()
	 */
	protected void configMonitoringLevelChanged() throws Throwable {
		((WebSphereRootEntity)fRootEntity).changeMonitoringLevel(fConfiguration.getMonitoringLevel());
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#configValidateCustom(com.ixora.rms.agents.AgentCustomConfiguration)
	 */
	protected void configValidateCustom(AgentCustomConfiguration c)
			throws InvalidConfiguration {
		if(c == null) {
			InvalidConfiguration ex = new InvalidConfiguration("Custom configuration missing");
			// that should never happen
			ex.setIsInternalAppError();
			throw ex;
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#prepareBuffer(DataBuffer)
	 */
	protected void prepareBuffer(AgentDataBufferImpl buffer) throws Throwable {
		// get data first
		CpdCollection[] data = getCpdCollection();
		if(data == null) {
			return;
		}
		((WebSphereRootEntity)fRootEntity).dataAvailable(data);
		// call super
		super.prepareBuffer(buffer);
	}

    /**
     * @return
     * @throws PmiException
     * @throws RemoteException
     */
    protected CpdCollection[] getCpdCollection() throws RemoteException, PmiException {
        return client.gets(enabledPerfDescriptors.values().toArray(
                new PerfDescriptor[enabledPerfDescriptors.size()]), false);
    }

	/**
	 * @return the WAS context
	 */
	private WebSphereAgentContext getWASContext() {
		return (WebSphereAgentContext)fContext;
	}

	/**
	 * @return
	 * @throws PmiException
	 * @throws RemoteException
	 * @throws RemoteException
	 * @throws PmiException
	 */
	private PerfDescriptor[] getWASNodes() throws RemoteException, PmiException {
		return ((WebSphereRootEntity)fRootEntity).getPerfDescriptorsForNodes();
	}

	/**
	 * @param node
	 * @return
	 * @throws PmiException
	 * @throws RemoteException
	 * @throws PmiException
	 * @throws RemoteException
	 */
	private PerfDescriptor[] getWASServers(String nodeName) throws RemoteException, PmiException {
		return ((WebSphereRootEntity)fRootEntity).getPerfDescriptorsForServers(nodeName);
	}
}
