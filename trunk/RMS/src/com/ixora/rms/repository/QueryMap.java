/*
 * Created on 10-Jul-2004
 */
package com.ixora.rms.repository;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.collections.CaseInsensitiveLinkedMap;
import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.dataengine.RealizedQuery;

/**
 * This is a container for a set of queries that knows to
 * serialize itself as XML.
 * @author Daniel Moraru
 */
public class QueryMap implements XMLExternalizable {
	private static final long serialVersionUID = -2753779034638751220L;
	/** Logger */
	@SuppressWarnings("unused")
	private static final AppLogger logger =
		AppLoggerFactory.getLogger(QueryMap.class);
	/** Queries */
	private CaseInsensitiveLinkedMap<RealizedQuery> queries;

	/**
	 * Constructor.
	 */
	public QueryMap() {
		super();
		queries = new CaseInsensitiveLinkedMap<RealizedQuery>();
	}

	/**s
	 * @return the counter groups
	 */
	public RealizedQuery[] getQueries() {
		return (RealizedQuery[])this.queries.values().toArray(
				new RealizedQuery[this.queries.size()]);
	}

	/**
	 * Adds a query.
	 * @param query
	 */
	public void addQuery(RealizedQuery query) {
		this.queries.put(query.getIdentifier(), query);
	}

	/**
	 * Removes a query.
	 * @param queryName
	 */
	public void removeQuery(String queryName) {
		this.queries.remove(queryName);
	}

	/**
	 * Returns the query with the given name or null if not found.
	 * @param name
	 * @return
	 */
	public RealizedQuery getQuery(String name) {
		return (RealizedQuery)this.queries.get(name);
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element qe = doc.createElement("queries");
		parent.appendChild(qe);
		if(this.queries != null) {
/**			XMLUtils.writeObjects(
					Cube.class,
					qe,
					(Cube[])this.queries.values().toArray(
						new Cube[this.queries.size()]));
*/		}
	}
	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		// TODO not implemented yet... at the moment we don't use query maps.
		throw new AppRuntimeException("Not implemented.");
/*		Node n = XMLUtils.findChild(node, "queries");
		if(n != null) {
			try {
				XMLExternalizable[] objs = XMLUtils.readObjects(
						Cube.class, n, "query");
				if(objs == null) {
				    return;
				}
				Cube c;
				for(int i = 0; i < objs.length; i++) {
					c = (Cube)objs[i];
					addQuery(c);
				}
			} catch(Exception e) {
				logger.error(e);
			}
		}
*/	}
}
