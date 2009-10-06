/*
 * Created on 19-Dec-2003
 */
package com.ixora.rms.client.model;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultMutableTreeNode;

import com.ixora.rms.ResourceId;

/**
 * The root node of the session model.
 * @author Daniel Moraru
 */
public final class SessionNode extends DefaultMutableTreeNode
		implements SessionModelTreeNode {
    /** Reference to the model */
	private SessionModel model;

	/**
	 * Constructor.
	 */
	public SessionNode() {
		super();
	}

	/**
	 * @return the scheme info.
	 */
	public SessionInfo getSessionInfo() {
		return (SessionInfo)this.userObject;
	}

	/**
	 * Use this to enumerate hosts rather then <code>children()</code>
	 * @return an enumeration with all hosts for this scheme
	 */
	public Enumeration hosts() {
		return children();
	}

// package

	/**
	 * @return the scheme info.
	 */
	SessionInfoImpl getSessionInfoImpl() {
		return (SessionInfoImpl)this.userObject;
	}

	/**
	 * @return the host node data for the given host or null if not found
	 */
	HostNode findHostNode(String host) {
		Enumeration e = children();
		while(e.hasMoreElements()) {
			HostNode element = (HostNode)e.nextElement();
			if(element.getHostInfo().getName().equals(host)) {
				return element;
			}
		}
		return null;
	}

	/**
	 * @return the host nodes matching the given regex
	 */
	List<ResourceNode> findHostNodes(String regex) {
		List<ResourceNode> ret = new LinkedList<ResourceNode>();
		Pattern p = Pattern.compile(regex);
		Matcher m;
		Enumeration e = children();
		while(e.hasMoreElements()) {
			HostNode element = (HostNode)e.nextElement();
			m = p.matcher(element.getHostInfo().getName());
			if(m.matches()) {
				ret.add(element);
			}
		}
		return ret;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
//		if(children != null && children.size() == 0) {
//			Throwable t = new Throwable();
//			System.out.println("SessionNode.toString() - no children");
//			t.printStackTrace();
//		}

		return ((SessionInfo)this.userObject).getName();
	}

	/**
	 * @return the session model
	 */
	SessionModel getModel() {
	    return this.model;
	}

	/**
	 * Sets the model.
	 * @param model
	 */
	void setModel(SessionModel model) {
	    this.model = model;
		this.userObject = new SessionInfoImpl(model);
	}

    /**
     * @param ridex
     * @param aggressive
     * @return a list of ResourceNodes that match the given
     * regex resource id.
     */
    public List<ResourcePath> getPathsMatching(ResourceId ridex, boolean aggressive) {
        List ret = new LinkedList();
		Enumeration e = children();
		while(e.hasMoreElements()) {
			HostNode hostNode = (HostNode)e.nextElement();
			hostNode.findPathsMatching(ridex, ret, aggressive);
		}
		return ret;
    }

    /**
     * @see com.ixora.rms.client.model.SessionModelTreeNode#getArtefactInfoContainer()
     */
    public ArtefactInfoContainer getArtefactInfoContainer() {
        return getSessionInfo();
    }

    /**
     * @see com.ixora.rms.client.model.SessionModelTreeNode#getResourceId()
     */
    public ResourceId getResourceId() {
        return null; // this is the rid corresponding to the root node
    }

    /**
     * @see com.ixora.rms.client.model.SessionModelTreeNode#hasEnabledDescendants()
     */
    public boolean hasEnabledDescendants() {
        Enumeration e = children();
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
