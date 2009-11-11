/*
 * Created on 18-Aug-2004
 */
package com.ixora.rms.client.model;

import javax.swing.tree.TreeNode;

import com.ixora.rms.ResourceId;

/**
 * Interface implemented by all nodes in the session tree.
 * @author Daniel Moraru
 */
public interface SessionModelTreeNode extends TreeNode {
    /**
     * @return the artefact info container for this node
     */
    public ArtefactInfoContainer getArtefactInfoContainer();

    /**
     * @return the resource id that corresponds to this node.
     */
    ResourceId getResourceId();

    /**
     * @return true if this node has enabled descendants
     */
    boolean hasEnabledDescendants();
}
