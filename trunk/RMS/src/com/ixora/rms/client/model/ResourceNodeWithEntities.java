/*
 * Created on 20-Dec-2004
 */
package com.ixora.rms.client.model;

import javax.swing.tree.MutableTreeNode;

import com.ixora.rms.EntityId;

/**
 * @author Daniel Moraru
 */
public interface ResourceNodeWithEntities extends ResourceNode, MutableTreeNode {
	/**
	 * @param id
	 * @return the descendant with the given
	 * id
	 */
	EntityNode findChild(EntityId id);
	/**
	 * @param id
	 * @param aggressive if true the model
	 * will use the monitoring session to retrieve
	 * children entities from childless parents
	 * that are supposed to have children
	 * @return the descendant with the given
	 * id
	 */
	EntityNode findDescendant(EntityId id, boolean aggressive);
}
