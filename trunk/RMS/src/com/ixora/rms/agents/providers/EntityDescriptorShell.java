/*
 * Created on 09-Apr-2005
 */
package com.ixora.rms.agents.providers;

import com.ixora.rms.EntityDescriptorImpl;
import com.ixora.rms.EntityId;

/**
 * Represents the 'shell' of an entity descriptor. i.e a descriptor
 * holding the minimum info on it's entity.
 * @author Daniel Moraru
 */
final class EntityDescriptorShell extends EntityDescriptorImpl {
	private static final long serialVersionUID = -4470278692395571229L;

	/**
	 * Constructor.
	 * @param id
	 * @param hasChildren
	 */
	public EntityDescriptorShell(EntityId id, boolean hasChildren) {
		super(id, hasChildren);
	}
}
