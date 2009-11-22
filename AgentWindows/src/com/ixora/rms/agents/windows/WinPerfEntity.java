package com.ixora.rms.agents.windows;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.data.CounterValue;

/**
 * WinPerfEntity The actual object which fits into the agent framework, gets
 * created from a NativeObject or a NativeInstance, depending on the level in
 * hierarchy.
 */
public class WinPerfEntity extends Entity {
	private static final long serialVersionUID = 5759170854665145731L;
	private String machine;
	private int nativeQuery;
	private int[] nativeHandles;
	private int[] nativeFormats;

	// Reusable objects
	private List<WinPerfCounter> activeCounters = new ArrayList<WinPerfCounter>();

	/**
	 * Create a root entity from a performance object and its _Total instance
	 * 
	 * @throws Throwable
	 */
	public WinPerfEntity(String machine, EntityId idParent,
			NativeObject nativeObject, AgentExecutionContext ctxt)
			throws Throwable {
		// Create a main entity with the _Total instance
		super(new EntityId(idParent, nativeObject.name),
				nativeObject.description, ctxt);
		this.machine = machine;
		// win entities are not able to update their own children,
		// setting up this flag, pushes the refresh operation back to
		// the root entity which updates the entire hierarchy
		fCanRefreshChildren = false;
		updateObject(nativeObject);
	}

	/**
	 * Create a child entity based on an instance of a performance object
	 */
	public WinPerfEntity(EntityId idParent, NativeObject nativeObject,
			NativeInstance nativeInstance, AgentExecutionContext ctxt) {
		super(new EntityId(idParent, nativeInstance.name), ctxt);

		updateInstance(nativeObject, nativeInstance);
	}

	/**
	 * Initializes counters for both object-level and instance-level entities.
	 * If nativeInstance is null then this is an object-level entity.
	 * 
	 * @param nativeObject
	 *            windows performance object
	 * @param nativeInstance
	 *            instance of the above object; may be null
	 */
	private void updateCounters(NativeObject nativeObject,
			NativeInstance nativeInstance) {
		for (int i = 0; i < nativeObject.counters.length; i++) {
			NativeCounter c = nativeObject.counters[i];
			// don't show hidden counters
			if (c != null
					&& (c.type & WinPerfConstants.PERF_DISPLAY_NOSHOW) != WinPerfConstants.PERF_DISPLAY_NOSHOW) {
				// Only add if it didn't exist
				if (this.getCounter(new CounterId(c.name)) == null) {
					String nativePath = WinPerfRootEntity.makeNativePath(
							this.machine, nativeObject.name,
							nativeInstance != null ? nativeInstance.name
									: nativeObject.totalInstanceName,
							nativeInstance != null ? nativeInstance.id : -1,
							c.name);
					addCounter(new WinPerfCounter(c, nativePath));
				}
			}
		}
	}

	/**
	 * Called either when creating or updating an entity from a performance
	 * object
	 * 
	 * @param nativeObject
	 *            performance object to turn into an entity (or update existing)
	 * @throws Throwable
	 */
	public void updateObject(NativeObject nativeObject) throws Throwable {
		setTouchedByUpdate(true);
		// Initialize counters
		if (nativeObject.totalInstanceName != null) {
			updateCounters(nativeObject, null);
		}
		// Add the other instances (or update, if any) as children
		if (!Utils.isEmptyArray(nativeObject.instances)) {
			// the new children entities
			resetTouchedByUpdateForChildren();
			for (int j = 0; j < nativeObject.instances.length; j++) {
				NativeInstance ni = nativeObject.instances[j];
				EntityId childId = new EntityId(getId(), ni.name);
				WinPerfEntity instanceEntity = (WinPerfEntity) fChildrenEntities
						.get(childId);
				if (instanceEntity == null) {
					// Create a new one
					addChildEntity(new WinPerfEntity(fEntityId, nativeObject,
							ni, fContext));
				} else {
					// Update children of an existing one
					instanceEntity.updateInstance(nativeObject, ni);
				}
			}
			removeStaleChildren();
		}
	}

	/**
	 * Called either when creating a new instance entity, or updating one
	 * 
	 * @param nativeObject
	 *            parent object of this instance
	 * @param nativeInstance
	 *            instance to create an entity from
	 */
	public void updateInstance(NativeObject nativeObject,
			NativeInstance nativeInstance) {
		updateCounters(nativeObject, nativeInstance);
		setTouchedByUpdate(true);
	}

	/**
	 * @see com.ixora.rms.agents.EntityDefault#retrieveCounterValues()
	 */
	protected void retrieveCounterValues() throws Throwable {
		// Get the performance data and place it in counters
		CounterValue[] data = WinPerfRootEntity.CollectQueryData(nativeQuery,
				nativeHandles, nativeFormats);
		if (data != null) {
			for (int j = 0; j < data.length; j++) {
				WinPerfCounter c = activeCounters.get(j);
				c.dataReceived(data[j]);
			}
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.EntityForest#testForChildren()
	 */
	protected boolean testForChildren() throws Throwable {
		return fChildrenEntities.size() > 0;
	}

	/**
	 * @throws Throwable
	 * @see com.ixora.rms.agents.impl.Entity#enableCounters(java.util.Set)
	 */
	protected void enableCounters(Set<CounterId> ec) throws Throwable {
		super.enableCounters(ec);

		// Close old query, if any, and open a new one
		nativeQuery = WinPerfRootEntity.ResetQuery(nativeHandles, nativeQuery);

		// Prepare an array of active counters
		activeCounters.clear();
		@SuppressWarnings("unused")
		boolean enableAll = true;
		for (Iterator<Counter> it = fCounters.values().iterator(); it.hasNext();) {
			WinPerfCounter c = (WinPerfCounter) it.next();
			if (c.isEnabled()) {
				activeCounters.add(c);
			} else {
				enableAll = false;
			}
		}

		// No counters? nothing to add
		if (activeCounters.size() < 1) {
			return;
		}

		// If all counters are enabled send a wildcard query rather
		// than an array of individual queries.
		String[] counters = null;
		// Extract their names into an array
		counters = new String[activeCounters.size()];
		nativeFormats = new int[activeCounters.size()];
		int i = 0;
		for (WinPerfCounter c : activeCounters) {
			counters[i] = c.getNativePath();
			nativeFormats[i] = c.getFormatType();
			i++;
		}
		nativeHandles = WinPerfRootEntity.SetCounters(machine, nativeQuery,
				counters);
	}
}