/*
 * Created on 22-Apr-2004
 */
package com.ixora.rms.agents.impl;

import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;

/**
 * All agents have an invisible root entity. This
 * class provides a convenient implementation to use
 * as a root for agents with static entities (agents whose
 * entities are known at compile time).
 * @author Daniel Moraru
 */
public class RootEntity extends Entity {
    public static final EntityId ROOT_ID = new EntityId("root");

    /**
	 * @param id
	 * @param c
	 */
	public RootEntity(AgentExecutionContext c) {
		super(ROOT_ID, c);
		// the root must always be enabled
		// and have children
		this.fEnabled = true;
		this.fHasChildren = true;
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
	 */
	protected void retrieveCounterValues() throws Throwable {
	}

	/**
	 * Just delegates to the base method, but increases the visibility
	 * to public. In other words outside clients can only add entities to
	 * the root entity.
	 * @throws Throwable
	 * @see com.ixora.rms.agents.impl.EntityTree#addChildEntity(com.ixora.rms.agents.Entity)
	 */
	public void addChildEntity(Entity entity) throws Throwable {
	    super.addChildEntity(entity);
	}

	/**
	 * Note: the search will be non-aggressive.
	 * @param eid
	 * @return true if any ancestor other than 'root' of the given entity is already
	 * in the tree represented by this root
	 * @throws Throwable
	 */
	public boolean isRootFor(EntityId eid) throws Throwable {
		EntityId[] nas = eid.getAncestors();
		EntityId[] as = new EntityId[nas.length + 1];
		int i = 0;
		for(; i < nas.length; ++i) {
			as[i] = nas[i];
		}
		as[i] = eid;
		for(i = 1; i < as.length; i++) {
			if(findEntity(as[i], false) != null) {
				return true;
			}
		}
		return false;
	}


	/**
	 * The default implementation will call <code>updateChildrenEntities(boolean)</code>
	 * on all children.
	 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		for(Entity e : fChildrenEntities.values()) {
			e.updateChildrenEntities(recursive);
		}
	}
}
