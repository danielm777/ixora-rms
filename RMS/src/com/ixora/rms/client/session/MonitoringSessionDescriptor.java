/*
 * Created on 15-Nov-2003
 */
package com.ixora.rms.client.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLAttributeMissing;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.repository.DashboardId;
import com.ixora.rms.repository.QueryId;
import com.ixora.rms.ui.dataviewboard.handler.DataViewScreenDescriptor;

/**
 * A monitoring session describing hosts, agents, entities and their
 * configurations as well as queries used to monitor a certain environment.
 * @author Daniel Moraru
 */
public final class MonitoringSessionDescriptor implements XMLExternalizable {
	/** The location where this session is externalized */
	private String fLocation;
	/** The name of the monitoring scheme */
	private String fName;
	/** Agents for every host in the scheme */
	private Map<String, HostDetails> fHosts;
	/** Map of queries */
	private Map<QueryId, QueryDef> fQueries;
	/** List of enabled queries */
	private List<DashboardId> fDashboards;
	/** List of data view screens */
	private List<DataViewScreenDescriptor> fDataViewScreens;

	/**
	 * Constructor for MonitoringSession.
	 */
	public MonitoringSessionDescriptor() {
		this(null);
	}

	/**
	 * Constructor for MonitoringSession.
	 * @param name
	 */
	public MonitoringSessionDescriptor(String name) {
		this(name, null);
	}

	/**
	 * Constructor for MonitoringSession.
	 * @param name
	 * @param location
	 */
	public MonitoringSessionDescriptor(String name, String location) {
		this.fName = name;
		this.fLocation = location;
		this.fHosts = new HashMap<String, HostDetails>();
	}

	/**
	 * @return the list of host names in the Session.
	 */
	public Collection<String> getHostNames() {
		return Collections.unmodifiableCollection(fHosts.keySet());
	}

	/**
	 * @return
	 */
	public Collection<HostDetails> getHostDetails() {
		return Collections.unmodifiableCollection(fHosts.values());
	}

	/**
	 * @return
	 */
	public Map<QueryId, QueryDef> getQueries() {
	    if(fQueries == null) {
	        return null;
	    }
		return Collections.unmodifiableMap(fQueries);
	}

	/**
	 * @return
	 */
	public Collection<DashboardId> getDashboards() {
	    if(fDashboards == null) {
	        return null;
	    }
		return Collections.unmodifiableCollection(fDashboards);
	}

	/**
	 * @return
	 */
	public Collection<DataViewScreenDescriptor> getDataViewScreens() {
	    if(fDataViewScreens == null) {
	        return null;
	    }
		return Collections.unmodifiableCollection(fDataViewScreens);
	}

	/**
	 * Adds details for a host.
	 * @param host
	 * @param dtls
	 */
	public void addHost(HostDetails dtls) {
		this.fHosts.put(dtls.getHost(), dtls);
	}

	/**
	 * Adds the given data view screens.
	 * @param screens
	 */
	public void addDataViewScreens(Collection<DataViewScreenDescriptor> screens) {
	    if(this.fDataViewScreens == null) {
	        this.fDataViewScreens = new LinkedList<DataViewScreenDescriptor>();
	    }
	    this.fDataViewScreens.addAll(screens);
	}

	/**
	 * Adds the given queries.
	 * @param queries
	 */
	public void addQueries(Map<QueryId, QueryDef> queries) {
	    if(this.fQueries == null) {
	        this.fQueries = new HashMap<QueryId, QueryDef>();
	    }
	    this.fQueries.putAll(queries);
	}

	/**
	 * Adds the given dashboards.
	 * @param d
	 */
	public void addDashboards(Collection<DashboardId> d) {
	    if(this.fDashboards == null) {
	        this.fDashboards = new LinkedList<DashboardId>();
	    }
	    this.fDashboards.addAll(d);
	}

	/**
	 * @return the name of the session
	 */
	public String getName() {
		return fName;
	}

	/**
	 * @param name the name of the session
	 */
	public void setName(String name) {
		this.fName = name;
	}

	/**
	 * @return the location of the session
	 */
	public String getLocation() {
		return fLocation;
	}

	/**
	 * @param name the name of the session
	 */
	public void setLocation(String location) {
		this.fLocation = location;
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		if(fName == null) {
			XMLException e = new XMLException("session name is null");
			e.setIsInternalAppError();
			throw e;
		}
		Document doc = parent.getOwnerDocument();
		Element elSession = doc.createElement("session");
		elSession.setAttribute("name", fName);
		parent.appendChild(elSession);
		Element elHosts = doc.createElement("hosts");
		elSession.appendChild(elHosts);
		HostDetails hd;
		for(Iterator iter = fHosts.values().iterator(); iter.hasNext();) {
			hd = (HostDetails)iter.next();
			hd.toXML(elHosts);
		}
		// save queries
		if(fQueries != null) {
			Element elQueries = doc.createElement("queries");
			elSession.appendChild(elQueries);
			for(Map.Entry<QueryId, QueryDef> me : fQueries.entrySet()) {
				Element elQuery = doc.createElement("query");
				elQueries.appendChild(elQuery);
				XMLUtils.writeObject(QueryDef.class, elQuery, me.getValue());
				Node nQuery = elQuery.getLastChild();
				((Element)nQuery).setAttribute("id", me.getKey().toString());
			}
		}
		// save dashboards
		if(fDashboards != null) {
			Element elDashboards = doc.createElement("dashboards");
			elSession.appendChild(elDashboards);
			for(DashboardId did : fDashboards) {
	            Element elDashboard = doc.createElement("dashboard");
	            elDashboards.appendChild(elDashboard);
	            elDashboard.setAttribute("id", did.toString());
            }
		}
		// save screens
		if(fDataViewScreens != null) {
			Element elScreens = doc.createElement("screens");
			elSession.appendChild(elScreens);
			XMLUtils.writeObjects(DataViewScreenDescriptor.class, elScreens,
			       this.fDataViewScreens.toArray(
			               new XMLExternalizable[this.fDataViewScreens.size()]));
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Attr a = XMLUtils.findAttribute(node, "name");
		if(a == null) {
			throw new XMLAttributeMissing("name");
		}
		this.fName = a.getValue();
		Node n = XMLUtils.findChild(node, "hosts");
		if(n == null) {
			throw new XMLNodeMissing("hosts");
		}
		List l = XMLUtils.findChildren(n, "host");
		HostDetails hd;
		for(Iterator iter = l.iterator(); iter.hasNext();) {
			n = (Node)iter.next();
			hd = new HostDetails();
			hd.fromXML(n);
			addHost(hd);
		}
		// load queries
		n = XMLUtils.findChild(node, "queries");
		if(n != null) {
			List nl = XMLUtils.findChildren(n, "query");
			this.fQueries = new HashMap<QueryId, QueryDef>();
			for(Iterator iter = nl.iterator(); iter.hasNext();) {
	            n = (Node)iter.next();
	            Node nQuery = XMLUtils.findChild(n, "query");
	            if(nQuery == null) {
	            	throw new XMLNodeMissing("query");
	            }
	            try {
	            	QueryDef query = (QueryDef)XMLUtils.readObject(QueryDef.class, nQuery);
		            a = XMLUtils.findAttribute(nQuery, "id");
		            if(a == null) {
		                throw new XMLAttributeMissing("id");
		            }
		            QueryId qId = new QueryId(a.getValue().trim());
		            // IMPORTANT: set query identifier
		            // QueryDef doesn't externalize its identifier
		            // so it's important to be set here
		            query.setIdentifier(qId.getName());
		            this.fQueries.put(qId, query);
	            } catch (XMLException e) {
	                throw e;
	            } catch (Exception e) {
	                throw new XMLException(e);
	            }
	        }
		}
		// load dashboards
		n = XMLUtils.findChild(node, "dashboards");
		if(n != null) {
			List nl = XMLUtils.findChildren(n, "dashboard");
			this.fDashboards = new LinkedList<DashboardId>();
			for(Iterator iter = nl.iterator(); iter.hasNext();) {
	            n = (Node)iter.next();
	            a = XMLUtils.findAttribute(n, "id");
	            if(a == null) {
	                throw new XMLAttributeMissing("id");
	            }
	            this.fDashboards.add(new DashboardId(a.getNodeValue()));
	        }
		}
		// load screens
		n = XMLUtils.findChild(node, "screens");
		if(n != null) {
			XMLExternalizable[] vbs;
            try {
                vbs = XMLUtils.readObjects(DataViewScreenDescriptor.class, n, "screen");
                if(!Utils.isEmptyArray(vbs)) {
                    this.fDataViewScreens = new ArrayList<DataViewScreenDescriptor>(vbs.length);
                    for(XMLExternalizable vb : vbs) {
                    	this.fDataViewScreens.add((DataViewScreenDescriptor)vb);
                    }
                }
            } catch (XMLException e) {
                throw e;
            } catch (Exception e) {
                throw new XMLException(e);
            }
		}
	}
}
