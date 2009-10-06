/*
 * Created on 04-Jan-2005
 */
package com.ixora.rms.agents.providers;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ixora.rms.HostId;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDataBufferImpl;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityDescriptorImpl;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.Agent;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentDataBufferImpl;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgent;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.providers.parsers.MonitoringDataParser;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.InvalidEntity;
import com.ixora.rms.exception.ParserIsMissing;
import com.ixora.rms.exception.ProviderConfigurationTokenReplacementError;
import com.ixora.rms.exception.ProviderError;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.ProviderConfiguration;
import com.ixora.rms.providers.ProviderCustomConfiguration;
import com.ixora.rms.providers.ProviderDataBuffer;
import com.ixora.rms.providers.ProviderId;
import com.ixora.rms.providers.ProviderState;
import com.ixora.rms.providers.ProvidersManager;
import com.ixora.rms.providers.exception.InvalidProviderConfiguration;
import com.ixora.rms.providers.exception.ProviderNotActivated;
import com.ixora.rms.providers.exception.ProviderNotInstalled;
import com.ixora.rms.providers.parsers.Parser;
import com.ixora.rms.providers.parsers.exception.ParserException;
import com.ixora.rms.repository.ParserInstallationData;
import com.ixora.rms.repository.ParserInstance;
import com.ixora.rms.repository.ParserRepositoryManager;
import com.ixora.rms.repository.ProviderInstallationData;
import com.ixora.rms.repository.ProviderInstance;
import com.ixora.rms.repository.ProviderInstanceMap;
import com.ixora.rms.repository.ProviderInstanceRepositoryManager;
import com.ixora.rms.repository.ProviderRepositoryManager;
import com.ixora.rms.services.ProvidersManagerService;

/**
 * The agent shadow is a class which is paired with every activated agent
 * and it handles provider based data on behalf of the paired agent.
 * @author Daniel Moraru
 */
public final class AgentShadow extends AbstractAgent {
	/** Logger */
	private static final AppLogger sLogger = AppLoggerFactory.getLogger(AgentShadow.class);

	/**
	 * Listener.
	 */
	public interface AgentShadowListener {
		/**
		 * Invoked when the state of a provider owned by this agent shadow has changed.
		 * @param host
		 * @param agentId
		 * @param providerInstanceName
		 * @param state
		 * @param t
		 */
		void providerStateChanged(String host,
				AgentId agentId, String providerInstanceName, ProviderState state, Throwable t);
	}

	/** Providers for this agent */
	private Map<ProviderId, ProviderData> fProviders;
	/** Provider repository */
	private ProviderRepositoryManager fProviderRepository;
	/** Parser repository */
	private ParserRepositoryManager fParserRepository;
	/** Managers for providers */
	private ProvidersManager fProvidersManager;
	/** Event handler */
	private EventHandler fEventHandler;
	/** Listener */
	private AgentShadowListener fAgentShadowListener;

	/**
	 * Provider data.
	 */
	private final static class ProviderData {
		String providerInstanceName;
		ProviderId providerId;
		Parser parser;
		ProviderCustomConfiguration unsubstitutedCustomProviderConfiguration;
		ProviderCustomConfiguration currentCustomProviderConfiguration;
		boolean entitySamplingInterval;
		ProviderConfiguration currentConfiguration;

		public ProviderData(String providerInstanceName, ProviderId pid,
				Parser parser, ProviderCustomConfiguration unsubstitutedConf,
				ProviderCustomConfiguration currentCustomConf,
				ProviderConfiguration currentConf) {
			this.providerInstanceName = providerInstanceName;
			this.providerId = pid;
			this.parser = parser;
			this.unsubstitutedCustomProviderConfiguration = unsubstitutedConf;
			this.currentCustomProviderConfiguration = currentCustomConf;
			this.entitySamplingInterval = false;
			this.currentConfiguration = currentConf;
		}
	}

	/** Event handler */
	private final class EventHandler implements MonitoringDataParser.Listener,
		ProvidersManagerService.Listener {
		/**
		 * @see com.ixora.rms.agents.providers.parsers.MonitoringDataParser.Listener#newEntities(com.ixora.rms.providers.ProviderId, com.ixora.rms.EntityDescriptor[])
		 */
		public void newEntities(ProviderId pid, EntityDescriptor[] descs) {
			handleNewEntities(pid, descs);
		}
		/**
		 * @see com.ixora.rms.agents.providers.parsers.MonitoringDataParser.Listener#expiredEntities(com.ixora.rms.providers.ProviderId, com.ixora.rms.EntityId[])
		 */
		public void expiredEntities(ProviderId pid, EntityId[] eids) {
			handleExpiredEntities(pid, eids);
		}
		/**
		 * @see com.ixora.rms.services.ProvidersManagerService.Listener#providerStateChanged(com.ixora.rms.providers.ProviderId, com.ixora.rms.internal.providers.ProviderState, java.lang.Throwable)
		 */
		public void providerStateChanged(ProviderId provider, ProviderState state, Throwable err) {
			handleProviderStateChanged(provider, state, err);
		}
		/**
		 * @see com.ixora.rms.services.ProvidersManagerService.Listener#data(com.ixora.rms.providers.ProviderDataBuffer[])
		 */
		public void data(ProviderDataBuffer[] buff) {
			handleProviderData(buff);
		}
		/**
		 * @see com.ixora.rms.agents.providers.parsers.MonitoringDataParser.Listener#sampleEnded(ProviderId pid)
		 */
		public void sampleEnded(ProviderId pid) {
			handleSampleEnded(pid);
		}
		/**
		 * @see com.ixora.rms.agents.providers.parsers.MonitoringDataParser.Listener#sampleData(com.ixora.rms.providers.ProviderId, com.ixora.rms.EntityId, java.util.List, java.util.List)
		 */
		public void sampleData(ProviderId pid, EntityId eid, List<CounterId> counters, List<CounterValue> values) {
			handleSampleData(pid, eid, counters, values);
		}
	}

	/**
	 * This constructor will be invoked when an agent is activated and only if the
	 * agent has providers defined.
	 * @param providerRepository
	 * @param parserRepository
	 * @param providersManager
	 * @param agentConf
	 * @param agentIsRemote
	 * @param host
	 * @param agentInstallationId
	 * @param agentListener
	 * @param providers
	 * @throws Throwable
	 * @throws InvalidConfiguration
	 */
	public AgentShadow(
			ProviderRepositoryManager providerRepository,
			ParserRepositoryManager parserRepository,
			ProvidersManager providersManager,
			ProviderInstanceRepositoryManager providerInstanceRepository,
			AgentConfiguration agentConf,
			boolean agentIsRemote,
			String host,
			String agentInstallationId,
			AgentId agentId,
			AgentShadowListener listener, Agent.Listener agentListener) throws InvalidConfiguration, Throwable {
		super(agentId, agentListener, false);
		fAgentShadowListener = listener;
		fRootEntity = new AgentShadowRootEntity(fContext);
		fEventHandler = new EventHandler();
		fAgentId = agentId;
		fConfiguration = agentConf;
		fProviders = new HashMap<ProviderId, ProviderData>();
		fProviderRepository = providerRepository;
		fParserRepository = parserRepository;
		fProvidersManager = providersManager;
		ProviderInstanceMap pis = providerInstanceRepository.getAgentProviderInstances(agentInstallationId);
		String[] selectedOptionalProviders = fConfiguration.getProviderInstances();
		if(selectedOptionalProviders != null) {
			Arrays.sort(selectedOptionalProviders);
		}
		if(pis != null) {
			 Collection<ProviderInstance> providers = pis.getForAgentVersion(fConfiguration.getSystemUnderObservationVersion());
             if(!Utils.isEmptyCollection(providers)) {
				for(ProviderInstance pid : providers) {
					String providerName = pid.getProviderName();
					String providerInstanceName = pid.getInstanceName();
					if(selectedOptionalProviders == null && pid.isOptional()) {
						continue;
					}
					if(selectedOptionalProviders != null && pid.isOptional()
							&& (Arrays.binarySearch(selectedOptionalProviders, providerInstanceName) < 0)) {
						continue;
					}
					ProviderInstallationData pinstd = fProviderRepository.getInstalledProviders()
						.get(providerName);
					if(pinstd == null) {
						throw new ProviderNotInstalled(providerName);
					}

					// create assigned parser
					ParserInstance parserInstance = pid.getParserInstance();
					String parserName = parserInstance.getParserName();
					ParserInstallationData parserInstallData = fParserRepository
							.getInstalledParsers().get(parserName);
					if(parserInstallData == null) {
						throw new ParserIsMissing(parserName);
					}
					String parserClass = parserInstallData.getParserClass();
					Class c = Class.forName(parserClass);
					MonitoringDataParser parser = (MonitoringDataParser)c.newInstance();
					parser.setListener(fEventHandler);
					parser.setRules(parserInstance.getRules());

					// activate provider
					boolean providerIsRemote;
					if(pid.inheritsLocationFromAgent()) {
						providerIsRemote = agentIsRemote;
					} else {
						providerIsRemote = pid.isRemote();
					}

					// make a copy of the unsubstituted custom provider configuration
					ProviderCustomConfiguration unsubstitutedProviderConfiguration =
							(ProviderCustomConfiguration)pid.getConfiguration().getProviderCustomConfiguration().clone();

					// build provider configuration, this will modify pid.getConfiuration()
					ProviderConfiguration providerConf = createProviderConfig(
							pid.getConfiguration().getProviderCustomConfiguration(), agentConf, null);
					providerConf.setUsePrivateCollector(pid.getConfiguration().usePrivateCollector());
					ProviderId providerId = fProvidersManager.installProvider(
							new HostId(host),
							pinstd.getProviderName(),
							providerConf, providerIsRemote);

					// everything ok, create provider data
					ProviderData pd = new ProviderData(
							pid.getInstanceName(), providerId, parser,
							unsubstitutedProviderConfiguration,
							(ProviderCustomConfiguration)providerConf.getCustom(),
							providerConf);
					fProviders.put(providerId, pd);

					// create entities
					Map<EntityId, EntityDescriptor> entities = pid.getEntityDescriptors();
					parser.setEntityDescriptors(entities);
					((AgentShadowRootEntity)this.fRootEntity).populateFromDescriptors(parser, providerId, entities);
					parser.setEntityDescriptors(entities);
					parser.setProviderId(providerId);
				}
			}
		}
		// apply the configuration for the super class
		super.configure(agentConf);
		fProvidersManager.addListener(fEventHandler);
	}

	/**
	 * @param pid
	 * @param agentConf
	 * @return
	 * @throws ProviderConfigurationTokenReplacementError
	 */
	private ProviderConfiguration createProviderConfig(
			ProviderCustomConfiguration pconf,
			AgentConfiguration agentConf,
			EntityConfiguration entityConf) throws ProviderConfigurationTokenReplacementError {
		ProviderConfiguration providerConf = new ProviderConfiguration();
		providerConf.setHost(agentConf.getMonitoredHost());
		if(pconf != null) {
			ProviderConfigurationProcessor.process(agentConf, entityConf, pconf);
			providerConf.setCustom(pconf);
		}
		if(entityConf == null) {
			providerConf.setGlobalSamplingInterval(agentConf.isGlobalSamplingInterval());
			providerConf.setSamplingInterval(agentConf.getSamplingInterval());
		} else {
			providerConf.setGlobalSamplingInterval(entityConf.isGlobalSamplingInterval());
			providerConf.setSamplingInterval(entityConf.getSamplingInterval());
		}
		return providerConf;
	}


	/**
	 * @see com.ixora.rms.agents.Agent#configure(com.ixora.rms.agents.AgentConfiguration)
	 */
	public synchronized EntityDescriptorTree configure(AgentConfiguration newConf)
			throws InvalidConfiguration, Throwable {
		super.configure(newConf);
		// configure all providers
		for(ProviderData pd : fProviders.values()) {
			// configure providers only if they haven't being reconfigured
			// through their entities
			if(!pd.entitySamplingInterval) {
				configureProvider(pd, fConfiguration, null);
			}
		}
		return getEntities(null, newConf.hasRecursiveSettings(), true);
	}

	/**
	 * @param pd
	 * @param aConf
	 * @param eConf
	 * @throws RMSException
	 * @throws RemoteException
	 * @throws ProviderNotActivated
	 * @throws InvalidProviderConfiguration
	 */
	private void configureProvider(ProviderData pd, AgentConfiguration aConf, EntityConfiguration eConf) throws InvalidProviderConfiguration, ProviderNotActivated, RemoteException, RMSException {
		// we need to modify the custom configuration if there substitution tags are used
		// make a copy of the unsubstituted config which will be modified by createProviderConfig()
		ProviderCustomConfiguration newCustomProviderConfiguration = (ProviderCustomConfiguration)
				pd.unsubstitutedCustomProviderConfiguration.clone();
		ProviderConfiguration provConf = createProviderConfig(newCustomProviderConfiguration, aConf, eConf);
		if(pd.currentCustomProviderConfiguration.equals(newCustomProviderConfiguration)) {
			// if custom config not changed, don't send it
			provConf.setCustom(null);
		}
		fProvidersManager.configureProvider(pd.providerId, provConf);
		// save this config
		pd.currentCustomProviderConfiguration.apply(newCustomProviderConfiguration);
		pd.currentConfiguration.applyDelta(provConf);
	}

	/**
	 * @see com.ixora.rms.agents.Agent#configureEntity(com.ixora.rms.EntityId, com.ixora.rms.EntityConfiguration)
	 */
	public synchronized EntityDescriptorTree configureEntity(EntityId entity,
			EntityConfiguration conf) throws InvalidEntity,
			InvalidConfiguration, Throwable {
		super.configureEntity(entity, conf);
		// reconfigure provider if the sampling interval has changed
		if(conf.isSamplingIntervalSet()) {
			AgentShadowEntity ase = (AgentShadowEntity)fRootEntity.findEntity(entity, false);
			if(ase != null) {
				ProviderId pid = ase.getProviderId();
				ProviderData pd = fProviders.get(pid);
				if(pd != null) {
					configureProvider(pd, fConfiguration,
							ase.getConfiguration()); // don't forget to get the real entity config, not the delta
					// if it's global sampling give back control to the agent
					// when it comes to setting the sampling time
					pd.entitySamplingInterval = !ase.getConfiguration().isGlobalSamplingInterval();
				}
				// update the sampling time only for all other entities belonging to this provider
				EntityConfiguration newConf = new EntityConfiguration();
				newConf.setGlobalSamplingInterval(conf.isGlobalSamplingInterval());
				newConf.setSamplingInterval(conf.getSamplingInterval());
				((AgentShadowRootEntity)fRootEntity).configureEntitiesForProvider(pid, newConf);
			}
		}
		return getEntities(entity, conf.hasRecursiveSettings(), true);
	}
	/**
	 * @see com.ixora.rms.agents.Agent#deactivate()
	 */
	public synchronized void deactivate() throws Throwable {
		// no need to call super
		Exception lastError = null;
		for(ProviderData pd : fProviders.values()) {
			try {
				fProvidersManager.uninstallProvider(pd.providerId);
			} catch(Exception e) {
				fContext.error(e);
				lastError = e;
			}
		}
		if(lastError != null) {
			throw lastError;
		}
	}

	/**
	 * @see com.ixora.common.Startable#start()
	 */
	public synchronized void start() throws Throwable {
		super.start();
		// start all providers
		for(ProviderData pd : fProviders.values()) {
            try {
                fProvidersManager.startProvider(pd.providerId);
            } catch(Throwable t) {
            	// not a fatal error, keep going...
                // TODO localize
                fContext.error(new RMSException(
                	"Provider " + pd.providerInstanceName
                	+ " failed to start. Error: " + t.getLocalizedMessage()));
            }
		}
	}
	/**
	 * @see com.ixora.common.Startable#stop()
	 */
	public synchronized void stop() throws Throwable {
		super.stop();
		// stop all providers
		Throwable lastError = null;
		for(ProviderData pd : fProviders.values()) {
			try {
				fProvidersManager.stopProvider(pd.providerId);
			} catch(Throwable e) {
				lastError = e;
			}
		}
		if(lastError != null) {
			throw lastError;
		}
	}

	/**
	 * @return true if this shadow provides for this entity
	 * @param eid
	 */
	public synchronized boolean providesForEntity(EntityId eid) {
		try {
			// provides for root
			if(eid == null || eid.equals("root")) {
				return true;
			}
			return this.fRootEntity.isRootFor(eid);
		} catch (Throwable e) {
			// this type of agent shouldn't throw exceptions here
			// so just log it and don't propagate it
			sLogger.error(e);
		}
		return false;
	}

	/**
	 * @see com.ixora.rms.services.ProvidersManagerService.Listener#providerStateChanged(com.ixora.rms.providers.ProviderId, com.ixora.rms.internal.providers.ProviderState, java.lang.Throwable)
	 */
	private synchronized void handleProviderStateChanged(ProviderId provider, ProviderState state, Throwable err) {
		ProviderData pd = fProviders.get(provider);
		if(pd != null) {
			if(state == ProviderState.ERROR) {
				// signal non-fatal error
				this.fContext.error(new ProviderError(pd.providerInstanceName,
						err == null ? "Unknown" : err.getMessage() != null ? err.getMessage() : err.toString()));
			}
			fAgentShadowListener.providerStateChanged(fConfiguration.getMonitoredHost(),
					fAgentId, pd.providerInstanceName, state, err);
		}
	}

	/**
	 * @see com.ixora.rms.services.ProvidersManagerService.Listener#data(com.ixora.rms.providers.ProviderDataBuffer[])
	 */
	// NOTE: Parsers are not allow to have their own threads so synchronizing this method
	// should make it safe not to synchronize the methods handling parser events
	private synchronized void handleProviderData(ProviderDataBuffer[] buffs) {
		for(ProviderDataBuffer buff : buffs) {
			ProviderId provider = buff.getProviderId();
			ProviderData pd = fProviders.get(provider);
			if(pd != null) {
				try {
					pd.parser.parse(buff.getData());
				} catch(ParserException e) {
					// signal non-fatal error
					// build a more meaningful error, add provider name and parser error data
					StringBuffer msg = new StringBuffer(
							"Failed to parse data for provider: ");
					msg.append(pd.providerInstanceName);
					msg.append(" ");
					msg.append(Utils.getNewLine());
					msg.append("Parsing error: ");
					msg.append(e.getLocalizedMessage());
					msg.append(" ");
					msg.append(Utils.getNewLine());
					String data = e.getData();
					if(!Utils.isEmptyString(data)) {
						msg.append("Provider data that caused the error: ");
						msg.append(Utils.getNewLine());
						msg.append(data);
					}
					RMSException error = new RMSException(msg);
					this.fContext.error(error);
				} catch (Throwable e) {
					// signal non-fatal error
					RMSException error = new RMSException(
							"Failed to parse data for provider "
							+ pd.providerInstanceName, e);
					this.fContext.error(error);
				}
			}
		}
	}

	/**
	 * The parser discovered a new entity, add it to this agent's entities.
	 * @param pid
	 * @param descs
	 */
	// Note: Thread safe zone
	private void handleNewEntities(ProviderId pid, EntityDescriptor[] descs) {
        try {
			ProviderData pd = fProviders.get(pid);
			if(pd == null) {
				return;
			}
    		// fire events as seldom as possible so group here descriptors by parent
            // in most cases there will only be one parent entity anyway
            Map<EntityId, List<EntityDescriptor>> parents =
                new HashMap<EntityId, List<EntityDescriptor>>();
            for(EntityDescriptor ed : descs) {
                EntityId parent = ed.getId().getParent();
                List<EntityDescriptor> lst = parents.get(parent);
                if(lst == null) {
                    lst = new LinkedList<EntityDescriptor>();
                    parents.put(parent, lst);
                }
                lst.add(ed);
            }

            for(Map.Entry<EntityId, List<EntityDescriptor>> me : parents.entrySet()) {
    			EntityId parent = me.getKey();
                Entity entity;
                if(AgentShadowRootEntity.ROOT_ID.equals(parent)) {
                    entity = fRootEntity;
                } else {
                    entity = fRootEntity.findEntity(parent, false);
                }
    			if(entity == null) {
    				throw new RMSException("Entity " + parent + " not found for provider "
    						+ pd.providerInstanceName);
    			}

                List<EntityDescriptor> children = me.getValue();
                for(EntityDescriptor ed : children) {
                    // update config data in descriptor which is common between all entities
        			// like sampling interval
                	EntityConfiguration eConf = new EntityConfiguration();
    				eConf.setGlobalSamplingInterval(!pd.entitySamplingInterval);
    				eConf.setSamplingInterval(pd.currentConfiguration.getSamplingInterval());
    				EntityDescriptor newDesc = new EntityDescriptorImpl(
    						ed.getId(),
    						ed.getAlternateName(),
    						ed.getDescription(),
    						ed.getLevel(),
    						ed.getSupportedLevels(),
    						new ArrayList(ed.getCounterDescriptors()),
    						false,
    						eConf,
    						ed.supportsSamplingInterval(),
    						ed.hasChildren());
                    if(entity instanceof AgentShadowEntity) {
                        ((AgentShadowEntity)entity).addChildEntity(new AgentShadowEntity(newDesc, pid, fContext));
                    } else if(entity instanceof AgentShadowRootEntity) {
                        ((AgentShadowRootEntity)entity).addChildEntity(new AgentShadowEntity(newDesc, pid, fContext));
                    }
                }
                // fire now event for parent
                if(entity instanceof AgentShadowEntity) {
                    ((AgentShadowEntity)entity).fireChildrenEntitiesChanged();
                } else if(entity instanceof AgentShadowRootEntity) {
                    ((AgentShadowRootEntity)entity).fireChildrenEntitiesChanged();
                }
    		}
        } catch (Throwable e) {
            fContext.error(e);
        }
	}

	/**
	 * @param pid
	 * @param eids
	 */
	// Note: Thread safe zone
	private void handleExpiredEntities(ProviderId pid, EntityId[] eids) {
        try {
			ProviderData pd = fProviders.get(pid);
			if(pd == null) {
				return;
			}
            // fire events as seldomly as possible so cache here parents
    		Map<EntityId, AgentShadowEntity> parents = new HashMap<EntityId, AgentShadowEntity>();
            for(EntityId eid : eids) {
    			EntityId parent = eid.getParent();
				AgentShadowEntity entity = (AgentShadowEntity)fRootEntity.findEntity(parent, false);
				if(entity == null) {
					throw new RMSException(
							"Parent for entity " + eid + " not found for provider "
							+ pd.providerInstanceName);
				}
				entity.removeChildEntity(eid);
                if(parents.get(parent) == null) {
                    parents.put(parent, entity);
                }
            }
            // fire events for parents
            for(AgentShadowEntity parent : parents.values()) {
                parent.fireChildrenEntitiesChanged();
            }
        } catch (Throwable e) {
            fContext.error(e);
        }
    }

	/**
	 * @param pid
	 */
	// Note: Thread safe zone
	private void handleSampleEnded(ProviderId pid) {
		try {
			AgentDataBufferImpl buffer = new AgentDataBufferImpl();
			buffer.setHost(this.fConfiguration.getMonitoredHost());
			buffer.setAgent(fAgentId);
			List<EntityDataBufferImpl> listEDB = new LinkedList<EntityDataBufferImpl>();
			for(Entity e : fRootEntity.getChildrenEntities()) {
				AgentShadowEntity ase = (AgentShadowEntity)e;
				if(ase.getProviderId().equals(pid)) {
					ase.prepareEntityDataBuffers(listEDB);
				}
			}
			if (listEDB.size() > 0) {
				buffer.setBuffers(listEDB.toArray(new EntityDataBufferImpl[listEDB.size()]));
			}
			else {
				buffer.setBuffers(null);
			}
            if(fSendAgentDescriptor) {
                buffer.setAgentDescriptor(extractDescriptor());
            }
			fListener.receiveDataBuffer(buffer);
		} catch(Throwable t) {
			fContext.error(t);
		}
	}

	/**
	 * @param pid
	 * @param eid
	 * @param counters
	 * @param values
	 */
	// Note: Thread safe zone
	private void handleSampleData(ProviderId pid, EntityId eid, List<CounterId> counters, List<CounterValue> values) {
		try {
			AgentShadowEntity entity = (AgentShadowEntity)fRootEntity.findEntity(eid, false);
			if(entity == null) {
				throw new RMSException("Entity " + eid + " not found");
			}
			entity.setCounterValues(counters, values);
		} catch(Throwable e) {
          //  logger.error(e);
			fContext.error(e);
		}
	}
}
