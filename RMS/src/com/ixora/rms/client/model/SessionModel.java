/*
 * Created on 07-Jul-2004
 */
package com.ixora.rms.client.model;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.ixora.rms.HostInformation;
import com.ixora.rms.HostState;
import com.ixora.rms.ResourceId;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.remote.ServiceState;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityDescriptorImpl;
import com.ixora.rms.EntityDescriptorImplMutable;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentConfigurationTuple;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.AgentState;
import com.ixora.rms.client.AgentInstanceData;
import com.ixora.rms.client.session.AgentDetails;
import com.ixora.rms.client.session.EntityDetails;
import com.ixora.rms.client.session.HostDetails;
import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.ProviderState;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.repository.Dashboard;
import com.ixora.rms.repository.DashboardId;
import com.ixora.rms.repository.DashboardMap;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.repository.DataViewId;
import com.ixora.rms.repository.DataViewMap;
import com.ixora.rms.repository.ProviderInstanceMap;
import com.ixora.rms.services.AgentRepositoryService;
import com.ixora.rms.services.DashboardRepositoryService;
import com.ixora.rms.services.DataViewRepositoryService;
import com.ixora.rms.services.HostMonitorService;
import com.ixora.rms.services.MonitoringSessionService;
import com.ixora.rms.services.ProviderInstanceRepositoryService;

/**
 * This class represents the tree with hosts, agents, entities
 * and associated data for a monitoring session. Should be used as
 * a model by all client implementations.
 * Note: this class needs to be protected against multiple thread access as it
 * implements ResourceInfoLocator whose methods are invoked in a lot places (specifically in
 * the data engine) on various threads.
 * @author Daniel Moraru
 */
public class SessionModel
	extends DefaultTreeModel
		implements ResourceInfoLocator {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(SessionModel.class);

	/**
	 * Listener interface for rough grained events.
	 */
	public interface RoughListener {
		public static final int CHANGE_HOSTS = 0;
		public static final int CHANGE_AGENTS = 1;
		public static final int CHANGE_ENTITIES = 2;
		public static final int CHANGE_TYPE_ADDED = 0;
		public static final int CHANGE_TYPE_REMOVED = 1;
		public static final int CHANGE_TYPE_UPDATED = 2;

	    /**
	     * Invoked when changes have been made to the model. These events offer a rougher
	     * level of granularity.
	     * @param ridChanged the id of the resource that changed
	     * @param change one of CHANGE_HOSTS, CHANGE_AGENTS or CHANGE_ENTITIES
	     * @param changeType one of CHANGE_TYPE_ADDED, CHANGE_TYPE_REMOVED, CHANGE_TYPE_UPDATED
	     */
	    void modelChanged(ResourceId[] ridChanged, int change, int changeType);
	}

	/**
	 * Listener interface for fine grained events.
	 */
	public interface FineListener {
		/**
	     * Invoked when the show identifiers flag changes.
	     * @param value
	     */
	    void showIdentifiersChanged(boolean value);
	    /**
	     * Invoked when the entity represented by the given node
	     * has changed.
	     * @param en
	     */
	    void entityUpdated(EntityNode en);
	    /**
	     * Invoked when an entity has been added to the model.
	     * @param en
	     */
	    void entityAdded(EntityNode en);
	    /**
	     * Invoked when an entity has been removed from the model.
	     * @param en
	     */
	    void entityRemoved(EntityNode en);
	    /**
	     * Invoked when an agent has been added to the model.
	     * @param en
	     */
	    void agentAdded(AgentNode an);
	    /**
	     * Invoked when an agent has been removed from the model.
	     * @param en
	     */
	    void agentRemoved(AgentNode an);
	    /**
	     * Invoked when an agent has been updated in the model.
	     * @param en
	     */
	    void agentUpdated(AgentNode an);
	    /**
	     * Invoked when an host has been added to the model.
	     * @param en
	     */
	    void hostAdded(HostNode hn);
	    /**
	     * Invoked when an entity has been removed from the model.
	     * @param en
	     */
	    void hostRemoved(HostNode hn);
	    /**
	     * Invoked when an host has been updated in the model.
	     * @param en
	     */
	    void hostUpdated(HostNode hn);
	    /**
	     * Invoked when the session has been updated.
	     * @param sn
	     */
	    void sessionUpdated(SessionNode sn);
	}

	/**
	 * Event handler.
	 */
	private class EventHandler implements
				HostMonitorService.Listener,
					MonitoringSessionService.Listener {
		/**
		 * @see com.ixora.rms.services.HostMonitorService.Listener#hostStateChanged(java.lang.String, int, com.ixora.common.remote.ServiceState)
		 */
		public void hostStateChanged(String host, int serviceID, ServiceState state) {
			handleHostStateChanged(host, serviceID, state);
		}
		/**
		 * @see com.ixora.rms.services.HostMonitorService.Listener#updateHostInfo(java.lang.String, com.ixora.rms.struct.HostInformation)
		 */
		public void updateHostInfo(String host, HostInformation info) {
			handleUpdateHostInfo(host, info);
		}
		/**
		 * @see com.ixora.rms.services.MonitoringSessionService.Listener#agentStateChanged(java.lang.String, AgentId, com.ixora.rms.AgentCategory, java.lang.Throwable)
		 */
		public void agentStateChanged(String host, AgentId agentId, AgentState state, Throwable e) {
			handleAgentStateChanged(host, agentId, state, e);

		}
		/**
		 * @see com.ixora.rms.services.MonitoringSessionService.Listener#entitiesChanged(java.lang.String, java.lang.String, com.ixora.rms.EntityDescriptorTree)
		 */
		public void entitiesChanged(String host, AgentId agentId, EntityDescriptorTree entities) {
			handleEntitiesChanged(host, agentId, entities);
		}
		/**
		 * @see com.ixora.rms.services.MonitoringSessionService.Listener#agentNonFatalError(java.lang.String, java.lang.String, java.lang.Throwable)
		 */
		public void agentNonFatalError(String host, AgentId agentId, Throwable t) {
			handleAgentNonFatalError(host, agentId, t);
		}

		/**
		 * @see com.ixora.rms.services.MonitoringSessionService.Listener#providerStateChanged(java.lang.String, com.ixora.rms.internal.agents.AgentId, java.lang.String, com.ixora.rms.internal.providers.ProviderState, java.lang.Throwable)
		 */
		public void providerStateChanged(String host, AgentId agentId, String providerInstanceName, ProviderState state, Throwable e) {
			handleProviderStateChanged(host, agentId, providerInstanceName, state, e);
		}
		/**
		 * @see com.ixora.rms.services.HostMonitorService.Listener#aboutToRemoveHost(java.lang.String)
		 */
		public void aboutToRemoveHost(String host) {
		}
	}

	/** Monitoring session, see contructor */
	private MonitoringSessionService rmsMonitoringSession;
	/** Host monitor, see constructor */
	private HostMonitorService rmsHostMonitor;
	/** DashboardRepositoryService, see constructor */
	private DashboardRepositoryService rmsDashboardRepository;
	/** DataViewRepositoryService, see constructor */
	private DataViewRepositoryService rmsDataViewRepository;
	/** DataViewRepositoryService, see constructor */
	private ProviderInstanceRepositoryService rmsProviderInstanceRepository;
	/** Event handler */
	private EventHandler eventHandler;
	/** Counter model helper */
	private CounterModelHelper counterHelper;
	/** Query model helper */
	private QueryModelHelper queryHelper;
	/** Dashboard model helper */
	private DashboardModelHelper dashboardHelper;
	/** Data view model helper */
	private DataViewModelHelper dataViewHelper;
	/** Provider instance model helper */
	private ProviderInstanceModelHelper providerInstanceHelper;
	/** Log replay mode flag */
	private boolean logReplayMode;
	/** Show identifiers or translated names */
	private boolean showIdentifiers;
	/** Whether or not to filter out unselected items */
	private boolean filterOutUnselectedItems;
	/** Fine grained listeners */
	private List<FineListener> listenersFine;
	/** Rough grained listeners */
	private List<RoughListener> listenersRough;

	/**
	 * Constructor used by the log replay view.
	 * @param qgrs needed to load resource dashboards as resources are added to the model
	 * @param dvrs needed to load resource data views as resources are added to the model
	 * @param pirs needed to load agent provider instances as agents are loaded to the model
	 */
	public SessionModel(
	        AgentRepositoryService ars,
			DashboardRepositoryService qgrs,
			DataViewRepositoryService dvrs) {
	    this(null, null, qgrs, dvrs, null);
	    this.logReplayMode = true;
	}

	/**
	 * Constructor used by the live session view.
	 * @param mss needed to aggressively search for resources
	 * @param hms needed to update the host states and info
	 * @param qgrs needed to load resource dashboards as resources are added to the model
	 * @param dvrs needed to load resource data views as resources are added to the model
	 */
	public SessionModel(
			MonitoringSessionService mss,
			HostMonitorService hms,
			DashboardRepositoryService qgrs,
			DataViewRepositoryService dvrs,
			ProviderInstanceRepositoryService pirs) {
		super(new SessionNode());
		getSessionNode().setModel(this);
		queryHelper = new QueryModelHelperImpl(this);
		dashboardHelper = new DashboardModelHelperImpl(this);
		dataViewHelper = new DataViewModelHelperImpl(this);
		counterHelper = new CounterModelHelperImpl(this);
		providerInstanceHelper = new ProviderInstanceModelHelperImpl(this);
		eventHandler = new EventHandler();
		if(hms != null) {
		    hms.addListener(eventHandler);
		}
		if(mss != null) {
		    mss.addListener(eventHandler);
		}
		this.rmsMonitoringSession = mss;
		this.rmsHostMonitor = hms;
		this.rmsDashboardRepository = qgrs;
		this.rmsDataViewRepository = dvrs;
		this.rmsProviderInstanceRepository = pirs;
		if(!logReplayMode) {
		    loadArtefactsForNode(getSessionNode());
		}
		this.listenersFine = new LinkedList<FineListener>();
		this.listenersRough = new LinkedList<RoughListener>();
	}

	/**
	 * Adds a listener.
	 * @param l
	 */
	public void addListener(FineListener l) {
	    if(!listenersFine.contains(l)) {
	        listenersFine.add(l);
	    }
	}

	/**
	 * Removes a listener.
	 * @param l
	 */
	public void removeListener(FineListener l) {
       listenersFine.remove(l);
	}

	/**
	 * Adds a listener.
	 * @param l
	 */
	public void addListener(RoughListener l) {
	    if(!listenersRough.contains(l)) {
	        listenersRough.add(l);
	    }
	}

	/**
	 * Removes a listener.
	 * @param l
	 */
	public void removeListener(RoughListener l) {
       listenersRough.remove(l);
	}

	/**
	 * Finds the resource descriptors for a regex resource id
	 * @param ridex
	 * @param aggressive
	 * @return
	 */
	protected ResourceInfo[] findInfoForResourceId(
			ResourceId ridex,
			boolean aggressive) {
		SessionNode root = (SessionNode)getRoot();
	    if(ridex == null) {
	        // the session node
	        return new ResourceInfo[] {new ResourceInfo(
	                root.getSessionInfo(), null, null, null, null)};
	    }
		List lst = root.getPathsMatching(ridex, aggressive);
		ResourcePath rp;
		ResourceInfo[] ret = new ResourceInfo[lst.size()];
		int i = 0;
		for(Iterator iter = lst.iterator(); iter.hasNext(); ++i) {
            rp = (ResourcePath)iter.next();
            ret[i] = new ResourceInfo(
	            root.getSessionInfo(),
                rp.getHost() != null ? rp.getHost().getHostInfo() : null,
                rp.getAgent() != null ? rp.getAgent().getAgentInfo() : null,
				rp.getEntity() != null ? rp.getEntity().getEntityInfo() : null,
	        	rp.getCounter() != null ? rp.getCounter() : null);
        }
		return ret;
	}

	/**
	 * Finds a host node.
	 * @param hostName
	 * @return
	 */
	HostNode findHostNode(String hostName) {
		SessionNode root = (SessionNode)getRoot();
		return root.findHostNode(hostName);
	}

	/**
	 * Finds an agent node.
	 * @param hostName
	 * @param agentId
	 * @return
	 */
	AgentNode findAgentNode(String hostName, AgentId agentId) {
		HostNode hn = findHostNode(hostName);
		if(hn == null) {
			return null;
		}
		return hn.findAgentNode(agentId);
	}

	/**
	 * Finds an entity node.
	 * @param hostName
	 * @param agentId
	 * @param aggressive
	 * @return
	 */
	EntityNode findEntityNode(
			String hostName, AgentId agentId,
			EntityId eid, boolean aggressive) {
		AgentNode an = findAgentNode(hostName, agentId);
		if(an == null) {
			return null;
		}
		return an.findDescendant(eid, aggressive);
	}

	/**
	 * Finds the node that owns the given entity.
	 * @param host
	 * @param agentId
	 * @param aggressive
	 * @return
	 */
	ResourceNodeWithEntities findResourceNodeWithEntities(
			String host, AgentId agentId,
			EntityId eid, boolean aggressive) {
		 if(eid == null || eid.getName().equals("root")) {
	        // that's for an agent node
	        AgentNode an = findAgentNode(host, agentId);
			if(an == null) {
				if(logger.isTraceEnabled()) {
					logger.error("Couldn't find agent node for: " + agentId, new Throwable());
				}
				return null;
			}
			return an;
	    } else {
			EntityNode en = findEntityNode(host, agentId, eid, aggressive);
			if(en == null) {
				if(logger.isTraceEnabled()) {
					logger.error("Couldn't find entity node for: " + eid, new Throwable());
				}
				return null;
			}
			return en;
	    }
	}

	/**
	 * Fills the given monitoring session descriptor with data
	 * from the session tree.
	 * @param session
	 */
	public synchronized void fillMonitoringSessionDescriptor(MonitoringSessionDescriptor session) {
		SessionNode root = (SessionNode)getRoot();
		Enumeration e = root.children();
		// go through hosts
		while(e.hasMoreElements()) {
			HostNode hn = (HostNode)e.nextElement();
			HostDetails hd = new HostDetails(hn.getHostInfo().getName());
			Enumeration e2 = hn.agents();
			// go through agents
			while(e2.hasMoreElements()) {
				AgentNode an = (AgentNode)e2.nextElement();
				AgentDetails ad = new AgentDetails(an.getAgentInfo().getDeploymentDtls());
				// go through dirty (user modified) entities
				EntityDetails ed;
				Enumeration e3 = an.children();
				while(e3.hasMoreElements()) {
					EntityNode en = (EntityNode)e3.nextElement();
					Enumeration enumeration = en.breadthFirstEnumeration();
					while(enumeration.hasMoreElements()) {
						en = (EntityNode)enumeration.nextElement();
						// only dirty ones...
						if(en.getEntityInfo().isDirty()) {
							ed = new EntityDetails(
									en.getEntityInfoImpl().getEntityDescriptor(),
									en.getEntityInfo().getConfiguration());
							ad.addEntityDetails(ed);
						}
					}
				}
				hd.addAgentDetails(ad);
			}
			session.addHost(hd);
		}
	}

	/**
	 * Fills the given monitoring session descriptor with data
	 * from the session tree. This version will write a trimmed down
	 * version of the monitoring session descriptor without entity information as that
	 * is not required for logging.
	 * @param session
	 */
	public synchronized void fillMonitoringSessionDescriptorForLog(MonitoringSessionDescriptor session) {
		SessionNode root = (SessionNode)getRoot();
		Enumeration e = root.children();
		// go through hosts
		while(e.hasMoreElements()) {
			HostNode hn = (HostNode)e.nextElement();
			HostDetails hd = new HostDetails(hn.getHostInfo().getName());
			Enumeration e2 = hn.agents();
			// go through agents
			while(e2.hasMoreElements()) {
				AgentNode an = (AgentNode)e2.nextElement();
				AgentDetails ad = new AgentDetails(an.getAgentInfo().getDeploymentDtls());
				hd.addAgentDetails(ad);
			}
			session.addHost(hd);
		}
	}

	/**
	 * Removes the given host node.
	 * @param hn
	 */
	public synchronized void removeHost(HostNode hn) {
		fireModelChanged(hn.getResourceId(), RoughListener.CHANGE_HOSTS, RoughListener.CHANGE_TYPE_REMOVED);
		fireHostRemoved(hn);
		removeNodeFromParent(hn);
	}

	/**
	 * Removes the given agent node.
	 * @param an
	 */
	public synchronized void removeAgent(AgentNode an) {
		fireModelChanged(an.getResourceId(), RoughListener.CHANGE_AGENTS, RoughListener.CHANGE_TYPE_REMOVED);
		fireAgentRemoved(an);
		removeNodeFromParent(an);
	}

	/**
	 * Adds a host node.
	 * @param host
	 * @throws RemoteException
	 * @throws RMSException
	 */
	public synchronized void addHost(String host) throws RMSException, RemoteException {
		SessionNode root = (SessionNode)getRoot();
		boolean added = false;
		// already in?
		if(root.findHostNode(host) != null) {
			if(logger.isTraceEnabled()) {
				logger.error("Host already in the model: " + host);
			}
			return;
		}
		HostNode node = new HostNode(host, this);
		root.add(node);
		if(!logReplayMode) {
		    // load artefacts
		    loadArtefactsForNode(node);
		}
		// fire model event
		added = true;
		nodesWereInserted(root, new int[] {root.getIndex(node)});
		fireHostAdded(node);

		if(added) {
			fireModelChanged(node.getResourceId(), RoughListener.CHANGE_HOSTS, RoughListener.CHANGE_TYPE_ADDED);
		}

		if(!logReplayMode) {
			// now see if the host is already registered
			// with the host monitor and if so get its state and info
			HostInformation hi = this.rmsHostMonitor.getHostInfo(host);
			if(hi != null) {
			    handleUpdateHostInfo(host, hi);
			}
			HostState hs = this.rmsHostMonitor.getHostState(host);
			if(hs != null) {
			    int[] sids = hs.getServices();
			    int sid;
			    for(int i = 0; i < sids.length; i++) {
			        sid = sids[i];
			        handleHostStateChanged(host, sid, hs.getServiceState(sid));
	            }
			}
		}
		return;
	}

	/**
	 * Adds a new agent node using the given data.
	 * @param host
	 * @param aid
	 * @param ad
	 */
	public synchronized void addAgent(String host, AgentInstallationData aid, AgentInstanceData ad) {
		HostNode hostNode = findHostNode(host);
		if(hostNode == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find host node for: " + host, new Throwable());
			}
			return;
		}
		if(hostNode.findAgentNode(ad.getAgentId()) == null) {
    		AgentNode agentNode = new AgentNode(aid, ad, this);
    		hostNode.add(agentNode);
    		if(!logReplayMode) {
    		    // load agent artefacts
    		    loadArtefactsForNode(agentNode);
    		}
    		nodesWereInserted(hostNode, new int[] {hostNode.getIndex(agentNode)});
    		fireAgentAdded(agentNode);
    		fireModelChanged(agentNode.getResourceId(), RoughListener.CHANGE_AGENTS, RoughListener.CHANGE_TYPE_ADDED);
        } else if(logReplayMode) {
            updateAgent(host, ad);
        }
	}

	/**
	 * Marks the entity as dirty (modified by the user) so that it will be
	 * stored in the monitoring session when this is requested.
	 * @param dirty
	 */
	public synchronized void setDirtyEntity(EntityNode en, boolean dirty) {
		en.getEntityInfoImpl().setDirty(dirty);
		nodeChanged(en);
	}

    /**
     * Updates an agent node using the given data.
     * @param host
     * @param aid
     * @param ad
     */
    public synchronized void updateAgent(String host, AgentInstanceData ad) {
        if(!logReplayMode) {
            throw new RuntimeException("updateAgent() can only be called for a log replay session");
        }
        HostNode hostNode = findHostNode(host);
        if(hostNode == null) {
            if(logger.isTraceEnabled()) {
                logger.error("Couldn't find host node for: " + host);
            }
            return;
        }
        AgentNode agentNode = hostNode.findAgentNode(ad.getAgentId());
        if(agentNode == null) {
            if(logger.isTraceEnabled()) {
                logger.error("Couldn't find agent node: " + ad.getAgentId());
            }
            return;
        }
        agentNode.getAgentInfoImpl().update(ad);
        nodeChanged(agentNode);
        fireAgentUpdated(agentNode);
        fireModelChanged(agentNode.getResourceId(), RoughListener.CHANGE_AGENTS, RoughListener.CHANGE_TYPE_UPDATED);
    }

	/**
	 * Updates an entity for a log replay session.
	 * @param node
	 * @param ed
	 */
	public synchronized void updateEntity(EntityNode en, EntityDescriptor entity) {
		if(!logReplayMode) {
			throw new RuntimeException("updateEntity() can only be called for a log replay session");
		}

        // log replay mode, accumulate information
		EntityDescriptor currentDesc = en.getEntityInfoImpl().getEntityDescriptor();
		// update config first
		EntityConfiguration currentConfig = currentDesc.getConfiguration();
		Set<CounterId> currentSet = null;
		if(currentConfig != null) {
			currentSet = currentConfig.getMonitoredCountersIds();
		}
		EntityConfiguration newConfig = entity.getConfiguration();
		Set<CounterId> newSet = newConfig.getMonitoredCountersIds();
		Set<CounterId> updatedSet = new HashSet<CounterId>();
		if(!Utils.isEmptyCollection(newSet)) {
			updatedSet.addAll(newSet);
		}
		if(!Utils.isEmptyCollection(currentSet)) {
			updatedSet.addAll(currentSet);
		}
		EntityConfiguration updatedConfig = newConfig;
		updatedConfig.setMonitoredCountersIds(updatedSet);

		// create a new entity descriptor accumulating info
		EntityDescriptorImplMutable updatedDesc = new EntityDescriptorImplMutable(
				entity.getId());
		updatedDesc.setAlternateName(
				currentDesc.getAlternateName() == null ? entity.getAlternateName() : currentDesc.getAlternateName());
		updatedDesc.setDescription(
				currentDesc.getDescription() == null ? entity.getDescription() : currentDesc.getDescription());
		updatedDesc.setLevel(
				currentDesc.getLevel() == null ? entity.getLevel() : currentDesc.getLevel());
		updatedDesc.setSupportedLevels(
				currentDesc.getSupportedLevels() == null ? entity.getSupportedLevels() : currentDesc.getSupportedLevels());
		updatedDesc.setCounterDescriptors(entity.getCounterDescriptors());
		updatedDesc.setEnabled(
				currentDesc.isEnabled() == false ? entity.isEnabled() : false);
		updatedDesc.setConfiguration(updatedConfig);
		updatedDesc.setSupportsIndependentSamplingInterval(
				currentDesc.supportsSamplingInterval() == false ? entity.supportsSamplingInterval() : false);
        updatedDesc.setHasChildren(entity.hasChildren());
        updatedDesc.setCanRefreshChildren(entity.canRefreshChildren());
        updatedDesc.setSafeToRefreshRecursivelly(entity.safeToRefreshRecursivelly());
		en.getEntityInfoImpl().update(updatedDesc);
		en.setAllowsChildren(en.getEntityInfo().hasChildren());
		nodeChanged(en);
	}

	/**
	 * Adds an entity for a log replay session.
	 * All ancestor entities for the added entity will be added without a descriptor.
	 * @param host
	 * @param agentId
	 * @param entity
	 */
	public synchronized void addEntity(String host,
	        AgentId agentId, EntityDescriptor entity) {
		if(!logReplayMode) {
			throw new RuntimeException("addEntity() can only be called for a log replay session");
		}
	    EntityId eid = entity.getId();
	    EntityId[] ancestors = eid.getAncestors();
	    if(ancestors.length < 1) {
	        return;
	    }
        // ignore 'root' entity
        // that's for an agent node
        AgentNode an = findAgentNode(host, agentId);
		if(an == null) {
			if(logger.isTraceEnabled()) {
				logger.error("Couldn't find agent node for: " + agentId, new Throwable());
			}
			return;
		}
		EntityNode newEntity = null;
		if(ancestors.length == 1) {
		    // add it straight to the agent node if not already in
			if(an.findChild(eid) == null) {
				newEntity = new EntityNode(an, null, entity);
			    an.add(newEntity);
				nodesWereInserted(an, new int[] {an.getIndex(newEntity)});
			}
		} else {
			// add ancestors first
			// add to the agent
			EntityId parent = ancestors[1];
			newEntity = an.findDescendant(parent, false);
			if(newEntity == null) {
				newEntity = new EntityNode(an, null,
				        new EntityDescriptorImpl(ancestors[1], true));
			    an.add(newEntity);
			    nodesWereInserted(an, new int[] {an.getIndex(newEntity)});
			}
			// now add the other ancestors
			EntityNode currentNode = newEntity;
			for(int i = 2; i < ancestors.length; i++) {
				// see if the ancestor is already in the model
				EntityId current = ancestors[i];
				newEntity = currentNode.findDescendant(current, false);
				if(newEntity == null) {
					// not there, add it
					newEntity = new EntityNode(an, currentNode,
				        new EntityDescriptorImpl(current, true));
					currentNode.add(newEntity);
					nodesWereInserted(currentNode, new int[] {currentNode.getIndex(newEntity)});
				}
				currentNode = newEntity;
	        }
			// now add the actual entity
			// if not already in
			EntityNode en = currentNode.findChild(eid);
			if(en == null) {
				EntityNode lastChild = new EntityNode(an, currentNode, entity);
				currentNode.add(lastChild);
			    nodesWereInserted(currentNode, new int[] {currentNode.getIndex(lastChild)});
			} else {
				updateEntity(en, entity);
			}
		}
	}

	/**
	 * Updates the children of the given entity.
	 * @param agentId
	 * @param host
	 * @param entity
	 * @param descs
	 */
	public synchronized void updateEntities(String host, AgentId agentId, EntityDescriptorTree entities) {
		if(entities == null) {
			return;
		}
		List<ResourceId> addedLst = new LinkedList<ResourceId>();
		List<ResourceId> removedLst = new LinkedList<ResourceId>();
		List<ResourceId> updatedLst = new LinkedList<ResourceId>();

		updateNodes(host, agentId, entities, addedLst, removedLst, updatedLst);

	    if(addedLst.size() > 0) {
	    	fireModelChanged(addedLst.toArray(new ResourceId[addedLst.size()]), RoughListener.CHANGE_ENTITIES, RoughListener.CHANGE_TYPE_ADDED);
	    }
	    if(updatedLst.size() > 0) {
	    	fireModelChanged(updatedLst.toArray(new ResourceId[updatedLst.size()]), RoughListener.CHANGE_ENTITIES, RoughListener.CHANGE_TYPE_UPDATED);
	    }
	    if(removedLst.size() > 0) {
	    	fireModelChanged(removedLst.toArray(new ResourceId[removedLst.size()]), RoughListener.CHANGE_ENTITIES, RoughListener.CHANGE_TYPE_REMOVED);
	    }
	}

	/**
	 * Updates all nodes in the model using the given entity descriptor tree.
	 * @param host
	 * @param agentId
	 * @param tree
	 * @param added
	 * @param removed
	 * @param updated
	 */
	private void updateNodes(String host, AgentId agentId, EntityDescriptorTree tree,
			List<ResourceId> added, List<ResourceId> removed, List<ResourceId> updated) {
		EntityId parent = tree.getEntityDescriptor().getId();
		ResourceNodeWithEntities parentNode = findResourceNodeWithEntities(host, agentId, parent, false);
		if(parentNode == null) {
			return;
		}

		AgentNode agentNode = null;
		if(parentNode instanceof EntityNode) {
			// update current entity
			EntityNode entityNode = (EntityNode)parentNode;
			agentNode = entityNode.getAgentNode();
			EntityDescriptor newDesc = tree.getEntityDescriptor();
			boolean wasUpdated = entityNode.getEntityInfoImpl().update(newDesc);
            if(wasUpdated) {
                entityNode.setAllowsChildren(newDesc.hasChildren());
    			updated.add(parentNode.getResourceId());
    			nodesChanged(parentNode.getParent(), new int[] {parentNode.getParent().getIndex(parentNode)});
    			fireEntityUpdated(entityNode);
            }
		} else {
			agentNode = (AgentNode)parentNode;
		}

		// we need to handle the situation where the new tree descriptor is just the
		// top fragment of a subtree and for this we are only going to update children
		// if the descriptor says it should have children and the list of children is not
		// empty
		EntityDescriptor nodeDesc = tree.getEntityDescriptor();
		if(nodeDesc.hasChildren() && tree.getChildrenCount() > 0) {
			// remove children which are not in the new data
			List<EntityNode> toRemove = new LinkedList<EntityNode>();
			Enumeration children = parentNode.children();
			while(children.hasMoreElements()) {
				EntityNode child = (EntityNode)children.nextElement();
				EntityId childId = child.getEntityInfo().getId();
				if(tree.getChild(childId) == null) {
					// remove
					toRemove.add(child);
				}
			}
			for(EntityNode child : toRemove) {
				int idx = parentNode.getIndex(child);
				removed.add(child.getResourceId());
				parentNode.remove(child);
				nodesWereRemoved(parentNode, new int[] {idx}, new Object[] {child});
				fireEntityRemoved(child);
			}

			// and add or update children that are in the new data
			for(Iterator<EntityDescriptorTree> iter = tree.children(); iter.hasNext();) {
				EntityDescriptorTree child = iter.next();
				// update the list of children nodes
				// add new...
				EntityNode n = parentNode.findChild(child.getEntityDescriptor().getId());
				if(n == null) {
					n = new EntityNode(agentNode,
	                        parentNode instanceof EntityNode ? ((EntityNode)parentNode) : null,
	                        child.getEntityDescriptor());
					parentNode.insert(n, parentNode.getChildCount());
					added.add(n.getResourceId());
					if(!logReplayMode) {
					    // load artefacts
					    loadArtefactsForNode(n);
					}
					nodesWereInserted(parentNode, new int[] {parentNode.getIndex(n)});
					fireEntityAdded(n);
				}

				// call recursivelly on children
				updateNodes(host, agentId, child, added, removed, updated);
			}
		}
	}

	/**
	 * @return all the hosts in the model
	 */
	public synchronized Collection getAllHosts() {
		SessionNode root = (SessionNode)getRoot();
		Enumeration e = root.children();
		List ret = new ArrayList(root.getChildCount());
		while(e.hasMoreElements()) {
			HostNode hn = (HostNode)e.nextElement();
			ret.add(hn.getHostInfo().getName());
		}
		return ret;
	}

    /**
     * @return the total number of agents on all hosts; required by the licensing mechanism
     */
	public synchronized int getTotalNumberOfAgents() {
        SessionNode root = (SessionNode)getRoot();
        Enumeration e = root.children();
        int ret = 0;
        while(e.hasMoreElements()) {
            HostNode hn = (HostNode)e.nextElement();
            ret += hn.getChildCount();
        }
        return ret;

    }

	/**
	 * Sets the session name
	 * @param name
	 */
	public synchronized void setSessionName(String name) {
		SessionNode root = (SessionNode)getRoot();
		root.getSessionInfoImpl().setName(name);
		nodeChanged(root);
	}

	/**
	 * Used by the log replay view to load all artefacts
	 * at once after the model has been setup.
	 */
	public synchronized void loadArtefacts() {
		if(!logReplayMode) {
			throw new RuntimeException("loadArtefacts() can only be called for a replay session");
		}
	    // session
	    SessionNode sn = getSessionNode();
	    loadArtefactsForNode(sn);
	    // hosts
	    Enumeration e = sn.hosts();
	    HostNode hn;
	    while(e.hasMoreElements()) {
	        hn = (HostNode)e.nextElement();
            loadArtefactsForNode(hn);
            // agents
            Enumeration e1 = hn.agents();
            AgentNode an;
            while(e1.hasMoreElements()) {
                an = (AgentNode)e1.nextElement();
                loadArtefactsForNode(an);
                // entities
                Enumeration e2 = an.entities();
                EntityNode en;
                while(e2.hasMoreElements()) {
                    en = (EntityNode)e2.nextElement();
                    loadArtefactsForNode(en);
                }
            }
        }
	}

	/**
	 * Changes the host service state.
	 * @param host
	 * @param serviceID
	 * @param state
	 */
	protected  void handleHostStateChanged(String host, int serviceID, ServiceState state) {
		try {
			HostNode node = findHostNode(host);
			if(node == null) {
				if(logger.isTraceEnabled()) {
					logger.error("Couldn't find host node for: " + host, new Throwable());
				}
				return;
			}
			node.setHostState(serviceID, state);
			nodeChanged(node);
			fireHostUpdated(node);
			fireModelChanged(node.getResourceId(), RoughListener.CHANGE_HOSTS, RoughListener.CHANGE_TYPE_UPDATED);
		} catch(Exception ex) {
			logger.error(ex);
		}
	}

	/**
	 * Updates host info.
	 * @param host
	 * @param info
	 */
	protected  void handleUpdateHostInfo(String host, HostInformation info) {
		try {
			SessionNode root = (SessionNode)getRoot();
			HostNode node = root.findHostNode(host);
			if(node == null) {
				if(logger.isTraceEnabled()) {
					logger.error("Couldn't find host node for: " + host, new Throwable());
				}
				return;
			}
			node.setHostInfo(info);
			nodeChanged(node);
			fireHostUpdated(node);
			fireModelChanged(node.getResourceId(), RoughListener.CHANGE_HOSTS, RoughListener.CHANGE_TYPE_UPDATED);
		} catch(Exception ex) {
			logger.error(ex);
		}
	}

	/**
	 * Updates agent state.
	 * @param host
	 * @param agentId
	 * @param state
	 * @param e
	 */
	protected  void handleAgentStateChanged(String host, AgentId agentId, AgentState state, Throwable e) {
		try {
			AgentNode an = findAgentNode(host, agentId);
			if(an == null) {
				// do not log this event as it happens when AgentState.READY state
				// is broadcast
				//logger.error("Couldn't find agent node for: " + agentId, new Throwable());
				return;
			}
			if(state == AgentState.UNKNOWN) {
				ResourceId rid = an.getResourceId();
				// agent lost
                an.getAgentInfoImpl().setAgentState(AgentState.ERROR, e);
				fireModelChanged(rid, RoughListener.CHANGE_AGENTS, RoughListener.CHANGE_TYPE_UPDATED);
			} else {
				an.getAgentInfoImpl().setAgentState(state, e);
				nodeChanged(an);
				fireAgentUpdated(an);
				fireModelChanged(an.getResourceId(), RoughListener.CHANGE_AGENTS, RoughListener.CHANGE_TYPE_UPDATED);
			}
		} catch(Exception ex) {
			logger.error(ex);
		}
	}

	/**
	 * @param host
	 * @param agentId
	 * @param providerInstanceName
	 * @param state
	 * @param e
	 */
	protected void handleProviderStateChanged(String host, AgentId agentId, String providerInstanceName, ProviderState state, Throwable e) {
		try {
			AgentNode an = findAgentNode(host, agentId);
			if(an == null) {
				// do not log this event as it happens when AgentState.READY state
				// is broadcast
				//logger.error("Couldn't find agent node for: " + agentId, new Throwable());
				return;
			}
			an.getAgentInfoImpl().setProviderState(providerInstanceName, state, e);
			nodeChanged(an);
			fireAgentUpdated(an);
			fireModelChanged(an.getResourceId(), RoughListener.CHANGE_AGENTS, RoughListener.CHANGE_TYPE_UPDATED);
		} catch(Exception ex) {
			logger.error(ex);
		}
	}

	/**
	 * Updates children entities.
	 * @param host
	 * @param agentId
	 * @param entities
	 */
	protected void handleEntitiesChanged(String host, AgentId agentId, EntityDescriptorTree entities) {
		try {
			updateEntities(host, agentId, entities);
		} catch(Exception e) {
			logger.error(e);
		}
	}

	/**
	 * @param host
	 * @param agentId
	 * @param t
	 */
	protected  void handleAgentNonFatalError(String host, AgentId agentId, Throwable t) {
		try {
			AgentNode an = findAgentNode(host, agentId);
			if(an == null) {
				// do not log this event as it happens when AgentState.READY state
				// is broadcast
				//logger.error("Couldn't find agent node for: " + agentId, new Throwable());
				return;
			}
			an.getAgentInfoImpl().setNonFatalError(t);
			nodeChanged(an);
			fireAgentUpdated(an);
			fireModelChanged(an.getResourceId(), RoughListener.CHANGE_AGENTS, RoughListener.CHANGE_TYPE_UPDATED);
		} catch(Exception ex) {
			logger.error(ex);
		}
	}

	/**
	 * Removes registered listeners. Called when the monitoring
	 * session is closed.
	 */
	public synchronized void close() {
		SessionNode sn = (SessionNode)getRoot();
		sn.removeAllChildren();
		if(rmsHostMonitor != null) {
		    rmsHostMonitor.removeListener(eventHandler);
		}
		if(rmsMonitoringSession != null) {
		    rmsMonitoringSession.removeListener(eventHandler);
		}
		nodeChanged(sn);
	}

	/**
	 * Returns the artefact info container associated with the
	 * given resource.
	 * Note: this has an aggressive behaviour, if it can't find a resource
	 * in the model it will try to retrieve it from the monitoring session.
	 * @param id
     * @param aggresive
   	 * @return
	 */
	public synchronized ArtefactInfoContainer getArtefactContainerForResource(ResourceId id, boolean aggresive) {
	    return getArtefactContainerImplForResource(id, aggresive);
	}

	/**
	 * Returns the artefact info container associated with the
	 * given resource.<br>
	 * Note: this has an aggressive behaviour, if it can't find a resource
	 * in the model it will try to retrieve it from the monitoring session.
	 * @param id
     * @param aggresive
  	 * @return
	 */
	ArtefactInfoContainerImpl getArtefactContainerImplForResource(ResourceId id, boolean aggresive) {
		if(id == null) {
			SessionNode sn = (SessionNode)getRoot();
			return sn.getSessionInfoImpl();
		}
		int r = id.getRepresentation();
		switch(r) {
			case ResourceId.INVALID:
				return null;
			case ResourceId.HOST:
				HostNode hn = findHostNode(id.getHostId().toString());
				if(hn == null) {
					if(logger.isTraceEnabled()) {
						logger.error("Couldn't find host node for: " + id, new Throwable());
					}
					return null;
				}
				return hn.getHostInfoImpl();
			case ResourceId.AGENT:
				AgentNode an = findAgentNode(
						id.getHostId().toString(),
						id.getAgentId());
				if(an == null) {
					if(logger.isTraceEnabled()) {
						logger.error("Couldn't find agent node for: " + id, new Throwable());
					}
					return null;
				}
				return an.getAgentInfoImpl();
			case ResourceId.ENTITY:
				EntityNode en = findEntityNode(
						id.getHostId().toString(),
						id.getAgentId(),
						id.getEntityId(),
						aggresive);
				if(en == null) {
					if(logger.isTraceEnabled()) {
						logger.error("Couldn't find entity node for: " + id, new Throwable());
					}
					return null;
				}
				return en.getEntityInfoImpl();
		}
		return null;
	}

	/**
	 * Refreshes the node for the given context.
	 * @param context
	 */
	void refreshNode(ResourceId context) {
        TreeNode node = getNodeForResourceId(context);
        if(node == null) {
        	if(logger.isTraceEnabled()) {
        		logger.error("Couldn't find node for: " + context);
        	}
            return;
        }
        nodeChanged(node);
	}

	/**
	 * This method has an aggressive behaviour when searching.
	 * @param rid
	 * @return the tree node corresponding to the
	 * given resource that must be host, agent,
	 * entity or null to represent the session
	 */
	public synchronized DefaultMutableTreeNode getNodeForResourceId(ResourceId id) {
		return findNodeForResource(id, true);
	}

	/**
	 * This method has a non aggressive behaviour when searching.
	 * @param rid
	 * @return the tree node corresponding to the
	 * given resource that must be host, agent,
	 * entity or null to represent the session
	 */
	public synchronized DefaultMutableTreeNode getExistingNodeForResourceId(ResourceId id) {
		return findNodeForResource(id, false);
	}

    /**
     * This method has an aggressive behaviour when searching.
     * @param rid
     * @return the tree node corresponding to the
     * given resource that must be host, agent,
     * entity or null to represent the session
     */
    public synchronized SessionModelTreeNode getSessionModelTreeNodeForResourceId(ResourceId id) {
        return (SessionModelTreeNode)getNodeForResourceId(id);
    }

    /**
     * This method has a non aggressive behaviour when searching.
     * @param rid
     * @return the tree node corresponding to the
     * given resource that must be host, agent,
     * entity or null to represent the session
     */
    public synchronized SessionModelTreeNode getExistingSessionModelTreeNodeForResourceId(ResourceId id) {
        return (SessionModelTreeNode)getExistingNodeForResourceId(id);
    }

	/**
	 * Finds the node for the given resource.
	 * @param rid
	 * @param aggressive
	 * @return
	 */
	private DefaultMutableTreeNode findNodeForResource(ResourceId rid, boolean aggressive) {
		if(rid == null) {
			SessionNode sn = (SessionNode)getRoot();
			return sn;
		}
		int r = rid.getRepresentation();
		switch(r) {
			case ResourceId.INVALID:
				return null;
			case ResourceId.HOST:
				HostNode hn = findHostNode(rid.getHostId().toString());
				return hn;
			case ResourceId.AGENT:
				AgentNode an = findAgentNode(
						rid.getHostId().toString(),
						rid.getAgentId());
				return an;
			case ResourceId.ENTITY:
				EntityNode en = findEntityNode(
						rid.getHostId().toString(),
						rid.getAgentId(),
						rid.getEntityId(), aggressive);
				return en;
		}
		return null;
	}

	/**
	 * @return the hosts root node
	 */
	SessionNode getSessionNode() {
		return (SessionNode)getRoot();
	}

	/**
     * @return the counter model helper.
     */
    public CounterModelHelper getCounterHelper() {
        return counterHelper;
    }

	/**
     * @return the query model helper.
     */
    public QueryModelHelper getQueryHelper() {
        return queryHelper;
    }

	/**
     * @return the dashboard model helper.
     */
    public DashboardModelHelper getDashboardHelper() {
        return dashboardHelper;
    }

    /**
     * @return
     */
    public DataViewModelHelper getDataViewHelper() {
        return dataViewHelper;
    }

	/**
     * @return the provider instance model helper.
     */
    public ProviderInstanceModelHelper getProviderInstanceHelper() {
        return providerInstanceHelper;
    }

    /**
     * Filters query groups. If in log replay mode only dashboards which have
     * all views ready are returned.
     * @param node
     * @param dashboards
     * @return
     */
    private void filterDashboards(SessionModelTreeNode node,
            	Dashboard[] dashboards) {
	    Dashboard[] ret;
	    if(logReplayMode) {
	        // filter out dashboards, see comments for
	        // filterDataViews()
	    	List lst = new LinkedList();
	    	Dashboard db;
	    	for(int i = 0; i < dashboards.length; i++) {
                db = dashboards[i];
                if(dashboardHelper.isDashboardReady(node.getResourceId(),
                        db)) {
                    lst.add(db);
                }
            }
	    	ret = (Dashboard[])lst.toArray(new Dashboard[lst.size()]);
	    } else {
	        ret = dashboards;
	    }
	    if(node instanceof EntityNode) {
	        EntityNode en = (EntityNode)node;
	        en.getEntityInfoImpl().setDashboards(ret);
	    } else if(node instanceof AgentNode) {
	        AgentNode an = (AgentNode)node;
	        an.getAgentInfoImpl().setDashboards(ret);
	    } else if(node instanceof HostNode) {
	        HostNode hn = (HostNode)node;
	        hn.getHostInfoImpl().setDashboards(ret);
	    } else if(node instanceof SessionNode) {
	        SessionNode sn = (SessionNode)node;
	        sn.getSessionInfoImpl().setDashboards(ret);
	    }
    }

    /**
     * Filters query groups. If in log replay mode only data views
     * which are ready.
     * @param node
     * @param views
     * @return
     */
    private void filterDataViews(SessionModelTreeNode node,
            	DataView[] views) {
	    DataView[] ret;
	    if(logReplayMode) {
	        // filter out views that don't have their
	        // query ready, as we are in logReplayMode
	        // we can safely use the helper models which
	        // by default use aggressive searching
	    	List lst = new LinkedList();
	    	ResourceId context = node.getResourceId();
	    	DataView dv;
	    	for(int i = 0; i < views.length; i++) {
	    	    dv = views[i];
                if(queryHelper.isQueryReady(context, dv.getQueryDef())) {
                    lst.add(dv);
                }
            }
	    	ret = (DataView[])lst.toArray(new DataView[lst.size()]);
	    } else {
	        ret = views;
	    }
        QueryDef[] queries = new QueryDef[ret.length];
        for(int i = 0; i < ret.length; i++) {
            queries[i] = ret[i].getQueryDef();
        }
	    if(node instanceof EntityNode) {
	        EntityNode en = (EntityNode)node;
	        en.getEntityInfoImpl().setDataViews(ret);
	        en.getEntityInfoImpl().setQueries(queries, logReplayMode);
	    } else if(node instanceof AgentNode) {
	        AgentNode an = (AgentNode)node;
	        an.getAgentInfoImpl().setDataViews(ret);
	        an.getAgentInfoImpl().setQueries(queries, logReplayMode);
	    } else if(node instanceof HostNode) {
	        HostNode hn = (HostNode)node;
	        hn.getHostInfoImpl().setDataViews(ret);
	        hn.getHostInfoImpl().setQueries(queries, logReplayMode);
	    } else if(node instanceof SessionNode) {
	        SessionNode sn = (SessionNode)node;
	        sn.getSessionInfoImpl().setDataViews(ret);
	        sn.getSessionInfoImpl().setQueries(queries, logReplayMode);
	    }
    }

    /**
     * Loads artefacts for the given node.
     * @param node
     */
    private void loadArtefactsForNode(SessionModelTreeNode node) {
        ResourceId context = node.getResourceId();
		// load views
        String suoVersion = null;
        DataViewMap dvm = null;
        DashboardMap dbm = null;
        ProviderInstanceMap pim = null;
        if(context == null) {
            // session
    		dvm = this.rmsDataViewRepository.getGlobalDataViews();
    		dbm = this.rmsDashboardRepository.getGlobalDashboards();
        } else {
	        int rep = context.getRepresentation();
	        switch(rep) {
	        	case ResourceId.HOST:
	        	    dvm = this.rmsDataViewRepository.getHostDataViews(context.getHostId().toString());
	        		dbm = this.rmsDashboardRepository.getHostDashboards(context.getHostId().toString());
	        	break;
	        	case ResourceId.AGENT:
                    suoVersion = ((AgentNode)node).getAgentInfo().getDeploymentDtls().getConfiguration().getSystemUnderObservationVersion();
	        	    dvm = this.rmsDataViewRepository.getAgentDataViews(context.getAgentId().getInstallationId().toString());
	        		dbm = this.rmsDashboardRepository.getAgentDashboards(context.getAgentId().getInstallationId().toString());
	        		if(!logReplayMode) {
	        			pim = this.rmsProviderInstanceRepository.getAgentProviderInstances(context.getAgentId().getInstallationId().toString());
	        		}
	        	break;
	        	case ResourceId.ENTITY:
                    suoVersion = ((EntityNode)node).getAgentNode().getAgentInfo().getDeploymentDtls().getConfiguration().getSystemUnderObservationVersion();
	        	    dvm = this.rmsDataViewRepository.getEntityDataViews(
	        	            context.getAgentId().getInstallationId().toString(),
	        	            context.getEntityId());
	        		dbm = this.rmsDashboardRepository.getEntityDashboards(
	        		        context.getAgentId().getInstallationId().toString(),
	        		        context.getEntityId());
	        	break;
	        }
        }
        // load only artefact for the current system under observation version
		if(dvm != null) {
            Collection<DataView> coll = dvm.getForAgentVersion(suoVersion);
		    filterDataViews(node, coll.toArray(new DataView[coll.size()]));
		}
		if(dbm != null) {
            Collection<Dashboard> coll = dbm.getForAgentVersion(suoVersion);
		    filterDashboards(node, coll.toArray(new Dashboard[coll.size()]));
		}
		if(pim != null) {
	        AgentNode an = (AgentNode)node;
	        an.getAgentInfoImpl().setProviderInstanceData(pim.getForAgentVersion(suoVersion));
		}
    }

	/**
	 * Uses the monitoring session to retrieve the entity children of the
	 * given agent or entity node.
	 * @param node agent or entity node
	 * @return
	 */
	void retrieveEntityChildrenForNode(DefaultMutableTreeNode node) {
		try {
		    if(rmsMonitoringSession == null) {
		        return;
		    }
		    AgentNode agentNode;
		    EntityId eid = null;
		    if(node instanceof AgentNode) {
		        agentNode = (AgentNode)node;
		    } else if(node instanceof EntityNode) {
		        EntityNode entityNode = (EntityNode)node;
		        agentNode = entityNode.getAgentNode();
		        eid = entityNode.getEntityInfo().getId();
		    } else {
		        logger.error("Node must be agent or entity");
		        return;
		    }

		    if(logger.isTraceEnabled()) {
		        logger.info(
		                "Retrieving entity nodes from the monitoring session for "
		                + (eid == null ? "root" : eid.toString()));
		    }
			EntityDescriptorTree eds = rmsMonitoringSession
				.getAgentEntities(
					agentNode.getHostNode().getHostInfo().getName(),
					agentNode.getAgentInfo().getDeploymentDtls().getAgentId(),
					eid, false, false);
			if(eds != null) {
			    EntityNode entityNode;
				for(Iterator<EntityDescriptorTree> iter = eds.children(); iter.hasNext();) {
					EntityDescriptor ed = iter.next().getEntityDescriptor();
					entityNode = new EntityNode(
                            agentNode,
                            node instanceof EntityNode ? (EntityNode)node : null,
                            ed);
					node.add(entityNode);
					// attach artefacts to new entity
					loadArtefactsForNode(entityNode);
					nodesWereInserted(node, new int[] {node.getIndex(entityNode)});
					fireEntityAdded(entityNode);
				}
			}
		} catch(Exception e) {
			logger.error(e);
		}
	}

    /**
     * @return the logReplayMode flag.
     */
    boolean isLogReplayMode() {
        return logReplayMode;
    }

    /**
     * @param b
     */
    public void setShowIdentifiers(boolean b) {
        this.showIdentifiers = b;
		SessionNode root = (SessionNode)getRoot();
		Enumeration e = root.breadthFirstEnumeration();
        while(e.hasMoreElements()) {
            nodeChanged((SessionModelTreeNode)e.nextElement());
        }
        fireShowIdentifiersChanged();
    }

    /**
     * @return
     */
    public boolean getShowIdentifiers() {
        return showIdentifiers;
    }

     /**
     * @return
     */
    public boolean getFilterOutUnselectedItems() {
        return filterOutUnselectedItems;
    }

    /**
     * Fires event.
     */
    private void fireShowIdentifiersChanged() {
        for(FineListener l : listenersFine) {
            l.showIdentifiersChanged(this.showIdentifiers);
        }
    }

    /**
     * @param en
     */
    private void fireEntityUpdated(EntityNode en) {
        for(FineListener l : listenersFine) {
            l.entityUpdated(en);
        }
    }

    /**
     * @param en
     */
    private void fireEntityAdded(EntityNode en) {
        for(FineListener l : listenersFine) {
            l.entityAdded(en);
        }
    }

    /**
     * @param en
     */
    private void fireEntityRemoved(EntityNode en) {
        for(FineListener l : listenersFine) {
            l.entityRemoved(en);
        }
    }

    /**
     * @param an
     */
    private void fireAgentUpdated(AgentNode an) {
        for(FineListener l : listenersFine) {
            l.agentUpdated(an);
        }
    }

    /**
     * @param an
     */
    private void fireAgentAdded(AgentNode an) {
        for(FineListener l : listenersFine) {
            l.agentAdded(an);
        }
    }

    /**
     * @param an
     */
    private void fireAgentRemoved(AgentNode an) {
        for(FineListener l : listenersFine) {
            l.agentRemoved(an);
        }
    }

    /**
     * @param hn
     */
    private void fireHostUpdated(HostNode hn) {
        for(FineListener l : listenersFine) {
            l.hostUpdated(hn);
        }
    }

    /**
     * @param hn
     */
    private void fireHostAdded(HostNode hn) {
        for(FineListener l : listenersFine) {
            l.hostAdded(hn);
        }
    }

    /**
     * @param hn
     */
    private void fireHostRemoved(HostNode hn) {
        for(FineListener l : listenersFine) {
            l.hostRemoved(hn);
        }
    }

    /**
     * @param sn
     */
//    private void fireSessionUpdated(SessionNode sn) {
//        for(FineListener l : listenersFine) {
//            l.sessionUpdated(sn);
//        }
//    }

    /**
     * @param ridChanged
     * @param change
     * @param changeType
     */
    private void fireModelChanged(ResourceId  ridChanged, int change, int changeType) {
    	fireModelChanged(new ResourceId[] {ridChanged}, change, changeType);
    }

    /**
     * @param ridChanged
     * @param change
     * @param changeType
     */
    private void fireModelChanged(ResourceId[] ridChanged, int change, int changeType) {
        for(RoughListener l : listenersRough) {
            l.modelChanged(ridChanged, change, changeType);
        }
    }

	/**
	 * @see com.ixora.rms.client.model.ResourceInfoLocator#getDashboardInfo(com.ixora.rms.repository.DashboardId)
	 */
	public synchronized DashboardInfo getDashboardInfo(DashboardId id, boolean aggressive) {
		ArtefactInfoContainer aic = getArtefactContainerForResource(id.getContext(), aggressive);
        if(aic == null) {
        	if(logger.isTraceEnabled()) {
        		logger.error("Couldn't find artefact container for : " + id);
        	}
        	return null;
        }
        DashboardInfo dbInfo = aic.getDashboardInfo(id.getName());
        if(dbInfo == null) {
            if(logger.isTraceEnabled()) {
            	logger.error("Couldn't find dashboard info for " + id);
            }
            return null;
        }
        return dbInfo;
	}

	/**
	 * @see com.ixora.rms.client.model.ResourceInfoLocator#getDataViewInfo(com.ixora.rms.repository.DataViewId, boolean)
	 */
	public synchronized DataViewInfo getDataViewInfo(DataViewId id, boolean aggressive) {
		ArtefactInfoContainer aic = getArtefactContainerForResource(id.getContext(), aggressive);
        if(aic == null) {
        	if(logger.isTraceEnabled()) {
        		logger.error("Couldn't find artefact container for : " + id);
        	}
        	return null;
        }
        DataViewInfo dvInfo = aic.getDataViewInfo(id.getName());
        if(dvInfo == null) {
            if(logger.isTraceEnabled()) {
            	logger.error("Couldn't find view info for " + id);
            }
            return null;
        }
        return dvInfo;
    }

    /**
     * @see com.ixora.rms.client.model.ResourceInfoLocator#getCounterInfo(com.ixora.rms.ResourceId)
     */
    public synchronized CounterInfo getCounterInfo(ResourceId cid, boolean aggresive) {
        if(cid.isRegex() || !cid.isValid()) {
            throw new IllegalArgumentException("Resource id " + cid + " must be a valid, non-regex id");
        }

        ResourceId id = cid.getSubResourceId(ResourceId.ENTITY);
        DefaultMutableTreeNode node = aggresive
            ? getNodeForResourceId(id) : getExistingNodeForResourceId(id);
        if(node == null) {
            if(logger.isTraceEnabled()) {
                logger.error("Couldn't find entity node for counter " + id);
            }
            return null;
        }
        EntityNode en = (EntityNode)node;
        CounterInfo counter = en.getEntityInfo().getCounterInfo(cid.getCounterId());
        if(counter == null) {
            if(logger.isTraceEnabled()) {
                logger.error("Counter " + cid + " not found");
            }
            return null;
        }
        return counter;
    }

	/**
	 * @see com.ixora.rms.client.model.ResourceInfoLocator#getResourceInfo(com.ixora.rms.ResourceId, boolean)
	 */
	public synchronized ResourceInfo[] getResourceInfo(ResourceId id, boolean aggresive) {
		return findInfoForResourceId(id, aggresive);
	}

    /**
     * @param context
     * @return the agent version of the given context; if the context is above agent
     * it returns null; otherwise (if it's agent or entity it returns the version of the
     * agent which is the ancestor of the given context)
     */
    public synchronized String getAgentVersionInContext(ResourceId context) {
        SessionModelTreeNode node = getSessionModelTreeNodeForResourceId(context);
        return getAgentVersionInContext(node);
    }

    /**
     * @param context
     * @return the agent version of the context represented by the given node
     */
    public synchronized String getAgentVersionInContext(SessionModelTreeNode node) {
        if(node instanceof AgentNode) {
            return ((AgentNode)node).getAgentInfo().getDeploymentDtls().getConfiguration().getSystemUnderObservationVersion();
        } else if (node instanceof EntityNode) {
            EntityNode en = (EntityNode) node;
            return en.getAgentNode().getAgentInfo().getDeploymentDtls().getConfiguration().getSystemUnderObservationVersion();
        }
        return null;
    }

    /**
     * @param context
     * @return all agent versions for the given context; if the context is above agent
     * it returns null; otherwise (if it's agent or entity it returns all versions of the
     * agent which is the ancestor of the given context)
     */
    public synchronized Set<String> getAllAgentVersionsInContext(ResourceId context) {
        SessionModelTreeNode node = getSessionModelTreeNodeForResourceId(context);
        return getAllAgentVersionsInContext(node);
    }

    /**
     * @param context
     * @return all agent versions of the context represented by the given node
     */
    public synchronized Set<String> getAllAgentVersionsInContext(SessionModelTreeNode node) {
        String[] versions = null;
        if(node instanceof AgentNode) {
            versions = ((AgentNode)node).getAgentInfo().getInstallationDtls().getSystemUnderObservationVersions();
        } else if (node instanceof EntityNode) {
            EntityNode en = (EntityNode) node;
            versions = en.getAgentNode().getAgentInfo().getInstallationDtls().getSystemUnderObservationVersions();
        }
        if(!Utils.isEmptyArray(versions)) {
            return new HashSet(Arrays.asList(versions));
        }
        return null;
    }

	/**
	 * @param an
	 * @param tuple
	 */
	public synchronized void setAgentConfigurationTuple(AgentNode an, AgentConfigurationTuple tuple) {
		an.getAgentInfoImpl().setAgentDescriptor(tuple.getAgentDescriptor());
		fireAgentUpdated(an);

		EntityDescriptorTree entities = tuple.getEntities();
		if(entities != null) {
			updateEntities(an.getHostNode().getHostInfo().getName(),
					an.getAgentInfo().getDeploymentDtls().getAgentId(), entities);
		}
		// fireModelChanged(RoughListener.CHANGE_AGENTS, RoughListener.CHANGE_TYPE_UPDATED);
	}

	/**
	 * Returns the
	 * @param node
	 */
	public synchronized ResourceNode findParentNodeAbleToRefreshSubtree(EntityNode node) {
		if(node.getEntityInfo().canRefreshChildren()) {
			return node;
		} else {
			// find parent able to refreh subtree
			TreeNode tn = node;
			do {
				tn = tn.getParent();
				if(tn instanceof AgentNode) {
					return (AgentNode)tn;
				} else if(tn instanceof EntityNode) {
					EntityNode en = (EntityNode)tn;
					if(en.getEntityInfo().canRefreshChildren()) {
						return en;
					}
				}
			} while(true);
		}
	}
}
