/*
 * Created on 17-Oct-2004
 */
package com.ixora.rms.agents.db2;

import java.util.LinkedList;
import java.util.List;

import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.impl.RootEntity;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;


/**
 * DB2Entity
 * Performs main logic of retrieving and updating entities, child entities
 * and counters for a DB2 agent.
 */
public class DB2RootEntity extends RootEntity {
	private static final long serialVersionUID = -1137531563087778883L;
	private boolean fDirty = false;
    private static final String SEP = "`";

	protected static native int Attach(
			String user, String password, String instance)
		throws DB2NativeException;

	protected static native void Detach(int handle)
		throws DB2NativeException;

	protected static native void EnableMonitors(int connHandle, boolean aggAllNodes,
	        boolean monitorUOW,
	        boolean monitorStatement, boolean monitorTable, boolean monitorBP,
	        boolean monitorLock, boolean monitorSort, boolean monitorTimestamp)
		throws DB2NativeException;

	protected static native NativeEntity EnumSystemEntities(int handle)
		throws DB2NativeException;

	protected static native NativeEntity EnumDatabaseEntities(int handle, String dbName)
		throws DB2NativeException;

	/** Connection handle returned by Attach and used by other natives */
	protected int		connHandle = 0;
	/** Database name; if empty only system level properties will be returned */
	protected String	dbName = "";

	/**
	 * NativeCounter
	 * Holds the definition of a DB2 counter as provided by native code
	 */
	public static final class NativeCounter {
		String			name;
		CounterType		type;
		CounterValue	value;

		/**
		 * Constructs a native counter. Called from native code
		 * @param name untranslated counter name to display
		 * @param value counter value
		 */
		public NativeCounter(String name, CounterValue value) {
			this.name = name;
			this.value = value;
			if (value instanceof CounterValueDouble) {
		        this.type = CounterType.DOUBLE;
			} else {
				this.type = CounterType.STRING;
			}
		}
	}

	/**
	 * NativeEntity
	 * Holds the definition of a DB2 entity as provided by native code
	 */
	public static final class NativeEntity {
		String	name;
		List<NativeEntity>	listEntities = new LinkedList<NativeEntity>();
		List<NativeCounter>	listCounters = new LinkedList<NativeCounter>();

		public NativeEntity(String name) {
			this.name = name;
		}

		/**
		 * Called from native code: adds a counter definition to this entity
		 * @param counter counter to add
		 */
		public void addCounter(NativeCounter counter) {
			listCounters.add(counter);
		}

		/**
		 * Called from native code: adds a child entity to this entity
		 * @param entity child entity to add
		 */
		public void addEntity(NativeEntity entity) {
			listEntities.add(entity);
		}
	}

	/**
	 * DB2Counter
	 * Holds the definition of a DB2 counter as understood by the framework.
	 * Note that the native code sends counter values together with their names,
	 * so there's no need to collect data separately.
	 */
	public static class DB2Counter extends Counter {
		private static final long serialVersionUID = 3823002234277848589L;

		/**
	     * Builds a DB2Counter from a native definition.
	     * @param nc definition of counter, as provided by native code
	     */
		public DB2Counter(NativeCounter nc) {
			super(nc.name, nc.name+".desc", nc.type);
			updateData(nc);
		}

        /**
         * Called when a counter already exists: just update its details
         * @param nc NativeCounter to extract data from
         */
        public void updateData(NativeCounter nc) {
            this.reset(); // counter always holds one sample
            this.dataReceived(nc.value);
        }

	}

	/**
	 * DB2Entity
	 * Holds the definition of a DB2 entity as understood by the framework.
	 */
	public static class DB2Entity extends Entity {
		private static final long serialVersionUID = 6666349577813355717L;
		/**
	     * Constructs a DB2Entity from native data provided.
	     * @param id Id of parent entity to add this new entity to
	     * @param nativeEntity Native data to use as a definition
	     * @param ctxt agent execution context
	     * @throws Throwable for any errors
	     */
		/*public DB2Entity(
			EntityId id,
			NativeEntity nativeEntity,
			AgentExecutionContext ctxt) throws Throwable
		{
			super(new EntityId(id, nativeEntity.name), ctxt);
			// Initialize counters
			for (NativeCounter nc : nativeEntity.listCounters) {
				addCounter(new DB2Counter(nc));
			}
			// Initialize child entities
			for (NativeEntity ne : nativeEntity.listEntities) {
				addChildEntity(new DB2Entity(getId(), ne, ctxt));
			}
		}*/
		/**
		 * Constructs a DB2 entity with the name supplied, with no counters
		 * @param id
		 * @param name
		 * @param ctxt
		 * @throws Throwable
		 */
		public DB2Entity(
				EntityId id,
				NativeEntity nativeEntity,
				String[] namePath,
				int pathIndex,
				AgentExecutionContext ctxt) throws Throwable
		{
			super(new EntityId(id, namePath[pathIndex]), ctxt);

			// The last entity is the real one, so just add counters
			if (pathIndex >= namePath.length-1) {
				// Initialize counters
				for (NativeCounter nc : nativeEntity.listCounters) {
					addCounter(new DB2Counter(nc));
				}
				// Initialize child entities
				for (NativeEntity ne : nativeEntity.listEntities) {
				    String[] childNamePath = ne.name.split(SEP);
					addChildEntity(new DB2Entity(getId(), ne, childNamePath, 0, ctxt));
				}
			} else {
				// Take the next path component and search for such a child
				String 		childName = namePath[pathIndex];
				EntityId 	childId = new EntityId(getId(), childName);
				DB2Entity	oldEntity = (DB2Entity)getChildEntity(childId);

				// Did not exist? add it and all its children
		        if (oldEntity == null) {
		            oldEntity = new DB2Entity(getId(), nativeEntity, namePath, pathIndex+1, fContext);
		            addChildEntity(oldEntity);
		        }
			}
		}

		/**
		 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
		 */
		protected void retrieveCounterValues() throws Throwable {
			;
		}
		/**
		 * @see com.ixora.rms.agents.impl.EntityForest#testForChildren()
		 */
		protected boolean testForChildren() throws Throwable {
			return fChildrenEntities.size() > 0;
		}

        /**
         * Override just to increase visibility of this method: the DB2RootEntity needs
         * access to it.
         * @see com.ixora.rms.agents.impl.Entity#addChildEntity(com.ixora.rms.agents.impl.Entity)
         */
        public void addChildEntity(Entity entity) throws Throwable {
            super.addChildEntity(entity);
        }
        /**
         * Creates new counters (if any) for this already existing entity
         * @param ne NativeEntity to update with
         * @throws Throwable
         * @return true if the structure has changed (new entities or counters added)
         */
        public boolean updateData(NativeEntity ne,
				String[] namePath,
				int pathIndex) throws Throwable
        {
            boolean hasAddedNew = false;
			// The last entity is the real one, so just add counters
			if (pathIndex >= namePath.length-1) {
				// Initialize counters
				for (NativeCounter nc : ne.listCounters) {
					DB2Counter c = (DB2Counter)getCounter(new CounterId(nc.name));
					if (c != null) {
					    c.updateData(nc);
					} else {
					    addCounter(new DB2Counter(nc));
					    hasAddedNew = true;
					}
				}
				// Initialize child entities
				for (NativeEntity nchild : ne.listEntities) {
					// This entity may have been created already; search and update it
				    String[] 	childNamePath = nchild.name.split(SEP);
					DB2Entity	oldEntity = (DB2Entity)getChildEntity(new EntityId(getId(), childNamePath[0]));
					if (oldEntity != null) {
					    // overwrite counters of the old entity, rather than creating another
					    hasAddedNew = oldEntity.updateData(nchild, childNamePath, 0) || hasAddedNew;
					} else {
					    addChildEntity(new DB2Entity(getId(), nchild, childNamePath, 0, fContext));
					    hasAddedNew = true;
					}
				}
			} else {
				// Take the next path component and search for such a child
				String 		childName = namePath[pathIndex+1];
				EntityId 	childId = new EntityId(getId(), childName);
				DB2Entity	oldEntity = (DB2Entity)getChildEntity(childId);

				// Did not exist? add it and all its children
		        if (oldEntity == null) {
		            oldEntity = new DB2Entity(getId(), ne, namePath, pathIndex+1, fContext);
		            addChildEntity(oldEntity);
		            hasAddedNew = true;
		        } else {
		        	hasAddedNew = oldEntity.updateData(ne, namePath, pathIndex+1) || hasAddedNew;
		        }
			}
			return hasAddedNew;
        }
	}

	/**
	 * Default constructor
	 * @param c agent execution context
	 */
	public DB2RootEntity(AgentExecutionContext c) throws DB2AgentNotSupportedException {
		super(c);

		// Load native DLL (which in turn loads DB2 client dlls);
		// if not available then this agent is not supported.
		try {
			System.loadLibrary("db2perf");
		} catch (Throwable e) {
			throw new DB2AgentNotSupportedException(e);
		}
	}

	/**
	 * Refreshes entity tree and updates it with new entities and counters
	 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		// Don't do anything if not currently attached
		if (connHandle == 0)
			return;

		NativeEntity root;
		if (dbName == null || dbName.length() == 0) {
			root = EnumSystemEntities(connHandle);
		} else {
			root = EnumDatabaseEntities(connHandle, dbName);
		}

		// Will overwrite existing entity entries
		for (NativeEntity ne : root.listEntities) {
		    if (ne != null && ne.name != null) {
			    // Deal with entity paths
			    String entityNames[] = ne.name.split(SEP);

				// This entity may have been created already; search and update it
				EntityId childId = new EntityId(getId(), entityNames[0]);
				DB2Entity	oldEntity = (DB2Entity)getChildEntity(childId);
				if (oldEntity != null) {
					// overwrite counters of the old entity, rather than creating another
					fDirty = oldEntity.updateData(ne, entityNames, 0) || fDirty;
				} else {
				    addChildEntity(new DB2Entity(getId(), ne, entityNames, 0, fContext));
					fDirty = true;
				}
		    }
		}

		// remove the System(collected) entity as it is in the way of
		// the database entities (as far as regex selection is concerned as they
		// are on the same level in the tree) and it does not provide any useful info
		fChildrenEntities.remove(new EntityId("root/collected"));
	}

	/**
	 * Attaches to the DB2 instance. Will throw an exception
	 * if any problem occurs.
	 */
	public void beginSession() throws Throwable {
		endSession();

		Configuration cfg = (Configuration)fContext.getAgentConfiguration().getAgentCustomConfiguration();

		// Attach to instance
		String user = cfg.getString(Configuration.USERNAME);
		String password = cfg.getString(Configuration.PASSWORD);
		String instance = cfg.getString(Configuration.INSTANCE);
		this.dbName = cfg.getString(Configuration.DATABASE);

	    connHandle = Attach(user, password, instance);

	    // Enables / disables monitors as configured
		boolean monitorUOW = cfg.getBoolean(Configuration.UOW_SW);
		boolean monitorStatement = cfg.getBoolean(Configuration.STATEMENT_SW);
		boolean monitorTable = cfg.getBoolean(Configuration.TABLE_SW);
		boolean monitorBP = cfg.getBoolean(Configuration.BUFFER_POOL_SW);
		boolean monitorLock = cfg.getBoolean(Configuration.LOCK_SW);
		boolean monitorSort = cfg.getBoolean(Configuration.SORT_SW);
		boolean monitorTimestamp = cfg.getBoolean(Configuration.TIMESTAMP_SW);
		boolean aggAllNodes = cfg.getBoolean(Configuration.AGG_ALL_NODES);

		EnableMonitors(connHandle, aggAllNodes, monitorUOW, monitorStatement,
		        monitorTable,
		        monitorBP, monitorLock, monitorSort, monitorTimestamp);
	}

	/**
	 * Disables monitors and detaches from the DB2 instance
	 */
	public void endSession() throws Throwable {
		if (connHandle != 0) {
			Detach(connHandle);
			connHandle = 0;
		}
	}

	/**
	 * For each data collection cycle will refresh the entity tree,
	 * adding new entities and counters (if any) and updating counter values.
	 * @see com.ixora.rms.agents.impl.EntityForest#beginCycle()
	 */
	public void beginCycle() throws Throwable {
		try {
		    // Try to attach if currently disconnected
		    if (connHandle == 0) {
		        beginSession();
		    }

		    // Refresh entities and their counters. Only fires a changed event
		    // if at least one entity/counter has changed
		    fDirty = false;
			updateChildrenEntities(true);
			if (fDirty) {
		        try {
		            this.fContext.childrenEntitiesChanged(extractDescriptorTree(true));
				} catch (Throwable e) {
					fContext.error(e);
				}
			}

		} catch (DB2AgentException e) {
			// If we can't retrieve anything now maybe the DB2 instance
		    // wasn't started, or is otherwise (temporarily) unavailable.
			// Rather than throwing an exception we can just wait patiently.
        }
	}
}
