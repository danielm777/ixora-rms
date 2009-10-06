package com.ixora.rms.agents.impl;

import java.util.BitSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.ixora.rms.Collector;
import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CustomConfiguration;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDataBufferImpl;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.Agent;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentDataBufferImpl;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentDescriptorImpl;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.InvalidEntity;
import com.ixora.rms.exception.RMSException;

/**
 * Default implementation for a monitoring agent. Handles common operations.
 * @author Daniel Moraru
 * @author Cristian Costache
 */
public abstract class AbstractAgent implements Agent {
	/** Configuration token for host */
	protected final String TOKEN_HOST = "{host}";
	/** Agent id */
	protected AgentId fAgentId;
	/** Root entity */
	protected RootEntity fRootEntity;
	/** Agent configuration */
	protected AgentConfiguration fConfiguration;
	/** Execution context */
	protected ExecutionContext fContext;
    /** Tells if the agent is started */
    protected boolean fIsRunning;
	/** Listener */
	protected Listener fListener;
	/** For agents that require an individual collector */
	protected Collector fCollector;
	/** True if the descriptor must be sent with the next data buffer */
    protected boolean fSendAgentDescriptor;
    /** True if it's safe to refresh recursivelly the entities of this agent */
    protected boolean fSafeToRefreshEntitiesRecursivelly;
	/** The counter used to calculate when to refresh entities */
	private int fRefreshPeriodCounter;

	/**
	 * Execution context for classes used by this agent.
	 */
	protected class ExecutionContext implements AgentExecutionContext {
	    /**
	     * @see com.ixora.rms.agents.AgentExecutionContext#error(java.lang.Throwable)
	     */
		public void error(Throwable e) {
			fListener.nonFatalError(fConfiguration.getMonitoredHost(), fAgentId, e);
		}
		/**
		 * @see com.ixora.rms.agents.AgentExecutionContext#getAgentConfiguration()
		 */
		public AgentConfiguration getAgentConfiguration() {
			return getConfiguration();
		}
		/**
		 * @see com.ixora.rms.agents.AgentExecutionContext#childrenEntitiesChanged(com.ixora.rms.EntityId, com.ixora.rms.EntityDescriptorTree)
		 */
		public void childrenEntitiesChanged(EntityDescriptorTree descs) {
			fListener.entitiesChanged(fConfiguration.getMonitoredHost(), fAgentId, descs);
		}
		/**
		 * @see com.ixora.rms.agents.AgentExecutionContext#getAgentId()
		 */
		public AgentId getAgentId() {
			return fAgentId;
		}
		/**
		 * @see com.ixora.rms.agents.AgentExecutionContext#isRunning()
		 */
        public boolean isRunning() {
            return fIsRunning;
        }
		/**
		 * @see com.ixora.rms.agents.AgentExecutionContext#sortEntities()
		 */
		public boolean sortEntities() {
			return AbstractAgent.this.sortEntities();
		}
	}

	/**
	 * @param agentId
	 * @param listener
	 * @param useOwnCollector
	 * @param safeForRefresh
	 */
	protected AbstractAgent(AgentId agentId,
			Listener listener,
			boolean useOwnCollector,
			boolean safeForRefresh) {
        fAgentId = agentId;
        fListener = listener;
        fSendAgentDescriptor = true;
        fSafeToRefreshEntitiesRecursivelly = safeForRefresh;
		replaceContext(new ExecutionContext());
		if(useOwnCollector) {
			this.fCollector = createCollector();
		}
	}

	/**
	 * Constructor.
	 * @param agentId
	 * @param listener
	 */
	protected AbstractAgent(AgentId agentId, Listener listener) {
		this(agentId, listener, false, true);
	}

	/**
	 * Constructor.
	 * @param agentId
	 * @param listener
	 * @param useOwnCollector true to create and use a collector thread
	 * for this agent, false(default) to use the global collector thread
	 */
	protected AbstractAgent(AgentId agentId, Listener listener, boolean useOwnCollector) {
		this(agentId, listener, useOwnCollector, true);
	}

	/**
	 * Creates the private collector.
	 * @return
	 */
	protected Collector createCollector() {
		return new Collector("Collector for agent " + getClass().getName()) {
			protected void collect() throws Throwable {
				try {
					collectData();
				}catch(Throwable e) {
					fContext.error(e);
				}
			}
		};
	}

    /**
     * @return builds the descriptor for this agent
     */
    protected AgentDescriptor extractDescriptor() {
        return new AgentDescriptorImpl(this.fConfiguration, this.fAgentId);
    }

	/**
	 * Sublasses that need to replace the default context
	 * must invoke this method in their constructor.
	 * @param ctxt
	 */
	protected final void replaceContext(ExecutionContext ctxt) {
		this.fContext = ctxt;
		this.fRootEntity = new RootEntity(ctxt);
	}

	/**
	 * Note: this method doesn't have to be synched as the listener
	 * is set immediately after creation and it never changes
	 * throughout the life of the agent
	 * @see com.ixora.rms.agents.Agent#setListener(com.ixora.rms.Agent.Listener)
	 */
	public void setListener(Listener l)	{
		if(l == null) {
			throw new IllegalArgumentException("null listener");
		}
		fListener = l;
	}

	/**
	 * Default implementation for configuring agent's entities
	 * @see com.ixora.rms.agents.Agent#configureEntity(com.ixora.rms.EntityId, com.ixora.rms.struct.EntityConfiguration)
	 */
	public synchronized EntityDescriptorTree configureEntity(EntityId entity, EntityConfiguration conf)
		throws InvalidEntity, InvalidConfiguration, Throwable {
		// search aggressivelly
		Entity en = fRootEntity.findEntity(entity, true);
		if (en == null)
			throw new InvalidEntity(entity);
		en.configure(conf);
		boolean recursive = en.getConfiguration().hasRecursiveSettings();
		return en.extractDescriptorTree(recursive);
	}

	/**
	 * @see com.ixora.rms.agents.Agent#getEntities(com.ixora.rms.EntityId, boolean, boolean)
	 */
	public synchronized EntityDescriptorTree getEntities(EntityId idParent, boolean recursive, boolean refresh) throws InvalidEntity, Throwable {
		// find the parent
		Entity parent;
		if (idParent == null || idParent.equals(fRootEntity.getId())) {
			parent = fRootEntity;
		}
		else {
			// search aggressivelly
			parent = fRootEntity.findEntity(idParent, true);
		}
		if (parent == null) {
			return null;
		}
		// update children before extracting descriptor if the parent entity
		// supports lazy subtree creation
		if(refresh || parent.isLazilyLoadingEntityTree()) {
			parent.updateChildrenEntities(recursive);
		}
		return parent.extractDescriptorTree(recursive);
	}

	/**
	 * Returns the entire entity subtree starting at <code>idParent</code> without being
	 * aggressive (the subtree will contain only entities which are already in the tree)
	 * @param idParent
	 * @param recursive
	 * @throws Throwable
	 */
	protected EntityDescriptorTree getExistingEntities(EntityId idParent, boolean recursive) throws Throwable {
		// find the parent
		Entity parent;
		if (idParent == null || idParent.equals(fRootEntity.getId())) {
			parent = fRootEntity;
		}
		else {
			// search non-aggressivelly
			parent = fRootEntity.findEntity(idParent, false);
		}
		if (parent == null) {
			return null;
		}
		return parent.extractDescriptorTree(recursive);
	}

	/**
	 * Configures the agent.
	 * @see com.ixora.rms.agents.Agent#configure(com.ixora.rms.struct.AgentConfiguration)
	 */
	public synchronized EntityDescriptorTree configure(AgentConfiguration newConf)
		throws InvalidConfiguration, Throwable {
		// process the configuration first to replace tokens
		replaceTokensInConfiguration(newConf);
        // make sure the descriptor is sent again
        fSendAgentDescriptor = true;
		// this setting must be reaplied even if the current configuration
		// holds the same values
		Boolean enableAllCounters = newConf.getEnableAllCounters();

		if(this.fConfiguration == null) { // set activation settings
			this.fConfiguration = newConf;
			if(fConfiguration.usePrivateCollector()) {
				if(this.fCollector == null) {
					this.fCollector = createCollector();
				}
			}
			// Note: Order here is important!
			configValidateCustom();
			configCustomChanged();
			configMonitoringLevelChanged();
			configSamplingIntervalChanged();
		} else { // set runtime settings
			// apply differences between the old and the new config
			// sampling interval is always set
			BitSet bs = this.fConfiguration.applyDelta(newConf);
			if(bs.get(AgentConfiguration.BIT_CUSTOM)) {
				// ask subclasses to validate custom configuration
				configValidateCustom();
				configCustomChanged();
			}
			if(bs.get(AgentConfiguration.BIT_MONITORING_LEVEL)) {
				configMonitoringLevelChanged();
			}
			if(bs.get(AgentConfiguration.BIT_SAMPLING_INTERVAL)) {
				configSamplingIntervalChanged();
			}
		}
		if(enableAllCounters != null) {
			// enable all counters recursivelly
			EntityConfiguration eConf = new EntityConfiguration();
			eConf.setEnableAllCounters(enableAllCounters);
			eConf.setRecursiveEnableAllCounters(true);
			fRootEntity.configure(eConf);
		}
		if(fCollector != null) {
			fCollector.setCollectionInterval(1000 * fConfiguration.getSamplingInterval().intValue());
		}

		// decide here whether or not to retrieve the entire existing entity tree
		// this must happen only if all counters have been recursivelly
		// enabled or disabled or the monitoring level has been changed
		// (settings that have changed the state of the entities)
		boolean retrieveEntityTree = (enableAllCounters != null
				|| newConf.getMonitoringLevel() != null);
		return getExistingEntities(null, retrieveEntityTree);
	}

	/**
	 * This methods will replace tokens in the string parameters in the custom configuration given.
	 * @param newConf
	 */
	protected void replaceTokensInConfiguration(AgentConfiguration newConf) {
		if(newConf == null) {
			return;
		}
		CustomConfiguration conf = newConf.getCustom();
		if(conf == null) {
			return;
		}
		Collection<PropertyEntry> props = conf.getEntries().values();
		if(Utils.isEmptyCollection(props)) {
			return;
		}
		String host = fConfiguration == null ? newConf.getMonitoredHost() : fConfiguration.getMonitoredHost();
		for(PropertyEntry pe : props) {
			if(pe.getType() == TypedProperties.TYPE_STRING) {
				String val = (String)pe.getValue();
				if(!Utils.isEmptyString(val)) {
					String newVal = Utils.replace(val, TOKEN_HOST, host);
					conf.setString(pe.getProperty(), newVal);
				}
			}
		}
	}

	/**
	 * @see com.ixora.rms.agents.Agent#getConfiguration()
	 */
	public synchronized AgentConfiguration getConfiguration() {
		return this.fConfiguration;
	}

	/**
	 * @see com.ixora.rms.agents.Agent#getDescriptor()
	 */
	public synchronized AgentDescriptor getDescriptor() {
		return new AgentDescriptorImpl(this.fConfiguration, this.fAgentId, this.fSafeToRefreshEntitiesRecursivelly);
	}

	/**
	 * @see com.ixora.rms.agents.Agent#collectData()
	 */
	public synchronized void collectData() throws Throwable {
		// sanity check
		if(fCollector != null && !fCollector.ownsThread(Thread.currentThread())) {
			return;
		}
		AgentDataBufferImpl buffer = new AgentDataBufferImpl();
		fRootEntity.beginCycle();
		prepareBuffer(buffer);
		fRootEntity.endCycle();
		// send the data (note: buffer may be empty)
		fListener.receiveDataBuffer(buffer);
	}

	/**
	 * @see com.ixora.common.Startable#start()
	 */
	public synchronized void start() throws Throwable {
		this.fIsRunning = true;
		if(fCollector != null) {
			fCollector.startCollector();
		}
	}
	/**
	 * @see com.ixora.common.Startable#stop()
	 */
	public synchronized void stop() throws Throwable {
		this.fIsRunning = false;
		if(fCollector != null) {
			fCollector.stopCollector();
		}
	}

	/**
	 * Subclasses should perform here any cleanup operations.
	 * @see com.ixora.rms.agents.Agent#deactivate()
	 */
	public synchronized void deactivate() throws Throwable {
		; // nothing by default
	}

	/**
	 * @see com.ixora.rms.agents.Agent#requiresExternalCollector()
	 */
	public boolean requiresExternalCollector() {
		return fCollector == null;
	}

	/**
	 * @see com.ixora.rms.agents.Agent#setId(AgentId)
	 */
	public void setId(AgentId agentId) {
		this.fAgentId = agentId;
	}

	/**
	 * Invoked after <code>config()</code> was called if
	 * the monitoring level has changed.
	 */
	protected void configMonitoringLevelChanged() throws Throwable {
		// nothing
	}

	/**
	 * Invoked when the sampling interval has changed.
	 * @throws Throwable
	 */
	protected void configSamplingIntervalChanged() throws Throwable {
		; // nothing
	}

	/**
	 * Invoked after <code>config()</code> was called if
	 * the custom configuration has changed.
	 * @throws InvalidConfiguration
	 * @throws Throwable
	 */
	protected void configCustomChanged()
		throws InvalidConfiguration, Throwable {
		// nothing
	}

	/**
	 * Subclasses should check here if the new custom configuration
	 * is valid.
	 * @throws InvalidConfiguration
	 */
	protected void configValidateCustom()
		throws InvalidConfiguration {
		// nothing
	}

	/**
	 * Prepares the data buffer.
	 * @throws Throwable
	 */
	protected void prepareBuffer(AgentDataBufferImpl buffer) throws Throwable {
		buffer.setHost(this.fConfiguration.getMonitoredHost());
		buffer.setAgent(fAgentId);
		// this array contains data only from active entities
		List<EntityDataBufferImpl> listEDB = new LinkedList<EntityDataBufferImpl>();
		// see if we need to refresh all entities
		Integer interval = fConfiguration.getRefreshInterval();
		if(interval != null && interval > 0) {
			fRefreshPeriodCounter++;
			if(fRefreshPeriodCounter % interval == 0) {
				fRefreshPeriodCounter = 0;
				fRootEntity.updateChildrenEntities(fSafeToRefreshEntitiesRecursivelly);
				fRootEntity.fireChildrenEntitiesChanged(fSafeToRefreshEntitiesRecursivelly);
			}
		}
		// call each entity to place their data in the buffer
		for(Entity e : fRootEntity.getChildrenEntities()) {
			// don't let one entity failing to spoil the entire agent buffer
			try {
				e.prepareEntityDataBuffers(listEDB);
			} catch(Throwable ex) {
				// fire a non fatal error
				fContext.error(new RMSException("Failed to collect data for entity " + e.getId(), ex));
			}
		}
		// send null if no entity has data available
		if (listEDB.size() > 0) {
			buffer.setBuffers(listEDB.toArray(new EntityDataBufferImpl[listEDB.size()]));
		} else {
			buffer.setBuffers(null);
		}
        // send descriptor if needed
        if(fSendAgentDescriptor) {
            buffer.setAgentDescriptor(extractDescriptor());
            // this is because some listeners ignore empty buffers when
            // they shouldn't so this is just a safeguard
            if(!Utils.isEmptyArray(buffer.getBuffers())) {
            	fSendAgentDescriptor = false;
            }
        }
	}

	/**
	 * @return whether or not to sort entities
	 */
	protected boolean sortEntities() {
		return false;
	}
}
