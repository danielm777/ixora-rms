/*
 * Created on 07-Aug-2004
 */
package com.ixora.rms.repository;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.rms.ResourceId;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLAttributeMissing;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;

/**
 * A group of queries.
 * @author Daniel Moraru
 */
public final class Dashboard extends VersionableAgentArtefactAbstract implements AuthoredArtefact {
	private static final long serialVersionUID = -3786962747123778552L;
	/** Dashboard name */
	private String name;
	/** Dashboard description */
	private String description;
	/** Data views that are part of this dashboard */
	private DataViewId[] views;
	/** Counters that are part of this dashboard */
	private ResourceId[] counters;
	/** The author of this artefact */
	private String author;

	/**
	 * Default constructor to support XML.
	 */
	public Dashboard() {
        super();
	}

	/**
	 * Constructor.
	 * @param name
	 * @param description
	 * @param views
	 * @param counters
     * @param agentVersions
	 */
	public Dashboard(String name, String description, String author,
			List<DataViewId> views, List<ResourceId> counters, Set<String> agentVersions) {
        super(agentVersions);
	    this.name = name;
	    this.description = description;
	    this.author = author;
	    if(views != null) {
	        this.views = views.toArray(new DataViewId[views.size()]);
        }
        if(counters != null) {
            this.counters = counters.toArray(new ResourceId[counters.size()]);
        }
	}

    /**
     * @return the group description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the group name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the dashboard views.
     */
    public DataViewId[] getViews() {
        return views;
    }

    /**
     * @return the dashboard counters.
     */
    public ResourceId[] getCounters() {
        return counters;
    }

    /**
     * @see com.ixora.rms.repository.VersionableAgentArtefact#getArtefactName()
     */
    public String getArtefactName() {
        return this.name;
    }

    /**
     * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
     */
    public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element elDashboard = doc.createElement("dashboard");
		parent.appendChild(elDashboard);
		// save name and description
		Element el = doc.createElement("name");
		elDashboard.appendChild(el);
		el.appendChild(doc.createTextNode(name));
		el = doc.createElement("description");
		elDashboard.appendChild(el);
		el.appendChild(doc.createTextNode(description));
		Element elQueries = doc.createElement("views");
		elDashboard.appendChild(elQueries);
		// save author
		if(author != null) {
			el = doc.createElement("author");
			elDashboard.appendChild(el);
			el.appendChild(doc.createTextNode(author));
		}
		// save view ids
		if(views != null) {
			for(DataViewId dvid : views) {
	            el = doc.createElement("view");
	            elQueries.appendChild(el);
	            el.setAttribute("id", dvid.toString());
	        }
		}
		Element elCounters = doc.createElement("counters");
		elDashboard.appendChild(elCounters);
		// save counters
		if(counters != null) {
			for(ResourceId rid : counters) {
	            el = doc.createElement("counter");
	            elCounters.appendChild(el);
	            el.setAttribute("id", rid.toString());
	        }
		}
        super.toXML(elDashboard);
    }

    /**
     * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
     */
    public void fromXML(Node node) throws XMLException {
        super.fromXML(node);
		Node n = XMLUtils.findChild(node, "name");
		if(n == null) {
			throw new XMLNodeMissing("name");
		}
		this.name = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "description");
		if(n == null) {
			throw new XMLNodeMissing("description");
		}
		this.description = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "author");
		if(n != null) {
			this.author = XMLUtils.getText(n);
		}
		// views
		n = XMLUtils.findChild(node, "views");
		if(n != null) {
			List<Node> nl = XMLUtils.findChildren(n, "view");
			if(!Utils.isEmptyCollection(nl)){
				this.views = new DataViewId[nl.size()];
				int i = 0;
				for(Iterator<Node> iter = nl.iterator(); iter.hasNext(); ++i) {
		            n = iter.next();
		            Attr a = XMLUtils.findAttribute(n, "id");
		            if(a == null) {
		                throw new XMLAttributeMissing("id");
		            }
		            this.views[i] = new DataViewId(a.getNodeValue());
		        }
			}
		}
		// counters
		n = XMLUtils.findChild(node, "counters");
		if(n != null) {
			List<Node> nl = XMLUtils.findChildren(n, "counter");
			if(!Utils.isEmptyCollection(nl)){
				this.counters = new ResourceId[nl.size()];
				int i = 0;
				for(Iterator<Node> iter = nl.iterator(); iter.hasNext(); ++i) {
		            n = (Node)iter.next();
		            Attr a = XMLUtils.findAttribute(n, "id");
		            if(a == null) {
		                throw new XMLAttributeMissing("id");
		            }
		            this.counters[i] = new ResourceId(a.getNodeValue());
		        }
			}
		}
    }

	/**
	 * @see com.ixora.rms.repository.AuthoredArtefact#getAuthor()
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @see com.ixora.rms.repository.AuthoredArtefact#isSystem()
	 */
	public boolean isSystem() {
		return Utils.isEmptyString(author) || SYSTEM.equalsIgnoreCase(author);
	}

	/**
	 * @see com.ixora.rms.repository.AuthoredArtefact#setAuthor(java.lang.String)
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
}
