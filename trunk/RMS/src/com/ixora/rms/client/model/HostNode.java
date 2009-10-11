package com.ixora.rms.client.model;

import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import com.ixora.rms.HostId;
import com.ixora.rms.HostInformation;
import com.ixora.rms.HostReachability;
import com.ixora.rms.HostState;
import com.ixora.rms.ResourceId;
import com.ixora.common.remote.ServiceState;
import com.ixora.rms.agents.AgentId;


/**
 * Host node in the hosts tree.
 * @author: Daniel Moraru
 */
public final class HostNode extends DefaultMutableTreeNode implements ResourceNode {
	private static final long serialVersionUID = -5272245546929547825L;
	/** Resusable string buffer */
	private StringBuffer buff;

    /**
     * HostNode constructor.
     * @param info
     * @param model
     */
    public HostNode(HostInformation info, HostState state, SessionModel model) {
        super(new HostInfoImpl(info, state, model), true);
        buff = new StringBuffer();
    }

	/**
	 * HostNode constructor.
	 * @param host
	 * @param model
	 */
	public HostNode(String host, SessionModel model) {
		super(new HostInfoImpl(null, new HostState(host), model), true);
		buff = new StringBuffer();
	}

	/**
	 * @return the host info
	 */
	 public HostInfo getHostInfo() {
		 return (HostInfo)this.userObject;
	 }

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		HostState state = ((HostInfoImpl)this.userObject).getState();
		ServiceState ss = state.getServiceState(HostReachability.HOST_MANAGER);
		buff.delete(0, buff.length());
		buff.append(state.getHost());
		buff.append("[HM:");
		buff.append(ss == null || ss == ServiceState.UNKNOWN ? "-" : ss.toString());
		buff.append("]");
		ss = state.getServiceState(HostReachability.ICMP_PING);
		buff.append("[P:");
		buff.append(ss == null || ss == ServiceState.UNKNOWN ? "-" : ss.toString());
		buff.append("]");

		return buff.toString();
	}

	/**
	 * Use this to enumerate agents rather then <code>children()</code>
	 * as a host will also contain performance, probe and service children.
	 * @return an enumeration with all monitoring agents for this host
	 */
	@SuppressWarnings("unchecked")
	public Enumeration<AgentNode> agents() {
		return children();
	}

// package
	/**
	 * @return the agent node data with the given id
	 */
	@SuppressWarnings("unchecked")
	AgentNode findAgentNode(AgentId agentId) {
		Enumeration<AgentNode> e = children();
		while(e.hasMoreElements()) {
			AgentNode element = e.nextElement();
			if(agentId.equals(element.getAgentInfo().getDeploymentDtls()
					.getAgentId())) {
				return element;
			}
		}
		return null;
	}

	/**
	 * Sets host info.
	 * @param info
	 */
	void setHostInfo(HostInformation info) {
		((HostInfoImpl)this.userObject).setInfo(info);
	}

	/**
	 * Sets host state.
	 * @param state
	 */
	void setHostState(int serviceID, ServiceState state) {
		((HostInfoImpl)this.userObject).getState().setServiceState(serviceID, state);
	}

	/**
	 * Adds an agent node.
	 * @param node
	 * @return false if the agent was already a child, true if
	 * it was added successfuly
	 */
	boolean add(AgentNode node) {
		AgentId agentId = node.getAgentInfo().getDeploymentDtls()
				.getAgentId();
		if(findAgentNode(agentId) != null) {
			return false;
		}
		super.add(node);
		return true;
	}

	/**
	 * @return the host info
	 */
	 HostInfoImpl getHostInfoImpl() {
		 return (HostInfoImpl)this.userObject;
	 }

	/**
	 * @return the scheme node which is the parent
	 * of this node
	 */
	public SessionNode getSchemeNode() {
		return (SessionNode)this.parent;
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
            Enumeration<ResourceNode> e = children();
            ResourceNode rn;
            while(e.hasMoreElements()) {
                rn = e.nextElement();
                rn.findPathsMatching(ridex, result, aggressive);
            }
        }
        return;
    }

    /**
     * @see com.ixora.rms.client.model.ResourceNode#getResourcePath()
     */
    public ResourcePath getResourcePath() {
        return new ResourcePath(this, null, null, null);
    }

    /**
     * @see com.ixora.rms.client.model.ResourceNode#getResourceId()
     */
    public ResourceId getResourceId() {
        return new ResourceId(new HostId(getHostInfo().getName()),
                null, null, null);
    }

    /**
     * @see com.ixora.rms.client.model.SessionModelTreeNode#getArtefactInfoContainer()
     */
    public ArtefactInfoContainer getArtefactInfoContainer() {
        return getHostInfo();
    }

    /**
     * @see com.ixora.rms.client.model.SessionModelTreeNode#hasEnabledDescendants()
     */
    @SuppressWarnings("unchecked")
	public boolean hasEnabledDescendants() {
        Enumeration<SessionModelTreeNode> e = children();
        SessionModelTreeNode sn;
        while(e.hasMoreElements()) {
            sn = (SessionModelTreeNode)e.nextElement();
            if(sn.hasEnabledDescendants()) {
                return true;
            }
        }
        return false;
    }
}