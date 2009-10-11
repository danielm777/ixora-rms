package com.ixora.rms.agents.impl;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterDescriptor;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDataBufferImpl;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityDescriptorImpl;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.EntityId;
import com.ixora.rms.MonitoringLevel;
import com.ixora.rms.RecordDefinition;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.RMSException;

/**
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public abstract class Entity extends EntityDescriptorImpl {
	private static final long serialVersionUID = 343839279957915379L;
	/**
	 * Logger. This must be used for debugging only. For any other reasons the errors
	 * must be propagated using the context.
	 */
	private static final AppLogger logger = AppLoggerFactory.getLogger(Entity.class);
	/** Counters */
	protected Map<CounterId, Counter> fCounters;
	/** Execution context */
	protected AgentExecutionContext fContext;
	/** Child entities keyed by EntityId */
	protected Map<EntityId, Entity> fChildrenEntities;
	/** Created only while the entity is enabled */
	protected RecordDefinition fRecordDefinition;
	/** True when all counters must be enabled */
	protected boolean fAllCountersMustBeEnabled;
	/**
	 * Whether or not the enabled counters have changed
	 * and we need to send the updated record definition
	 * with the entity buffer.
	 */
	protected boolean fSendRecordDefinition;
	/**
	 * Used during children updates to mark a child as updated so that
	 * stale children can be easily removed.
	 */
	protected boolean fTouchedByUpdate;
	/**
	 * The counter used to calculate when to refresh the children entities.
	 */
	private int fRefreshPeriodCounter;

	/**
	 * Provides preorder enumeration for the subtree starting at a given node.<br>
	 * The first node returned by the enumeration's nextElement() method is this node.
	 */
	// Copied from swing.TreeNode.
    final class PreorderEnumeration implements Enumeration<Entity> {
    	protected Stack<Enumeration<Entity>> stack;

    	public PreorderEnumeration(Entity rootNode) {
    	    super();
    	    ArrayList<Entity> v = new ArrayList<Entity>(1);
    	    v.add(rootNode);
    	    stack = new Stack<Enumeration<Entity>>();
    	    stack.push(Collections.enumeration(v));
    	}

    	public boolean hasMoreElements() {
    	    return (!stack.empty() &&
    		    stack.peek().hasMoreElements());
    	}

    	public Entity nextElement() {
    	    Enumeration<Entity>	enumer = stack.peek();
    	    Entity node = enumer.nextElement();
    	    Enumeration<Entity>	children = Collections.enumeration(node.getChildrenEntities());
    	    if (!enumer.hasMoreElements()) {
    	    	stack.pop();
    	    }
    	    if (children.hasMoreElements()) {
    	    	stack.push(children);
    	    }
    	    return node;
    	}
	}

	/**
	 * Default constructor to create the encapsulated entity with an
	 * empty list of counters. The name and description are obtained
	 * by RMS from the localized messages.
	 * @param uri The id of this entity
	 * @param c
	 */
	protected Entity(EntityId id, AgentExecutionContext c) {
		this(id, null, null, c);
	}

	/**
	 * Default constructor to create the encapsulated entity with an
	 * empty list of counters. Used for dynamic entities, where there
	 * will be no localized string for description.
	 *
	 * @param id
	 * @param description
	 * @param c
	 */
	protected Entity(EntityId id, String description, AgentExecutionContext c) {
		this(id, null, description, c);
	}

	/**
	 * @param id
	 * @param alternateName
	 * @param description
	 * @param c
	 */
	protected Entity(EntityId id,
			String alternateName,
			String description,
			AgentExecutionContext c) {
		super();
		this.fEntityId = id;
		this.fName = id.getName();
		this.fAlternateName = alternateName;
		this.fDescription = description;
		this.fCounters = new LinkedHashMap<CounterId, Counter>();
		this.fChildrenEntities = new LinkedHashMap<EntityId, Entity>();
		this.fContext = c;
		this.fConfiguration = new EntityConfiguration();
		this.fTouchedByUpdate = true;
	}


	/**
	 * Constructor.
	 * @param ed
	 * @param c
	 */
	public Entity(EntityDescriptor ed, AgentExecutionContext c) {
		this(ed.getId(), ed.getAlternateName(), ed.getDescription(), c);
		this.fHasChildren = ed.hasChildren();
		this.fLevel = ed.getLevel();
        this.fConfiguration = ed.getConfiguration();
        if(this.fConfiguration == null) {
            this.fConfiguration = new EntityConfiguration();
        }
		Collection<CounterDescriptor> cds = ed.getCounterDescriptors();
		for(CounterDescriptor cd : cds) {
			addCounter(new Counter(cd));
		}
	}

	/**
	 * Helper to search a counter by its id
	 * @param counterId
	 * @return
	 */
	public Counter getCounter(CounterId id) {
		return fCounters.get(id);
	}

	/**
	 * Default implementation for configuring an entity: sets the
	 * counters on/off as specified in the configuration details.
	 * As in the case of agent configuration the same class is used
	 * to update different parameters of the configuration.
	 * For instance if the list of counters is null only the other
	 * configuration details will be updated.
	 */
	public void configure(EntityConfiguration conf)
				throws InvalidConfiguration, Throwable	{
		BitSet bs = this.fConfiguration.applyDelta(conf);
		// NOTE: it is very important that the monitoring level will be
		// updated before the counters as only counters which apply to
		// current monitoring level will be enabled
		if(fSupportsIndependentSamplingInterval
				&& bs.get(EntityConfiguration.BIT_SAMPLING_INTERVAL)) {
			configSamplingIntervalChanged();
		}
		boolean monitoringLevelChanged = false;
		if(bs.get(EntityConfiguration.BIT_MONITORING_LEVEL)
				|| bs.get(EntityConfiguration.BIT_RECURSIVE_MONITORING_LEVEL)) {
			configMonitoringLevelChanged();
			monitoringLevelChanged = true;
		}
		// now do the counters
		if(bs.get(EntityConfiguration.BIT_COUNTERS)) {
			enableCounters(this.fConfiguration.getMonitoredCountersIds());
		}

		// these settings must be reaplied even if the current configuration
		// holds the same values
		Boolean enableAllCounters = conf.getEnableAllCounters();
		Boolean isRecursiveEnableAllCounters = conf.isRecursiveEnableAllCounters();

		// if a change in level occured and enabled all counters is set
		// then we must set again the enable flag for all counters
		if(monitoringLevelChanged) {
			// if this entity must have all it's counters enabled, reapply
			// do not override new value
			if(enableAllCounters == null) {
				// must reapply only if in current settings all counters are enabled
				if(fAllCountersMustBeEnabled) {
					enableAllCounters = Boolean.TRUE;
					if(isRecursiveEnableAllCounters == null) {
						isRecursiveEnableAllCounters = this.fConfiguration.isRecursiveEnableAllCounters();
					}
				}
			}
		}

		// see if we must enable or disable counters
		if(enableAllCounters != null) {
			boolean recursive = (isRecursiveEnableAllCounters == null ? false
					: isRecursiveEnableAllCounters.booleanValue());
			if(enableAllCounters.booleanValue()) {
				// must enable all counters for all possible entities
				if(recursive) {
					setEnabledAllCountersWithChildrenUpdate(true, recursive);
				} else {
					setEnabledAllCountersWithNoChildrenUpdate(true, recursive);
				}
			} else {
				// must disable all counters but only for existing entities
				setEnabledAllCountersWithNoChildrenUpdate(false, recursive);
			}
		}
	}

	/**
	 * Instructs this entity and all its children to collect whatever data
	 * they have to gathered and to place it in the supplied list.
	 * @param listEDBs
	 */
	public void prepareEntityDataBuffers(List<EntityDataBufferImpl> listEDBs) throws Throwable	{
		// if entity is enabled load counter values
		// in any case propagate call to children
		if(this.fEnabled) {
			// load counter values
			retrieveCounterValues();
			try {
				// find the number of active counters and their sample length
				// the sample length is the minimum non-zero sample size for all enabled counters
				int activeCounters = 0;
				int samplesLength = 0;
				for(Iterator<Counter> it = fCounters.values().iterator(); it.hasNext();) {
					Counter c = it.next();
					if(c.isEnabled()) {
						int size = c.getSamples().size();
						// ignore counters which are enabled but have no samples
						if(size > 0) {
							activeCounters++;
							if(samplesLength == 0) {
								samplesLength = size;
							} else {
								// get the minimum history to avoid problems, this shouldn't happen
								// under normal circumstances
								samplesLength = Math.min(samplesLength, size);
								if(logger.isTraceEnabled()) {
									if(samplesLength > size) {
										logger.error("Counter "
												+ c.getId()
												+ " has less samples than the others: "
												+ size);
									}
								}
							}
						}
					}
				}

				// no active counters or no history data => nothing for the list
				if(activeCounters != 0 && samplesLength != 0) {
					// prepare common data
					EntityDataBufferImpl edb = new EntityDataBufferImpl();
					edb.setEntityId(fEntityId);
					// fill the buffer with data from counters
					List<CounterValue[]> buffer = new LinkedList<CounterValue[]>();
					List<CounterId> fields = null;
					if(fSendRecordDefinition) {
						fields = new LinkedList<CounterId>();
					}
					for(Iterator<Counter> itC = fCounters.values().iterator(); itC.hasNext();) {
						// add data from the enabled ones
						Counter c = itC.next();
						if (c.isEnabled()) {
							boolean counterOk = true;
							// add counter samples
							CounterValue[] curr = new CounterValue[samplesLength];
							int samples = c.getSamples().size();
							if(samples >= samplesLength) {
								for(int i = 0; i < samplesLength; i++) {
									curr[i] = c.getSamples().get(i);
								}
							} else if(samples == 0){ // no samples
								counterOk = false;
								if(logger.isTraceEnabled()) {
									logger.error("Counter "
											+ fEntityId + EntityId.DELIMITER
											+ c.getId() + "is enabled but it has no samples");
								}
							} else { // less than required
								counterOk = false;
								if(logger.isTraceEnabled()) {
									logger.error("Counter "
											+ fEntityId + EntityId.DELIMITER
											+ c.getId() + "has less samples than required: Samples: " + samples
											+ " Required: " + samplesLength);
								}
							}
							if(counterOk) {
								buffer.add(curr);
								// add counter id if record definition is needed
								if(fSendRecordDefinition) {
									fields.add(c.getId());
								}
							}
						}
					}

					if(fSendRecordDefinition) {
						// create now the new record definition
						fRecordDefinition = new RecordDefinition();
						// set up record definition
						fRecordDefinition.setFields(fields.toArray(new CounterId[fields.size()]));
						fRecordDefinition.setEntityDescriptor(extractDescriptor());
						// add it to buffer
						edb.setDefinition(fRecordDefinition);
					}
					edb.setBuffer(buffer.toArray(new CounterValue[buffer.size()][]));
					edb.setTimestamp(System.currentTimeMillis());
					listEDBs.add(edb);
                    fSendRecordDefinition = false;
				}
			} finally {
				// reset all counters
				// NOTE: this is the only safe place to reset the counters
				// because entities are not enforced to load the value
				// of their counters in retrieveCounters()
				for(Iterator<Counter> itC = fCounters.values().iterator(); itC.hasNext();) {
					itC.next().reset();
				}
			}
		}

		// refresh children if necessary
		Integer interval = fConfiguration.getRefreshInterval();
		if(fCanRefreshChildren && interval != null && interval > 0) {
			if(interval > 0) {
				fRefreshPeriodCounter++;
				if(fRefreshPeriodCounter % interval == 0) {
					fRefreshPeriodCounter = 0;
					updateChildrenEntities(false);
					// IMPORTANT: this must always fire a subtree event
					// as some agents (some JMX agents for instance)
					// could update more than one level
					// in the tree when asked to update their children only
					fireChildrenEntitiesChanged(true);
				}
			}
		}

		// prepare all children's buffers
		for(Entity entity : fChildrenEntities.values()) {
			entity.prepareEntityDataBuffers(listEDBs);
		}
	}

	/**
	 * Adds a counter for this entity. Subclasses must invoke this method rather
	 * then adding counters directly to the map.
	 * @param c
	 */
	protected void addCounter(Counter c) {
		this.fCounters.put(c.getId(), c);
		this.fCounterDescriptors.put(c.getId(), c);
		if(this.fAllCountersMustBeEnabled) {
			c.setEnabled(true);
		}
	}

	/**
	 * Resets this entity's settings.
	 */
	protected void reset() {
		for(Iterator<Counter> it = fCounters.values().iterator(); it.hasNext();) {
			Counter cnt = it.next();
			cnt.setEnabled(false);
			cnt.reset();
		}
		// clear conf information
		this.fConfiguration = new EntityConfiguration();
		// mark this entity as disabled
		this.fEnabled = false;
		// clear the record definition
		this.fRecordDefinition = null;
	}

	/**
	 * Subclasses should do here whatever necessary to enable this entity as this method
	 * will be invoked whenever this entity must be enabled.
	 */
	protected void enable() {
		; // nothing
	}

	/**
	 * Subclasses should do here whatever necessary to disable this entity as this method
	 * will be invoked whenever this entity must be disabled.
	 */
	protected void disable() {
		; // nothing
	}

	/**
	 * This method must be invoked after setting the monitoring level so that maintanance work
	 * can be done on this entity and its descendands.
	 * @param rec
	 * @throws Throwable
	 */
	protected void reconfigureAfterChangingMonitoringLevel(boolean rec) throws Throwable {
		// we must go through all entities and:
		// enable the new set of counters that match the new level or
		// reenable all counters if enableAllCounters flag is set
		Enumeration<Entity> enu = preorderEnumeration();
		while(enu.hasMoreElements()) {
			Entity e = enu.nextElement();
			MonitoringLevel newLevel = e.fConfiguration.getMonitoringLevel();
			Boolean enableAllCounters = e.fConfiguration.getEnableAllCounters();
			if(enableAllCounters != null &&
					enableAllCounters.booleanValue()) {
				e.enableCounters(e.getCounterIdsForLevel(newLevel));
			} else {
				Set<CounterId> counters = e.fConfiguration.getMonitoredCountersIds();
				if(!Utils.isEmptyCollection(counters)) {
					Set<CounterId> newCounters = new HashSet<CounterId>();
					for(CounterId counterId : counters) {
						Counter counter = e.fCounters.get(counterId);
						if(counter == null) {
							throw new RMSException("Counter " + counterId + " not found");
						}
						if(counter.appliesToLevel(newLevel)) {
							newCounters.add(counterId);
						}
					}
					e.enableCounters(newCounters);
				}
			}
			// if not recursive stop after this
			// entity was reconfigured
			if(!rec) {
				if(e == this) {
					break;
				}
			}
		}
	}

	/**
	 * Subclasses must invoked this method instead of adding children directly to the map.
	 */
	protected void addChildEntity(Entity entity) throws Throwable {
		Boolean enableAllCounters = fConfiguration.getEnableAllCounters();
		Boolean isRecursiveEnableAllCounters = fConfiguration.isRecursiveEnableAllCounters();
		// if recursive configuration and in allCountersEnabled mode, enable all counters
		// of the newly added children
		if(this.fAllCountersMustBeEnabled
				&& enableAllCounters != null
				&& enableAllCounters.booleanValue()
				&& isRecursiveEnableAllCounters != null
				&& isRecursiveEnableAllCounters.booleanValue()) {
			entity.setEnabledAllCountersWithNoChildrenUpdate(true, true);
		}
		this.fChildrenEntities.put(entity.fEntityId, entity);
		this.fHasChildren = true;
	}

	/**
	 * Invoked when the monitoring level has changed. Implementations are responsible
	 * of handling recursivity of monitoring level.
	 * @throws Throwable
	 */
	protected void configMonitoringLevelChanged() throws Throwable {
		; // nothing
	}

	/**
	 * Invoked when the sampling interval has changed.
	 * @throws Throwable
	 */
	protected void configSamplingIntervalChanged() throws Throwable {
		; // nothing
	}

	/**
	 * Loads the new values for counters.
	 * @throws Throwable
	 */
	protected abstract void retrieveCounterValues() throws Throwable;

	/**
	 * Subclasses must update their descriptor and the list of children and their descriptors
	 * in this method.
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		; // nothing
	}

	/**
	 * By default the lazy creation of entity tree is considered; subclasses that
	 * load their entire entity tree at startup should override this and return false
	 * to improve performance by avoiding unnecessary calls to <code>updateChildrenEntities()</code>.
	 * @return true
	 */
	public boolean isLazilyLoadingEntityTree() {
		return true;
	}

	/**
	 * Enables the counters with the given counter ids.<br>
	 * Subclasses should only override this method when they need to do special
	 * processing when enabling/disabling counters.
	 * @param ec set of CounterId to enable
	 * @throws Throwable
	 */
	protected void enableCounters(Set<CounterId> ec) throws Throwable {
        // check if monitoring level for this entity needs to change in order to satisfy
        // the new list of counters and if so update
        MonitoringLevel currentLevel = fConfiguration.getMonitoringLevel();
        if(currentLevel != null) {
            MonitoringLevel newLevel = currentLevel;
            if(!Utils.isEmptyCollection(ec)) {
	            for(CounterId cid : ec) {
	                // check to see if the monitoring level of the entity
	                // needs to be upgraded
	                Counter c = fCounters.get(cid);
	                if(c.getLevel().compareTo(currentLevel) > 0) {
	                    newLevel = c.getLevel();
	                }
	            }
            }
            if(newLevel != currentLevel) {
                Boolean rec = fConfiguration.isRecursiveMonitoringLevel();
                if(rec != null && rec.booleanValue()) {
                    // disable temporarily the recursive flag
                	// so that the change in monitoring level wan't propagate
                	// to children entities
                    fConfiguration.setRecursiveMonitoringLevel(false);
                }

                // change monitoring level; because the recursive flag
                // has been disabled this will affect only this entity
                fConfiguration.setMonitoringLevel(newLevel);
                configMonitoringLevelChanged();

                if(rec != null && rec.booleanValue()) {
                	// change recursive flag back to the original value
                    fConfiguration.setRecursiveMonitoringLevel(rec);
                }
            }
        }

		// disable all counters first
		for(Iterator<Counter> i = fCounters.values().iterator(); i.hasNext();) {
			Counter c = i.next();
			c.setEnabled(false);
		}
		if(Utils.isEmptyCollection(ec)) {
			this.fRecordDefinition = null;
			this.fEnabled = false;
			// this entity was disabled, call disable hook
			disable();
			return;
		}
		// check and see if this entity is currently configured to have all it's counters
		// enabled and if the the new config does not contain all required counters
		// for the current monitoring level disable fAllCountersMustBeEnabled
		Collection<CounterId> allCountersForCurrentLevel = getCounterIdsForLevel(
				fConfiguration.getMonitoringLevel());
		if(fAllCountersMustBeEnabled && allCountersForCurrentLevel != null
				&& !ec.containsAll(allCountersForCurrentLevel)) {
			fAllCountersMustBeEnabled = false;
		}
		boolean ok = false;
		// and enable the ones in the given set
		for(CounterId cid : ec) {
			Counter c = fCounters.get(cid);
			if(c == null) {
				// that's an unexpected error
				// unknown counter
				InvalidConfiguration e = new InvalidConfiguration("Unknown counter: " + cid);
				e.setIsInternalAppError();
				throw e;
			}
			// enable it only if it applies to current monitoring level
			if(c.appliesToLevel(this.fConfiguration.getMonitoringLevel())) {
				c.setEnabled(true);
				ok = true;
			}
		}
		if(ok) {
			// mark entity as enabled
			this.fEnabled = true;
			// record definition changed, in the
			// next data collection append the new
			// record defintion to the entity buffer
			this.fSendRecordDefinition = true;
			// this entity was enabled, call enable hook
			enable();
		} else {
			this.fRecordDefinition = null;
			this.fEnabled = false;
			// this entity was disabled, call disable hook
			disable();
			return;
		}
	}

	/**
	 * Enables all counters that are already in the tree.<br>
	 * Subclasses should only override this method when they need to do special
	 * processing when enabling/disabling counters.
	 * @param enabled
	 * @param recursive
	 * @throws Throwable
	 */
	protected void setEnabledAllCountersWithChildrenUpdate(boolean enabled, boolean recursive) throws Throwable {
		if(isLazilyLoadingEntityTree()) {
			updateChildrenEntities(recursive);
		}
		setEnabledAllCountersWithNoChildrenUpdate(enabled, recursive);
	}

	/**
	 * @param enabled
	 * @param recursive
	 * @throws Throwable
	 */
	protected void setEnabledAllCountersWithNoChildrenUpdate(boolean enabled, boolean recursive) throws Throwable {
		this.fAllCountersMustBeEnabled = enabled;
		this.fConfiguration.setEnableAllCounters(enabled);
		this.fConfiguration.setRecursiveEnableAllCounters(recursive);
		if(enabled) {
			// enable all counters
			enableCounters(getCounterIdsForLevel(fConfiguration.getMonitoringLevel()));
		} else {
			// disable all counters
			enableCounters(null);
		}
		if(recursive) {
			for(Entity e : fChildrenEntities.values()) {
				e.setEnabledAllCountersWithNoChildrenUpdate(enabled, true);
			}
		}
	}

	/**
	 * Searches the entity tree for the one with the specified id.
	 * @param idEntity Id to search for
	 * @param aggressive if true the search will be aggressive, i.e <code>updateChildrenEntities()</code>
	 * will be invoked on any entity in the search path that has the flag <code>hasChildren</code>
	 * true but has no chidren.
	 * @return null if no such entity is found
	 */
	public Entity findEntity(EntityId idEntity, boolean aggressive) throws Throwable {
		// if no children don't bother
		if(!fHasChildren) {
			return null;
		}
		// if not on the search path, stop
		if(!fEntityId.isAncestorOf(idEntity)) {
			return null;
		}
		// ask entity to prepare it's children if it's suppose
		// to have any
		if(aggressive && fChildrenEntities.size() == 0) {
			if(isLazilyLoadingEntityTree()) {
				// check if we reached the maximum search depth
				int maxDepth = getMaximumRecursivityLevel();
				if(maxDepth > 0 && this.fEntityId.getPathLength() >= maxDepth) {
					return null;
				}
				updateChildrenEntities(false);
			}
		}
		for(Entity e : fChildrenEntities.values()) {
			// is this entity one of the roots?
			if(e.fEntityId.equals(idEntity)) {
				return e;
			}
			// delegate to child entities to search their children
			Entity child = e.findEntity(idEntity, aggressive);
			if(child != null) {
				return child;
			}
		}
		return null;
	}

	/**
	 * @return children entities
	 */
	public Collection<Entity> getChildrenEntities() {
		return Collections.unmodifiableCollection(fChildrenEntities.values());
	}

	/**
	 * @return the number of children
	 */
	public int getChildrenCount() {
		return this.fChildrenEntities.size();
	}

	/**
	 * @return the child with the given id
	 */
	public Entity getChildEntity(EntityId eid) {
	    return fChildrenEntities.get(eid);
	}

	/**
	 * @return the children entities descriptors
	 * @throws Throwable
	 */
	public Collection<EntityDescriptor> getChildrenEntitiesDescriptors() throws Throwable {
		Collection<EntityDescriptor> ret = new ArrayList<EntityDescriptor>(fChildrenEntities.size());
		for(Entity e : fChildrenEntities.values()) {
			ret.add(e.extractDescriptor());
		}
		return ret;
	}

	/**
	 * Removes the given child.
	 * @param eid
	 */
	protected void removeChildEntity(EntityId eid) {
		this.fChildrenEntities.remove(eid);
	}

	/**
	 * Create and return the entity descriptor. Makes a clone of
	 * the descriptor used as a base class, in order to send minimum
	 * of information through the network.
	 */
	public EntityDescriptor extractDescriptor() throws Throwable {
		if(testForChildren()) {
			fHasChildren = true;
		}
		// update configuration with the enabled counters
		Set<CounterId> enabledCounters = new HashSet<CounterId>();
		// update counter descriptors
		this.fCounterDescriptors.clear();
		for(Counter counter : fCounters.values()) {
			if(counter.isEnabled()) {
				enabledCounters.add(counter.getId());
			}
			this.fCounterDescriptors.put(counter.getId(), counter);
		}
		this.fConfiguration.setMonitoredCountersIds(enabledCounters);
		return (EntityDescriptorImpl)clone();
	}

	/**
	 * @param recursive
	 * @return a entity descriptor tree; if <code>recursive</code> is false it contains
	 * the descriptor of this entity plus the descriptors of it's children
	 */
	public EntityDescriptorTree extractDescriptorTree(boolean recursive) throws Throwable {
		EntityDescriptorTree ret = new EntityDescriptorTree(extractDescriptor());
		for(Iterator<Entity> iter = fChildrenEntities.values().iterator(); iter.hasNext();) {
			Entity child = iter.next();
			if(!recursive) {
				ret.addChild(new EntityDescriptorTree(child.extractDescriptor()));
			} else {
				// stop the recursivity if not safe to drill down
				if(!this.fSafeToRefreshRecursivelly) {
					recursive = false;
				}
				ret.addChild(child.extractDescriptorTree(recursive));
			}
		}
		return ret;
	}

	/**
	 * Invoked when a new monitoring cycle is begining.
	 * Used to signal down through the hierarchy the begining
	 * of a new cycle.
	 * Override this in your RootEntity to prevent cascading the signal to
	 * all children. Otherwise override onBeginCycle for each child Entity.
	 */
	public void beginCycle() throws Throwable {
		onBeginCycle();
		for(Entity e : fChildrenEntities.values()) {
			e.beginCycle();
		}
	}

	/**
	 * Invoked when the monitoring cycle has ended. This method will
	 * signal down through the hierarchy the end of a cycle.
	 * Override this in your RootEntity to prevent cascading the signal to
	 * all children. Otherwise override onEndCycle for each child Entity.
	 */
	public void endCycle() throws Throwable {
		onEndCycle();
		for(Entity e : fChildrenEntities.values()) {
			e.endCycle();
		}
	}

	/**
	 * Subclasses must invoke this method when their
	 * children have changed.
	 */
	protected void fireChildrenEntitiesChanged() {
		fireChildrenEntitiesChanged(false);
	}

	/**
	 * Subclasses must invoke this method when their children or the entire subtree has changed.
	 */
	protected void fireChildrenEntitiesChanged(boolean recursive) {
		try {
			this.fContext.childrenEntitiesChanged(extractDescriptorTree(recursive));
		} catch (Throwable e) {
			fContext.error(e);
		}
	}

	/**
	 * Invoked for each Entity before a new monitoring cycle begins.<p>
	 * Subclases can override this method if special work needs to be done
	 * just after before a monitoring cycle begins. This method will be called
	 * for each entity; if you want to prevent this override beginCycle
	 * on your RootEntity.
	 */
	protected void onBeginCycle() throws Throwable {
		;
	}

	/**
	 * Invoked for each Entity before a new monitoring cycle ends.<p>
	 * Subclases can override this method if special work needs to be done
	 * just after a monitoring cycle has ended. This method will be called
	 * for each entity; if you want to prevent this override beginCycle
	 * on your RootEntity.
	 */
	protected void onEndCycle() throws Throwable {
		;
	}

	/**
	 * Subclasses could implement here a small test to check whether or not the entity has children.<br>
	 * The default implementation returns the value of <code>fHasChildren</code>.
	 */
	protected boolean testForChildren() throws Throwable {
		return this.fHasChildren;
	}

	/**
	 * @return the maximum number of levels in the entity tree to inspect during aggressive searches
	 * and other recursive operations.
	 * In the case where the entity tree can be infinite subclasses must override this method and
	 * return a strictly positive value.
	 * The default value is 0 which means no limit.
	 */
	protected int getMaximumRecursivityLevel() {
		return 0;
	}

	/**
	 * @return true if the maximum depth in the entity tree
	 */
	protected boolean reachedMaximumRecursivityLevel() {
		int maxDepth = getMaximumRecursivityLevel();
		if(maxDepth > 0 && this.fEntityId.getPathLength() >= maxDepth) {
			return true;
		}
		return false;
	}

	/**
	 * Sorts the children entities by their id.
	 */
	protected void sortChildren() {
		// sort children
		TreeMap<EntityId, Entity> sorted = new TreeMap<EntityId, Entity>();
		for(Entity child : fChildrenEntities.values()) {
			sorted.put(child.getId(), child);
		}
		fChildrenEntities.clear();
		fChildrenEntities.putAll(sorted);
	}

	/**
	 * @return a preorder enumeration of the subtree starting with this entity.
	 */
	public Enumeration<Entity> preorderEnumeration() {
		return new PreorderEnumeration(this);
    }

	/**
	 * Invoke this method when an entity is updated during a call
	 * to its parent <code>updateChildrenEntities(boolean)</code>.
	 * @param
	 */
	protected void setTouchedByUpdate(boolean touched) {
		fTouchedByUpdate = touched;
	}

	/**
	 * @return true if this entity has been updated during it's parent
	 * <code>updateChildreEntities(boolean)</code>.
	 */
	public boolean isTouchedByUpdate() {
		return fTouchedByUpdate;
	}

	/**
	 * Helper method that can be invoked before updating the children entities.
	 */
	protected void resetTouchedByUpdateForChildren() {
		for(Entity child : fChildrenEntities.values()) {
			child.setTouchedByUpdate(false);
		}
	}

	/**
	 * Helper method that can be invoked after updating the children entities in order
	 * to remove stale children. <code>resetTouchedByUpdateForChildren()</code> must be invoked
	 * before updating the children in order for this method to have any effect.
	 */
	protected void removeStaleChildren() {
		for(Iterator<Entity> iter = fChildrenEntities.values().iterator(); iter.hasNext(); ) {
			Entity child = iter.next();
			if(!child.isTouchedByUpdate()) {
				iter.remove();
			}
		}
	}
}
