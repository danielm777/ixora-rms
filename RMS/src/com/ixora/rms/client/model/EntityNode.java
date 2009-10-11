package com.ixora.rms.client.model;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.tree.TreeNode;

import com.ixora.rms.HostId;
import com.ixora.rms.ResourceId;
import com.ixora.common.ui.DefaultCheckTreeNode;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityId;


/**
 * EntityDescriptor node in the hosts tree.
 * @author: Daniel Moraru
 */
public final class EntityNode extends DefaultCheckTreeNode
		implements ResourceNodeWithEntities {
	private static final long serialVersionUID = 3947441358009431146L;
	/** Agent node, the ancestor of this node */
	private AgentNode agentNode;

	/**
     * EntityNode constructor.
     * @param aparent
     * @param entity
     */
    public EntityNode(AgentNode aparent, EntityNode eparent, EntityDescriptor entity) {
        super(
        	new EntityInfoImpl(
                eparent != null ? eparent.getEntityInfoImpl() : null,
        		aparent.getAgentInfo().getInstallationDtls().getMessageCatalog(),
        		entity, entity.isEnabled(), aparent.getHostNode().getSchemeNode().getModel()),
				entity.isEnabled(),
				true,
				entity.hasChildren());
        this.agentNode = aparent;
    }

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
	    return this.userObject.toString();
	}

	/**
	 * @return the entity information
	 */
	public EntityInfo getEntityInfo() {
		return (EntityInfo)this.userObject;
	}

	/**
	 * @see com.ixora.common.ui.DefaultCheckTreeNode#setChecked(boolean)
	 */
	// override the parent's behaviour as we do not
	// want to select children and parent in this case
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/**
	 * @return the agentNode
	 */
	public AgentNode getAgentNode() {
		return agentNode;
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
        int ridLen = rid.getPathLength();
        int ridexLen = ridex.getPathLength();
        // stop if search space exceeded
        if(ridLen > ridexLen) {
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
            // first see if a counter must be searched
            if(ridex.getRepresentation() == ResourceId.COUNTER
                    && ridLen == ridexLen - 1) {
	            Collection<CounterInfo> cinfos = getEntityInfo().getCounterInfo();
	            CounterInfo ci;
	            for(Iterator<CounterInfo> iter = cinfos.iterator(); iter.hasNext();) {
	                ci = iter.next();
	                if(ridex.matchesCounterId(ci.getId())) {
	                    result.add(getResourcePath(ci));
	                }
	            }
            } else {
                // search children entities
                if(aggressive) {
                    retrieveChildren(this);
                }
                Enumeration<TreeNode> e = children();
                ResourceNode rn;
                while(e.hasMoreElements()) {
                	TreeNode tn = e.nextElement();
                	if(tn instanceof ResourceNode) {
	                    rn = (ResourceNode)tn;
	                    rn.findPathsMatching(ridex, result, aggressive);
                	}
                }
            }
        }
        return;
    }

    /**
     * @see com.ixora.rms.client.model.ResourceNode#getResourcePath()
     */
    public ResourcePath getResourcePath() {
        return new ResourcePath(
                this.getAgentNode().getHostNode(),
                this.getAgentNode(),
                this,
                null);
    }

    /**
     * @see com.ixora.rms.client.model.ResourceNode#getResourceId()
     */
    public ResourceId getResourceId() {
        return new ResourceId(
                new HostId(this.getAgentNode().getHostNode().getHostInfo().getName()),
                this.getAgentNode().getAgentInfo().getDeploymentDtls().getAgentId(),
                getEntityInfo().getId(),
                null);
    }

    /**
     * @see com.ixora.rms.client.model.SessionModelTreeNode#getArtefactInfoContainer()
     */
    public ArtefactInfoContainer getArtefactInfoContainer() {
        return getEntityInfo();
    }

    /**
     * @see com.ixora.rms.client.model.SessionModelTreeNode#hasEnabledDescendants()
     */
    @SuppressWarnings("unchecked")
	public boolean hasEnabledDescendants() {
		Enumeration<TreeNode> e = breadthFirstEnumeration();
		while(e.hasMoreElements()) {
			TreeNode tn = e.nextElement();
			if(tn instanceof EntityNode) {
				EntityNode node = (EntityNode)tn;
				if(node.getEntityInfoImpl().isEnabled()) {
				    return true;
				}
			}
		}
		return false;
    }

//	package access

	/**
	 * @param p pattern to match
	 * @return true if the path of the entity matches
	 * the given pattern
	 */
	boolean matches(Pattern p) {
		String path = getEntityInfoImpl().getId().getPath();
		Matcher m = p.matcher(path);
		if(m.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * @return an iterator over all the enabled descendant entities
	 */
	@SuppressWarnings("unchecked")
	Iterator<EntityNode> enabledDescendants() {
		List<EntityNode> ret = new LinkedList<EntityNode>();
		Enumeration<TreeNode> e = breadthFirstEnumeration();
		while(e.hasMoreElements()) {
			TreeNode tn = e.nextElement();
			if(tn instanceof EntityNode) {
				EntityNode node = (EntityNode)tn;
				if(node.getEntityInfoImpl().isEnabled()) {
					// skip this node;
					if(node != this) {
						ret.add(node);
					}
				}
			}
		}
		return ret.iterator();
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
	 * Sets the entity configuration.
	 * @param conf
	 */
	void setEntityConfiguration(EntityConfiguration conf) {
		EntityInfoImpl eii = ((EntityInfoImpl)this.userObject);
		eii.setConfiguration(conf);
	}


	/**
	 * @see com.ixora.common.ui.DefaultCheckTreeNode#isChecked()
	 */
	public boolean isChecked() {
		return ((EntityInfoImpl)this.userObject).isEnabled();
	}

	/**
	 * @param cid
	 * @return the counter descriptor for the counter with the given id
	 */
	CounterInfo findCounter(CounterId cid) {
		return getEntityInfoImpl().getCounterInfo(cid);
	}

	/**
	 * @param regex
	 * @return the counter info for counters
	 * whose ids matches the given regex
	 */
	List<CounterInfo> findCounters(String regex) {
		List<CounterInfo> ret = new LinkedList<CounterInfo>();
		Pattern p = Pattern.compile(regex);
		Collection<CounterInfo> counters = getEntityInfoImpl().getCounterInfo();
		for(Iterator<CounterInfo> iter = counters.iterator(); iter.hasNext();) {
			CounterInfo ci = iter.next();
			Matcher m = p.matcher(ci.getId().toString());
			if(m.matches()) {
				ret.add(ci);
			}
		}
		return ret;
	}

	/**
	 * @see com.ixora.rms.client.model.ResourceNodeWithEntities#findDescendant(com.ixora.rms.EntityId, boolean)
	 */
	@SuppressWarnings("unchecked")
	public EntityNode findDescendant(EntityId id, boolean aggressive) {
	    // take the shortest path
	    // if this is it return this
	    EntityId eid = getEntityInfo().getId();
	    if(eid.equals(id)) {
	        return this;
	    }
	    // see if the id to be search applies to this nodes path
	    if(!eid.isAncestorOf(id)) {
	        return null;
	    }
	    // it's not this node but this one is an ancestor for
	    // the looked up entity so now get children...
		if(aggressive) {
			retrieveChildren(this);
		}
	    Enumeration<TreeNode> e = children();
		EntityNode node, ret;
		while(e.hasMoreElements()) {
			TreeNode tn = e.nextElement();
			if(tn instanceof EntityNode) {
				node = (EntityNode)tn;
				ret = node.findDescendant(id, aggressive);
				if(ret != null) {
				    return ret;
				}
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	EntityInfoImpl getEntityInfoImpl() {
		return ((EntityInfoImpl)this.userObject);
	}

	/**
	 * Aggressively retrieves children for the given entity
	 * using the monitoring session.
	 * @param node
	 */
	private void retrieveChildren(EntityNode node) {
		// test and see if we need to retrieve
		// the children
		if(node.getEntityInfo().hasChildren()
			&& node.getChildEntityCount() == 0) {
		    getAgentNode().getHostNode().getSchemeNode()
		    	.getModel().retrieveEntityChildrenForNode(this);
		}
	}

    /**
     * @return the resource path for the given counter
     */
    private ResourcePath getResourcePath(CounterInfo ci) {
        return new ResourcePath(
                this.getAgentNode().getHostNode(),
                this.getAgentNode(),
                this,
                ci);
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