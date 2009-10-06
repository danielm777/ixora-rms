/*
 * Created on 16-Jan-2005
 */
package com.ixora.rms;

import java.util.LinkedHashMap;

import com.ixora.common.TreeNode;

/**
 * @author Daniel Moraru
 */
public final class EntityDescriptorTree extends TreeNode<EntityId, EntityDescriptorTree> {
	/** Descriptor */
	private EntityDescriptor fDescriptor;

	/**
	 * Constructor.
	 * @param d
	 */
	public EntityDescriptorTree(EntityDescriptor d) {
		super();
		fChildren = new LinkedHashMap<EntityId, EntityDescriptorTree>();
		fDescriptor = d;
	}

	/**
	 * @see com.ixora.common.TreeNode#getKey()
	 */
	protected EntityId getKey() {
		return fDescriptor.getId();
	}

	/**
	 * @return the entity descriptor.
	 */
	public EntityDescriptor getEntityDescriptor() {
		return fDescriptor;
	}

	/**
	 * Merges the given tree with this one. Priorities are given
	 * to entities found in this tree.<br>
	 * Note: Both trees must have as root an entity with the same id
	 * @param tree
	 */
	public void merge(EntityDescriptorTree tree) {
		if(!fDescriptor.getId().equals(tree.fDescriptor.getId())) {
			throw new IllegalArgumentException("Can only merge trees with the same root. Conflict: "
					+ fDescriptor.getId() + " : " + tree.fDescriptor.getId());
		}
		for(EntityDescriptorTree child : tree.fChildren.values()) {
			EntityId eid = child.getEntityDescriptor().getId();
			if(getChild(eid) == null) {
				fChildren.put(eid, child);
			}
		}
		for(EntityDescriptorTree child : fChildren.values()) {
			EntityId eid = child.getEntityDescriptor().getId();
			EntityDescriptorTree otherChild = tree.getChild(eid);
			if(otherChild != null) { // should never be null
				child.merge(otherChild);
			}
		}
	}
}
