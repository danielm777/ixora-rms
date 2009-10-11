package com.ixora.rms.client.model;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import com.ixora.rms.HostId;
import com.ixora.rms.ResourceId;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentState;
import com.ixora.rms.client.AgentInstanceData;
import com.ixora.rms.repository.AgentInstallationData;


/**
 * Agent node in the hosts tree.
 * @author: Daniel Moraru
 */
public final class AgentNode extends DefaultMutableTreeNode
			implements ResourceNodeWithEntities {
	private static final long serialVersionUID = 5601572853552532003L;

	/**
     * AgentNode constructor.
     * @param idtls
     * @param ddtls
     * @param model
     */
    public AgentNode(AgentInstallationData idtls, AgentInstanceData ddtls, SessionModel model) {
        super(new AgentInfoImpl(idtls, ddtls, AgentState.READY, model), true);
    }

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
        return this.userObject.toString();
	}

	/**
	 * @return the host node which is the ancestor
	 * of this agent node
	 */
	public HostNode getHostNode() {
		return (HostNode)this.parent;
	}

	/**
	 * @return the agent information
	 */
	public AgentInfo getAgentInfo() {
		return (AgentInfo)this.userObject;
	}

    /**
     * @see com.ixora.rms.client.model.ResourceNode#findPathsMatching(com.ixora.rms.ResourceId, java.util.List, boolean)
     */
    @SuppressWarnings("unchecked")
	public void findPathsMatching(ResourceId ridex, List<ResourcePath> result, boolean aggressive) {
        if(!ridex.isValid()) {
            return;
        }
        ResourceId rid = getResourceId();
        // stop if search space exceeded
        if(rid.getPathLength() > ridex.getPathLength()) {
            return;
        }
        // stop if match
        if(ridex.matches(rid)) {
            result.add(getResourcePath());
            return;
        }
        // see if we are on the search path, if so
        // keep searching through the children
        if(ridex.contains(rid)) {
            retrieveChildren();
            Enumeration<TreeNode> e = children();
            while(e.hasMoreElements()) {
            	TreeNode tn = (TreeNode)e.nextElement();
            	if(tn instanceof ResourceNode) {
	            	ResourceNode rn = (ResourceNode)tn;
	                rn.findPathsMatching(ridex, result, aggressive);
            	}
            }
        }
        return;
    }

	/**
	 * @see com.ixora.rms.client.model.ResourceNodeWithEntities#findChild(com.ixora.rms.EntityId)
	 */
	@SuppressWarnings("unchecked")
	public EntityNode findChild(EntityId id) {
		Enumeration<TreeNode> e = children();
		while(e.hasMoreElements()) {
			TreeNode tn = e.nextElement();
			if(tn instanceof EntityNode) {
				EntityNode node = (EntityNode)tn;
				if(node.getEntityInfoImpl().getId().equals(id)) {
					return node;
				}
			}
		}
		return null;
	}

	/**
	 * @see com.ixora.rms.client.model.ResourceNodeWithEntities#findDescendant(com.ixora.rms.EntityId, boolean)
	 */
	@SuppressWarnings("unchecked")
	public EntityNode findDescendant(EntityId id, boolean aggressive) {
		// search all children first
		if(aggressive) {
			retrieveChildren();
		}
		Enumeration<TreeNode> e = children();
		while(e.hasMoreElements()) {
			TreeNode tn = e.nextElement();
			if(tn instanceof EntityNode) {
				EntityNode node = (EntityNode)tn;
				node = node.findDescendant(id, aggressive);
				if(node != null) {
					return node;
				}
			}
		}
		return null;
	}

	/**
	 * Use this to enumerate all entities.
	 * @return an enumeration with all entities for this agent
	 */
	@SuppressWarnings("unchecked")
	public Enumeration<EntityNode> entities() {
		// TODO revisit, possible memory problems
		// but we can't assume all children are entities
		List<EntityNode> ret = new LinkedList<EntityNode>();
		Enumeration<TreeNode> en = breadthFirstEnumeration();
		// get rid off this node
		en.nextElement();
		while(en.hasMoreElements()) {
			TreeNode tn = (TreeNode)en.nextElement();
			if(tn instanceof EntityNode) {
				ret.add((EntityNode)tn);
			}
		}
		return Collections.enumeration(ret);
	}

    /**
     * @see com.ixora.rms.client.model.ResourceNode#getResourcePath()
     */
    public ResourcePath getResourcePath() {
        return new ResourcePath(this.getHostNode(), this, null, null);
    }

    /**
     * @see com.ixora.rms.client.model.ResourceNode#getResourceId()
     */
    public ResourceId getResourceId() {
        return new ResourceId(
                new HostId(this.getHostNode().getHostInfo().getName()),
                this.getAgentInfo().getDeploymentDtls().getAgentId(),
                null, null);
    }

    /**
     * @see com.ixora.rms.client.model.SessionModelTreeNode#getArtefactInfoContainer()
     */
    public ArtefactInfoContainer getArtefactInfoContainer() {
        return getAgentInfo();
    }

    /**
     * @see com.ixora.rms.client.model.SessionModelTreeNode#hasEnabledDescendants()
     */
    @SuppressWarnings("unchecked")
	public boolean hasEnabledDescendants() {
        Enumeration<TreeNode> e = children();
        while(e.hasMoreElements()) {
        	TreeNode tn = e.nextElement();
        	if(tn instanceof SessionModelTreeNode) {
	        	SessionModelTreeNode sn = (SessionModelTreeNode)tn;
	            if(sn.hasEnabledDescendants()) {
	                return true;
	            }
        	}
        }
        return false;
    }

// package access
	 /**
	  * @return
	  */
	 AgentInfoImpl getAgentInfoImpl() {
	 	return ((AgentInfoImpl)this.userObject);
	 }

	/**
	 * Aggressively retrieves the entities for this agent using the
	 * monitoring session.
	 */
	private void retrieveChildren() {
		if(this.getChildEntityCount() == 0) {
			// if no children go and retrieve
			// them using the monitoring session
		    getHostNode().getSchemeNode().getModel().retrieveEntityChildrenForNode(this);
		}
	}

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
	private int getChildEntityCount() {
    	int ret = 0;
    	Enumeration<TreeNode> enumer = children();
    	while(enumer.hasMoreElements()) {
			TreeNode tn = enumer.nextElement();
			if(tn instanceof EntityNode) {
				ret++;
			}
		}
    	return ret;
    }
}